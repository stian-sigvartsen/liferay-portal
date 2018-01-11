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

package com.liferay.oauth2.provider.sample.oauth;

import com.liferay.oauth2.provider.model.LiferayOAuth2ScopeInternalIdentifier;
import com.liferay.oauth2.provider.scopes.api.LiferayOauth2OSGiFeatureFactory;
import com.liferay.oauth2.provider.scopes.liferay.api.ScopeContext;
import com.liferay.oauth2.provider.scopes.liferay.api.ScopeFinderLocator;
import com.liferay.oauth2.provider.scopes.api.RequiresScope;
import com.liferay.oauth2.provider.scopes.api.ScopesDescriptionBundle;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	service = Application.class
)
@ScopesDescriptionBundle("content.Language")
public class Test extends Application {

	@Override
	public Set<Object> getSingletons() {
		return new HashSet<>(
			Arrays.asList(
				this, _liferayOauth2OSGiFeatureFactory.create("Sample")));
	}

	@GET
	@RequiresScope("everything.readonly")
	public String method() {
		return "hello";
	}

	@GET
	@Path("/scopes")
	@RequiresScope("everything.readonly")
	public String scopes() throws PortalException {
		Collection<LiferayOAuth2ScopeInternalIdentifier> scopes =
			_scopeFinderLocator.listScopes();

		List<String> names =
			scopes.stream().map(
				LiferayOAuth2ScopeInternalIdentifier::getScopeInternalIdentifier
			).collect(
				Collectors.toList()
			);

		List<String> apps =
			scopes.stream().map(
				LiferayOAuth2ScopeInternalIdentifier::getApplicationName
			).collect(
				Collectors.toList()
			);

		return "Scopes: " + names + "\n" +
			   "Names: " + apps;
	}


	@PUT
	@Path("/write")
	@RequiresScope("everything")
	public String writeSomething() {
		return "Written";
	}

	@PUT
	@Path("/destroy")
	@RequiresScope("admin")
	public String destroy() {
		return "Destroyed";
	}


	@DELETE
	@Path("/grant-scope")
	public String grantScope() {
		HttpSession httpSession = _httpServletRequest.getSession();

		httpSession.removeAttribute("GRANTED_SCOPES");

		return "Success!";
	}

	@Reference
	LiferayOauth2OSGiFeatureFactory _liferayOauth2OSGiFeatureFactory;

	@Reference
	ScopeContext _scopeContext;

	@Context
	HttpServletRequest _httpServletRequest;

	@Reference
	ScopeFinderLocator _scopeFinderLocator;

	@Reference
	OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;
}