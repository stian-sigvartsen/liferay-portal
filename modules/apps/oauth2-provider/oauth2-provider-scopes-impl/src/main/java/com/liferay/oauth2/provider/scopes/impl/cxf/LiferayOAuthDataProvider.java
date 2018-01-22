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

import com.liferay.oauth2.provider.exception.NoSuchOAuth2TokenException;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2RefreshToken;
import com.liferay.oauth2.provider.model.OAuth2Token;
import com.liferay.oauth2.provider.scopes.liferay.api.ScopeFinderLocator;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2RefreshTokenLocalService;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.oauth2.provider.service.OAuth2TokenLocalService;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.code.AbstractAuthorizationCodeDataProvider;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeRegistration;
import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.tokens.bearer.BearerAccessToken;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component(service = LiferayOAuthDataProvider.class)
public class LiferayOAuthDataProvider extends AbstractAuthorizationCodeDataProvider {

	private PortalCache<String, ServerAuthorizationCodeGrant>
		_codeGrantsPortalCache;

	public LiferayOAuthDataProvider() {
		setInvisibleToClientScopes(
			Collections.singletonList(OAuthConstants.REFRESH_TOKEN_SCOPE));
	}

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

		List<String> approvedScope = reg.getApprovedScope();

		approvedScope.add(OAuthConstants.REFRESH_TOKEN_SCOPE);

		reg.setApprovedScope(approvedScope);

		_codeGrantsPortalCache.put(
			authorizationCodeGrant.getCode(),
			authorizationCodeGrant,
			Math.toIntExact(authorizationCodeGrant.getExpiresIn()));

		return authorizationCodeGrant;
	}

	@Override
	public ServerAuthorizationCodeGrant removeCodeGrant(String code)
		throws OAuthServiceException {
		if (code == null) {
			return null;
		}

		ServerAuthorizationCodeGrant serverAuthorizationCodeGrant =
			_codeGrantsPortalCache.get(code);

		_codeGrantsPortalCache.remove(code);

		return serverAuthorizationCodeGrant;
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
		OAuth2Token oAuth2Token = _oAuth2TokenLocalService.createOAuth2Token(
			serverToken.getTokenKey());

		Client client = serverToken.getClient();

		oAuth2Token.setOAuth2ApplicationId(client.getClientId());
		oAuth2Token.setOAuth2RefreshTokenId(serverToken.getRefreshToken());
		oAuth2Token.setCreateDate(new Date(serverToken.getIssuedAt()));
		oAuth2Token.setLifeTime(
			serverToken.getExpiresIn() == -1 ? -1 :
				serverToken.getExpiresIn() - serverToken.getIssuedAt());
		oAuth2Token.setOAuth2TokenType(OAuthConstants.BEARER_TOKEN_TYPE);
		UserSubject subject = serverToken.getSubject();
		oAuth2Token.setUserId(Long.parseLong(subject.getId()));
		oAuth2Token.setUserName(subject.getLogin());
		oAuth2Token.setScopes(
			OAuthUtils.convertPermissionsToScope(serverToken.getScopes()));

		_oAuth2TokenLocalService.updateOAuth2Token(oAuth2Token);

		List<String> scopeList =
			OAuthUtils.convertPermissionsToScopeList(serverToken.getScopes());

		for (String scope : scopeList) {
			try {
				_oAuth2ScopeGrantLocalService.grantScopesToToken(
					serverToken.getTokenKey(),
					_scopeFinderLocator.locateScopes(
						CompanyThreadLocal.getCompanyId(), scope));
			}
			catch (NoSuchOAuth2TokenException e) {
				continue;
			}
		}
	}

	@Override
	protected void saveRefreshToken(RefreshToken refreshToken) {
		String tokenKey = refreshToken.getTokenKey();

		OAuth2RefreshToken oAuth2RefreshToken =
			_oAuth2RefreshTokenLocalService.createOAuth2RefreshToken(
				tokenKey);

		oAuth2RefreshToken.setLifeTime(
			refreshToken.getExpiresIn() - refreshToken.getIssuedAt());

		Client client = refreshToken.getClient();

		oAuth2RefreshToken.setOAuth2ApplicationId(client.getClientId());
		oAuth2RefreshToken.setUserName(refreshToken.getSubject().getLogin());

		_oAuth2RefreshTokenLocalService.updateOAuth2RefreshToken(
			oAuth2RefreshToken);

		List<String> accessTokens = refreshToken.getAccessTokens();

		for (String accessToken : accessTokens) {
			OAuth2Token oAuth2Token =
				_oAuth2TokenLocalService.fetchOAuth2Token(accessToken);

			oAuth2Token.setOAuth2RefreshTokenId(tokenKey);

			_oAuth2TokenLocalService.updateOAuth2Token(oAuth2Token);
		}
	}

	@Override
	protected void doRevokeAccessToken(ServerAccessToken accessToken) {
		try {
			_oAuth2TokenLocalService.deleteOAuth2Token(accessToken.getTokenKey());
		}
		catch (PortalException e) {
		}
	}

	@Override
	protected void doRevokeRefreshToken(RefreshToken refreshToken) {
		try {

			Collection<OAuth2Token> oAuth2Tokens =
				_oAuth2TokenLocalService.findByRefreshToken(
					refreshToken.getTokenKey());

			for (OAuth2Token oAuth2Token : oAuth2Tokens) {
				_oAuth2TokenLocalService.deleteOAuth2Token(oAuth2Token);
			}

			_oAuth2RefreshTokenLocalService.deleteOAuth2RefreshToken(
				refreshToken.getTokenKey());
		}
		catch (PortalException e) {

		}
	}

	@Override
	protected RefreshToken getRefreshToken(String refreshTokenKey) {
		try {
			OAuth2RefreshToken oAuth2RefreshToken =
				_oAuth2RefreshTokenLocalService.getOAuth2RefreshToken(
					refreshTokenKey);

			Date createDate = oAuth2RefreshToken.getCreateDate();

			return new RefreshToken(
				getClient(oAuth2RefreshToken.getOAuth2ApplicationId()),
				refreshTokenKey, oAuth2RefreshToken.getLifeTime(),
				createDate.getTime());
		}
		catch (PortalException e) {
			throw new OAuthServiceException(e);
		}
	}

	@Override
	protected void doRemoveClient(Client c) {
		throw new UnsupportedOperationException();
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

		client.setRedirectUris(
			Collections.singletonList(oAuth2Application.getWebUrl()));

		return client;
	}

	@Override
	public ServerAccessToken getAccessToken(String accessToken)
		throws OAuthServiceException {

		try {
			OAuth2Token oAuth2Token = _oAuth2TokenLocalService.getOAuth2Token(
				accessToken);

			return populateToken(oAuth2Token);
		}
		catch (PortalException e) {
			throw new OAuthServiceException(e);
		}
	}

	private ServerAccessToken populateToken(OAuth2Token oAuth2Token)
		throws PortalException {

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.getOAuth2Application(
				oAuth2Token.getOAuth2ApplicationId());
		Date createDate = oAuth2Token.getCreateDate();

		BearerAccessToken bearerAccessToken = new BearerAccessToken(
			getClient(oAuth2Application.getOAuth2ApplicationId()),
			oAuth2Token.getOAuth2TokenId(), oAuth2Token.getLifeTime(),
			createDate.getTime());

		bearerAccessToken.setSubject(
			new UserSubject(oAuth2Token.getUserName()));

		return bearerAccessToken;
	}

	@Override
	public List<ServerAccessToken> getAccessTokens(
			Client client, UserSubject subject)
		throws OAuthServiceException {

		Collection<OAuth2Token> oAuth2Tokens =
			_oAuth2TokenLocalService.findByApplicationAndUserName(
				client.getClientId(), subject.getLogin());

		List<ServerAccessToken> serverAccessTokens = new ArrayList<>();

		for (OAuth2Token oAuth2Token : oAuth2Tokens) {
			try {
				serverAccessTokens.add(populateToken(oAuth2Token));
			}
			catch (PortalException e) {
				try {
					_oAuth2TokenLocalService.deleteOAuth2Token(
						oAuth2Token.getOAuth2TokenId());
				}
				catch (PortalException e1) {
				}
			}
		}

		return serverAccessTokens;
	}

	@Override
	public List<RefreshToken> getRefreshTokens(
			Client client, UserSubject subject)
		throws OAuthServiceException {

		return null;
	}

	@Override
	public List<OAuthPermission> convertScopeToPermissions(
		Client client, List<String> requestedScopes) {

		List<String> invisibleToClientScopes = getInvisibleToClientScopes();

		List<OAuthPermission> permissions = new ArrayList<>();

		for (String requestedScope : requestedScopes) {
			OAuthPermission oAuthPermission = new OAuthPermission(
				requestedScope);

			if (invisibleToClientScopes.contains(
				oAuthPermission.getPermission())) {

				oAuthPermission.setInvisibleToClient(true);
			}

			permissions.add(oAuthPermission);
		}

		return permissions;
	}

	@Reference
	private MultiVMPool _multiVMPool;

	@Reference
	private OAuth2TokenLocalService _oAuth2TokenLocalService;

	@Reference
	private OAuth2RefreshTokenLocalService _oAuth2RefreshTokenLocalService;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference
	private OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

	@Reference
	ScopeFinderLocator _scopeFinderLocator;

}
