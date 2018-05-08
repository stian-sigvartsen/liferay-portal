/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.web.internal.display.context;

import com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.constants.OAuth2ProviderActionKeys;
import com.liferay.oauth2.provider.constants.OAuth2ProviderConstants;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2AdminActionKeys;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletPreferences;

/**
 * @author Tomas Polesovsky
 */
public class OAuth2AdminPortletDisplayContext {

	public OAuth2AdminPortletDisplayContext(
		OAuth2ProviderConfiguration oAuth2ProviderConfiguration) {

		_oAuth2ProviderConfiguration = oAuth2ProviderConfiguration;
	}

	public List<GrantType> getOAuth2Grants(
		PortletPreferences portletPreferences) {

		String[] oAuth2Grants = StringUtil.split(
			portletPreferences.getValue("oAuth2Grants", StringPool.BLANK));

		List<GrantType> result = new ArrayList<>();

		for (String oAuth2Grant : oAuth2Grants) {
			result.add(GrantType.valueOf(oAuth2Grant));
		}

		if (result.isEmpty()) {
			Collections.addAll(result, GrantType.values());
		}

		if (!_oAuth2ProviderConfiguration.allowAuthorizationCodeGrant()) {
			result.remove(GrantType.AUTHORIZATION_CODE);
		}

		if (!_oAuth2ProviderConfiguration.allowAuthorizationCodePKCEGrant()) {
			result.remove(GrantType.AUTHORIZATION_CODE_PKCE);
		}

		if (!_oAuth2ProviderConfiguration.allowClientCredentialsGrant()) {
			result.remove(GrantType.CLIENT_CREDENTIALS);
		}

		if (!_oAuth2ProviderConfiguration.allowRefreshTokenGrant()) {
			result.remove(GrantType.REFRESH_TOKEN);
		}

		if (!_oAuth2ProviderConfiguration.
				allowResourceOwnerPasswordCredentialsGrant()) {

			result.remove(GrantType.RESOURCE_OWNER_PASSWORD);
		}

		return result;
	}

	public boolean hasAddApplicationPermission() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.hasPermission(
				0, OAuth2ProviderConstants.RESOURCE_NAME,
				OAuth2ProviderConstants.RESOURCE_NAME,
				OAuth2ProviderActionKeys.ACTION_ADD_APPLICATION)) {

			return true;
		}

		return false;
	}

	public boolean hasDeletePermission(OAuth2Application oAuth2Application) {
		return hasPermission(oAuth2Application, ActionKeys.DELETE);
	}

	public boolean hasPermission(
		OAuth2Application oAuth2Application, String actionId) {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.hasOwnerPermission(
				oAuth2Application.getCompanyId(),
				OAuth2Application.class.getName(),
				oAuth2Application.getOAuth2ApplicationId(),
				oAuth2Application.getUserId(), actionId)) {

			return true;
		}

		if (permissionChecker.hasPermission(
				0, OAuth2Application.class.getName(),
				oAuth2Application.getOAuth2ApplicationId(), actionId)) {

			return true;
		}

		return false;
	}

	public boolean hasPermissionsPermission(
		OAuth2Application oAuth2Application) {

		return hasPermission(oAuth2Application, ActionKeys.PERMISSIONS);
	}

	public boolean hasRevokeTokenPermission(
		OAuth2Application oAuth2Application) {

		return hasPermission(
			oAuth2Application, OAuth2ProviderActionKeys.ACTION_REVOKE_TOKEN);
	}

	public boolean hasUpdatePermission(OAuth2Application oAuth2Application) {
		return hasPermission(oAuth2Application, ActionKeys.UPDATE);
	}

	public boolean hasViewGrantedAuthorizationsPermission() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			return PortletPermissionUtil.contains(
				permissionChecker,
				OAuth2ProviderPortletKeys.OAUTH2_ADMIN_PORTLET,
				OAuth2AdminActionKeys.VIEW_GRANTED_AUTHORIZATIONS);
		}
		catch (PortalException pe) {
			_log.error(pe);

			return false;
		}
	}

	public boolean hasViewPermission(OAuth2Application oAuth2Application) {
		return hasPermission(oAuth2Application, ActionKeys.VIEW);
	}

	private static Log _log = LogFactoryUtil.getLog(
		OAuth2AdminPortletDisplayContext.class);

	private final OAuth2ProviderConfiguration _oAuth2ProviderConfiguration;

}