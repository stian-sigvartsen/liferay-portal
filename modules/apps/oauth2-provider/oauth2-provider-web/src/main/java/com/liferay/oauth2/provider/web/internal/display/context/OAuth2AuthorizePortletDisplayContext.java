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

import com.liferay.oauth2.provider.constants.OAuth2ProviderActionKeys;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public class OAuth2AuthorizePortletDisplayContext {

	public AuthorizationModel getAuthorizationModel() {
		return _authorizationModel;
	}

	public OAuth2Application getOAuth2Application() {
		return _oAuth2Application;
	}

	public Map<String, String> getOAuth2Parameters() {
		return _oAuth2Parameters;
	}

	public boolean hasCreateTokenApplicationPermission(
		OAuth2Application oAuth2Application) {

		return hasPermission(
			oAuth2Application, OAuth2ProviderActionKeys.ACTION_CREATE_TOKEN);
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

	public void setAuthorizationModel(AuthorizationModel authorizationModel) {
		_authorizationModel = authorizationModel;
	}

	public void setOAuth2Application(OAuth2Application oAuth2Application) {
		_oAuth2Application = oAuth2Application;
	}

	public void setOAuth2Parameters(Map<String, String> oAuth2Parameters) {
		_oAuth2Parameters = oAuth2Parameters;
	}

	private AuthorizationModel _authorizationModel;
	private OAuth2Application _oAuth2Application;
	private Map<String, String> _oAuth2Parameters = new HashMap<>();

}