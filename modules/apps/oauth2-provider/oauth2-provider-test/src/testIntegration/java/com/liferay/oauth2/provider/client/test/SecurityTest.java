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
	public void testCSRFStateParam() throws URISyntaxException {
		
		String state = "csrf_token";
		
		String responseState = getCode(
			"test@liferay.com", "test", null,
			getAuthorizationCodeResponseFunction(webTarget -> webTarget.queryParam(
					"client_id", "oauthTestApplicationCode"
				).queryParam(
					"response_type", "code"
				).queryParam(
					"redirect_uri", "http://redirecturi:8080"
				).queryParam(
					"state", state
				)),
			this::parseStateString);

		Assert.assertNotNull(state);
		
		Assert.assertEquals(state, responseState);
	}
	
	//@Test
	public void testAuthorizationCodeFlow() throws URISyntaxException {
		
		String tokenString = getToken(
			"oauthTestApplicationCode", null,
			getAuthorizationCode("test@liferay.com", "test", null),
			this::parseTokenString);

		Assert.assertNotNull(tokenString);
	}

	@Test
	public void testAuthorizationCodeFlowCode() throws URISyntaxException {
				
		String code = getCode(
			"test@liferay.com", "test", null,
			getAuthorizationCodeResponseFunction(
				webTarget -> webTarget.queryParam(
						"client_id", "oauthTestApplicationCode"
					).queryParam(
						"response_type", "code"
					).queryParam(
						"redirect_uri", "http://redirecturi:8080"
					)),
			this::parseAuthorizationCodeString);

		Assert.assertNotNull(code);
		
		System.out.println("CODE: " + code);
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
	
}