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

import com.liferay.oauth2.provider.scopes.liferay.api.ScopeContext;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

public class OAuth2ThreadLocalScopeCheckerContainerResponseFilter
	implements ContainerResponseFilter{
	private ScopeContext _scopeContext;

	public OAuth2ThreadLocalScopeCheckerContainerResponseFilter(
		ScopeContext scopeContext) {

		_scopeContext = scopeContext;
	}

	@Override
	public void filter(
			ContainerRequestContext requestContext,
			ContainerResponseContext responseContext)
		throws IOException {

		_scopeContext.clear();
	}
}
