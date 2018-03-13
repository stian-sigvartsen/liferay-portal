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

package com.liferay.oauth2.provider.rest;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.ProtectedPrincipal;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.net.URI;
import java.security.Principal;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = "liferay.oauth2.endpoint=true",
	service=Object.class
)
@PreMatching
@Provider
@Priority(Priorities.AUTHENTICATION)
public class LiferayAuthorizationServiceFilter
	implements ContainerRequestFilter {

	public void activate(Map<String, Object> properties) {
		_loginPage = MapUtil.getString(properties, "loginPage");
	}

	@Override
	public void filter(ContainerRequestContext requestContext) {
		String path = requestContext.getUriInfo().getPath();

		if (!StringUtil.startsWith(path, "authorize")) {
			return;
		}

		try {
			User user = _portal.getUser(_request);

			if ((user != null) && !user.isDefaultUser()) {
				SecurityContext securityContext =
					requestContext.getSecurityContext();

				requestContext.setSecurityContext(
					new PortalCXFSecurityContext(){
						@Override
						public Principal getUserPrincipal() {
							return new ProtectedPrincipal(
								Long.toString(user.getUserId()));
						}

						@Override
						public boolean isSecure() {
							return securityContext.isSecure();
						}
					});

				return;
			}

			String loginPage = _loginPage;

			if (Validator.isBlank(loginPage)) {
				StringBundler sb = new StringBundler();
				sb.append(_portal.getPortalURL(_request));
				sb.append(_portal.getPathContext());
				sb.append(_portal.getPathMain());
				sb.append("/portal/login");
				loginPage = sb.toString();
			}

			loginPage = HttpUtil.addParameter(
				loginPage, "redirect",
				requestContext.getUriInfo().getRequestUri().toASCIIString());

			requestContext.abortWith(
				Response
					.status(Response.Status.FOUND)
					.location(URI.create(loginPage))
					.build());

		}
		catch (Exception e) {
			_log.error(
				"Unable to resolve authenticated user", e);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayAuthorizationServiceFilter.class);

	@Context
	private HttpServletRequest _request;

	@Reference
	private Portal _portal;

	private String _loginPage;

	abstract class PortalCXFSecurityContext
		implements javax.ws.rs.core.SecurityContext,
		org.apache.cxf.security.SecurityContext {

		@Override
		public boolean isUserInRole(String role) {
			return false;
		}

		@Override
		public String getAuthenticationScheme() {
			return "session";
		}
	}
}
