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

import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeGrantHandler;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenGrantHandler;
import org.apache.cxf.rs.security.oauth2.services.AccessTokenService;
import org.osgi.service.component.annotations.Component;

import javax.ws.rs.core.Application;
import java.util.Set;

@Component(
	immediate = true,
	service = Application.class
)
public class OAuth2EnpointApplication extends Application {

	@Override
	public Set<Object> getSingletons() {
		AccessTokenGrantHandler authorizationCodeGrantHandler =
			new AuthorizationCodeGrantHandler();

		AccessTokenService accessTokenService = new AccessTokenService();

		accessTokenService.setGrantHandler(authorizationCodeGrantHandler);

	}
}
