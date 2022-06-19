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

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.saml.admin.rest.client.dto.v1_0.Idp;
import com.liferay.saml.admin.rest.client.dto.v1_0.IdpConnection;
import com.liferay.saml.admin.rest.client.dto.v1_0.SamlProvider;
import com.liferay.saml.admin.rest.client.dto.v1_0.Sp;
import com.liferay.saml.admin.rest.client.dto.v1_0.SpConnection;
import com.liferay.saml.admin.rest.client.http.HttpInvoker;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.util.PortletPropsKeys;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Stian Sigvartsen
 */
@RunWith(Arquillian.class)
public class SamlProviderResourceTest extends BaseSamlProviderResourceTestCase {

	@Override
	protected SamlProvider testPostSamlProvider_addSamlProvider(
		SamlProvider samlProvider)
		throws Exception {

		return new SamlProvider();
	}

	public void testGetSamlProvider() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		autoCloseables = new ArrayList<>();

		_temporaryClearConfiguration(CompanyConstants.SYSTEM);
		_temporaryClearConfiguration(TestPropsValues.getCompanyId());
	}

	private void _temporaryClearConfiguration(long companyId) throws Exception {
		Configuration configuration =
			_getSamlProviderConfiguration(companyId);

		if (configuration != null) {
			final Dictionary<String, Object> dictionary =
				configuration.getProperties();

			dictionary.put("companyId", -companyId);
			ConfigurationTestUtil.saveConfiguration(configuration, dictionary);

			autoCloseables.add(() -> {
				_deleteSamlProviderConfiguration(companyId);
				dictionary.put("companyId", companyId);
				ConfigurationTestUtil.saveConfiguration(configuration, dictionary);
			});
		}
		else {
			autoCloseables.add(
				() -> _deleteSamlProviderConfiguration(companyId));
		}
	}

	@After
	public void tearDown() {
		_cleanUp();
	}

	private void _addSAPEntry() throws PortalException {
		_sapEntry = _sapEntryLocalService.addSAPEntry(
			TestPropsValues.getUserId(),
			"com.liferay.saml.admin.rest.internal.resource.v1_0." +
			"SamlProviderResourceImpl#*",
			true, true, "Guest",
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "Guest"
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _cleanUp() {
		ListIterator<AutoCloseable> listIterator = autoCloseables.listIterator(
			autoCloseables.size());

		while (listIterator.hasPrevious()) {
			AutoCloseable previousAutoCloseable = listIterator.previous();

			try {
				previousAutoCloseable.close();
			}
			catch (Exception exception) {
				//_log.error(exception);
			}
		}
	}

	private void _deleteSamlProviderConfiguration(long companyId) throws Exception {
		Configuration configuration =
			_getSamlProviderConfiguration(companyId);

		if (configuration != null) {
			ConfigurationTestUtil.deleteConfiguration(
				configuration);
		}
	}

	private Configuration _getSamlProviderConfiguration(long companyId) throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(&(service.factoryPid=com.liferay.saml.runtime.configuration." +
			"SamlProviderConfiguration)(companyId=" + companyId + "))");

		if (configurations != null && configurations.length > 0) {
			return configurations[0];
		}

		return null;
	}

	protected ArrayList<AutoCloseable> autoCloseables;

	@DeleteAfterTestRun
	SAPEntry _sapEntry;

	public void testPatchSamlProvider() throws Exception {

		_addSAPEntry();

		SamlProvider defaultSamlProvider = samlProviderResource.getSamlProvider();

		Assert.assertEquals(
			_defaultSamlProviderConfiguration.sslRequired(),
			defaultSamlProvider.getSslRequired());

		ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.saml.runtime.configuration.SamlProviderConfiguration",
			HashMapDictionaryBuilder.put(
				"companyId", (Object)testCompany.getCompanyId()
			).put(
				PortletPropsKeys.SAML_SSL_REQUIRED,
				!_defaultSamlProviderConfiguration.sslRequired()
			).build());

		SamlProvider systemSamlProvider = samlProviderResource.getSamlProvider();

		Assert.assertNotEquals(
			defaultSamlProvider.getSslRequired(),
			systemSamlProvider.getSslRequired());

		SamlProvider patchSamlProvider = new SamlProvider() {
			{
				entityId = "test";
			}
		};

		SamlProvider samlProvider =
			samlProviderResource.patchSamlProvider(patchSamlProvider);

		Assert.assertEquals(
			samlProvider.getEntityId(), patchSamlProvider.getEntityId());

		Assert.assertEquals(
			systemSamlProvider.getSslRequired(), samlProvider.getSslRequired());

		patchSamlProvider = new SamlProvider() {
			{
				enabled = true;
			}
		};

		HttpInvoker.HttpResponse httpResponse =
			samlProviderResource.patchSamlProviderHttpResponse(patchSamlProvider);

		Assert.assertEquals(
			Response.Status.BAD_REQUEST.getStatusCode(),
			httpResponse.getStatusCode());

		JSONObject jsonObject =
			JSONFactoryUtil.createJSONObject(httpResponse.getContent());

		Assert.assertEquals(
			"Credential is required", jsonObject.get("title"));
	}

	public void testPostSamlProvider() throws Exception {

		_addSAPEntry();

		SamlProvider postSamlProvider = new SamlProvider() {
			{
				enabled = false;
				entityId = "";
				signMetadata = false;
				sslRequired = false;
			}
		};

		String role = SamlProvider.Role.SP.getValue();

		postSamlProvider.setSp(new Sp() {
			{
				allowShowingTheLoginPortlet = false;
				assertionSignatureRequired = false;
				clockSkew = 1000l;
				ldapImportEnabled = false;
				signAuthnRequest = false;
			}
		});

		SamlProvider samlProvider =
			samlProviderResource.postSamlProvider(postSamlProvider);

		_addExpectedEnrichment(postSamlProvider);

		Assert.assertEquals(postSamlProvider, samlProvider);

		postSamlProvider.setIdp(new Idp() {
			{
				authnRequestSignatureRequired = false;
				defaultAssertionLifetime = 10000;
				sessionMaximumAge = 60000l;
				sessionTimeout = 60000l;
			}
		});

		HttpInvoker.HttpResponse httpResponse =
			samlProviderResource.postSamlProviderHttpResponse(postSamlProvider);

		Assert.assertEquals(
			Response.Status.BAD_REQUEST.getStatusCode(),
			httpResponse.getStatusCode());

		JSONObject jsonObject =
			JSONFactoryUtil.createJSONObject(httpResponse.getContent());

		Assert.assertEquals(
			"Can only configure one of sp & idp roles",
			jsonObject.get("title"));

		postSamlProvider.setSp((Sp)null);

		samlProvider = samlProviderResource.postSamlProvider(postSamlProvider);

		_addExpectedEnrichment(postSamlProvider);

		Assert.assertEquals(postSamlProvider, samlProvider);
	}

	private void _addExpectedEnrichment(SamlProvider samlProvider) {
		if (samlProvider.getIdp() != null) {
			Idp idp = samlProvider.getIdp();

			if (idp.getSpConnections() == null) {
				idp.setSpConnections(new SpConnection[0]);
			}
			samlProvider.setRole(SamlProvider.Role.IDP);
		}
		else if (samlProvider.getSp() != null) {
			Sp sp = samlProvider.getSp();

			if (sp.getIdpConnections() == null) {
				sp.setIdpConnections(new IdpConnection[0]);
			}
			samlProvider.setRole(SamlProvider.Role.SP);
		}
	}

	@Override
	public void testDeleteRole() throws Exception {
	}

	@Override
	public void testGetRole() throws Exception {
	}

	@Override
	public void testPatchRole() throws Exception {
	}

	@Override
	public void testPutRole() throws Exception {
		SamlProvider samlProvider = new SamlProvider() {
			{
				enabled = true;
				entityId = "";
				signMetadata = false;
				sslRequired = false;
			}
		};

		String role = SamlProvider.Role.SP.getValue();

		HttpInvoker.HttpResponse httpResponse =
			samlProviderResource.putRoleHttpResponse(role,samlProvider);
	}

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

	private final SamlProviderConfiguration _defaultSamlProviderConfiguration =
		ConfigurableUtil.createConfigurable(
			SamlProviderConfiguration.class, Collections.emptyMap());

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private SAPEntryLocalService _sapEntryLocalService;

	private static Configuration _defaultConfiguration;
}