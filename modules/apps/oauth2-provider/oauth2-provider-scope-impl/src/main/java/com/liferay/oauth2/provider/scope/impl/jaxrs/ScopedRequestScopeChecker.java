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

package com.liferay.oauth2.provider.scope.impl.jaxrs;

import com.liferay.oauth2.provider.rest.spi.request.scope.checker.filter.RequestScopeCheckerFilter;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.oauth2.provider.scope.liferay.ScopedServiceTrackerMap;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * @author Carlos Sierra Andrés
 */
public class ScopedRequestScopeChecker implements ContainerRequestFilter {

	public ScopedRequestScopeChecker(
		ScopedServiceTrackerMap<RequestScopeCheckerFilter>
			scopedServiceTrackerMap, ScopeChecker scopeChecker) {

		_scopedServiceTrackerMap = scopedServiceTrackerMap;

		_scopeChecker = scopeChecker;
	}

	@Override
	public void filter(ContainerRequestContext requestContext)
		throws IOException {

		try {
			Company company = PortalUtil.getCompany(_httpServletRequest);

			Class<? extends Application> clazz = _application.getClass();

			RequestScopeCheckerFilter requestScopeChecker =
				_scopedServiceTrackerMap.getService(
					company.getCompanyId(), clazz.getName());

			if (!requestScopeChecker.isAllowed(
					_scopeChecker, requestContext.getRequest(),
					_resourceInfo)) {

				requestContext.abortWith(Response.status(403).build());

				return;
			}
		}
		catch (PortalException pe) {
			Response.ResponseBuilder builder = Response.status(500);

			Response response = builder.entity(
				pe.getMessage()
			).build();

			requestContext.abortWith(response);

			return;
		}
	}

	@Context
	private Application _application;

	@Context
	private HttpServletRequest _httpServletRequest;

	@Context
	private ResourceInfo _resourceInfo;

	private final ScopeChecker _scopeChecker;
	private final ScopedServiceTrackerMap<RequestScopeCheckerFilter>
		_scopedServiceTrackerMap;

}