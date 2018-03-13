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

import com.liferay.oauth2.provider.configuration.OAuth2Configuration;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.exception.NoSuchOAuth2TokenException;
import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2RefreshToken;
import com.liferay.oauth2.provider.model.OAuth2Token;
import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProvider;
import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProvider.AccessToken;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.scope.liferay.ScopedServiceTrackerMap;
import com.liferay.oauth2.provider.scope.liferay.ScopedServiceTrackerMapFactory;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2RefreshTokenLocalService;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.oauth2.provider.service.OAuth2TokenLocalService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;

import com.liferay.portal.kernel.util.Validator;
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
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component(
	service = LiferayOAuthDataProvider.class,
	configurationPid = "com.liferay.oauth2.provider.configuration.OAuth2Configuration"
)
public class LiferayOAuthDataProvider extends AbstractAuthorizationCodeDataProvider {

	public static final String CLIENT_REMOTE_ADDR_PROPERTY =
		"CLIENT_REMOTE_ADDR_ADDRESS";
	public static final String CLIENT_REMOTE_HOST_PROPERTY =
		"CLIENT_REMOTE_HOST";
	public static final String FEATURE_PREFIX_PROPERTY = "FEATURE-";
	public static final String FEATURES_PROPERTY = "FEATURES";

	private PortalCache<String, ServerAuthorizationCodeGrant>
		_codeGrantsPortalCache;

	private ScopedServiceTrackerMap<BearerTokenProvider>
		_scopedBearerTokenProvider;

	public LiferayOAuthDataProvider() {
		setInvisibleToClientScopes(
			Collections.singletonList(OAuthConstants.REFRESH_TOKEN_SCOPE));
	}

	@Activate
	@SuppressWarnings("unchecked")
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_codeGrantsPortalCache = (PortalCache<String, ServerAuthorizationCodeGrant>)
			_multiVMPool.getPortalCache("oauth2-provider-code-grants");
		_oAuth2Configuration =
			ConfigurableUtil.createConfigurable(
				OAuth2Configuration.class, properties);
		_codeGrantsPortalCache = (PortalCache<String, ServerAuthorizationCodeGrant>)
			_multiVMPool.getPortalCache("oauth2-provider-code-grants");

		_scopedBearerTokenProvider = _scopedServiceTrackerMapFactory.create(
			bundleContext, BearerTokenProvider.class,
			"liferay.oauth2.client.id", () -> _defaultBearerTokenProvider);
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
	protected ServerAccessToken doCreateAccessToken(
		AccessTokenRegistration accessTokenRegistration) {

		ServerAccessToken serverAccessToken = super.doCreateAccessToken(
			accessTokenRegistration);

		BearerTokenProvider.AccessToken accessToken = fromCXFAccessToken(
			serverAccessToken);

		BearerTokenProvider tokenProvider = getBearerTokenProvider(
			accessToken.getOAuth2Application().getCompanyId(),
			accessToken.getOAuth2Application().getClientId());

		tokenProvider.enrichAccessToken(accessToken);

		serverAccessToken.setAudiences(accessToken.getAudiences());
		serverAccessToken.setClientCodeVerifier(
			accessToken.getClientCodeVerifier());
		serverAccessToken.setExpiresIn(accessToken.getExpiresIn());
		serverAccessToken.setExtraProperties(accessToken.getExtraProperties());
		serverAccessToken.setGrantCode(accessToken.getGrantCode());
		serverAccessToken.setGrantType(accessToken.getGrantType());
		serverAccessToken.setIssuedAt(accessToken.getIssuedAt());
		serverAccessToken.setIssuer(accessToken.getIssuer());
		serverAccessToken.setNonce(accessToken.getNonce());
		serverAccessToken.setParameters(accessToken.getParameters());
		serverAccessToken.setRefreshToken(accessToken.getRefreshToken());
		serverAccessToken.setResponseType(accessToken.getResponseType());
		serverAccessToken.setScopes(
			convertScopeToPermissions(
				serverAccessToken.getClient(), accessToken.getScopes()));
		serverAccessToken.setTokenKey(accessToken.getTokenKey());
		serverAccessToken.setTokenType(accessToken.getTokenType());
		serverAccessToken.getSubject().setId(
			String.valueOf(accessToken.getUserId()));
		serverAccessToken.getSubject().setLogin(accessToken.getUserName());

		return serverAccessToken;
	}

	protected BearerTokenProvider getBearerTokenProvider(
		long companyId, String clientId) {

		return _scopedBearerTokenProvider.getService(companyId, clientId);
	}

	protected BearerTokenProvider.AccessToken fromCXFAccessToken(
		ServerAccessToken serverAccessToken) {

		OAuth2Application oAuth2Application =
			resolveOAuth2Application(serverAccessToken.getClient());

		return new AccessToken(
			oAuth2Application,
			serverAccessToken.getAudiences(),
			serverAccessToken.getClientCodeVerifier(),
			serverAccessToken.getExpiresIn(),
			serverAccessToken.getExtraProperties(),
			serverAccessToken.getGrantCode(),
			serverAccessToken.getGrantType(),
			serverAccessToken.getIssuedAt(),
			serverAccessToken.getIssuer(),
			serverAccessToken.getNonce(),
			serverAccessToken.getParameters(),
			serverAccessToken.getRefreshToken(),
			serverAccessToken.getResponseType(),
			OAuthUtils.convertPermissionsToScopeList(
				serverAccessToken.getScopes()),
			serverAccessToken.getTokenKey(),
			serverAccessToken.getTokenType(),
			Long.parseLong(serverAccessToken.getSubject().getId()),
			serverAccessToken.getSubject().getLogin());
	}

	@Override
	public ServerAccessToken refreshAccessToken(
			Client client, String refreshTokenKey,
			List<String> restrictedScopes)
		throws OAuthServiceException {

		RefreshToken cxfRefreshToken = getRefreshToken(refreshTokenKey);

		BearerTokenProvider.RefreshToken refreshToken =
			fromCXFRefreshToken(cxfRefreshToken);

		OAuth2Application oAuth2Application = resolveOAuth2Application(client);

		BearerTokenProvider tokenProvider =
			getBearerTokenProvider(
				oAuth2Application.getCompanyId(),
				oAuth2Application.getClientId());

		if (!tokenProvider.isValid(refreshToken)) {
			throw new OAuthServiceException(OAuthConstants.ACCESS_DENIED);
		}

		OAuth2RefreshToken oAuth2RefreshToken =
			_oAuth2RefreshTokenLocalService.fetchByContent(
				refreshTokenKey);

		if (oAuth2RefreshToken == null) {
			throw new OAuthServiceException(OAuthConstants.ACCESS_DENIED);
		}

		restrictedScopes = oAuth2RefreshToken.getScopesList();

		return super.refreshAccessToken(
			client, refreshTokenKey, restrictedScopes);
	}

	@Override
	protected RefreshToken doCreateNewRefreshToken(
		ServerAccessToken serverAccessToken) {

		RefreshToken cxfRefreshToken = super.doCreateNewRefreshToken(
			serverAccessToken);

		BearerTokenProvider.RefreshToken refreshToken =
			fromCXFRefreshToken(cxfRefreshToken);

		BearerTokenProvider tokenProvider = getBearerTokenProvider(
			refreshToken.getOAuth2Application().getCompanyId(),
			refreshToken.getOAuth2Application().getClientId());

		tokenProvider.enrichRefreshToken(refreshToken);

		cxfRefreshToken.setAudiences(refreshToken.getAudiences());
		cxfRefreshToken.setClientCodeVerifier(refreshToken.getClientCodeVerifier());
		cxfRefreshToken.setExpiresIn(refreshToken.getExpiresIn());
		cxfRefreshToken.setGrantType(refreshToken.getGrantType());
		cxfRefreshToken.setIssuedAt(refreshToken.getIssuedAt());
		cxfRefreshToken.setScopes(
			convertScopeToPermissions(
				serverAccessToken.getClient(), refreshToken.getScopes()));
		cxfRefreshToken.setTokenKey(refreshToken.getTokenKey());
		cxfRefreshToken.setTokenType(refreshToken.getTokenType());
		cxfRefreshToken.getSubject().setId(
			String.valueOf(refreshToken.getUserId()));
		cxfRefreshToken.getSubject().setLogin(refreshToken.getUserName());

		return cxfRefreshToken;
	}

	protected BearerTokenProvider.RefreshToken fromCXFRefreshToken(
		RefreshToken cxfRefreshToken) {

		OAuth2Application oAuth2Application =
			resolveOAuth2Application(cxfRefreshToken.getClient());

		return new BearerTokenProvider.RefreshToken(
			oAuth2Application,
			cxfRefreshToken.getAudiences(),
			cxfRefreshToken.getClientCodeVerifier(),
			cxfRefreshToken.getExpiresIn(),
			cxfRefreshToken.getGrantType(),
			cxfRefreshToken.getIssuedAt(),
			OAuthUtils.convertPermissionsToScopeList(
				cxfRefreshToken.getScopes()),
			cxfRefreshToken.getTokenKey(),
			cxfRefreshToken.getTokenType(),
			Long.parseLong(cxfRefreshToken.getSubject().getId()),
			cxfRefreshToken.getSubject().getLogin());
	}

	@Override
	protected ServerAccessToken doRefreshAccessToken(
		Client client, RefreshToken oldRefreshToken,
		List<String> restrictedScopes) {

		ServerAccessToken serverAccessToken =
			super.doRefreshAccessToken(
				client, oldRefreshToken, restrictedScopes);

		BearerTokenProvider.AccessToken accessToken = fromCXFAccessToken(
			serverAccessToken);

		BearerTokenProvider tokenProvider = getBearerTokenProvider(
			accessToken.getOAuth2Application().getCompanyId(),
			accessToken.getOAuth2Application().getClientId());

		tokenProvider.enrichAccessToken(accessToken);

		serverAccessToken.setAudiences(accessToken.getAudiences());
		serverAccessToken.setClientCodeVerifier(
			accessToken.getClientCodeVerifier());
		serverAccessToken.setExpiresIn(accessToken.getExpiresIn());
		serverAccessToken.setExtraProperties(accessToken.getExtraProperties());
		serverAccessToken.setGrantCode(accessToken.getGrantCode());
		serverAccessToken.setGrantType(accessToken.getGrantType());
		serverAccessToken.setIssuedAt(accessToken.getIssuedAt());
		serverAccessToken.setIssuer(accessToken.getIssuer());
		serverAccessToken.setNonce(accessToken.getNonce());
		serverAccessToken.setParameters(accessToken.getParameters());
		serverAccessToken.setRefreshToken(accessToken.getRefreshToken());
		serverAccessToken.setResponseType(accessToken.getResponseType());
		serverAccessToken.setScopes(
			convertScopeToPermissions(
				serverAccessToken.getClient(), accessToken.getScopes()));
		serverAccessToken.setTokenKey(accessToken.getTokenKey());
		serverAccessToken.setTokenType(accessToken.getTokenType());
		serverAccessToken.getSubject().setId(
			String.valueOf(accessToken.getUserId()));
		serverAccessToken.getSubject().setLogin(accessToken.getUserName());

		return serverAccessToken;
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
		try {
			_invokeTransactionally(
				() -> _transactionalSaveAccessToken(serverToken));
		}
		catch (Throwable throwable) {
			throw new OAuthServiceException(throwable);
		}
	}

	private void _transactionalSaveAccessToken(ServerAccessToken token) {
		Client client = token.getClient();

		OAuth2Token oAuth2Token = _oAuth2TokenLocalService.createOAuth2Token(
			token.getTokenKey());

		OAuth2Application oAuth2Application = resolveOAuth2Application(client);

		long companyId = oAuth2Application.getCompanyId();

		oAuth2Token.setCompanyId(companyId);
		oAuth2Token.setCreateDate(fromCXFTime(token.getIssuedAt()));
		oAuth2Token.setExpirationDate(
			fromCXFTime(token.getIssuedAt() + token.getExpiresIn()));

		oAuth2Token.setOAuth2ApplicationId(
			oAuth2Application.getOAuth2ApplicationId());

		Map<String, String> clientProperties = client.getProperties();

		String remoteAddr = clientProperties.get(CLIENT_REMOTE_ADDR_PROPERTY);
		String remoteHost = clientProperties.get(CLIENT_REMOTE_HOST_PROPERTY);

		oAuth2Token.setRemoteIPInfo(remoteAddr + ", " + remoteHost);

		OAuth2RefreshToken oAuth2RefreshToken =
			_oAuth2RefreshTokenLocalService.fetchByContent(
				token.getRefreshToken());

		if (oAuth2RefreshToken != null) {
			oAuth2Token.setOAuth2RefreshTokenId(
				oAuth2RefreshToken.getOAuth2RefreshTokenId());
		}

		oAuth2Token.setOAuth2TokenType(OAuthConstants.BEARER_TOKEN_TYPE);

		oAuth2Token.setScopes(
			OAuthUtils.convertPermissionsToScope(token.getScopes()));

		UserSubject subject = token.getSubject();

		if (subject != null) {
			try {
				long userId = Long.parseLong(subject.getId());
				User user = _userLocalService.getUser(userId);
				oAuth2Token.setUserId(userId);
				oAuth2Token.setUserName(user.getFullName());
			}
			catch (Exception e) {
				_log.error("Unable to load user " + subject.getId(), e);
				throw new RuntimeException(e);
			}
		}

		try {
			_oAuth2TokenLocalService.updateOAuth2Token(oAuth2Token);
		}
		catch (Exception e) {
			StringBundler sb = new StringBundler(6);
			sb.append("Unable to save user ");
			sb.append(token.getSubject().getId());
			sb.append(" token for client ");
			sb.append(client.getClientId());
			sb.append(" with granted scope ");
			sb.append(
				OAuthUtils.convertPermissionsToScopeList(
					token.getScopes()));

			_log.error(
				 sb.toString(), e);

			throw new OAuthServiceException(
				"Unable to save access token", e);
		}

		List<String> scopeList =
			OAuthUtils.convertPermissionsToScopeList(token.getScopes());

		Set<LiferayOAuth2Scope> liferayScopes = new HashSet<>();

		for (String scope : scopeList) {
			liferayScopes.addAll(
				_scopeFinderLocator.getLiferayOAuth2Scopes(companyId, scope));
		}

		try {
			_oAuth2ScopeGrantLocalService.grantScopesToToken(
				token.getTokenKey(), liferayScopes);
		}
		catch (NoSuchOAuth2TokenException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to find token for key " +
						token.getTokenKey());
			}

			throw new OAuthServiceException(
				"Unable to grant scope for token", e);
		}
		catch (Exception e) {
			StringBundler sb = new StringBundler(6);
			sb.append("Unable to save user ");
			sb.append(token.getSubject().getId());
			sb.append(" scope for client ");
			sb.append(client.getClientId());
			sb.append(" with approved scope ");
			sb.append(
				OAuthUtils.convertPermissionsToScopeList(
					token.getScopes()));

			_log.error(sb.toString(), e);

			throw new OAuthServiceException(
				"Unable to grant scope for token", e);
		}
	}

	@Override
	protected void saveRefreshToken(RefreshToken refreshToken) {
		try {
			_invokeTransactionally(
				() -> _transactionalSaveRefreshToken(refreshToken));
		}
		catch (Throwable throwable) {
			throw new OAuthServiceException(throwable);
		}
	}

	private void _transactionalSaveRefreshToken(RefreshToken refreshToken) {
		Client client = refreshToken.getClient();

		OAuth2Application oAuth2Application =
			resolveOAuth2Application(client);

		OAuth2RefreshToken oAuth2RefreshToken =
			_oAuth2RefreshTokenLocalService.createOAuth2RefreshToken(
				refreshToken.getTokenKey());

		oAuth2RefreshToken.setCompanyId(oAuth2Application.getCompanyId());
		oAuth2RefreshToken.setCreateDate(
			fromCXFTime(refreshToken.getIssuedAt()));

		oAuth2RefreshToken.setExpirationDate(
			fromCXFTime(
				refreshToken.getIssuedAt() + refreshToken.getExpiresIn()));

		oAuth2RefreshToken.setOAuth2ApplicationId(
			oAuth2Application.getOAuth2ApplicationId());

		Map<String, String> clientProperties = client.getProperties();

		String remoteAddr = clientProperties.get(CLIENT_REMOTE_ADDR_PROPERTY);
		String remoteHost = clientProperties.get(CLIENT_REMOTE_HOST_PROPERTY);

		oAuth2RefreshToken.setRemoteIPInfo(remoteAddr + ", " + remoteHost);

		UserSubject subject = refreshToken.getSubject();

		if (subject != null) {
			try {
				long userId = Long.parseLong(subject.getId());
				User user = _userLocalService.getUser(userId);
				oAuth2RefreshToken.setUserId(userId);
				oAuth2RefreshToken.setUserName(user.getFullName());
			}
			catch (Exception e) {
				_log.error("Unable to load user " + subject.getId(), e);
				throw new RuntimeException(e);
			}
		}

		try {
			oAuth2RefreshToken =
				_oAuth2RefreshTokenLocalService.updateOAuth2RefreshToken(
					oAuth2RefreshToken);
		}
		catch (Exception e) {
			StringBundler sb = new StringBundler();
			sb.append("Unable to save refresh token for client ");
			sb.append(client.getClientId());
			sb.append(" for user ");
			sb.append(oAuth2RefreshToken.getUserId());

			_log.error(sb.toString(), e);

			throw new OAuthServiceException(
				"Unable to save refresh token", e);
		}

		Set<String> authorizedScopes = new HashSet<>();

		List<String> accessTokens = refreshToken.getAccessTokens();

		for (String accessToken : accessTokens) {
			OAuth2Token oAuth2Token =
				_oAuth2TokenLocalService.fetchByContent(accessToken);

			if (oAuth2Token == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No access token found for refresh token for client "
							+ client.getClientId());
				}

				continue;
			}

			authorizedScopes.addAll(oAuth2Token.getScopesList());

			oAuth2Token.setOAuth2RefreshTokenId(
				oAuth2RefreshToken.getOAuth2RefreshTokenId());

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

		if (authorizedScopes != null) {
			oAuth2RefreshToken.setScopesList(new ArrayList(authorizedScopes));

			try {
				_oAuth2RefreshTokenLocalService.updateOAuth2RefreshToken(
					oAuth2RefreshToken);
			}
			catch (Exception e) {
				StringBundler sb = new StringBundler();
				sb.append("Unable to save refresh token for client ");
				sb.append(client.getClientId());
				sb.append(" for user ");
				sb.append(oAuth2RefreshToken.getUserId());
				sb.append(" with scopes ");
				sb.append(authorizedScopes);

				_log.error(sb.toString(), e);

				throw new OAuthServiceException(
					"Unable to save refresh token", e);
			}
		}
	}

	@Override
	protected void doRevokeAccessToken(ServerAccessToken accessToken) {
		try {
			OAuth2Token oAuth2Token = _oAuth2TokenLocalService.findByContent(
				accessToken.getTokenKey());

			_oAuth2TokenLocalService.deleteOAuth2Token(
				oAuth2Token.getOAuth2TokenId());
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
			OAuth2RefreshToken oAuth2RefreshToken =
				_oAuth2RefreshTokenLocalService.findByContent(
					refreshToken.getTokenKey());

			if (oAuth2RefreshToken != null) {
				_oAuth2RefreshTokenLocalService.deleteOAuth2RefreshToken(
					oAuth2RefreshToken.getOAuth2RefreshTokenId());
			}
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
				_oAuth2RefreshTokenLocalService.fetchByContent(refreshTokenKey);

			if (oAuth2RefreshToken == null ){
				// audit: trying to use expired token or brute-force token

				return null;
			}

			OAuth2Application oAuth2Application =
				_oAuth2ApplicationLocalService.getOAuth2Application(
					oAuth2RefreshToken.getOAuth2ApplicationId());

			long issuedAt = toCXFTime(oAuth2RefreshToken.getCreateDate());
			long expires =  toCXFTime(oAuth2RefreshToken.getExpirationDate());
			long lifetime = expires - issuedAt;

			RefreshToken refreshToken = new RefreshToken(
				populateClient(oAuth2Application), refreshTokenKey, lifetime,
				issuedAt);

			refreshToken.setSubject(
				populateUserSubject(
					oAuth2RefreshToken.getCompanyId(),
					oAuth2RefreshToken.getUserId(),
					oAuth2RefreshToken.getUserName()));

			Collection<OAuth2Token> oAuth2Tokens =
				_oAuth2TokenLocalService.findByRefreshToken(
					oAuth2RefreshToken.getOAuth2RefreshTokenId());

			List<String> accessTokens = new ArrayList<>(oAuth2Tokens.size());

			for (OAuth2Token oAuth2Token : oAuth2Tokens) {
				accessTokens.add(oAuth2Token.getOAuth2TokenContent());
			}

			refreshToken.setAccessTokens(accessTokens);

			refreshToken.setScopes(
				convertScopeToPermissions(
					refreshToken.getClient(),
					oAuth2RefreshToken.getScopesList()));

			refreshToken.getExtraProperties().put(
				"companyId", Long.toString(oAuth2RefreshToken.getCompanyId()));

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
		long companyId = CompanyThreadLocal.getCompanyId();

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				companyId, clientId);

		if (oAuth2Application == null) {
			//audit: trying non-existing or removed clientId

			return null;
		}

		getMessageContext().put(OAuthConstants.CLIENT_ID, clientId);

		return populateClient(oAuth2Application);
	}

	@Override
	public ServerAccessToken getAccessToken(String accessToken)
		throws OAuthServiceException {

		OAuth2Token oAuth2Token = _oAuth2TokenLocalService.fetchByContent(
			accessToken);

		if (oAuth2Token == null) {
			// audit: trying to use expired token or brute-force token

			return null;
		}

		return populateToken(oAuth2Token);
	}

	protected Client populateClient(OAuth2Application oAuth2Application) {
		String clientSecret = oAuth2Application.getClientSecret();

		if (Validator.isBlank(clientSecret)) {
			clientSecret = null;
		}

		Client client = new Client(
			oAuth2Application.getClientId(),
			clientSecret,
			oAuth2Application.getClientConfidential(),
			oAuth2Application.getName(),
			oAuth2Application.getHomePageURL());

		List<GrantType> allowedGrantTypes =
			oAuth2Application.getAllowedGrantTypesList();

		List<String> clientGrantTypes = client.getAllowedGrantTypes();

		for (GrantType allowedGrantType : allowedGrantTypes) {
			if (_oAuth2Configuration.allowAuthorizationCodeGrant() &&
					(allowedGrantType == GrantType.AUTHORIZATION_CODE)) {

				clientGrantTypes.add(OAuthConstants.AUTHORIZATION_CODE_GRANT);
			}
			else if (_oAuth2Configuration.allowAuthorizationCodePKCEGrant() &&
					 (allowedGrantType == GrantType.AUTHORIZATION_CODE_PKCE)) {

				clientGrantTypes.add(OAuthConstants.AUTHORIZATION_CODE_GRANT);
				clientGrantTypes.add(
					LiferayAuthorizationCodeGrantHandlerRegistrator.
						AUTHORIZATION_CODE_PKCE_GRANT);
			}
			else if (_oAuth2Configuration.allowClientCredentialsGrant() &&
					 (allowedGrantType == GrantType.CLIENT_CREDENTIALS)) {

				clientGrantTypes.add(OAuthConstants.CLIENT_CREDENTIALS_GRANT);
			}
			else if (
				_oAuth2Configuration.
					allowResourceOwnerPasswordCredentialsGrant() &&
				(allowedGrantType == GrantType.RESOURCE_OWNER_PASSWORD)) {

				clientGrantTypes.add(OAuthConstants.RESOURCE_OWNER_GRANT);
			}
			else if (_oAuth2Configuration.allowRefreshTokenGrant() &&
					 (allowedGrantType == GrantType.REFRESH_TOKEN)) {

				clientGrantTypes.add(OAuthConstants.REFRESH_TOKEN_GRANT);
			} else {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unknown or disabled grant type " + allowedGrantType);
				}
			}
		}

		// CXF considers no allowed grant types as allow all!

		if (clientGrantTypes.isEmpty()) {
			clientGrantTypes.add(StringPool.BLANK);
		}

		client.setApplicationDescription(
			oAuth2Application.getDescription());

		client.setRegisteredScopes(
			OAuthUtils.parseScope(oAuth2Application.getScopes()));

		client.setRedirectUris(oAuth2Application.getRedirectURIsList());

		client.setSubject(populateUserSubject(
			oAuth2Application.getCompanyId(), oAuth2Application.getUserId(),
			oAuth2Application.getUserName()));

		long companyId = oAuth2Application.getCompanyId();

		Map<String, String> clientProperties = client.getProperties();

		clientProperties.put("companyId", Long.toString(companyId));

		clientProperties.put(
			FEATURES_PROPERTY, oAuth2Application.getFeatures());

		for (String feature : oAuth2Application.getFeaturesList()) {
			clientProperties.put(FEATURE_PREFIX_PROPERTY + feature, feature);
		}

		return client;
	}

	protected ServerAccessToken populateToken(OAuth2Token oAuth2Token) {
		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				oAuth2Token.getOAuth2ApplicationId());

		if (oAuth2Application == null) {
			throw new SystemException(
				"Inconsistent DB state! No application found for token " +
					oAuth2Token.getOAuth2TokenId());
		}

		Client client = getClient(oAuth2Application.getClientId());

		long issuedAt = toCXFTime(oAuth2Token.getCreateDate());
		long expires =  toCXFTime(oAuth2Token.getExpirationDate());
		long lifetime = expires - issuedAt;

		BearerAccessToken bearerAccessToken = new BearerAccessToken(
			client, oAuth2Token.getOAuth2TokenContent(), lifetime, issuedAt);

		bearerAccessToken.setSubject(
			populateUserSubject(
				oAuth2Token.getCompanyId(), oAuth2Token.getUserId(),
				oAuth2Token.getUserName()));

		List<OAuthPermission> permissions = 
			convertScopeToPermissions(
				client, OAuthUtils.parseScope(oAuth2Token.getScopes()));
		
		bearerAccessToken.setScopes(permissions);

		bearerAccessToken.getExtraProperties().put(
			"companyId", Long.toString(oAuth2Token.getCompanyId()));

		return bearerAccessToken;
	}

	protected UserSubject populateUserSubject(
		long companyId, long userId, String userName) {

		UserSubject userSubject = new UserSubject(
			userName, Long.toString(userId));

		userSubject.getProperties().put("companyId", Long.toString(companyId));

		return userSubject;
	}

	protected OAuth2Application resolveOAuth2Application(Client client) {
		long companyId = Long.parseLong(
			client.getProperties().get("companyId"));

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				companyId, client.getClientId());

		if (oAuth2Application == null) {
			// audit: possible attack on client_id

			return null;
		}

		return oAuth2Application;
	}

	@Override
	public List<ServerAccessToken> getAccessTokens(
			Client client, UserSubject subject)
		throws OAuthServiceException {

		return null;
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

	protected Date fromCXFTime(long issuedAt) {
		return new Date(issuedAt * 1000);
	}

	protected long toCXFTime(Date dateCreated) {
		return dateCreated.getTime() / 1000;
	}

	private static Log _log =
		LogFactoryUtil.getLog(LiferayOAuthDataProvider.class);

	private static void _invokeTransactionally(Runnable runnable)
		throws Throwable{

		TransactionInvokerUtil.invoke(
			TransactionConfig.Factory.create(
				Propagation.REQUIRED, new Class[]{Exception.class}),
			() -> { runnable.run(); return null;});
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
	private UserLocalService _userLocalService;

	@Reference
	private ScopeLocator _scopeFinderLocator;

	@Reference
	private ConfigurationProvider _configurationProvider;
	
	private OAuth2Configuration _oAuth2Configuration;

	@Reference(
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(name=default)"
	)
	private BearerTokenProvider _defaultBearerTokenProvider;

	@Reference
	ScopedServiceTrackerMapFactory _scopedServiceTrackerMapFactory;
}
