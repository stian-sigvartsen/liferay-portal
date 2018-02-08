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

import com.liferay.oauth2.provider.constants.OAuth2ProviderActionKeys;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.owner.ResourceOwnerGrantHandler;
import org.apache.cxf.rs.security.oauth2.grants.owner.ResourceOwnerLoginHandler;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenGrantHandler;
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

@Component(immediate = true)
public class LiferayResourceOwnerGrantHandlerRegistrator {

	private ServiceRegistration<AccessTokenGrantHandler>
		_serviceRegistration;

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		boolean enabled = MapUtil.getBoolean(properties, "enabled", true);

		if (enabled) {
			ResourceOwnerGrantHandler resourceOwnerGrantHandler =
				new ResourceOwnerGrantHandler();

			resourceOwnerGrantHandler.setLoginHandler(
				_liferayLoginHandler);
			resourceOwnerGrantHandler.setDataProvider(
				_liferayOAuthDataProvider);

			_serviceRegistration = bundleContext.registerService(
				AccessTokenGrantHandler.class,
				new LiferayPermissionedAccessTokenGrantHandler(
					resourceOwnerGrantHandler,
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

	protected boolean hasCreateTokenPermission(
		Client client, MultivaluedMap<String, String> params) {

		String userName = params.getFirst("username");
		String password = params.getFirst("password");

		if (userName == null || password == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("username or password parameter was not provided.");
			}

			return false;
		}

		OAuth2Application oAuth2Application =
			_liferayOAuthDataProvider.resolveOAuth2Application(
				client);

		PermissionChecker permissionChecker = null;

		User user = null;

		try {
			if (_liferayLoginHandler instanceof LiferayResourceOwnerLoginHandler) {
				user =
					((LiferayResourceOwnerLoginHandler)_liferayLoginHandler).
						authenticateUser(userName, password);
			}
			else {
				UserSubject userSubject = _liferayLoginHandler.createSubject(
					userName, password);

				String subjectId = userSubject.getId();

				long userId = Long.parseLong(subjectId);

				user = _userLocalService.getUserById(userId);
			}

			permissionChecker =
				PermissionCheckerFactoryUtil.create(user);
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to create PermissionChecker for user " + userName);
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
				"User " + user.getUserId() +
					" doesn't have permission to create access token for " +
						"client " + client.getClientId());
		}

		return false;
	}
	private static Log _log =
		LogFactoryUtil.getLog(
			LiferayResourceOwnerGrantHandlerRegistrator.class);

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private ResourceOwnerLoginHandler _liferayLoginHandler;

	@Reference
	private UserLocalService _userLocalService;

}
