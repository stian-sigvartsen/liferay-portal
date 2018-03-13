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

package com.liferay.oauth2.provider.rest;

import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProvider;
import com.liferay.oauth2.provider.scope.liferay.ScopeContext;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.security.service.access.policy.ServiceAccessPolicyManager;
import com.liferay.portal.kernel.security.service.access.policy.ServiceAccessPolicyThreadLocal;
import com.liferay.portal.kernel.servlet.HttpHeaders;

import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	immediate = true,
	property = {
		"auth.verifier.OAuth2RestAuthVerifier.urls.includes=#N/A#"
	}
)
public class OAuth2RestAuthVerifier implements AuthVerifier {

	@Override
	public String getAuthType() {
		return "OAuth2";
	}

	@Override
	public AuthVerifierResult verify(
			AccessControlContext accessControlContext, Properties properties)
		throws AuthException {

		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		try {
			BearerTokenProvider.AccessToken accessToken =
				getAccessToken(accessControlContext);

			if (accessToken == null) {
				return authVerifierResult;
			}

			BearerTokenProvider bearerTokenProvider =
				_liferayOAuthDataProvider.getBearerTokenProvider(
					accessToken.getOAuth2Application().getCompanyId(),
					accessToken.getOAuth2Application().getClientId());

			if (bearerTokenProvider == null) {
				return authVerifierResult;
			}

			if (!bearerTokenProvider.isValid(accessToken)) {
				return authVerifierResult;
			}

			_scopeContext.setAccessToken(accessToken.getTokenKey());

			ServiceAccessPolicyThreadLocal.addActiveServiceAccessPolicyName(
				_serviceAccessPolicyManager.getDefaultUserServiceAccessPolicyName(
					accessToken.getOAuth2Application().getCompanyId()));

			authVerifierResult.getSettings().put(
				BearerTokenProvider.AccessToken.class.getName(), accessToken);

			authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);
			authVerifierResult.setUserId(accessToken.getUserId());

			return authVerifierResult;
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug("OAuth2 Access Token validation failed", e);
			}

			return authVerifierResult;
		}
	}

	protected BearerTokenProvider.AccessToken getAccessToken(
		AccessControlContext accessControlContext) {

		HttpServletRequest request = accessControlContext.getRequest();

		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authorization == null) {
			return null;
		}

		String[] basicAuthParts = authorization.split(" ");

		if (basicAuthParts.length != 2) {
			return null;
		}

		String basicAuthPart = basicAuthParts[0];

		if (!"Bearer".equalsIgnoreCase(basicAuthPart)) {
			return null;
		}

		String token = basicAuthParts[1];

		ServerAccessToken serverAccessToken =
			_liferayOAuthDataProvider.getAccessToken(token);

		if (serverAccessToken == null) {
			return null;
		}

		BearerTokenProvider.AccessToken accessToken =
			_liferayOAuthDataProvider.fromCXFAccessToken(serverAccessToken);

		return accessToken;
	}

	private static Log _log = LogFactoryUtil.getLog(OAuth2RestAuthVerifier.class);

	@Reference
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

	@Reference
	private ServiceAccessPolicyManager _serviceAccessPolicyManager;

	@Reference
	private ScopeContext _scopeContext;

}
