/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.saml.admin.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.test.rule.Inject;
import com.liferay.saml.admin.rest.client.http.HttpInvoker;
import com.liferay.saml.admin.rest.client.resource.v1_0.SamlProviderResource;
import com.liferay.saml.admin.rest.dto.v1_0.SamlProvider;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

/**
 * @author Stian Sigvartsen
 */
@RunWith(Arquillian.class)
public class SamlProviderResourceTest extends BaseSamlProviderResourceTestCase {

	private <T extends Exception> void _testPutSamlProviderProblem(
		String role, SamlProvider samlProvider, Class<T> exceptionClass)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			samlProviderResource.putRoleHttpResponse(role, samlProvider);

		Assert.assertEquals(
			Response.Status.BAD_REQUEST.getStatusCode(),
			httpResponse.getStatusCode());

		if (exceptionClass != null) {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				httpResponse.getContent());

			Assert.assertEquals(
				exceptionClass.getSimpleName(), jsonObject.get("type"));
		}
	}

	protected SamlProviderResource samlProviderResource;

	@Inject
	private JSONFactory _jsonFactory;
}