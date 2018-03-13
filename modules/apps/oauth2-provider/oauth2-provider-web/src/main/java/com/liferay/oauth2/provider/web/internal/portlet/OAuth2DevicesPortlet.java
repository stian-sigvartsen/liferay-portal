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

package com.liferay.oauth2.provider.web.internal.portlet;

import com.liferay.oauth2.provider.service.OAuth2AuthorizationService;
import com.liferay.oauth2.provider.service.OAuth2RefreshTokenService;
import com.liferay.oauth2.provider.service.OAuth2TokenService;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"javax.portlet.display-name=My OAuth2 Devices",
		"javax.portlet.init-param.portlet-title-based-navigation=true",
		"javax.portlet.init-param.template-path=/devices/",
		"javax.portlet.init-param.view-template=/devices/view.jsp",
		"javax.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_DEVICES_PORTLET,
		"javax.portlet.resource-bundle=content.Language",
	},
	service = Portlet.class
)
public class OAuth2DevicesPortlet extends MVCPortlet {

	public void revokeAuthorizationTokens(
		ActionRequest request, ActionResponse response)
		throws PortalException {

		long oAuth2TokenId = ParamUtil.getLong(request, "oAuth2TokenId");

		long oAuth2RefreshTokenId = ParamUtil.getLong(
			request, "oAuth2RefreshTokenId");

		_oAuth2AuthorizationService.revokeAuthorization(
			oAuth2TokenId, oAuth2RefreshTokenId);
	}

	@Reference
	private OAuth2AuthorizationService _oAuth2AuthorizationService;
	@Reference
	private OAuth2RefreshTokenService _oAuth2RefreshTokenService;
	@Reference
	private OAuth2TokenService _oAuth2TokenService;
}
