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

package com.liferay.oauth2.provider.client.test;

import com.google.common.base.Objects;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.test.internal.TestAnnotatedApplication;
import com.liferay.oauth2.provider.test.internal.activator.BaseTestPreparatorBundleActivator;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Sierra Andrés
 */
@RunAsClient
@RunWith(Arquillian.class)
public class SecurityTest extends BaseClientTestCase {

	@Deployment
	public static Archive<?> getDeployment() throws Exception {
		return BaseClientTestCase.getDeployment(
			SecurityTestPreparatorBundleActivator.class);
	}

	/*
	@Test
	public void test() throws Exception {
		WebTarget tokenWebTarget = getTokenWebTarget();

		Invocation.Builder invocationBuilder = tokenWebTarget.request();

		MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

		formData.add("client_id", "");
		formData.add("client_secret", "");
		formData.add("grant_type", "client_credentials");

		String errorString = parseError(
			invocationBuilder.post(Entity.form(formData)));

		Assert.assertEquals("invalid_client", errorString);

		formData = new MultivaluedHashMap<>();

		formData.add("client_id", "oauthTestApplication");
		formData.add("client_secret", "oauthTestApplicationSecret");
		formData.add("grant_type", "client_credentials");

		WebTarget webTarget = getWebTarget("/annotated");

		String tokenString = parseTokenString(
			invocationBuilder.post(Entity.form(formData)));

		invocationBuilder = authorize(webTarget.request(), tokenString);

		Assert.assertEquals(
			"everything.read", invocationBuilder.get(String.class));

		invocationBuilder =
			webTarget.request().header("Authorization", "Bearer ");

		Response response = invocationBuilder.get();

		Assert.assertEquals(403, response.getStatus());

		invocationBuilder =
			webTarget.request().header("Authorization", "Bearer wrong");

		response = invocationBuilder.get();

		Assert.assertEquals(403, response.getStatus());
	}
	*/
	
	@Test
	public void testGrantFlowHacks() throws Exception {
		String tokenString = getToken(
			"oauthTestApplicationCode", null,
			getAuthorizationCodeNEW("test@liferay.com", "test", null),
			this::parseTokenString);

		Assert.assertNotNull(tokenString);

		/*
		tokenString = getToken(
			"oauthTestApplicationCodePKCE", null,
			getAuthorizationCodePKCE("test@liferay.com", "test", null),
			this::parseTokenString);

		Assert.assertNotNull(tokenString);
		*/

	}
	
	@Test
	public void testCSRFStateParam() {
		
		String clientId = "oauthTestApplicationCode";
		String state = "csrf_token";
		
		URI location = 
			getAuthorizationCodeCallbackRequestURL(
				"test@liferay.com", "test", "localhost", webTarget -> {
					webTarget = webTarget.queryParam(
						"client_id", clientId
					).queryParam(
						"response_type", "code"
					);
	
					if (state != null) {
						webTarget = webTarget.queryParam("state", state);
					}
	
					return webTarget;
				});
		
		System.out.println("### " + location.toString());
		
		Map<String, String[]> parameterMap = HttpUtil.getParameterMap(
			location.getQuery());
		
		Assert.assertEquals(state, parameterMap.get("state"));
	}

	public static class SecurityTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long defaultCompanyId = PortalUtil.getDefaultCompanyId();

			User user = UserTestUtil.getAdminUser(defaultCompanyId);

			Dictionary<String, Object> properties = new HashMapDictionary<>();

			properties.put("oauth2.scopechecker.type", "annotations");

			registerJaxRsApplication(
				new TestAnnotatedApplication(), "annotated", properties);

			/*
			createOAuth2Application(
				defaultCompanyId, user, "oauthTestApplication");
			*/
			
			createOAuth2Application(
				defaultCompanyId, user, "oauthTestApplicationCode",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE),
				Collections.singletonList("everything"));

			createOAuth2Application(
				defaultCompanyId, user, "oauthTestApplicationCodePKCE", null,
				Collections.singletonList(GrantType.AUTHORIZATION_CODE_PKCE),
				Collections.singletonList("everything"));

			createOAuth2Application(
				defaultCompanyId, user, "oauthTestApplicationClient",
				Collections.singletonList(GrantType.CLIENT_CREDENTIALS),
				Collections.singletonList("everything"));

			createOAuth2Application(
				defaultCompanyId, user, "oauthTestApplicationPassword",
				Collections.singletonList(GrantType.RESOURCE_OWNER_PASSWORD),
				Collections.singletonList("everything"));
			
		}

	}
	
	/*
	protected BiFunction<String, Invocation.Builder, Response>
		getAuthorizationCodeWithStateParam(
			String user, String password, String hostname, String scope, String state) {
	
		return (clientId, invocationBuilder) -> {
			String authorizationCode = getAuthorizationCode(
				user, password, hostname,
				webTarget -> {
					webTarget = webTarget.queryParam(
						"client_id", clientId
					).queryParam(
						"response_type", "code"
					);

					if (state != null) {
						webTarget = webTarget.queryParam("state", state);
					}

					if (scope != null) {
						webTarget = webTarget.queryParam("scope", scope);
					}
	
					return webTarget;
				});
	
			MultivaluedMap<String, String> formData =
				new MultivaluedHashMap<>();
	
			formData.add("client_id", clientId);
			formData.add("client_secret", "oauthTestApplicationSecret");
			formData.add("code", authorizationCode);
			formData.add("grant_type", "authorization_code");
	
			return invocationBuilder.post(Entity.form(formData));
		};
	}
	*/
		
	/*
	protected BiFunction<String, Invocation.Builder, Response>
		getAuthorizationCodePKCEClientSecret(
			String userName, String password, String hostname) {
	
		return (clientId, invocationBuilder) -> {
			String codeVerifier = RandomTestUtil.randomString();
	
			String base64Digest = DigesterUtil.digestBase64(
				Digester.SHA_256, codeVerifier);
	
			String base64UrlDigest = StringUtil.replace(
				base64Digest, new char[] {CharPool.PLUS, CharPool.SLASH},
				new char[] {CharPool.MINUS, CharPool.UNDERLINE});
	
			base64UrlDigest = StringUtil.removeChar(
				base64UrlDigest, CharPool.EQUAL);
	
			final String codeChallenge = base64UrlDigest;
	
			String authorizationCode = getAuthorizationCode(
				userName, password, hostname,
				webTarget -> webTarget.queryParam(
					"client_id", clientId
				).queryParam(
					"code_challenge", codeChallenge
				).queryParam(
					"response_type", "code"
				));
	
			MultivaluedMap<String, String> formData =
				new MultivaluedHashMap<>();
	
			formData.add("client_id", clientId);
			formData.add("code", authorizationCode);
			formData.add("code_verifier", codeVerifier);
			formData.add("grant_type", "authorization_code");
	
			return invocationBuilder.post(Entity.form(formData));
		};
	}
	*/
	
	/*
	protected BiFunction<String, Invocation.Builder, Response>
		getAuthorizationCodePKCEMissingCode(
			String userName, String password, String hostname) {
	
		return (clientId, invocationBuilder) -> {
			String codeVerifier = RandomTestUtil.randomString();
	
			String base64Digest = DigesterUtil.digestBase64(
				Digester.SHA_256, codeVerifier);
	
			String base64UrlDigest = StringUtil.replace(
				base64Digest, new char[] {CharPool.PLUS, CharPool.SLASH},
				new char[] {CharPool.MINUS, CharPool.UNDERLINE});
	
			base64UrlDigest = StringUtil.removeChar(
				base64UrlDigest, CharPool.EQUAL);
	
			final String codeChallenge = base64UrlDigest;
	
			String authorizationCode = getAuthorizationCode(
				userName, password, hostname,
				webTarget -> webTarget.queryParam(
					"client_id", clientId
				).queryParam(
					"code_challenge", codeChallenge
				).queryParam(
					"response_type", "code"
				));
	
			MultivaluedMap<String, String> formData =
				new MultivaluedHashMap<>();
	
			formData.add("client_id", clientId);
			formData.add("code", authorizationCode);
			formData.add("code_verifier", codeVerifier);
			formData.add("grant_type", "authorization_code");
	
			return invocationBuilder.post(Entity.form(formData));
		};
	}
	*/
	
	
	protected String getAuthorizationCodeNEW(
		String login, String password, String hostname,
		Function<WebTarget, WebTarget> authorizeRequestFunction) {

		URI location = getAuthorizationCodeCallbackRequestURL(login, password, hostname, authorizeRequestFunction);
		
		Map<String, String[]> parameterMap = HttpUtil.getParameterMap(
			location.getQuery());

		if (parameterMap.containsKey("error")) {
			return parameterMap.get("error")[0];
		}

		return parameterMap.get("code")[0];
	}
	
	// Method for receiving a callback URI and completing the rest of the grant flow
	protected String getAuthorizationCodeNEW2(
		String login, String password, String hostname,
		Function<URI, Response> callbackURIFunction) {

		URI location = callbackURIFunction.apply(authorizationRequestWebTarget);
		
		URI location = getAuthorizationCodeCallbackRequestURL(login, password, hostname, authorizeRequestFunction);
		
		Map<String, String[]> parameterMap = HttpUtil.getParameterMap(
			location.getQuery());

		if (parameterMap.containsKey("error")) {
			return parameterMap.get("error")[0];
		}

		return parameterMap.get("code")[0];
	}

	protected BiFunction<String, Invocation.Builder, Response>
		getAuthorizationCodeNEW(String user, String password, String hostname) {
	
		return getAuthorizationCodeNEW(user, password, hostname, (String)null);
	}
	
	protected BiFunction<String, Invocation.Builder, Response>
		getAuthorizationCodeNEW(
			String user, String password, String hostname, String scope) {
	
		return (clientId, invocationBuilder) -> {
			String authorizationCode = getAuthorizationCodeNEW(
				user, password, hostname,
				webTarget -> {
					webTarget = webTarget.queryParam(
						"client_id", clientId
					).queryParam(
						"response_type", "code"
					);
	
					if (scope != null) {
						webTarget = webTarget.queryParam("scope", scope);
					}
	
					return webTarget;
				});
	
			MultivaluedMap<String, String> formData =
				new MultivaluedHashMap<>();
	
			formData.add("client_id", clientId);
			formData.add("client_secret", "oauthTestApplicationSecret");
			formData.add("code", authorizationCode);
			formData.add("grant_type", "authorization_code");
	
			return invocationBuilder.post(Entity.form(formData));
		};
	}
	
	protected URI getAuthorizationCodeCallbackRequestURL(
		String login, String password, String hostname,
		Function<WebTarget, WebTarget> authorizeRequestFunction) {

		try {
			Invocation.Builder invocationBuilder = getInvocationBuilder(
				hostname,
				authorizeRequestFunction.apply(getAuthorizeWebTarget()));

			Cookie authenticatedCookie = getAuthenticatedCookie(
				login, password, hostname);

			Response response = invocationBuilder.accept(
				"text/html"
			).cookie(
				authenticatedCookie
			).get();

			URI location = response.getLocation();

			if (location == null) {
				throw new RuntimeException(
					"Invalid authorization response: " + response.getStatus());
			}

			Map<String, String[]> parameterMap = HttpUtil.getParameterMap(
				location.getQuery());

			if (parameterMap.containsKey("error")) {
				return location;
			}

			MultivaluedMap<String, String> formData =
				new MultivaluedHashMap<>();

			formData.add("oauthDecision", "allow");

			for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
				String key = entry.getKey();

				if (!StringUtil.startsWith(key, "oauth2_")) {
					continue;
				}

				formData.add(
					key.substring("oauth2_".length()), entry.getValue()[0]);
			}

			invocationBuilder = getInvocationBuilder(
				hostname, getAuthorizeDecisionWebTarget());

			invocationBuilder = invocationBuilder.cookie(authenticatedCookie);

			response = invocationBuilder.post(Entity.form(formData));
			
			location = response.getLocation();

			if (location == null) {
				throw new RuntimeException(
					"Invalid authorization decision response: " +
						response.getStatus());
			}
			
			return location;
		}
		catch (URISyntaxException urise) {
			throw new RuntimeException(urise);
		}
		
	}
}