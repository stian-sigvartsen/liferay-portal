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

import static com.liferay.oauth2.provider.web.internal.constants.OAuth2AdminWebKeys.ASSIGN_SCOPES_MODEL;

import com.liferay.oauth2.provider.scope.liferay.ScopeDescriptorLocator;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.scope.spi.application.descriptor.ApplicationDescriptor;
import com.liferay.oauth2.provider.scope.spi.scope.descriptor.ScopeDescriptor;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.oauth2.provider.web.internal.display.context.AssignScopesModel;
import com.liferay.oauth2.provider.web.internal.display.context.AuthorizationModel;
import com.liferay.oauth2.provider.web.internal.display.context.AuthorizationModel.ApplicationScopeDescriptor;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Tomas Polesovsky
 * @author Stian Sigvartsen
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

		Map<String, String> invocationResultCache = new HashMap<>(50);

		AuthorizationModel.ApplicationDescriptor
			assignScopesModelApplicationDescriptor = (cid, applicationName) -> {
				return _getFromCacheOr(
					cid + "|" + applicationName, invocationResultCache,
					() -> {
						ApplicationDescriptor applicationDescriptor =
							_applicationDescriptors.getService(applicationName);

						return applicationDescriptor.describeApplication(
							themeDisplay.getLocale());
					});
			};

		ApplicationScopeDescriptor applicationScopeDescriptor =
			(cid, applicationName, scope) -> {
				String key = StringBundler.concat(
					String.valueOf(cid), "|", applicationName, "|", scope);

				return _getFromCacheOr(
					key, invocationResultCache,
					() -> {
						ScopeDescriptor scopeDescriptor =
							_scopeDescriptorLocator.getScopeDescriptor(
								applicationName);

						return scopeDescriptor.describeScope(
							scope, themeDisplay.getLocale());
					});
			};

		AssignScopesModel assignScopesModel = new AssignScopesModel(
			companyId, _scopeFinderLocator,
			assignScopesModelApplicationDescriptor, applicationScopeDescriptor);

		renderRequest.setAttribute(ASSIGN_SCOPES_MODEL, assignScopesModel);

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

	@SuppressWarnings("unchecked")
	private <T> T _getFromCacheOr(
		String key, Map<String, T> invocationResultCache,
		Supplier<T> supplier) {

		T value = invocationResultCache.get(key);

		if (value == null) {
			value = supplier.get();

			invocationResultCache.put(key, value);
		}

		return (T)value;
	}

	private ServiceTrackerMap<String, ApplicationDescriptor>
		_applicationDescriptors;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile ScopeDescriptorLocator _scopeDescriptorLocator;

	@Reference
	private ScopeLocator _scopeFinderLocator;

}