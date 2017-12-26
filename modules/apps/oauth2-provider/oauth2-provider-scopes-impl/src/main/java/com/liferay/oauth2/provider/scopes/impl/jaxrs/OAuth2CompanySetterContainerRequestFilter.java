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
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;

public class OAuth2CompanySetterContainerRequestFilter
	implements ContainerRequestFilter {

	public OAuth2CompanySetterContainerRequestFilter(ScopeContext scopeStack) {
		_scopeStack = scopeStack;
	}

	@Override
	public void filter(ContainerRequestContext requestContext)
		throws IOException {

		try {
			Company company = PortalUtil.getCompany(_httpServletRequest);

			_scopeStack.setCompany(company);
		}
		catch (PortalException pe) {
			pe.printStackTrace();
		}
	}

	@Context
	private HttpServletRequest _httpServletRequest;

	private final ScopeContext _scopeStack;

}