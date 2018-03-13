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

package com.liferay.oauth2.provider.web.internal.portlet.action;

import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scope.liferay.ScopeDescriptorLocator;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.scope.spi.application.descriptor.ApplicationDescriptor;
import com.liferay.oauth2.provider.scope.spi.scope.descriptor.ScopeDescriptor;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.oauth2.provider.web.internal.display.context.AuthorizationRequestModel;
import com.liferay.oauth2.provider.web.internal.display.context.AuthorizationRequestModel.ApplicationScopeDescriptor;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.liferay.oauth2.provider.web.internal.constants.OAuth2AdminWebKeys.SCOPES;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"javax.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_ADMIN_PORTLET,
		"mvc.command.name=/admin/assign_scopes"
	}
)
public class AssignScopesMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		long companyId = company.getCompanyId();

		Collection<String> externalAliases =
			_scopeFinderLocator.getScopeAliases(companyId);

		Map<String, AuthorizationRequestModel> aliasedScopes = new HashMap<>();
		
		ApplicationScopeDescriptor applicationScopeDescriptor =
			(cid, applicationName, scope) -> {
				ScopeDescriptor scopeDescriptor =
					_scopeDescriptorLocator.getScopeDescriptor(
						applicationName);

				return scopeDescriptor.describeScope(
					scope, themeDisplay.getLocale());
			};

		for (String externalAlias : externalAliases) {
			AuthorizationRequestModel authorizationRequestModel =
				aliasedScopes.computeIfAbsent(
					externalAlias,
					__ -> new AuthorizationRequestModel(
						externalAliases.size(),
						applicationScopeDescriptor));

			Collection<LiferayOAuth2Scope> liferayOAuth2Scopes =
				_scopeFinderLocator.getLiferayOAuth2Scopes(
					companyId, externalAlias);

			for (LiferayOAuth2Scope liferayOAuth2Scope : liferayOAuth2Scopes) {
				authorizationRequestModel.addLiferayOAuth2Scope(liferayOAuth2Scope);
			}
		}

		renderRequest.setAttribute(SCOPES, aliasedScopes);
		renderRequest.setAttribute(
			"applicationDescriptors", _applicationDescriptors);

		return "/admin/assign_scopes.jsp";
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_applicationDescriptors = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ApplicationDescriptor.class, "osgi.jaxrs.name");
	}

	@Deactivate
	protected void deactivate() {
		_applicationDescriptors.close();
	}

	@Reference(
		policyOption = ReferencePolicyOption.GREEDY,
		policy = ReferencePolicy.DYNAMIC
	)
	private volatile ScopeDescriptorLocator _scopeDescriptorLocator;

	private ServiceTrackerMap<String, ApplicationDescriptor>
		_applicationDescriptors;


	@Reference
	private ScopeLocator _scopeFinderLocator;


}