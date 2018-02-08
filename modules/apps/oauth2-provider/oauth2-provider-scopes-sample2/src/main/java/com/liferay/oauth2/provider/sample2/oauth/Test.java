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

package com.liferay.oauth2.provider.sample2.oauth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.liferay.oauth2.provider.model.LiferayAliasedOAuth2Scope;
import com.liferay.oauth2.provider.model.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scopes.api.RequiresScope;

import java.util.Collection;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

import com.liferay.oauth2.provider.scopes.liferay.api.ScopeFinderLocator;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Collections;

@ApplicationPath("/sample2")
@Component(
	immediate = true,
	property = {
		"oauth2.scopechecker.type=annotations",
		"requires.oauth2=true"
	},
	service = Application.class

)
public class Test extends Application {

	@Override
	public Set<Object> getSingletons() {
		return Collections.singleton(this);
	}

	@GET
	@RequiresScope("everything.readonly")
	public String hello() {
		return "hello";
	}

	@Path("/everything")
	@GET
	@RequiresScope("everything")
	public String helloEverything() {
		return "hello everything";
	}

	@Path("/scopes")
	@Produces("application/json")
	@GET
	public String scopes() {
		long companyId = CompanyThreadLocal.getCompanyId();

		Collection<LiferayAliasedOAuth2Scope> scopes =
			_scopeFinderLocator.listScopes(companyId);

		Gson gson = new GsonBuilder()
			.registerTypeAdapter(
				Bundle.class,
				(JsonSerializer<Bundle>)
					(src, typeOfSrc, context) -> {

						JsonObject json = new JsonObject();
						json.addProperty("bundleName", src.getSymbolicName());
						json.addProperty("bundleVersion", src.getVersion().toString());
						return json;
					})
			.create();

		return gson.toJson(scopes);
	}

	@Reference
	private ScopeFinderLocator _scopeFinderLocator;

	@Reference
	private CompanyLocalService _companyLocalService;
}