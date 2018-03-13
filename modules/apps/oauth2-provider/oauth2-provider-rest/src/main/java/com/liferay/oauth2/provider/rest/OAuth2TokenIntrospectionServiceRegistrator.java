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
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.services.AbstractTokenService;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component(
	immediate=true,
	property = {
		"oauth2.allow.token.introspection.endpoint=true",
		"oauth2.allow.token.introspection.endpoint.public.clients=true"
	}
)
public class OAuth2TokenIntrospectionServiceRegistrator {

	public static final String TOKEN_INTROSPECTION_FEATURE = "token-introspection";

	private ServiceRegistration<Object> _endpointServiceRegistration;
	private ServiceRegistration<Class> _classServiceRegistration;

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		boolean enabled = MapUtil.getBoolean(
			properties, "oauth2.allow.token.introspection.endpoint", true);

		if (enabled) {
			boolean publicClientsEnabled = MapUtil.getBoolean(
				properties,
				"oauth2.allow.token.introspection.endpoint.public.clients",
				true);

			OAuth2TokenIntrospectionService oAuth2TokenIntrospectionService =
				new OAuth2TokenIntrospectionService(publicClientsEnabled);

			oAuth2TokenIntrospectionService.setDataProvider(
				_liferayOAuthDataProvider);

			Hashtable<String, Object> endpointProperties = new Hashtable<>();

			endpointProperties.put("liferay.oauth2.endpoint", true);

			_endpointServiceRegistration = bundleContext.registerService(
				Object.class, oAuth2TokenIntrospectionService,
				endpointProperties);

			Hashtable<String, Object> classProperties = new Hashtable<>();

			classProperties.put("liferay.oauth2.class", true);

			_classServiceRegistration = bundleContext.registerService(
				Class.class, TokenIntrospectionJSONProvider.class,
				classProperties);
		}
	}

	@Deactivate
	protected void deactivate() {
		if (_classServiceRegistration != null) {
			_classServiceRegistration.unregister();
		}

		if (_endpointServiceRegistration != null) {
			_endpointServiceRegistration.unregister();
		}
	}

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

	@Path("introspect")
	public class OAuth2TokenIntrospectionService extends AbstractTokenService {

		public OAuth2TokenIntrospectionService(boolean publicClientsEnabled) {
			setCanSupportPublicClients(publicClientsEnabled);
		}

		@POST
		@Produces({MediaType.APPLICATION_JSON})
		@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
		public Response getTokenIntrospection(
			@Encoded MultivaluedMap<String, String> params) {

			Client authenticatedClient = authenticateClientIfNeeded(params);

			String tokenId = params.getFirst(OAuthConstants.TOKEN_ID);
			String tokenTypeHint =
				params.getFirst(OAuthConstants.TOKEN_TYPE_HINT);

			if (tokenTypeHint == null) {
				ServerAccessToken accessToken =
					_liferayOAuthDataProvider.getAccessToken(tokenId);

				if (accessToken != null) {
					return handleAccessToken(authenticatedClient, accessToken);
				}

				RefreshToken refreshToken =
					_liferayOAuthDataProvider.getRefreshToken(tokenId);

				if (refreshToken != null) {
					return handleRefreshToken(
						authenticatedClient, refreshToken);
				}
			}

			else if (OAuthConstants.ACCESS_TOKEN.equals(tokenTypeHint)) {
				ServerAccessToken accessToken =
					_liferayOAuthDataProvider.getAccessToken(tokenId);

				if (accessToken != null) {
					return handleAccessToken(authenticatedClient, accessToken);
				}
			}

			else if (OAuthConstants.REFRESH_TOKEN.equals(tokenTypeHint)) {
				RefreshToken refreshToken =
					_liferayOAuthDataProvider.getRefreshToken(tokenId);

				if (refreshToken != null) {
					return handleRefreshToken(
						authenticatedClient, refreshToken);
				}
			}

			else {
				return createErrorResponseFromErrorCode(
					OAuthConstants.UNSUPPORTED_TOKEN_TYPE);
			}

			return Response.ok(new TokenIntrospection(false)).build();
		}

		protected Response handleAccessToken(
			Client authenticatedClient, ServerAccessToken cxfAccessToken) {

			if (!verifyClient(authenticatedClient, cxfAccessToken)) {
				return createErrorResponseFromErrorCode(
					OAuthConstants.UNAUTHORIZED_CLIENT);
			}

			if (!verifyCXFToken(cxfAccessToken)) {
				return Response.ok(new TokenIntrospection(false)).build();
			}

			BearerTokenProvider.AccessToken accessToken =
				_liferayOAuthDataProvider.fromCXFAccessToken(
					cxfAccessToken);

			BearerTokenProvider bearerTokenProvider =
				_liferayOAuthDataProvider.getBearerTokenProvider(
					accessToken.getOAuth2Application().getCompanyId(),
					accessToken.getOAuth2Application().getClientId());

			if (!bearerTokenProvider.isValid(accessToken)) {
				return Response.ok(new TokenIntrospection(false)).build();
			}

			TokenIntrospection tokenIntrospection =
				_buildTokenIntrospection(cxfAccessToken);

			return Response.ok(tokenIntrospection).build();
		}

		protected Response handleRefreshToken(
			Client authenticatedClient, RefreshToken cxfRefreshToken) {

			if (!verifyClient(authenticatedClient, cxfRefreshToken)) {
				return createErrorResponseFromErrorCode(
					OAuthConstants.UNAUTHORIZED_CLIENT);
			}

			if (!verifyCXFToken(cxfRefreshToken)) {
				return Response.ok(new TokenIntrospection(false)).build();
			}

			BearerTokenProvider.RefreshToken refreshToken =
				_liferayOAuthDataProvider.fromCXFRefreshToken(
					cxfRefreshToken);

			BearerTokenProvider bearerTokenProvider =
				_liferayOAuthDataProvider.getBearerTokenProvider(
					refreshToken.getOAuth2Application().getCompanyId(),
					refreshToken.getOAuth2Application().getClientId());

			if (!bearerTokenProvider.isValid(refreshToken)) {
				return Response.ok(new TokenIntrospection(false)).build();
			}

			TokenIntrospection tokenIntrospection =
				_buildTokenIntrospection(cxfRefreshToken);

			return Response.ok().entity(tokenIntrospection).build();
		}

		protected boolean verifyClient(
			Client authenticatedClient, ServerAccessToken serverAccessToken) {

			if (!_clientsMatch(
				authenticatedClient, serverAccessToken.getClient())) {

				return false;
			}

			Map<String, String> clientProperties =
				authenticatedClient.getProperties();

			if (!clientProperties.containsKey(
				LiferayOAuthDataProvider.FEATURE_PREFIX_PROPERTY +
					TOKEN_INTROSPECTION_FEATURE)) {

				return false;
			}

			return true;
		}

		protected boolean verifyCXFToken(ServerAccessToken serverAccessToken) {
			if (OAuthUtils.isExpired(
				serverAccessToken.getIssuedAt(),
				serverAccessToken.getExpiresIn())) {

				return false;
			}

			return true;
		}

		private TokenIntrospection _buildTokenIntrospection(
			ServerAccessToken serverAccessToken) {

			TokenIntrospection tokenIntrospection =
				new TokenIntrospection(true);

			tokenIntrospection.setClientId(
				serverAccessToken.getClient().getClientId());

			if (!serverAccessToken.getScopes().isEmpty()) {
				tokenIntrospection.setScope(
					OAuthUtils.convertPermissionsToScope(
						serverAccessToken.getScopes()));
			}

			UserSubject userSubject = serverAccessToken.getSubject();

			if (userSubject != null) {
				tokenIntrospection.setUsername(
					serverAccessToken.getSubject().getLogin());

				if (userSubject.getId() != null) {
					tokenIntrospection.setSub(userSubject.getId());
				}
			}

			if (!serverAccessToken.getAudiences().isEmpty()) {
				tokenIntrospection.setAud(serverAccessToken.getAudiences());
			}

			if (serverAccessToken.getIssuer() != null) {
				tokenIntrospection.setIss(serverAccessToken.getIssuer());
			}

			tokenIntrospection.setIat(serverAccessToken.getIssuedAt());

			if (serverAccessToken.getExpiresIn() > 0) {
				tokenIntrospection.setExp(
					serverAccessToken.getIssuedAt() +
					serverAccessToken.getExpiresIn());
			}

			tokenIntrospection.setTokenType(serverAccessToken.getTokenType());

			tokenIntrospection.getExtensions().putAll(
				serverAccessToken.getExtraProperties());

			return tokenIntrospection;
		}

		private boolean _clientsMatch(Client client1, Client client2) {
			String client1Id = client1.getClientId();
			String client2Id = client2.getClientId();

			if (!Objects.equals(client1Id, client2Id)) {
				return false;
			}

			Map<String, String> properties = client1.getProperties();

			String companyId1 = properties.get("companyId");

			properties = client2.getProperties();

			String companyId2 = properties.get("companyId");

			if (!Objects.equals(companyId1, companyId2)) {
				return false;
			}

			return true;
		}
	}

	public class TokenIntrospection {
		private boolean _active;
		private List<String> _aud;
		private String _clientId;
		private Long _exp;
		private Map<String, String> _extensions = new HashMap<String, String>();
		private Long _iat;
		private String _iss;
		private String _jti;
		private Long _nbf;
		private String _scope;
		private String _sub;
		private String _tokenType;
		private String _username;

		public TokenIntrospection() {
		}

		public TokenIntrospection(boolean active) {
			this._active = active;
		}

		public boolean isActive() {
			return _active;
		}

		public void setActive(boolean active) {
			_active = active;
		}

		public List<String> getAud() {
			return _aud;
		}

		public void setAud(List<String> aud) {
			_aud = aud;
		}

		public String getClientId() {
			return _clientId;
		}

		public void setClientId(String clientId) {
			_clientId = clientId;
		}

		public Long getExp() {
			return _exp;
		}

		public void setExp(Long exp) {
			_exp = exp;
		}

		public Map<String, String> getExtensions() {
			return _extensions;
		}

		public void setExtensions(
			Map<String, String> extensions) {
			_extensions = extensions;
		}

		public Long getIat() {
			return _iat;
		}

		public void setIat(Long iat) {
			_iat = iat;
		}

		public String getIss() {
			return _iss;
		}

		public void setIss(String iss) {
			_iss = iss;
		}

		public String getJti() {
			return _jti;
		}

		public void setJti(String jti) {
			_jti = jti;
		}

		public Long getNbf() {
			return _nbf;
		}

		public void setNbf(Long nbf) {
			_nbf = nbf;
		}

		public String getScope() {
			return _scope;
		}

		public void setScope(String scope) {
			_scope = scope;
		}

		public String getSub() {
			return _sub;
		}

		public void setSub(String sub) {
			_sub = sub;
		}

		public String getTokenType() {
			return _tokenType;
		}

		public void setTokenType(String tokenType) {
			_tokenType = tokenType;
		}

		public String getUsername() {
			return _username;
		}

		public void setUsername(String username) {
			_username = username;
		}
	}

	@Provider
	@Produces("application/json")
	public static class TokenIntrospectionJSONProvider
		implements MessageBodyWriter<TokenIntrospection> {

		@Override
		public boolean isWriteable(
			Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType) {

			return TokenIntrospection.class.isAssignableFrom(type);
		}

		@Override
		public long getSize(
			TokenIntrospection tokenIntrospection, Class<?> type,
			Type genericType, Annotation[] annotations, MediaType mediaType) {

			return -1;
		}

		@Override
		public void writeTo(
				TokenIntrospection tokenIntrospection, Class<?> type,
				Type genericType, Annotation[] annotations, MediaType mediaType,
				MultivaluedMap<String, Object> httpHeaders,
				OutputStream entityStream)
			throws IOException, WebApplicationException {

			StringBundler sb = new StringBundler();
			sb.append("{");

			append(sb, "active", tokenIntrospection.isActive(), false);

			if (tokenIntrospection.isActive()) {
				if (tokenIntrospection.getAud() != null) {
					List<String> audience = new ArrayList<>(
						tokenIntrospection.getAud());

					audience.removeIf(String::isEmpty);

					if (!audience.isEmpty()) {
						if (audience.size() == 1) {
							append(sb, "aud", audience.get(0));
						}
						else {
							append(sb, "aud", audience);
						}
					}
				}

				append(
					sb, OAuthConstants.CLIENT_ID,
					tokenIntrospection.getClientId());

				append(sb, "exp", tokenIntrospection.getExp());
				append(sb, "iat", tokenIntrospection.getIat());
				append(sb, "iss", tokenIntrospection.getIss());
				append(sb, "jti", tokenIntrospection.getJti());
				append(sb, "nbf", tokenIntrospection.getNbf());
				append(sb, OAuthConstants.SCOPE, tokenIntrospection.getScope());
				append(sb, "sub", tokenIntrospection.getSub());
				append(
					sb, OAuthConstants.ACCESS_TOKEN_TYPE,
					tokenIntrospection.getTokenType());

				append(sb, "username", tokenIntrospection.getUsername());

				Map<String, String> extensions =
					tokenIntrospection.getExtensions();

				if ((extensions != null) && !extensions.isEmpty()) {
					for(Map.Entry<String, String> extension :
						extensions.entrySet()) {

						append(sb, extension.getKey(), extension.getValue());
					}
				}
			}

			sb.append("}");

			String result = sb.toString();

			entityStream.write(result.getBytes(StandardCharsets.UTF_8));
			
			entityStream.flush();
		}

		protected void append(
			StringBundler sb, String key, Long value) {

			if (value == null) {
				return;
			}

			sb.append(",");

			append(sb, key, value, false);
		}

		protected void append(
			StringBundler sb, String key, String value) {

			if (value == null) {
				return;
			}

			sb.append(",");

			append(sb, key, value, true);
		}

		protected void append(
			StringBundler sb, String key, List<String> value) {

			StringBundler arraySB = new StringBundler(
				(value.size() * 2 - 1) + 2);

			arraySB.append("[");

			for (int i = 0; i < value.size(); i++) {
				if (i > 0) {
					arraySB.append(",");
				}
				appendValue(arraySB, value.get(i), true);
			}

			arraySB.append("]");

			sb.append(",");

			append(sb, key, arraySB.toString(), false);
		}

		protected void append(
			StringBundler sb, String key, Object value, boolean quote) {

			sb.append("\"");
			sb.append(key);
			sb.append("\":");

			appendValue(sb, value, quote);
		}

		protected void appendValue(StringBundler sb, Object value, boolean quote) {
			if (quote) {
				sb.append("\"");

				String stringValue = StringUtil.replace(
					String.valueOf(value), new String[]{"\\", "\""},
					new String[]{"\\\\", "\\\""});

				sb.append(stringValue);
				sb.append("\"");
			}
			else {
				sb.append(value);
			}
		}
	}
}