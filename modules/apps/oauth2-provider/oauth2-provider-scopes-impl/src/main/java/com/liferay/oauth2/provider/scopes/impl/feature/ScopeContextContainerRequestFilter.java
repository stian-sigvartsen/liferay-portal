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

package com.liferay.oauth2.provider.scopes.impl.feature;

import com.liferay.oauth2.provider.scopes.liferay.api.ScopeContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.io.IOException;

class ScopeContextContainerRequestFilter implements ContainerRequestFilter {

	public ScopeContextContainerRequestFilter(ScopeContext scopeContext) {
		_scopeContext = scopeContext;
	}

	@Override
	public void filter(ContainerRequestContext requestContext)
		throws IOException {

		Bundle bundle = FrameworkUtil.getBundle(_application.getClass());

		if (bundle == null) {
			return;
		}

		_scopeContext.setBundle(bundle);
		_scopeContext.setApplicationName(_application.getClass().getName());
	}

	@Context
	private Application _application;

	private ScopeContext _scopeContext;
}
