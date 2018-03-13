/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.rest;

import com.liferay.oauth2.provider.configuration.OAuth2Configuration;
import com.liferay.oauth2.provider.constants.OAuth2ProviderActionKeys;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.grants.refresh.RefreshTokenGrantHandler;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenGrantHandler;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

@Component(
	configurationPid = "com.liferay.oauth2.provider.configuration.OAuth2Configuration",
	immediate = true
)
public class LiferayRefreshTokenGrantHandlerRegistrator {

	private ServiceRegistration<AccessTokenGrantHandler>
		_serviceRegistration;

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		OAuth2Configuration oAuth2Configuration =
			ConfigurableUtil.createConfigurable(
				OAuth2Configuration.class, properties);

		if (oAuth2Configuration.allowRefreshTokenGrant()) {
			RefreshTokenGrantHandler refreshTokenGrantHandler =
				new RefreshTokenGrantHandler();

			refreshTokenGrantHandler.setDataProvider(
				_liferayOAuthDataProvider);

			_serviceRegistration = bundleContext.registerService(
				AccessTokenGrantHandler.class,
				new LiferayPermissionedAccessTokenGrantHandler(
					refreshTokenGrantHandler,
					this::hasCreateTokenPermission),
				new Hashtable<>());
		}
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}


	protected boolean clientsMatch(Client client1, Client client2) {
		String client1Id = client1.getClientId();
		String client2Id = client2.getClientId();

		if (!Objects.equals(client1Id, client2Id)) {
			return false;
		}

		Map<String, String> properties = client1.getProperties();

		String companyId1 = properties.get("companyId");

		properties = client2.getProperties();

		String companyId2 = properties.get("companyId");

		if (!Objects.equals(companyId1, companyId2)) {
			return false;
		}

		return true;
	}

	protected boolean hasCreateTokenPermission(
		Client client, MultivaluedMap<String, String> params) {

		String refreshTokenString = params.getFirst("refresh_token");

		if (refreshTokenString == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("No refresh_token parameter was provided.");
			}

			return false;
		}

		RefreshToken refreshToken =
			_liferayOAuthDataProvider.getRefreshToken(refreshTokenString);

		if (refreshToken == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("No refresh token found for " + refreshTokenString);
			}

			return false;
		}

		if(!clientsMatch(client, refreshToken.getClient())) {
			// audit: Trying to refresh token with other client's authentication

			_liferayOAuthDataProvider.doRevokeRefreshToken(refreshToken);

			for (String accessToken : refreshToken.getAccessTokens()) {
				ServerAccessToken serverAccessToken =
					_liferayOAuthDataProvider.getAccessToken(accessToken);

				_liferayOAuthDataProvider.doRevokeAccessToken(
					serverAccessToken);
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Client authentication doesn't mach refresh token's client");
			}

			return false;
		}

		OAuth2Application oAuth2Application =
			_liferayOAuthDataProvider.resolveOAuth2Application(
				refreshToken.getClient());

		String subjectId = refreshToken.getSubject().getId();

		long userId = Long.parseLong(subjectId);

		PermissionChecker permissionChecker = null;

		try {
			User user = _userLocalService.getUserById(userId);

			permissionChecker =
				PermissionCheckerFactoryUtil.create(user);
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to create PermissionChecker for user " + userId);
			}

			return false;
		}

		if (permissionChecker.hasOwnerPermission(
			oAuth2Application.getCompanyId(), OAuth2Application.class.getName(),
			oAuth2Application.getOAuth2ApplicationId(),
			oAuth2Application.getUserId(),
			OAuth2ProviderActionKeys.ACTION_CREATE_TOKEN)) {

			return true;
		}

		if (permissionChecker.hasPermission(
			0, OAuth2Application.class.getName(),
			oAuth2Application.getOAuth2ApplicationId(),
			OAuth2ProviderActionKeys.ACTION_CREATE_TOKEN)) {

			return true;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"User " + userId +
					" doesn't have permission to create refresh token for " +
						"client " + client.getClientId());
		}

		return false;
	}

	private static Log _log =
		LogFactoryUtil.getLog(
			LiferayClientCredentialsGrantHandlerRegistrator.class);

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

	@Reference
	private UserLocalService _userLocalService;

}
