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

package com.liferay.oauth2.provider.web;

import com.liferay.oauth2.provider.exception.NoSuchOAuth2ApplicationException;
import com.liferay.oauth2.provider.model.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.scopes.liferay.api.ScopeFinderLocator;
import com.liferay.oauth2.provider.scopes.liferay.api.ScopedServiceTrackerMap;
import com.liferay.oauth2.provider.scopes.spi.ScopeDescriptor;
import com.liferay.oauth2.provider.service.OAuth2ApplicationService;
import com.liferay.oauth2.provider.web.internal.display.context.AuthorizationRequestModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import static com.liferay.oauth2.provider.web.internal.constants.OAuth2AdminWebKeys.AUTHORIZATION_REQUEST_MODEL;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

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
		"javax.portlet.name=com.liferay.oauth2.provider.web.OAuth2AuthorizePortlet",
		"javax.portlet.resource-bundle=content.Language"
	},
	service = Portlet.class
)
public class OAuth2AuthorizePortlet extends MVCPortlet {
	
	@Activate
	protected void activate(BundleContext bundleContext) {
		_scopedScopeDescriptors = new ScopedServiceTrackerMap<>(
			bundleContext, ScopeDescriptor.class, "osgi.jaxrs.name",
			() -> _defaultScopeDescriptor);
	}
		
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long companyId = themeDisplay.getCompanyId();

		HttpServletRequest originalServletRequest = 
			PortalUtil.getOriginalServletRequest(
				PortalUtil.getHttpServletRequest(renderRequest));
		
		String clientId = ParamUtil.getString(originalServletRequest, "client_id");
		String scope = ParamUtil.getString(originalServletRequest, "scope");		
		
		try {
			AuthorizationRequestModel authorizationRequestModel = 
				buildAuthorizationRequestModel(
					companyId, clientId, scope, themeDisplay.getLocale());
			
			renderRequest.setAttribute(AUTHORIZATION_REQUEST_MODEL, authorizationRequestModel);
		} 
		catch (NoSuchOAuth2ApplicationException | PrincipalException e) {
			throw new PortletException(e);
		}
		
		super.doView(renderRequest, renderResponse);
	}
	
	protected AuthorizationRequestModel buildAuthorizationRequestModel(
		long companyId, String clientId, String scopeAlias, Locale locale) 
		throws NoSuchOAuth2ApplicationException, PrincipalException {

		OAuth2Application oAuth2Application = 
			_oAuth2ApplicationService.getOAuth2Application(
					companyId, clientId);
		
		List<String> oAuth2ApplicationAllowedScopes = 
			oAuth2Application.getScopesList();
		
		AuthorizationRequestModel.ApplicationScopeDescriptor 
			applicationScopeDescriptor =
				(__, applicationName, internalScope) -> {
					ScopeDescriptor scopeDescriptor =
						_scopedScopeDescriptors.getService(
							companyId, applicationName);
	
					return scopeDescriptor.describe(
						internalScope, locale);
				};
		
		AuthorizationRequestModel authorizationRequestModel =
			new AuthorizationRequestModel(10, applicationScopeDescriptor);
			
		for (String requestedScope : scopeAlias.split(" ")) {
			
			if (!oAuth2ApplicationAllowedScopes.contains(requestedScope)) {
				continue;
			}
			
			Collection<LiferayOAuth2Scope> liferayOAuth2Scopes = 
				_scopeFinderLocator.locateScopes(companyId, requestedScope);
			
			for (LiferayOAuth2Scope liferayOAuth2Scope : liferayOAuth2Scopes) {				
				authorizationRequestModel.addLiferayOAuth2Scope(
					liferayOAuth2Scope);
			}
		}
		return authorizationRequestModel;
	}

	@Reference
	private OAuth2ApplicationService _oAuth2ApplicationService;
	
	@Reference
	private ScopeFinderLocator _scopeFinderLocator;

	private ScopedServiceTrackerMap<ScopeDescriptor> _scopedScopeDescriptors;

	@Reference(
		target = "(default=true)",
		policyOption = ReferencePolicyOption.GREEDY
	)
	private ScopeDescriptor _defaultScopeDescriptor;	
}