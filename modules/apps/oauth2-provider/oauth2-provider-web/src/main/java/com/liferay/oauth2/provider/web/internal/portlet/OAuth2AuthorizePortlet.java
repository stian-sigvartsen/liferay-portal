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

import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.scope.liferay.ScopeDescriptorLocator;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.scope.spi.scope.descriptor.ScopeDescriptor;
import com.liferay.oauth2.provider.service.OAuth2ApplicationService;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2AdminWebKeys;
import com.liferay.oauth2.provider.web.internal.display.context.AuthorizationRequestModel;
import com.liferay.oauth2.provider.web.internal.display.context.OAuth2AuthorizePortletDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.application-type=full-page-application",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"javax.portlet.display-name=OAuth2 Authorize Portlet",
		"javax.portlet.init-param.portlet-title-based-navigation=true",
		"javax.portlet.init-param.template-path=/authorize/",
		"javax.portlet.init-param.view-template=/authorize/authorize.jsp",
		"javax.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_AUTHORIZE_PORTLET,
		"javax.portlet.resource-bundle=content.Language"
	},
	service = Portlet.class
)
public class OAuth2AuthorizePortlet extends MVCPortlet {
	
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long companyId = themeDisplay.getCompanyId();

		HttpServletRequest request =
			PortalUtil.getOriginalServletRequest(
				PortalUtil.getHttpServletRequest(renderRequest));

		OAuth2AuthorizePortletDisplayContext context =
			new OAuth2AuthorizePortletDisplayContext();

		Map<String, String> oAuth2Parameters = getOAuth2Parameters(request);

		context.setOAuth2Parameters(oAuth2Parameters);

		String clientId = oAuth2Parameters.get("client_id");

		try {

			OAuth2Application oAuth2Application =
				_oAuth2ApplicationService.fetchOAuth2Application(
					companyId, clientId);

			context.setOAuth2Application(oAuth2Application);

			String[] requestedScopes = StringUtil.split(
				oAuth2Parameters.get("scope"), StringPool.SPACE);

			List<String> allowedScopes = oAuth2Application.getScopesList();

			AuthorizationRequestModel authorizationRequestModel =
				new AuthorizationRequestModel(
					allowedScopes.size(),
					buildApplicationScopeDescriptor(themeDisplay.getLocale()));

			locateLiferayOAuth2Scopes(
				companyId, allowedScopes,
				authorizationRequestModel, requestedScopes);

			context.setAuthorizationRequestModel(authorizationRequestModel);

			renderRequest.setAttribute(
				OAuth2AdminWebKeys.AUTHORIZE_DISPLAY_CONTEXT, context);
		} 
		catch (PrincipalException e) {
			throw new PortletException(e);
		}
		
		super.doView(renderRequest, renderResponse);
	}

	protected AuthorizationRequestModel.ApplicationScopeDescriptor
		buildApplicationScopeDescriptor(Locale locale){

		AuthorizationRequestModel.ApplicationScopeDescriptor
			applicationScopeDescriptor =
			(__, applicationName, internalScope) -> {
				ScopeDescriptor scopeDescriptor =
					_scopeDescriptorLocator.getScopeDescriptor(applicationName);

				return scopeDescriptor.describeScope(
					internalScope, locale);
			};

		return applicationScopeDescriptor;
	}

	protected Map<String, String> getOAuth2Parameters(
		HttpServletRequest request) {

		HashMap<String, String> result = new HashMap<>();
		for(Enumeration<String> names = request.getParameterNames();
			names.hasMoreElements();) {

			String name = names.nextElement();

			if (name.startsWith("oauth2_")) {
				result.put(
					name.substring("oauth2_".length()),
					ParamUtil.getString(request, name));
			}
		}

		return result;
	}

	protected void locateLiferayOAuth2Scopes(
		long companyId, List<String> allowedScopes,
		AuthorizationRequestModel authorizationRequestModel,
		String[] requestedScopes) {

		for (String requestedScope : requestedScopes) {
			if (!allowedScopes.contains(requestedScope)) {
				continue;
			}

			Collection<LiferayOAuth2Scope> liferayOAuth2Scopes =
				_scopeFinderLocator.getLiferayOAuth2Scopes(
					companyId, requestedScope);

			for (LiferayOAuth2Scope liferayOAuth2Scope : liferayOAuth2Scopes) {
				authorizationRequestModel.addLiferayOAuth2Scope(
					liferayOAuth2Scope);
			}
		}
	}

	@Reference
	private OAuth2ApplicationService _oAuth2ApplicationService;
	
	@Reference
	private ScopeLocator _scopeFinderLocator;

	@Reference
	ScopeDescriptorLocator _scopeDescriptorLocator;

}