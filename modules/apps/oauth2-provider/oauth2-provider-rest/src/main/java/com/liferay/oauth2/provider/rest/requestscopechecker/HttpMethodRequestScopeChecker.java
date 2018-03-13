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

package com.liferay.oauth2.provider.rest.requestscopechecker;

import com.liferay.oauth2.provider.rest.spi.request.scope.checker.filter.RequestScopeCheckerFilter;

import com.liferay.oauth2.provider.scope.ScopeChecker;
import org.osgi.service.component.annotations.Component;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Request;

@Component(
	immediate = true,
	property = {
		"default=true",
		"type=http.method"
	}
)
public class HttpMethodRequestScopeChecker
	implements RequestScopeCheckerFilter {

	@Override
	public boolean isAllowed(
		ScopeChecker scopeChecker, Request request, ResourceInfo resourceInfo) {

		return scopeChecker.checkScope(request.getMethod());
	}

}