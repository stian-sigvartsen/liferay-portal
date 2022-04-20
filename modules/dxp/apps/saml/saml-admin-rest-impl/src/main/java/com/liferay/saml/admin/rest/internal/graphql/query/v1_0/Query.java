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

package com.liferay.saml.admin.rest.internal.graphql.query.v1_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.saml.admin.rest.dto.v1_0.IdpConnection;
import com.liferay.saml.admin.rest.dto.v1_0.ProviderConfiguration;
import com.liferay.saml.admin.rest.resource.v1_0.IdpConnectionResource;
import com.liferay.saml.admin.rest.resource.v1_0.ProviderConfigurationResource;

import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
public class Query {

	public static void setIdpConnectionResourceComponentServiceObjects(
		ComponentServiceObjects<IdpConnectionResource>
			idpConnectionResourceComponentServiceObjects) {

		_idpConnectionResourceComponentServiceObjects =
			idpConnectionResourceComponentServiceObjects;
	}

	public static void setProviderConfigurationResourceComponentServiceObjects(
		ComponentServiceObjects<ProviderConfigurationResource>
			providerConfigurationResourceComponentServiceObjects) {

		_providerConfigurationResourceComponentServiceObjects =
			providerConfigurationResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {idpConnections(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the connected SAML IDPs")
	public IdpConnectionPage idpConnections(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_idpConnectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			idpConnectionResource -> new IdpConnectionPage(
				idpConnectionResource.getIdpConnections(
					search,
					_filterBiFunction.apply(
						idpConnectionResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						idpConnectionResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {idpConnection(idpConnectionId: ___){assertionSignatureRequired, clockSkew, enabled, entityId, forceAuthn, id, metadataUpdatedDate, metadataUrl, name, nameIdFormat, signAuthnRequest, unknownUsersAreStrangers, userAttributeMappings}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Gets details of a SAML IDP connection")
	public IdpConnection idpConnection(
			@GraphQLName("idpConnectionId") Long idpConnectionId)
		throws Exception {

		return _applyComponentServiceObjects(
			_idpConnectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			idpConnectionResource -> idpConnectionResource.getIdpConnection(
				idpConnectionId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {providerConfiguration{allowShowingTheLoginPortlet, assertionSignatureRequired, authnRequestSignatureRequired, clockSkew, defaultAssertionLifetime, enabled, entityId, idpConnections, keyStoreCredentialPassword, ldapImportEnabled, role, samlKeystoreCredentialPassword, sessionMaximumAge, sessionTimeout, signAuthnRequest, signMetadata, sslRequired}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Provider configuration")
	public ProviderConfiguration providerConfiguration() throws Exception {
		return _applyComponentServiceObjects(
			_providerConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			providerConfigurationResource ->
				providerConfigurationResource.getProviderConfiguration());
	}

	@GraphQLName("IdpConnectionPage")
	public class IdpConnectionPage {

		public IdpConnectionPage(Page idpConnectionPage) {
			actions = idpConnectionPage.getActions();

			items = idpConnectionPage.getItems();
			lastPage = idpConnectionPage.getLastPage();
			page = idpConnectionPage.getPage();
			pageSize = idpConnectionPage.getPageSize();
			totalCount = idpConnectionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map> actions;

		@GraphQLField
		protected java.util.Collection<IdpConnection> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProviderConfigurationPage")
	public class ProviderConfigurationPage {

		public ProviderConfigurationPage(Page providerConfigurationPage) {
			actions = providerConfigurationPage.getActions();

			items = providerConfigurationPage.getItems();
			lastPage = providerConfigurationPage.getLastPage();
			page = providerConfigurationPage.getPage();
			pageSize = providerConfigurationPage.getPageSize();
			totalCount = providerConfigurationPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map> actions;

		@GraphQLField
		protected java.util.Collection<ProviderConfiguration> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(
			IdpConnectionResource idpConnectionResource)
		throws Exception {

		idpConnectionResource.setContextAcceptLanguage(_acceptLanguage);
		idpConnectionResource.setContextCompany(_company);
		idpConnectionResource.setContextHttpServletRequest(_httpServletRequest);
		idpConnectionResource.setContextHttpServletResponse(
			_httpServletResponse);
		idpConnectionResource.setContextUriInfo(_uriInfo);
		idpConnectionResource.setContextUser(_user);
		idpConnectionResource.setGroupLocalService(_groupLocalService);
		idpConnectionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ProviderConfigurationResource providerConfigurationResource)
		throws Exception {

		providerConfigurationResource.setContextAcceptLanguage(_acceptLanguage);
		providerConfigurationResource.setContextCompany(_company);
		providerConfigurationResource.setContextHttpServletRequest(
			_httpServletRequest);
		providerConfigurationResource.setContextHttpServletResponse(
			_httpServletResponse);
		providerConfigurationResource.setContextUriInfo(_uriInfo);
		providerConfigurationResource.setContextUser(_user);
		providerConfigurationResource.setGroupLocalService(_groupLocalService);
		providerConfigurationResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<IdpConnectionResource>
		_idpConnectionResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProviderConfigurationResource>
		_providerConfigurationResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction<Object, String, Filter> _filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, Sort[]> _sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}