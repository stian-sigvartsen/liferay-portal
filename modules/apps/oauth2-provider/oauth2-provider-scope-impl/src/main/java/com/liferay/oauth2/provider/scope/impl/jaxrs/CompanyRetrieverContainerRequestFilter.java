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

import com.liferay.portal.kernel.util.PortalUtil;

import java.io.IOException;

import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;

/**
 * @author Carlos Sierra Andrés
 */
public class CompanyRetrieverContainerRequestFilter
	implements ContainerRequestFilter {

	public CompanyRetrieverContainerRequestFilter(Consumer<Long> consumer) {
		_consumer = consumer;
	}

	@Override
	public void filter(ContainerRequestContext requestContext)
		throws IOException {

		_consumer.accept(PortalUtil.getCompanyId(_httpServletRequest));
	}

	private final Consumer<Long> _consumer;

	@Context
	private HttpServletRequest _httpServletRequest;

}