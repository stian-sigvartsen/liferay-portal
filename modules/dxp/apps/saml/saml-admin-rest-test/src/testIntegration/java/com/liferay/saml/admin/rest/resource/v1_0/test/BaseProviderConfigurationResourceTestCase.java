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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.saml.admin.rest.client.dto.v1_0.ProviderConfiguration;
import com.liferay.saml.admin.rest.client.http.HttpInvoker;
import com.liferay.saml.admin.rest.client.pagination.Page;
import com.liferay.saml.admin.rest.client.resource.v1_0.ProviderConfigurationResource;
import com.liferay.saml.admin.rest.client.serdes.v1_0.ProviderConfigurationSerDes;

import java.lang.reflect.InvocationTargetException;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Generated;

import javax.ws.rs.core.MultivaluedHashMap;

import org.apache.commons.beanutils.BeanUtilsBean;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
public abstract class BaseProviderConfigurationResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_providerConfigurationResource.setContextCompany(testCompany);

		ProviderConfigurationResource.Builder builder =
			ProviderConfigurationResource.builder();

		providerConfigurationResource = builder.authentication(
			"test@liferay.com", "test"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};

		ProviderConfiguration providerConfiguration1 =
			randomProviderConfiguration();

		String json = objectMapper.writeValueAsString(providerConfiguration1);

		ProviderConfiguration providerConfiguration2 =
			ProviderConfigurationSerDes.toDTO(json);

		Assert.assertTrue(
			equals(providerConfiguration1, providerConfiguration2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};

		ProviderConfiguration providerConfiguration =
			randomProviderConfiguration();

		String json1 = objectMapper.writeValueAsString(providerConfiguration);
		String json2 = ProviderConfigurationSerDes.toJSON(
			providerConfiguration);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		ProviderConfiguration providerConfiguration =
			randomProviderConfiguration();

		providerConfiguration.setEntityId(regex);
		providerConfiguration.setKeyStoreCredentialPassword(regex);
		providerConfiguration.setRole(regex);
		providerConfiguration.setSamlKeystoreCredentialPassword(regex);

		String json = ProviderConfigurationSerDes.toJSON(providerConfiguration);

		Assert.assertFalse(json.contains(regex));

		providerConfiguration = ProviderConfigurationSerDes.toDTO(json);

		Assert.assertEquals(regex, providerConfiguration.getEntityId());
		Assert.assertEquals(
			regex, providerConfiguration.getKeyStoreCredentialPassword());
		Assert.assertEquals(regex, providerConfiguration.getRole());
		Assert.assertEquals(
			regex, providerConfiguration.getSamlKeystoreCredentialPassword());
	}

	@Test
	public void testGetProviderConfiguration() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGraphQLGetProviderConfiguration() throws Exception {
		Assert.assertTrue(true);
	}

	@Test
	public void testGraphQLGetProviderConfigurationNotFound() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		ProviderConfiguration providerConfiguration,
		List<ProviderConfiguration> providerConfigurations) {

		boolean contains = false;

		for (ProviderConfiguration item : providerConfigurations) {
			if (equals(providerConfiguration, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			providerConfigurations + " does not contain " +
				providerConfiguration,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProviderConfiguration providerConfiguration1,
		ProviderConfiguration providerConfiguration2) {

		Assert.assertTrue(
			providerConfiguration1 + " does not equal " +
				providerConfiguration2,
			equals(providerConfiguration1, providerConfiguration2));
	}

	protected void assertEquals(
		List<ProviderConfiguration> providerConfigurations1,
		List<ProviderConfiguration> providerConfigurations2) {

		Assert.assertEquals(
			providerConfigurations1.size(), providerConfigurations2.size());

		for (int i = 0; i < providerConfigurations1.size(); i++) {
			ProviderConfiguration providerConfiguration1 =
				providerConfigurations1.get(i);
			ProviderConfiguration providerConfiguration2 =
				providerConfigurations2.get(i);

			assertEquals(providerConfiguration1, providerConfiguration2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProviderConfiguration> providerConfigurations1,
		List<ProviderConfiguration> providerConfigurations2) {

		Assert.assertEquals(
			providerConfigurations1.size(), providerConfigurations2.size());

		for (ProviderConfiguration providerConfiguration1 :
				providerConfigurations1) {

			boolean contains = false;

			for (ProviderConfiguration providerConfiguration2 :
					providerConfigurations2) {

				if (equals(providerConfiguration1, providerConfiguration2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				providerConfigurations2 + " does not contain " +
					providerConfiguration1,
				contains);
		}
	}

	protected void assertValid(ProviderConfiguration providerConfiguration)
		throws Exception {

		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"allowShowingTheLoginPortlet", additionalAssertFieldName)) {

				if (providerConfiguration.getAllowShowingTheLoginPortlet() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"assertionSignatureRequired", additionalAssertFieldName)) {

				if (providerConfiguration.getAssertionSignatureRequired() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"authnRequestSignatureRequired",
					additionalAssertFieldName)) {

				if (providerConfiguration.getAuthnRequestSignatureRequired() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("clockSkew", additionalAssertFieldName)) {
				if (providerConfiguration.getClockSkew() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultAssertionLifetime", additionalAssertFieldName)) {

				if (providerConfiguration.getDefaultAssertionLifetime() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("enabled", additionalAssertFieldName)) {
				if (providerConfiguration.getEnabled() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("entityId", additionalAssertFieldName)) {
				if (providerConfiguration.getEntityId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("idpConnections", additionalAssertFieldName)) {
				if (providerConfiguration.getIdpConnections() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"keyStoreCredentialPassword", additionalAssertFieldName)) {

				if (providerConfiguration.getKeyStoreCredentialPassword() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"ldapImportEnabled", additionalAssertFieldName)) {

				if (providerConfiguration.getLdapImportEnabled() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("role", additionalAssertFieldName)) {
				if (providerConfiguration.getRole() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"samlKeystoreCredentialPassword",
					additionalAssertFieldName)) {

				if (providerConfiguration.getSamlKeystoreCredentialPassword() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"sessionMaximumAge", additionalAssertFieldName)) {

				if (providerConfiguration.getSessionMaximumAge() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sessionTimeout", additionalAssertFieldName)) {
				if (providerConfiguration.getSessionTimeout() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("signAuthnRequest", additionalAssertFieldName)) {
				if (providerConfiguration.getSignAuthnRequest() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("signMetadata", additionalAssertFieldName)) {
				if (providerConfiguration.getSignMetadata() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sslRequired", additionalAssertFieldName)) {
				if (providerConfiguration.getSslRequired() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<ProviderConfiguration> page) {
		boolean valid = false;

		java.util.Collection<ProviderConfiguration> providerConfigurations =
			page.getItems();

		int size = providerConfigurations.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) &&
			(page.getPageSize() > 0) && (page.getTotalCount() > 0) &&
			(size > 0)) {

			valid = true;
		}

		Assert.assertTrue(valid);
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.saml.admin.rest.dto.v1_0.ProviderConfiguration.
						class)) {

			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			graphQLFields.addAll(getGraphQLFields(field));
		}

		return graphQLFields;
	}

	protected List<GraphQLField> getGraphQLFields(
			java.lang.reflect.Field... fields)
		throws Exception {

		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field : fields) {
			com.liferay.portal.vulcan.graphql.annotation.GraphQLField
				vulcanGraphQLField = field.getAnnotation(
					com.liferay.portal.vulcan.graphql.annotation.GraphQLField.
						class);

			if (vulcanGraphQLField != null) {
				Class<?> clazz = field.getType();

				if (clazz.isArray()) {
					clazz = clazz.getComponentType();
				}

				List<GraphQLField> childrenGraphQLFields = getGraphQLFields(
					getDeclaredFields(clazz));

				graphQLFields.add(
					new GraphQLField(field.getName(), childrenGraphQLFields));
			}
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(
		ProviderConfiguration providerConfiguration1,
		ProviderConfiguration providerConfiguration2) {

		if (providerConfiguration1 == providerConfiguration2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"allowShowingTheLoginPortlet", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						providerConfiguration1.getAllowShowingTheLoginPortlet(),
						providerConfiguration2.
							getAllowShowingTheLoginPortlet())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"assertionSignatureRequired", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						providerConfiguration1.getAssertionSignatureRequired(),
						providerConfiguration2.
							getAssertionSignatureRequired())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"authnRequestSignatureRequired",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						providerConfiguration1.
							getAuthnRequestSignatureRequired(),
						providerConfiguration2.
							getAuthnRequestSignatureRequired())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("clockSkew", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						providerConfiguration1.getClockSkew(),
						providerConfiguration2.getClockSkew())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultAssertionLifetime", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						providerConfiguration1.getDefaultAssertionLifetime(),
						providerConfiguration2.getDefaultAssertionLifetime())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("enabled", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						providerConfiguration1.getEnabled(),
						providerConfiguration2.getEnabled())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("entityId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						providerConfiguration1.getEntityId(),
						providerConfiguration2.getEntityId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("idpConnections", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						providerConfiguration1.getIdpConnections(),
						providerConfiguration2.getIdpConnections())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"keyStoreCredentialPassword", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						providerConfiguration1.getKeyStoreCredentialPassword(),
						providerConfiguration2.
							getKeyStoreCredentialPassword())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"ldapImportEnabled", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						providerConfiguration1.getLdapImportEnabled(),
						providerConfiguration2.getLdapImportEnabled())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("role", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						providerConfiguration1.getRole(),
						providerConfiguration2.getRole())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"samlKeystoreCredentialPassword",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						providerConfiguration1.
							getSamlKeystoreCredentialPassword(),
						providerConfiguration2.
							getSamlKeystoreCredentialPassword())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"sessionMaximumAge", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						providerConfiguration1.getSessionMaximumAge(),
						providerConfiguration2.getSessionMaximumAge())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sessionTimeout", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						providerConfiguration1.getSessionTimeout(),
						providerConfiguration2.getSessionTimeout())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("signAuthnRequest", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						providerConfiguration1.getSignAuthnRequest(),
						providerConfiguration2.getSignAuthnRequest())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("signMetadata", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						providerConfiguration1.getSignMetadata(),
						providerConfiguration2.getSignMetadata())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sslRequired", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						providerConfiguration1.getSslRequired(),
						providerConfiguration2.getSslRequired())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equals(
		Map<String, Object> map1, Map<String, Object> map2) {

		if (Objects.equals(map1.keySet(), map2.keySet())) {
			for (Map.Entry<String, Object> entry : map1.entrySet()) {
				if (entry.getValue() instanceof Map) {
					if (!equals(
							(Map)entry.getValue(),
							(Map)map2.get(entry.getKey()))) {

						return false;
					}
				}
				else if (!Objects.deepEquals(
							entry.getValue(), map2.get(entry.getKey()))) {

					return false;
				}
			}

			return true;
		}

		return false;
	}

	protected java.lang.reflect.Field[] getDeclaredFields(Class clazz)
		throws Exception {

		Stream<java.lang.reflect.Field> stream = Stream.of(
			ReflectionUtil.getDeclaredFields(clazz));

		return stream.filter(
			field -> !field.isSynthetic()
		).toArray(
			java.lang.reflect.Field[]::new
		);
	}

	protected java.util.Collection<EntityField> getEntityFields()
		throws Exception {

		if (!(_providerConfigurationResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_providerConfigurationResource;

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type)
		throws Exception {

		java.util.Collection<EntityField> entityFields = getEntityFields();

		Stream<EntityField> stream = entityFields.stream();

		return stream.filter(
			entityField ->
				Objects.equals(entityField.getType(), type) &&
				!ArrayUtil.contains(
					getIgnoredEntityFieldNames(), entityField.getName())
		).collect(
			Collectors.toList()
		);
	}

	protected String getFilterString(
		EntityField entityField, String operator,
		ProviderConfiguration providerConfiguration) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("allowShowingTheLoginPortlet")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("assertionSignatureRequired")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("authnRequestSignatureRequired")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("clockSkew")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("defaultAssertionLifetime")) {
			sb.append(
				String.valueOf(
					providerConfiguration.getDefaultAssertionLifetime()));

			return sb.toString();
		}

		if (entityFieldName.equals("enabled")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("entityId")) {
			sb.append("'");
			sb.append(String.valueOf(providerConfiguration.getEntityId()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("idpConnections")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("keyStoreCredentialPassword")) {
			sb.append("'");
			sb.append(
				String.valueOf(
					providerConfiguration.getKeyStoreCredentialPassword()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("ldapImportEnabled")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("role")) {
			sb.append("'");
			sb.append(String.valueOf(providerConfiguration.getRole()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("samlKeystoreCredentialPassword")) {
			sb.append("'");
			sb.append(
				String.valueOf(
					providerConfiguration.getSamlKeystoreCredentialPassword()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("sessionMaximumAge")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("sessionTimeout")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("signAuthnRequest")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("signMetadata")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("sslRequired")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path("http://localhost:8080/o/graphql");
		httpInvoker.userNameAndPassword("test@liferay.com:test");

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected JSONObject invokeGraphQLMutation(GraphQLField graphQLField)
		throws Exception {

		GraphQLField mutationGraphQLField = new GraphQLField(
			"mutation", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(mutationGraphQLField.toString()));
	}

	protected JSONObject invokeGraphQLQuery(GraphQLField graphQLField)
		throws Exception {

		GraphQLField queryGraphQLField = new GraphQLField(
			"query", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(queryGraphQLField.toString()));
	}

	protected ProviderConfiguration randomProviderConfiguration()
		throws Exception {

		return new ProviderConfiguration() {
			{
				allowShowingTheLoginPortlet = RandomTestUtil.randomBoolean();
				assertionSignatureRequired = RandomTestUtil.randomBoolean();
				authnRequestSignatureRequired = RandomTestUtil.randomBoolean();
				clockSkew = RandomTestUtil.randomLong();
				defaultAssertionLifetime = RandomTestUtil.randomInt();
				enabled = RandomTestUtil.randomBoolean();
				entityId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				keyStoreCredentialPassword = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				ldapImportEnabled = RandomTestUtil.randomBoolean();
				role = StringUtil.toLowerCase(RandomTestUtil.randomString());
				samlKeystoreCredentialPassword = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				sessionMaximumAge = RandomTestUtil.randomLong();
				sessionTimeout = RandomTestUtil.randomLong();
				signAuthnRequest = RandomTestUtil.randomBoolean();
				signMetadata = RandomTestUtil.randomBoolean();
				sslRequired = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected ProviderConfiguration randomIrrelevantProviderConfiguration()
		throws Exception {

		ProviderConfiguration randomIrrelevantProviderConfiguration =
			randomProviderConfiguration();

		return randomIrrelevantProviderConfiguration;
	}

	protected ProviderConfiguration randomPatchProviderConfiguration()
		throws Exception {

		return randomProviderConfiguration();
	}

	protected ProviderConfigurationResource providerConfigurationResource;
	protected Group irrelevantGroup;
	protected Company testCompany;
	protected Group testGroup;

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(String key, List<GraphQLField> graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = Arrays.asList(graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			List<GraphQLField> graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append(")");
			}

			if (!_graphQLFields.isEmpty()) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append("}");
			}

			return sb.toString();
		}

		private final List<GraphQLField> _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(BaseProviderConfigurationResourceTestCase.class);

	private static BeanUtilsBean _beanUtilsBean = new BeanUtilsBean() {

		@Override
		public void copyProperty(Object bean, String name, Object value)
			throws IllegalAccessException, InvocationTargetException {

			if (value != null) {
				super.copyProperty(bean, name, value);
			}
		}

	};
	private static DateFormat _dateFormat;

	@Inject
	private
		com.liferay.saml.admin.rest.resource.v1_0.ProviderConfigurationResource
			_providerConfigurationResource;

}