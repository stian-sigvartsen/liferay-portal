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

package com.liferay.oauth2.provider.scopes.impl.cxf;

import com.liferay.oauth2.provider.scopes.impl.cxf.AuthorizationMessageBodyWriter;
import com.liferay.oauth2.provider.scopes.impl.cxf.LiferayOAuthDataProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeGrantHandler;
import org.apache.cxf.rs.security.oauth2.grants.refresh.RefreshTokenGrantHandler;
import org.apache.cxf.rs.security.oauth2.provider.OAuthJSONProvider;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.services.AccessTokenService;
import org.apache.cxf.rs.security.oauth2.services.AuthorizationCodeGrantService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
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

		authorizationCodeGrantService.setSubjectCreator(
			(mc, params) -> {
				SecurityContext securityContext = mc.getSecurityContext();

				Principal userPrincipal = securityContext.getUserPrincipal();

				try {
					User user = _userLocalService.getUser(
						Long.parseLong(userPrincipal.getName()));

					return new UserSubject(
						user.getLogin(), Long.toString(user.getUserId()));
				}
				catch (PortalException e) {
					throw new OAuthServiceException(e);
				}
			});

		AccessTokenService accessTokenService = new AccessTokenService();

		accessTokenService.setBlockUnsecureRequests(true);
		accessTokenService.setDataProvider(_liferayOAuthDataProvider);

		AuthorizationCodeGrantHandler authorizationCodeGrantHandler =
			new AuthorizationCodeGrantHandler();
		authorizationCodeGrantHandler.setDataProvider(
			_liferayOAuthDataProvider);

		RefreshTokenGrantHandler refreshTokenGrantHandler =
			new RefreshTokenGrantHandler();
		refreshTokenGrantHandler.setDataProvider(_liferayOAuthDataProvider);

		accessTokenService.setGrantHandlers(
			Arrays.asList(
				authorizationCodeGrantHandler, refreshTokenGrantHandler));

		return new HashSet<>(
			Arrays.asList(
				authorizationCodeGrantService, accessTokenService,
				_authorizationMessageBodyWriter));
	}

	@Override
	public Set<Class<?>> getClasses() {
		return Collections.singleton(OAuthJSONProvider.class);
	}

	@Reference
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

	@Reference
	AuthorizationMessageBodyWriter _authorizationMessageBodyWriter;

	@Reference
	UserLocalService _userLocalService;
}
