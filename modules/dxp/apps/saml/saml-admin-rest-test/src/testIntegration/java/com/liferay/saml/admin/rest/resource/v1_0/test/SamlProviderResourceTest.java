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
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.saml.admin.rest.client.dto.v1_0.SamlProvider;
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

	@BeforeClass
	public static void setUpClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		autoCloseables = new ArrayList<>();

//		deleteAllConfigurations();

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

	private void _createRevertingConfiguration(
		long companyId, Dictionary<String, Object> dictionary) throws Exception {

		//if (1 == 1) return;

		if (dictionary != null) {
			dictionary.put("companyId", companyId);
		}

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(&(service.factoryPid=com.liferay.saml.runtime.configuration." +
			"SamlProviderConfiguration)(companyId=" + companyId + "))");

		if (configurations == null || configurations.length == 0) {
			if (dictionary != null) {
				String pid = ConfigurationTestUtil.createFactoryConfiguration(
					"com.liferay.saml.runtime.configuration.SamlProviderConfiguration",
					dictionary);

				autoCloseables.add(
					() -> ConfigurationTestUtil.deleteConfiguration(pid));
			}
			else {
				autoCloseables.add(
					() -> {
						Configuration[] configurations2 = _configurationAdmin.listConfigurations(
							"(&(service.factoryPid=com.liferay.saml.runtime.configuration." +
							"SamlProviderConfiguration)(companyId=" + companyId + "))");

						if (configurations2 == null) return;

						ConfigurationTestUtil.deleteConfiguration(configurations2[0].getPid());
					});
			}
		}
		else {
			if (dictionary != null) {
				autoCloseables.add(
					new ConfigurationTemporarySwapper(
						configurations[0].getPid(), dictionary));

//				ConfigurationTestUtil.saveConfiguration(
//					configurations[0].getPid(), dictionary);
			}
			else {
				final Dictionary<String, Object> finalDictionary =
					configurations[0].getProperties();

				final String pid = configurations[0].getPid();

				autoCloseables.add(
					() ->
						//configurations[0]
						ConfigurationTestUtil.saveConfiguration(
							pid, finalDictionary));
//						ConfigurationTestUtil.createFactoryConfiguration(
//							"com.liferay.saml.runtime.configuration.SamlProviderConfiguration",
//							finalDictionary));

						// Experimenting
//						ConfigurationTestUtil.createFactoryConfiguration(
//							"com.liferay.saml.runtime.configuration.SamlProviderConfiguration",
//							finalDictionary);


				ConfigurationTestUtil.deleteConfiguration(pid);
			}
		}
	}

	private AutoCloseable _createResettableConfiguration() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(service.factoryPid=com.liferay.saml.runtime.configuration." +
			"SamlProviderConfiguration)");

		Dictionary <String, Object> systemDictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				PortletPropsKeys.SAML_ENABLED,
				!_defaultSamlProviderConfiguration.enabled()
			).put(
				"companyId", CompanyConstants.SYSTEM
			).build();

		Dictionary<String, ?> systemConfigurationDictionary = null;
		Configuration systemConfiguration = null;
		Dictionary<String, ?> testCompanyConfigurationDictionary = null;
		Configuration testCompanyConfiguration = null;

		List<AutoCloseable> autoCloseables = new ArrayList<>();

		AutoCloseable instanceAutoClosable = null;
		AutoCloseable systemAutoClosable = null;

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			Object companyId = properties.get("companyId");

			if (companyId.equals(this.testCompany)) {
				testCompanyConfigurationDictionary = properties;
				testCompanyConfiguration = configuration;

				instanceAutoClosable =
					new ConfigurationTemporarySwapper(
						configuration.getPid(), null);
			}
			else if (companyId.equals(CompanyConstants.SYSTEM)) {
				systemConfigurationDictionary = properties;
				systemConfiguration = configuration;

				systemAutoClosable =
					new ConfigurationTemporarySwapper(
						configuration.getPid(), systemDictionary);
			}
		}

		if (systemAutoClosable == null) {
			String systemPid =
				ConfigurationTestUtil.createFactoryConfiguration(
					"com.liferay.saml.runtime.configuration.SamlProviderConfiguration",
					systemDictionary);

			systemAutoClosable =
				() -> ConfigurationTestUtil.deleteConfiguration(systemPid);
		}

		if (instanceAutoClosable == null) {
			instanceAutoClosable =
				() -> { };
		}





		return null;
	}

	private String _createSystemConfiguration() throws Exception {

		return ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.saml.runtime.configuration.SamlProviderConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				PortletPropsKeys.SAML_ENABLED,
				!_defaultSamlProviderConfiguration.enabled()
			).put(
				"companyId", CompanyConstants.SYSTEM
			).build()
		);

//		Configuration defaultConfiguration = _configurationAdmin.getConfiguration(
//			"com.liferay.saml.runtime.configuration.SamlProviderConfiguration",
//			StringPool.QUESTION);
//
//		ConfigurationTestUtil.saveConfiguration(
//			defaultConfiguration,
//			HashMapDictionaryBuilder.<String, Object>put(
//				PortletPropsKeys.SAML_ENABLED,
//				!_defaultSamlProviderConfiguration.enabled()
//			).put(
//				"companyId", CompanyConstants.SYSTEM
//			).build());
//
//		return defaultConfiguration;
	}

	protected ArrayList<AutoCloseable> autoCloseables; //= new ArrayList<>();

	@DeleteAfterTestRun
	SAPEntry _sapEntry;

	private void deleteAllConfigurations() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(service.factoryPid=com.liferay.saml.runtime.configuration." +
			"SamlProviderConfiguration)");

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			long companyId = GetterUtil.getLong(properties.get("companyId"));

			if (companyId == 0 || companyId == 20097) {
				ConfigurationTestUtil.deleteConfiguration(configuration);
			}
		}
	}

	public void testPatchSamlProvider() throws Exception {

		_sapEntry = _sapEntryLocalService.addSAPEntry(
			TestPropsValues.getUserId(),
			"com.liferay.saml.admin.rest.internal.resource.v1_0." +
			"SamlProviderResourceImpl#*",
			true, true, "Guest",
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "Guest"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		SamlProvider defaultSamlProvider = samlProviderResource.getSamlProvider();

		Assert.assertEquals(
			_defaultSamlProviderConfiguration.enabled(), defaultSamlProvider.getEnabled());

		ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.saml.runtime.configuration.SamlProviderConfiguration",
			HashMapDictionaryBuilder.put(
				"companyId", (Object)testCompany.getCompanyId()
			).put(
				PortletPropsKeys.SAML_ENABLED,
				!_defaultSamlProviderConfiguration.enabled()
			).build());

		SamlProvider systemSamlProvider = samlProviderResource.getSamlProvider();

		Assert.assertNotEquals(
			defaultSamlProvider.getEnabled(), systemSamlProvider.getEnabled());

		SamlProvider patchSamlProvider = new SamlProvider() {
			{
				entityId = "test";
			}
		};

		SamlProvider samlProvider =
			samlProviderResource.patchSamlProvider(patchSamlProvider);

		Assert.assertEquals(
			patchSamlProvider.getEntityId(), samlProvider.getEntityId());

		Assert.assertEquals(
			systemSamlProvider.getEnabled(),
			samlProvider.getEnabled());
	}

	public void testPostSamlProvider() throws Exception {

		if (1 == 1) {
			return;
		}

		SamlProvider samlProvider = new SamlProvider() {
			{
				enabled = true;
				entityId = "";
				signMetadata = false;
				sslRequired = false;
			}
		};

		String role = SamlProvider.Role.SP.getValue();

		SamlProvider samlProvider2 =
			samlProviderResource.postSamlProvider(samlProvider);

//		Assert.assertEquals(
//			Response.Status.BAD_REQUEST.getStatusCode(),
//			httpResponse.getStatusCode());


//		SamlProvider randomSamlProvider = randomSamlProvider();
//
//		SamlProvider postSamlProvider = testPostSamlProvider_addSamlProvider(
//			randomSamlProvider);
//
//		assertEquals(randomSamlProvider, postSamlProvider);
//		assertValid(postSamlProvider);


//		SamlProvider samlProvider = new SamlProvider() {
//			{
//				enabled = true;
//				entityId = "";
//				signMetadata = false;
//				sslRequired = false;
//			}
//		};
//
//		String role = SamlProvider.Role.SP.getValue();
//
//		SamlProvider samlProvider2 =
//			samlProviderResource.postSamlProvider(samlProvider);
//
//		Assert.assertEquals(
//			Response.Status.BAD_REQUEST.getStatusCode(),
//			httpResponse.getStatusCode());

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