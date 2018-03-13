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

package com.liferay.oauth2.provider.scope.impl.feature;

import com.liferay.oauth2.provider.scope.liferay.ScopeContext;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Carlos Sierra Andrés
 */
public class ScopeContextContainerRequestFilter
	implements ContainerRequestFilter {

	public ScopeContextContainerRequestFilter(ScopeContext scopeContext) {
		_scopeContext = scopeContext;
	}

	@Override
	public void filter(ContainerRequestContext requestContext)
		throws IOException {

		Class<? extends Application> clazz = _application.getClass();

		Bundle bundle = FrameworkUtil.getBundle(clazz);

		if (bundle == null) {
			return;
		}

		_scopeContext.setBundle(bundle);
		_scopeContext.setApplicationName(clazz.getName());
	}

	@Context
	private Application _application;

	private final ScopeContext _scopeContext;

}