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
import com.liferay.portal.kernel.json.JSONArray;
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
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.saml.admin.rest.client.dto.v1_0.IdpConnection;
import com.liferay.saml.admin.rest.client.http.HttpInvoker;
import com.liferay.saml.admin.rest.client.pagination.Page;
import com.liferay.saml.admin.rest.client.pagination.Pagination;
import com.liferay.saml.admin.rest.client.resource.v1_0.IdpConnectionResource;
import com.liferay.saml.admin.rest.client.serdes.v1_0.IdpConnectionSerDes;

import java.lang.reflect.Method;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Generated;

import javax.ws.rs.core.MultivaluedHashMap;

import org.apache.commons.lang.time.DateUtils;

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
public abstract class BaseIdpConnectionResourceTestCase {

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

		_idpConnectionResource.setContextCompany(testCompany);

		IdpConnectionResource.Builder builder = IdpConnectionResource.builder();

		idpConnectionResource = builder.authentication(
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

		IdpConnection idpConnection1 = randomIdpConnection();

		String json = objectMapper.writeValueAsString(idpConnection1);

		IdpConnection idpConnection2 = IdpConnectionSerDes.toDTO(json);

		Assert.assertTrue(equals(idpConnection1, idpConnection2));
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

		IdpConnection idpConnection = randomIdpConnection();

		String json1 = objectMapper.writeValueAsString(idpConnection);
		String json2 = IdpConnectionSerDes.toJSON(idpConnection);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		IdpConnection idpConnection = randomIdpConnection();

		idpConnection.setEntityId(regex);
		idpConnection.setMetadataUrl(regex);
		idpConnection.setMetadataXml(regex);
		idpConnection.setName(regex);
		idpConnection.setNameIdFormat(regex);
		idpConnection.setUserAttributeMappings(regex);

		String json = IdpConnectionSerDes.toJSON(idpConnection);

		Assert.assertFalse(json.contains(regex));

		idpConnection = IdpConnectionSerDes.toDTO(json);

		Assert.assertEquals(regex, idpConnection.getEntityId());
		Assert.assertEquals(regex, idpConnection.getMetadataUrl());
		Assert.assertEquals(regex, idpConnection.getMetadataXml());
		Assert.assertEquals(regex, idpConnection.getName());
		Assert.assertEquals(regex, idpConnection.getNameIdFormat());
		Assert.assertEquals(regex, idpConnection.getUserAttributeMappings());
	}

	@Test
	public void testGetIdpConnections() throws Exception {
		Page<IdpConnection> page = idpConnectionResource.getIdpConnections(
			Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		IdpConnection idpConnection1 = testGetIdpConnections_addIdpConnection(
			randomIdpConnection());

		IdpConnection idpConnection2 = testGetIdpConnections_addIdpConnection(
			randomIdpConnection());

		page = idpConnectionResource.getIdpConnections(Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(idpConnection1, (List<IdpConnection>)page.getItems());
		assertContains(idpConnection2, (List<IdpConnection>)page.getItems());
		assertValid(page);

		idpConnectionResource.deleteIdpConnection(idpConnection1.getId());

		idpConnectionResource.deleteIdpConnection(idpConnection2.getId());
	}

	@Test
	public void testGetIdpConnectionsWithPagination() throws Exception {
		Page<IdpConnection> totalPage = idpConnectionResource.getIdpConnections(
			null);

		int totalCount = GetterUtil.getInteger(totalPage.getTotalCount());

		IdpConnection idpConnection1 = testGetIdpConnections_addIdpConnection(
			randomIdpConnection());

		IdpConnection idpConnection2 = testGetIdpConnections_addIdpConnection(
			randomIdpConnection());

		IdpConnection idpConnection3 = testGetIdpConnections_addIdpConnection(
			randomIdpConnection());

		Page<IdpConnection> page1 = idpConnectionResource.getIdpConnections(
			Pagination.of(1, totalCount + 2));

		List<IdpConnection> idpConnections1 =
			(List<IdpConnection>)page1.getItems();

		Assert.assertEquals(
			idpConnections1.toString(), totalCount + 2, idpConnections1.size());

		Page<IdpConnection> page2 = idpConnectionResource.getIdpConnections(
			Pagination.of(2, totalCount + 2));

		Assert.assertEquals(totalCount + 3, page2.getTotalCount());

		List<IdpConnection> idpConnections2 =
			(List<IdpConnection>)page2.getItems();

		Assert.assertEquals(
			idpConnections2.toString(), 1, idpConnections2.size());

		Page<IdpConnection> page3 = idpConnectionResource.getIdpConnections(
			Pagination.of(1, totalCount + 3));

		assertContains(idpConnection1, (List<IdpConnection>)page3.getItems());
		assertContains(idpConnection2, (List<IdpConnection>)page3.getItems());
		assertContains(idpConnection3, (List<IdpConnection>)page3.getItems());
	}

	protected IdpConnection testGetIdpConnections_addIdpConnection(
			IdpConnection idpConnection)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetIdpConnections() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"idpConnections",
			new HashMap<String, Object>() {
				{
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject idpConnectionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/idpConnections");

		long totalCount = idpConnectionsJSONObject.getLong("totalCount");

		IdpConnection idpConnection1 =
			testGraphQLGetIdpConnections_addIdpConnection();
		IdpConnection idpConnection2 =
			testGraphQLGetIdpConnections_addIdpConnection();

		idpConnectionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/idpConnections");

		Assert.assertEquals(
			totalCount + 2, idpConnectionsJSONObject.getLong("totalCount"));

		assertContains(
			idpConnection1,
			Arrays.asList(
				IdpConnectionSerDes.toDTOs(
					idpConnectionsJSONObject.getString("items"))));
		assertContains(
			idpConnection2,
			Arrays.asList(
				IdpConnectionSerDes.toDTOs(
					idpConnectionsJSONObject.getString("items"))));
	}

	protected IdpConnection testGraphQLGetIdpConnections_addIdpConnection()
		throws Exception {

		return testGraphQLIdpConnection_addIdpConnection();
	}

	@Test
	public void testPostIdpConnection() throws Exception {
		IdpConnection randomIdpConnection = randomIdpConnection();

		IdpConnection postIdpConnection =
			testPostIdpConnection_addIdpConnection(randomIdpConnection);

		assertEquals(randomIdpConnection, postIdpConnection);
		assertValid(postIdpConnection);
	}

	protected IdpConnection testPostIdpConnection_addIdpConnection(
			IdpConnection idpConnection)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeleteIdpConnection() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		IdpConnection idpConnection =
			testDeleteIdpConnection_addIdpConnection();

		assertHttpResponseStatusCode(
			204,
			idpConnectionResource.deleteIdpConnectionHttpResponse(
				idpConnection.getId()));

		assertHttpResponseStatusCode(
			404,
			idpConnectionResource.getIdpConnectionHttpResponse(
				idpConnection.getId()));

		assertHttpResponseStatusCode(
			404, idpConnectionResource.getIdpConnectionHttpResponse(0L));
	}

	protected IdpConnection testDeleteIdpConnection_addIdpConnection()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteIdpConnection() throws Exception {
		IdpConnection idpConnection =
			testGraphQLDeleteIdpConnection_addIdpConnection();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteIdpConnection",
						new HashMap<String, Object>() {
							{
								put("idpConnectionId", idpConnection.getId());
							}
						})),
				"JSONObject/data", "Object/deleteIdpConnection"));
		JSONArray errorsJSONArray = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"idpConnection",
					new HashMap<String, Object>() {
						{
							put("idpConnectionId", idpConnection.getId());
						}
					},
					new GraphQLField("id"))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray.length() > 0);
	}

	protected IdpConnection testGraphQLDeleteIdpConnection_addIdpConnection()
		throws Exception {

		return testGraphQLIdpConnection_addIdpConnection();
	}

	@Test
	public void testGetIdpConnection() throws Exception {
		IdpConnection postIdpConnection =
			testGetIdpConnection_addIdpConnection();

		IdpConnection getIdpConnection = idpConnectionResource.getIdpConnection(
			postIdpConnection.getId());

		assertEquals(postIdpConnection, getIdpConnection);
		assertValid(getIdpConnection);
	}

	protected IdpConnection testGetIdpConnection_addIdpConnection()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetIdpConnection() throws Exception {
		IdpConnection idpConnection =
			testGraphQLGetIdpConnection_addIdpConnection();

		Assert.assertTrue(
			equals(
				idpConnection,
				IdpConnectionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"idpConnection",
								new HashMap<String, Object>() {
									{
										put(
											"idpConnectionId",
											idpConnection.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/idpConnection"))));
	}

	@Test
	public void testGraphQLGetIdpConnectionNotFound() throws Exception {
		Long irrelevantIdpConnectionId = RandomTestUtil.randomLong();

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"idpConnection",
						new HashMap<String, Object>() {
							{
								put(
									"idpConnectionId",
									irrelevantIdpConnectionId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected IdpConnection testGraphQLGetIdpConnection_addIdpConnection()
		throws Exception {

		return testGraphQLIdpConnection_addIdpConnection();
	}

	@Test
	public void testPatchIdpConnection() throws Exception {
		IdpConnection postIdpConnection =
			testPatchIdpConnection_addIdpConnection();

		IdpConnection randomPatchIdpConnection = randomPatchIdpConnection();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		IdpConnection patchIdpConnection =
			idpConnectionResource.patchIdpConnection(
				postIdpConnection.getId(), randomPatchIdpConnection);

		IdpConnection expectedPatchIdpConnection = postIdpConnection.clone();

		BeanTestUtil.copyProperties(
			randomPatchIdpConnection, expectedPatchIdpConnection);

		IdpConnection getIdpConnection = idpConnectionResource.getIdpConnection(
			patchIdpConnection.getId());

		assertEquals(expectedPatchIdpConnection, getIdpConnection);
		assertValid(getIdpConnection);
	}

	protected IdpConnection testPatchIdpConnection_addIdpConnection()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutIdpConnection() throws Exception {
		IdpConnection postIdpConnection =
			testPutIdpConnection_addIdpConnection();

		IdpConnection randomIdpConnection = randomIdpConnection();

		IdpConnection putIdpConnection = idpConnectionResource.putIdpConnection(
			postIdpConnection.getId(), randomIdpConnection);

		assertEquals(randomIdpConnection, putIdpConnection);
		assertValid(putIdpConnection);

		IdpConnection getIdpConnection = idpConnectionResource.getIdpConnection(
			putIdpConnection.getId());

		assertEquals(randomIdpConnection, getIdpConnection);
		assertValid(getIdpConnection);
	}

	protected IdpConnection testPutIdpConnection_addIdpConnection()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected IdpConnection testGraphQLIdpConnection_addIdpConnection()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		IdpConnection idpConnection, List<IdpConnection> idpConnections) {

		boolean contains = false;

		for (IdpConnection item : idpConnections) {
			if (equals(idpConnection, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			idpConnections + " does not contain " + idpConnection, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		IdpConnection idpConnection1, IdpConnection idpConnection2) {

		Assert.assertTrue(
			idpConnection1 + " does not equal " + idpConnection2,
			equals(idpConnection1, idpConnection2));
	}

	protected void assertEquals(
		List<IdpConnection> idpConnections1,
		List<IdpConnection> idpConnections2) {

		Assert.assertEquals(idpConnections1.size(), idpConnections2.size());

		for (int i = 0; i < idpConnections1.size(); i++) {
			IdpConnection idpConnection1 = idpConnections1.get(i);
			IdpConnection idpConnection2 = idpConnections2.get(i);

			assertEquals(idpConnection1, idpConnection2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<IdpConnection> idpConnections1,
		List<IdpConnection> idpConnections2) {

		Assert.assertEquals(idpConnections1.size(), idpConnections2.size());

		for (IdpConnection idpConnection1 : idpConnections1) {
			boolean contains = false;

			for (IdpConnection idpConnection2 : idpConnections2) {
				if (equals(idpConnection1, idpConnection2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				idpConnections2 + " does not contain " + idpConnection1,
				contains);
		}
	}

	protected void assertValid(IdpConnection idpConnection) throws Exception {
		boolean valid = true;

		if (idpConnection.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"assertionSignatureRequired", additionalAssertFieldName)) {

				if (idpConnection.getAssertionSignatureRequired() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("clockSkew", additionalAssertFieldName)) {
				if (idpConnection.getClockSkew() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("enabled", additionalAssertFieldName)) {
				if (idpConnection.getEnabled() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("entityId", additionalAssertFieldName)) {
				if (idpConnection.getEntityId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("forceAuthn", additionalAssertFieldName)) {
				if (idpConnection.getForceAuthn() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"metadataUpdatedDate", additionalAssertFieldName)) {

				if (idpConnection.getMetadataUpdatedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("metadataUrl", additionalAssertFieldName)) {
				if (idpConnection.getMetadataUrl() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("metadataXml", additionalAssertFieldName)) {
				if (idpConnection.getMetadataXml() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (idpConnection.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("nameIdFormat", additionalAssertFieldName)) {
				if (idpConnection.getNameIdFormat() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("signAuthnRequest", additionalAssertFieldName)) {
				if (idpConnection.getSignAuthnRequest() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"unknownUsersAreStrangers", additionalAssertFieldName)) {

				if (idpConnection.getUnknownUsersAreStrangers() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"userAttributeMappings", additionalAssertFieldName)) {

				if (idpConnection.getUserAttributeMappings() == null) {
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

	protected void assertValid(Page<IdpConnection> page) {
		boolean valid = false;

		java.util.Collection<IdpConnection> idpConnections = page.getItems();

		int size = idpConnections.size();

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
					com.liferay.saml.admin.rest.dto.v1_0.IdpConnection.class)) {

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
		IdpConnection idpConnection1, IdpConnection idpConnection2) {

		if (idpConnection1 == idpConnection2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"assertionSignatureRequired", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						idpConnection1.getAssertionSignatureRequired(),
						idpConnection2.getAssertionSignatureRequired())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("clockSkew", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						idpConnection1.getClockSkew(),
						idpConnection2.getClockSkew())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("enabled", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						idpConnection1.getEnabled(),
						idpConnection2.getEnabled())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("entityId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						idpConnection1.getEntityId(),
						idpConnection2.getEntityId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("forceAuthn", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						idpConnection1.getForceAuthn(),
						idpConnection2.getForceAuthn())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						idpConnection1.getId(), idpConnection2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"metadataUpdatedDate", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						idpConnection1.getMetadataUpdatedDate(),
						idpConnection2.getMetadataUpdatedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("metadataUrl", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						idpConnection1.getMetadataUrl(),
						idpConnection2.getMetadataUrl())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("metadataXml", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						idpConnection1.getMetadataXml(),
						idpConnection2.getMetadataXml())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						idpConnection1.getName(), idpConnection2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("nameIdFormat", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						idpConnection1.getNameIdFormat(),
						idpConnection2.getNameIdFormat())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("signAuthnRequest", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						idpConnection1.getSignAuthnRequest(),
						idpConnection2.getSignAuthnRequest())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"unknownUsersAreStrangers", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						idpConnection1.getUnknownUsersAreStrangers(),
						idpConnection2.getUnknownUsersAreStrangers())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"userAttributeMappings", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						idpConnection1.getUserAttributeMappings(),
						idpConnection2.getUserAttributeMappings())) {

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

		if (!(_idpConnectionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_idpConnectionResource;

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
		EntityField entityField, String operator, IdpConnection idpConnection) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("assertionSignatureRequired")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("clockSkew")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("enabled")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("entityId")) {
			sb.append("'");
			sb.append(String.valueOf(idpConnection.getEntityId()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("forceAuthn")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("metadataUpdatedDate")) {
			if (operator.equals("between")) {
				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(
					_dateFormat.format(
						DateUtils.addSeconds(
							idpConnection.getMetadataUpdatedDate(), -2)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(
					_dateFormat.format(
						DateUtils.addSeconds(
							idpConnection.getMetadataUpdatedDate(), 2)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(
					_dateFormat.format(idpConnection.getMetadataUpdatedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("metadataUrl")) {
			sb.append("'");
			sb.append(String.valueOf(idpConnection.getMetadataUrl()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("metadataXml")) {
			sb.append("'");
			sb.append(String.valueOf(idpConnection.getMetadataXml()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			sb.append("'");
			sb.append(String.valueOf(idpConnection.getName()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("nameIdFormat")) {
			sb.append("'");
			sb.append(String.valueOf(idpConnection.getNameIdFormat()));
			sb.append("'");

			return sb.toString();
		}

		if (entityFieldName.equals("signAuthnRequest")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("unknownUsersAreStrangers")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("userAttributeMappings")) {
			sb.append("'");
			sb.append(String.valueOf(idpConnection.getUserAttributeMappings()));
			sb.append("'");

			return sb.toString();
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

	protected IdpConnection randomIdpConnection() throws Exception {
		return new IdpConnection() {
			{
				assertionSignatureRequired = RandomTestUtil.randomBoolean();
				clockSkew = RandomTestUtil.randomLong();
				enabled = RandomTestUtil.randomBoolean();
				entityId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				forceAuthn = RandomTestUtil.randomBoolean();
				id = RandomTestUtil.randomLong();
				metadataUpdatedDate = RandomTestUtil.nextDate();
				metadataUrl = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				metadataXml = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				nameIdFormat = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				signAuthnRequest = RandomTestUtil.randomBoolean();
				unknownUsersAreStrangers = RandomTestUtil.randomBoolean();
				userAttributeMappings = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected IdpConnection randomIrrelevantIdpConnection() throws Exception {
		IdpConnection randomIrrelevantIdpConnection = randomIdpConnection();

		return randomIrrelevantIdpConnection;
	}

	protected IdpConnection randomPatchIdpConnection() throws Exception {
		return randomIdpConnection();
	}

	protected IdpConnectionResource idpConnectionResource;
	protected Group irrelevantGroup;
	protected Company testCompany;
	protected Group testGroup;

	protected static class BeanTestUtil {

		public static void copyProperties(Object source, Object target)
			throws Exception {

			Class<?> sourceClass = _getSuperClass(source.getClass());

			Class<?> targetClass = target.getClass();

			for (java.lang.reflect.Field field :
					sourceClass.getDeclaredFields()) {

				if (field.isSynthetic()) {
					continue;
				}

				Method getMethod = _getMethod(
					sourceClass, field.getName(), "get");

				Method setMethod = _getMethod(
					targetClass, field.getName(), "set",
					getMethod.getReturnType());

				setMethod.invoke(target, getMethod.invoke(source));
			}
		}

		public static boolean hasProperty(Object bean, String name) {
			Method setMethod = _getMethod(
				bean.getClass(), "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod != null) {
				return true;
			}

			return false;
		}

		public static void setProperty(Object bean, String name, Object value)
			throws Exception {

			Class<?> clazz = bean.getClass();

			Method setMethod = _getMethod(
				clazz, "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod == null) {
				throw new NoSuchMethodException();
			}

			Class<?>[] parameterTypes = setMethod.getParameterTypes();

			setMethod.invoke(bean, _translateValue(parameterTypes[0], value));
		}

		private static Method _getMethod(Class<?> clazz, String name) {
			for (Method method : clazz.getMethods()) {
				if (name.equals(method.getName()) &&
					(method.getParameterCount() == 1) &&
					_parameterTypes.contains(method.getParameterTypes()[0])) {

					return method;
				}
			}

			return null;
		}

		private static Method _getMethod(
				Class<?> clazz, String fieldName, String prefix,
				Class<?>... parameterTypes)
			throws Exception {

			return clazz.getMethod(
				prefix + StringUtil.upperCaseFirstLetter(fieldName),
				parameterTypes);
		}

		private static Class<?> _getSuperClass(Class<?> clazz) {
			Class<?> superClass = clazz.getSuperclass();

			if ((superClass == null) || (superClass == Object.class)) {
				return clazz;
			}

			return superClass;
		}

		private static Object _translateValue(
			Class<?> parameterType, Object value) {

			if ((value instanceof Integer) &&
				parameterType.equals(Long.class)) {

				Integer intValue = (Integer)value;

				return intValue.longValue();
			}

			return value;
		}

		private static final Set<Class<?>> _parameterTypes = new HashSet<>(
			Arrays.asList(
				Boolean.class, Date.class, Double.class, Integer.class,
				Long.class, Map.class, String.class));

	}

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
		LogFactoryUtil.getLog(BaseIdpConnectionResourceTestCase.class);

	private static DateFormat _dateFormat;

	@Inject
	private com.liferay.saml.admin.rest.resource.v1_0.IdpConnectionResource
		_idpConnectionResource;

}