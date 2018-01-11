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

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.oauth2.provider.service.OAuth2TokenLocalService;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.code.AbstractAuthorizationCodeDataProvider;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeRegistration;
import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

@Component(service = LiferayOAuthDataProvider.class)
public class LiferayOAuthDataProvider extends AbstractAuthorizationCodeDataProvider {

	private PortalCache<String, ServerAuthorizationCodeGrant>
		_codeGrantsPortalCache;

	@Activate
	protected void activate() {
		_codeGrantsPortalCache = (PortalCache<String, ServerAuthorizationCodeGrant>)
			_multiVMPool.getPortalCache("oauth2-provider-code-grants");
	}

	@Override
	public ServerAuthorizationCodeGrant createCodeGrant(
			AuthorizationCodeRegistration reg)
		throws OAuthServiceException {

		ServerAuthorizationCodeGrant authorizationCodeGrant =
			super.createCodeGrant(reg);

		_codeGrantsPortalCache.put(
			authorizationCodeGrant.getCode(),
			authorizationCodeGrant,
			Math.toIntExact(authorizationCodeGrant.getExpiresIn()));

		return authorizationCodeGrant;
	}

	@Override
	public ServerAuthorizationCodeGrant removeCodeGrant(String code)
		throws OAuthServiceException {

		_codeGrantsPortalCache.remove(code);
	}

	@Override
	public List<ServerAuthorizationCodeGrant> getCodeGrants(
			Client c, UserSubject subject)
		throws OAuthServiceException {

		List<String> keys = _codeGrantsPortalCache.getKeys();

		List<ServerAuthorizationCodeGrant> authorizationCodeGrants =
			new ArrayList<>();

		for (String key : keys) {
			ServerAuthorizationCodeGrant serverAuthorizationCodeGrant =
				_codeGrantsPortalCache.get(key);

			if (c.equals(serverAuthorizationCodeGrant.getClient()) &&
				subject.equals(serverAuthorizationCodeGrant.getSubject())) {

				authorizationCodeGrants.add(serverAuthorizationCodeGrant);
			}
		}

		return authorizationCodeGrants;
	}

	@Override
	protected void saveAccessToken(ServerAccessToken serverToken) {
	}

	@Override
	protected void saveRefreshToken(RefreshToken refreshToken) {

	}

	@Override
	protected void doRevokeAccessToken(ServerAccessToken accessToken) {

	}

	@Override
	protected void doRevokeRefreshToken(RefreshToken refreshToken) {

	}

	@Override
	protected RefreshToken getRefreshToken(String refreshTokenKey) {
		return null;
	}

	@Override
	protected void doRemoveClient(Client c) {

	}

	@Override
	public void setClient(Client client) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Client> getClients(UserSubject resourceOwner) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Client getClient(String clientId) {
		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(clientId);

		Client client = new Client(
			oAuth2Application.getOAuth2ApplicationId(),
			oAuth2Application.getApplicationSecret(),
			oAuth2Application.getConfidential(),
			oAuth2Application.getName(),
			oAuth2Application.getWebUrl());

		client.setApplicationDescription(
			oAuth2Application.getDescription());

		client.setRegisteredScopes();
	}

	@Override
	public ServerAccessToken getAccessToken(String accessToken)
		throws OAuthServiceException {

		return null;
	}

	@Override
	public List<ServerAccessToken> getAccessTokens(
		Client client, UserSubject subject) throws OAuthServiceException {
		return null;
	}

	@Override
	public List<RefreshToken> getRefreshTokens(
		Client client, UserSubject subject) throws OAuthServiceException {
		return null;
	}

	@Reference
	private MultiVMPool _multiVMPool;

	@Reference
	private OAuth2TokenLocalService _oAuth2TokenLocalService;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference
	private OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

}
