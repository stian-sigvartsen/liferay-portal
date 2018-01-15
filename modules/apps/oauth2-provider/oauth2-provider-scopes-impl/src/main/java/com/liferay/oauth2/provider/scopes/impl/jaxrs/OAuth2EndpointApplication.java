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

import com.liferay.oauth2.provider.scopes.impl.cxf.LiferayOAuthDataProvider;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeGrantHandler;
import org.apache.cxf.rs.security.oauth2.services.AccessTokenService;
import org.apache.cxf.rs.security.oauth2.services.AuthorizationCodeGrantService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component(
	immediate = true,
	service = Application.class
)
@ApplicationPath("/oauth2")
public class OAuth2EndpointApplication extends Application {

	@Override
	public Set<Object> getSingletons() {
		AuthorizationCodeGrantService authorizationCodeGrantService =
			new AuthorizationCodeGrantService();

		authorizationCodeGrantService.setDataProvider(
			_liferayOAuthDataProvider);
		AccessTokenService accessTokenService = new AccessTokenService();

		accessTokenService.setBlockUnsecureRequests(true);
		accessTokenService.setDataProvider(_liferayOAuthDataProvider);
		AuthorizationCodeGrantHandler authorizationCodeGrantHandler =
			new AuthorizationCodeGrantHandler();

		authorizationCodeGrantHandler.setDataProvider(
			_liferayOAuthDataProvider);

		accessTokenService.setGrantHandler(authorizationCodeGrantHandler);

		return new HashSet<>(
			Arrays.asList(
				authorizationCodeGrantService, accessTokenService,
				_authorizationMessageBodyWriter));
	}

	@Reference
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

	@Reference
	AuthorizationMessageBodyWriter _authorizationMessageBodyWriter;

}
