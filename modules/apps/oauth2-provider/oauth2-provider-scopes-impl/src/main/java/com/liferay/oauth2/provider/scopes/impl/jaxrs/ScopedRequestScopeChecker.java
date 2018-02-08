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

package com.liferay.oauth2.provider.scopes.impl.jaxrs;

import com.liferay.oauth2.provider.scopes.api.ScopeChecker;
import com.liferay.oauth2.provider.scopes.liferay.api.ScopedServiceTrackerMap;
import com.liferay.oauth2.provider.scopes.spi.RequestScopeChecker;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.PortalUtil;
import org.osgi.framework.BundleContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.function.Supplier;

public class ScopedRequestScopeChecker implements ContainerRequestFilter {

	private final ScopedServiceTrackerMap<RequestScopeChecker>
		_scopedServiceTrackerMap;
	private ScopeChecker _scopeChecker;

	public ScopedRequestScopeChecker(
		BundleContext bundleContext, ScopeChecker scopeChecker,
		Supplier<RequestScopeChecker> defaultRequestScopeCheckerSupplier) {

		_scopeChecker = scopeChecker;
		_scopedServiceTrackerMap = new ScopedServiceTrackerMap<>(
			bundleContext, RequestScopeChecker.class, "osgi.jaxrs.name",
			defaultRequestScopeCheckerSupplier);
	}

	@Override
	public void filter(ContainerRequestContext requestContext)
		throws IOException {

		try {
			Company company = PortalUtil.getCompany(_httpServletRequest);

			Class<? extends Application> clazz = _application.getClass();

			RequestScopeChecker requestScopeChecker =
				_scopedServiceTrackerMap.getService(
					company.getCompanyId(), clazz.getName());

			if (!requestScopeChecker.isAllowed(
				_scopeChecker, requestContext.getRequest(), _resourceInfo)) {

				requestContext.abortWith(Response.status(403).build());

				return;
			}
		}
		catch (PortalException e) {
			requestContext.abortWith(Response.status(500).build());

			return;
		}

	}

	@Context
	Application _application;

	@Context
	HttpServletRequest _httpServletRequest;

	@Context
	ResourceInfo _resourceInfo;
}
