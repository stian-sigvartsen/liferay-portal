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

import com.liferay.oauth2.provider.exception.NoSuchOAuth2ApplicationException;
import com.liferay.oauth2.provider.exception.NoSuchOAuth2TokenException;
import com.liferay.oauth2.provider.model.LiferayOAuth2Scope;
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
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.StringBundler;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenRegistration;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

		_codeGrantsPortalCache.put(
			authorizationCodeGrant.getCode(),
			authorizationCodeGrant,
			Math.toIntExact(authorizationCodeGrant.getExpiresIn()));

		return authorizationCodeGrant;
	}

	@Override
	public ServerAccessToken createAccessToken(AccessTokenRegistration reg)
		throws OAuthServiceException {

		List<String> approvedScope = new ArrayList<>(
			reg.getApprovedScope());

		if (approvedScope.isEmpty()) {
			approvedScope.addAll(reg.getClient().getRegisteredScopes());
		}

		reg.setApprovedScope(approvedScope);

		if (!OAuthConstants.CLIENT_CREDENTIALS_GRANT.equals(
			reg.getGrantType())) {

			approvedScope.add(OAuthConstants.REFRESH_TOKEN_SCOPE);
		}

		return super.createAccessToken(reg);
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

	public  ServerAuthorizationCodeGrant getCodeGrant(String code) {
		if (code == null) {
			return null;
		}

		return _codeGrantsPortalCache.get(code);
	}

	@Override
	public List<ServerAuthorizationCodeGrant> getCodeGrants(
			Client client, UserSubject subject)
		throws OAuthServiceException {

		List<String> keys = _codeGrantsPortalCache.getKeys();

		List<ServerAuthorizationCodeGrant> authorizationCodeGrants =
			new ArrayList<>();

		for (String key : keys) {
			ServerAuthorizationCodeGrant serverAuthorizationCodeGrant =
				_codeGrantsPortalCache.get(key);

			if (serverAuthorizationCodeGrant == null) {
				continue;
			}

			if (client.equals(serverAuthorizationCodeGrant.getClient()) &&
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

		OAuth2Application oAuth2Application =
			resolveOAuth2Application(serverToken.getClient());
		
		oAuth2Token.setOAuth2ApplicationId(
			oAuth2Application.getOAuth2ApplicationId());
		
		oAuth2Token.setOAuth2RefreshTokenId(serverToken.getRefreshToken());
		oAuth2Token.setCreateDate(fromCXFIssuedAt(serverToken.getIssuedAt()));
		oAuth2Token.setLifeTime(serverToken.getExpiresIn());
		oAuth2Token.setOAuth2TokenType(OAuthConstants.BEARER_TOKEN_TYPE);
		UserSubject subject = serverToken.getSubject();

		if (subject != null) {
			oAuth2Token.setUserId(Long.parseLong(subject.getId()));
			oAuth2Token.setUserName(subject.getLogin());
		}

		oAuth2Token.setScopes(
			OAuthUtils.convertPermissionsToScope(serverToken.getScopes()));

		try {
			_oAuth2TokenLocalService.updateOAuth2Token(oAuth2Token);
		}
		catch (Exception e) {
			StringBundler sb = new StringBundler(6);
			sb.append("Unable to save user ");
			sb.append(serverToken.getSubject().getId());
			sb.append(" token for client ");
			sb.append(serverToken.getClient().getClientId());
			sb.append(" with granted scopes ");
			sb.append(
				OAuthUtils.convertPermissionsToScopeList(
					serverToken.getScopes()));

			_log.error(
				 sb.toString(), e);

			throw new OAuthServiceException(
				"Unable to save access token", e);
		}

		List<String> scopeList =
			OAuthUtils.convertPermissionsToScopeList(serverToken.getScopes());

		long companyId = CompanyThreadLocal.getCompanyId();

		Set<LiferayOAuth2Scope> liferayScopes = new HashSet<>();

		for (String scope : scopeList) {
			liferayScopes.addAll(
				_scopeFinderLocator.locateScopes(companyId, scope));
		}

		try {
			_oAuth2ScopeGrantLocalService.grantScopesToToken(
				serverToken.getTokenKey(), liferayScopes);
		}
		catch (NoSuchOAuth2TokenException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to find token for key " +
						serverToken.getTokenKey());
			}

			throw new OAuthServiceException(
				"Unable to grant scopes for token", e);
		}
		catch (Exception e) {
			StringBundler sb = new StringBundler(6);
			sb.append("Unable to save user ");
			sb.append(serverToken.getSubject().getId());
			sb.append(" scopes for client ");
			sb.append(serverToken.getClient().getClientId());
			sb.append(" with approved scopes ");
			sb.append(
				OAuthUtils.convertPermissionsToScopeList(
					serverToken.getScopes()));

			_log.error(sb.toString(), e);

			throw new OAuthServiceException(
				"Unable to grant scopes for token", e);

		}
	}

	@Override
	protected void saveRefreshToken(RefreshToken refreshToken) {
		
		OAuth2Application oAuth2Application = 
			resolveOAuth2Application(refreshToken.getClient());
		
		String tokenKey = refreshToken.getTokenKey();

		OAuth2RefreshToken oAuth2RefreshToken =
			_oAuth2RefreshTokenLocalService.createOAuth2RefreshToken(
				tokenKey);

		oAuth2RefreshToken.setCompanyId(oAuth2Application.getCompanyId());

		oAuth2RefreshToken.setCreateDate(
			fromCXFIssuedAt(refreshToken.getIssuedAt()));

		oAuth2RefreshToken.setLifeTime(refreshToken.getExpiresIn());

		oAuth2RefreshToken.setOAuth2ApplicationId(
			oAuth2Application.getOAuth2ApplicationId());

		UserSubject subject = refreshToken.getSubject();

		if (subject != null) {
			oAuth2RefreshToken.setUserId(Long.parseLong(subject.getId()));
			oAuth2RefreshToken.setUserName(subject.getLogin());
		}

		try {
			_oAuth2RefreshTokenLocalService.updateOAuth2RefreshToken(
				oAuth2RefreshToken);
		}
		catch (Exception e) {
			StringBundler sb = new StringBundler();
			sb.append("Unable to save refresh token for client ");
			sb.append(refreshToken.getClient().getClientId());
			sb.append(" for user ");
			sb.append(oAuth2RefreshToken.getUserId());

			_log.error(sb.toString(), e);

			throw new OAuthServiceException(
				"Unable to save refresh token", e);
		}


		List<String> accessTokens = refreshToken.getAccessTokens();

		for (String accessToken : accessTokens) {
			OAuth2Token oAuth2Token =
				_oAuth2TokenLocalService.fetchOAuth2Token(accessToken);

			if (oAuth2Token == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No access token found for refresh token for client "
							+ refreshToken.getClient().getClientId());
				}

				continue;
			}

			oAuth2Token.setOAuth2RefreshTokenId(tokenKey);

			try {
				_oAuth2TokenLocalService.updateOAuth2Token(oAuth2Token);
			}
			catch (Exception e) {
				_log.error(
					"Unable to update access token " +
						"with refresh token reference"
					, e);

				throw new OAuthServiceException(
					"Unable to save refresh token", e);
			}
		}
	}

	@Override
	protected void doRevokeAccessToken(ServerAccessToken accessToken) {
		try {
			_oAuth2TokenLocalService.deleteOAuth2Token(accessToken.getTokenKey());
		}
		catch (PortalException e) {
			_log.error(
				"Unable to revoke token for client "
					+ accessToken.getClient().getClientId(),
				e);

			throw new OAuthServiceException(
				"Unable to revoke access token", e);
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
			_log.error(
				"Unable to delete tokens and refresh token for client"
					+ refreshToken.getClient().getClientId()
				, e);

			throw new OAuthServiceException(
				"Unable to revoke refresh token", e);
		}
	}

	@Override
	protected RefreshToken getRefreshToken(String refreshTokenKey) {
		try {
			OAuth2RefreshToken oAuth2RefreshToken =
				_oAuth2RefreshTokenLocalService.getOAuth2RefreshToken(
					refreshTokenKey);

			OAuth2Application oAuth2Application = 
				_oAuth2ApplicationLocalService.getOAuth2Application(
					oAuth2RefreshToken.getOAuth2ApplicationId());
			
			RefreshToken refreshToken = new RefreshToken(
				populateClient(oAuth2Application),
				refreshTokenKey, oAuth2RefreshToken.getLifeTime(),
				toCXFIssuedAt(oAuth2RefreshToken.getCreateDate()));

			refreshToken.setSubject(
				populateUserSubject(
					oAuth2RefreshToken.getUserId(),
					oAuth2RefreshToken.getUserName()));

			return refreshToken;
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
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				CompanyThreadLocal.getCompanyId(), clientId);

		if (oAuth2Application == null) {
			throw new OAuthServiceException("Invalid client " + clientId);
		}

		return populateClient(oAuth2Application);
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

	protected Client populateClient(OAuth2Application oAuth2Application) {
		Client client = new Client(
			oAuth2Application.getClientId(),
			oAuth2Application.getClientSecret(),
			oAuth2Application.getClientConfidential(),
			oAuth2Application.getName(),
			oAuth2Application.getWebUrl());
	
		client.setApplicationDescription(
			oAuth2Application.getDescription());

		client.setRegisteredScopes(
			OAuthUtils.parseScope(oAuth2Application.getScopes()));

		client.setRedirectUris(
			Collections.singletonList(oAuth2Application.getRedirectUri()));

		client.setSubject(populateUserSubject(
			oAuth2Application.getUserId(), oAuth2Application.getUserName()));
	
		return client;
	}

	protected ServerAccessToken populateToken(OAuth2Token oAuth2Token)
		throws PortalException {

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.getOAuth2Application(
				oAuth2Token.getOAuth2ApplicationId());

		Client client = getClient(oAuth2Application.getClientId());
		
		BearerAccessToken bearerAccessToken = new BearerAccessToken(
			client, oAuth2Token.getOAuth2TokenId(), oAuth2Token.getLifeTime(),
			toCXFIssuedAt(oAuth2Token.getCreateDate()));

		bearerAccessToken.setSubject(
			populateUserSubject(
				oAuth2Token.getUserId(), oAuth2Token.getUserName()));

		List<OAuthPermission> permissions = 
			convertScopeToPermissions(
				client, OAuthUtils.parseScope(oAuth2Token.getScopes()));
		
		bearerAccessToken.setScopes(permissions);
		
		return bearerAccessToken;
	}

	protected UserSubject populateUserSubject(long userId, String userName) {
		return new UserSubject(userName, String.valueOf(userId));
	}

	protected OAuth2Application resolveOAuth2Application(Client client) {
		OAuth2Application oAuth2Application;
		
		try {
			oAuth2Application = 
				_oAuth2ApplicationLocalService.getOAuth2Application(
						CompanyThreadLocal.getCompanyId(), 
						client.getClientId());
		} 
		catch (NoSuchOAuth2ApplicationException e) {
			throw new SystemException(e);
		}
		return oAuth2Application;
	}

	@Override
	public List<ServerAccessToken> getAccessTokens(
			Client client, UserSubject subject)
		throws OAuthServiceException {

		OAuth2Application oAuth2Application;
		
		try {
			oAuth2Application = 
				_oAuth2ApplicationLocalService.getOAuth2Application(
					CompanyThreadLocal.getCompanyId(), client.getClientId());
		} 
		catch (NoSuchOAuth2ApplicationException e) {
			throw new OAuthServiceException(e);
		}
		
		Collection<OAuth2Token> oAuth2Tokens =
			_oAuth2TokenLocalService.findByApplicationAndUserName(
				oAuth2Application.getOAuth2ApplicationId(), subject.getLogin());

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

	protected Date fromCXFIssuedAt(long issuedAt) {
		return new Date(issuedAt * 1000);
	}

	protected long toCXFIssuedAt(Date dateCreated) {
		return dateCreated.getTime() / 1000;
	}

	private static Log _log =
		LogFactoryUtil.getLog(LiferayOAuthDataProvider.class);

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
