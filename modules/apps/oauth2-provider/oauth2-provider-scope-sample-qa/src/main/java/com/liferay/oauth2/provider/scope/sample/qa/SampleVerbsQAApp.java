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

package com.liferay.oauth2.provider.scope.sample.qa;

import java.util.Collections;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;

/**
 * @author Stian Sigvartsen
 */
@Component(
	property = {
		"osgi.jaxrs.application.base=/sample-verbs-qa-app",
		"osgi.jaxrs.extension.select=(liferay.extension=OAuth2)",
		"osgi.jaxrs.name=liferay-oauth2-scope-sample-verbs-qa-app"
	},
	service = Application.class
)
public class SampleVerbsQAApp extends Application {

	@GET
	@Path("/get")
	public String get() {
		return "get";
	}

	@PUT
	@Path("/put")
	public String anyScopesNeeded() {
		return "put";
	}

	@Override
	public Set<Object> getSingletons() {
		return Collections.singleton(this);
	}

	@POST
	@Path("/post")
	public String noScopesNeeded() {
		return "post";
	}

}