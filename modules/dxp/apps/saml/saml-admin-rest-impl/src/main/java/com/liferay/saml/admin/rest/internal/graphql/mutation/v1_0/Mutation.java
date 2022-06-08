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

package com.liferay.saml.admin.rest.internal.graphql.mutation.v1_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.saml.admin.rest.dto.v1_0.IdpConnection;
import com.liferay.saml.admin.rest.dto.v1_0.Metadata;
import com.liferay.saml.admin.rest.dto.v1_0.SamlProvider;
import com.liferay.saml.admin.rest.resource.v1_0.IdpConnectionResource;
import com.liferay.saml.admin.rest.resource.v1_0.MetadataResource;
import com.liferay.saml.admin.rest.resource.v1_0.SamlProviderResource;

import java.util.function.BiFunction;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setIdpConnectionResourceComponentServiceObjects(
		ComponentServiceObjects<IdpConnectionResource>
			idpConnectionResourceComponentServiceObjects) {

		_idpConnectionResourceComponentServiceObjects =
			idpConnectionResourceComponentServiceObjects;
	}

	public static void setMetadataResourceComponentServiceObjects(
		ComponentServiceObjects<MetadataResource>
			metadataResourceComponentServiceObjects) {

		_metadataResourceComponentServiceObjects =
			metadataResourceComponentServiceObjects;
	}

	public static void setSamlProviderResourceComponentServiceObjects(
		ComponentServiceObjects<SamlProviderResource>
			samlProviderResourceComponentServiceObjects) {

		_samlProviderResourceComponentServiceObjects =
			samlProviderResourceComponentServiceObjects;
	}

	@GraphQLField(description = "Creates a new SAML IDP connection")
	public IdpConnection createIdpConnection(
			@GraphQLName("idpConnection") IdpConnection idpConnection)
		throws Exception {

		return _applyComponentServiceObjects(
			_idpConnectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			idpConnectionResource -> idpConnectionResource.postIdpConnection(
				idpConnection));
	}

	@GraphQLField
	public Response createIdpConnectionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_idpConnectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			idpConnectionResource ->
				idpConnectionResource.postIdpConnectionBatch(
					callbackURL, object));
	}

	@GraphQLField(description = "Deletes the SAML IDP connection")
	public boolean deleteIdpConnection(
			@GraphQLName("idpConnectionId") Long idpConnectionId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_idpConnectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			idpConnectionResource -> idpConnectionResource.deleteIdpConnection(
				idpConnectionId));

		return true;
	}

	@GraphQLField
	public Response deleteIdpConnectionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_idpConnectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			idpConnectionResource ->
				idpConnectionResource.deleteIdpConnectionBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Updates the SAML IDP Connection with information sent in the request body. Only the provided fields are updated."
	)
	public IdpConnection patchIdpConnection(
			@GraphQLName("idpConnectionId") Long idpConnectionId,
			@GraphQLName("idpConnection") IdpConnection idpConnection)
		throws Exception {

		return _applyComponentServiceObjects(
			_idpConnectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			idpConnectionResource -> idpConnectionResource.patchIdpConnection(
				idpConnectionId, idpConnection));
	}

	@GraphQLField(
		description = "Replaces the SAML IDP connection with information sent in the request body. Any missing fields are deleted unless they are required."
	)
	public IdpConnection updateIdpConnection(
			@GraphQLName("idpConnectionId") Long idpConnectionId,
			@GraphQLName("idpConnection") IdpConnection idpConnection)
		throws Exception {

		return _applyComponentServiceObjects(
			_idpConnectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			idpConnectionResource -> idpConnectionResource.putIdpConnection(
				idpConnectionId, idpConnection));
	}

	@GraphQLField
	public Response updateIdpConnectionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_idpConnectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			idpConnectionResource ->
				idpConnectionResource.putIdpConnectionBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Creates a new metadata for an existing IDP connection. The request body must be `multipart/form-data` with two parts, a `file` part with the file's bytes, and an optional JSON string (`Metadata`) with the metadata."
	)
	@GraphQLName(
		description = "Creates a new metadata for an existing IDP connection. The request body must be `multipart/form-data` with two parts, a `file` part with the file's bytes, and an optional JSON string (`Metadata`) with the metadata.",
		value = "postIdpConnectionMetadataIdpConnectionIdMultipartBody"
	)
	public Metadata createIdpConnectionMetadata(
			@GraphQLName("idpConnectionId") Long idpConnectionId,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_metadataResourceComponentServiceObjects,
			this::_populateResourceContext,
			metadataResource -> metadataResource.postIdpConnectionMetadata(
				idpConnectionId, multipartBody));
	}

	@GraphQLField
	public Response createIdpConnectionMetadataBatch(
			@GraphQLName("idpConnectionId") Long idpConnectionId,
			@GraphQLName("multipartBody") MultipartBody multipartBody,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_metadataResourceComponentServiceObjects,
			this::_populateResourceContext,
			metadataResource -> metadataResource.postIdpConnectionMetadataBatch(
				idpConnectionId, multipartBody, callbackURL, object));
	}

	@GraphQLField(description = "Patch the SAML Provider configuration.")
	public SamlProvider patchSamlProvider(
			@GraphQLName("samlProvider") SamlProvider samlProvider)
		throws Exception {

		return _applyComponentServiceObjects(
			_samlProviderResourceComponentServiceObjects,
			this::_populateResourceContext,
			samlProviderResource -> samlProviderResource.patchSamlProvider(
				samlProvider));
	}

	@GraphQLField(
		description = "Creates a full SAML Provider configuration with peer connections."
	)
	public SamlProvider createSamlProvider(
			@GraphQLName("samlProvider") SamlProvider samlProvider)
		throws Exception {

		return _applyComponentServiceObjects(
			_samlProviderResourceComponentServiceObjects,
			this::_populateResourceContext,
			samlProviderResource -> samlProviderResource.postSamlProvider(
				samlProvider));
	}

	@GraphQLField
	public Response createSamlProviderBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_samlProviderResourceComponentServiceObjects,
			this::_populateResourceContext,
			samlProviderResource -> samlProviderResource.postSamlProviderBatch(
				callbackURL, object));
	}

	@GraphQLField(description = "Delete the SAML SP Provider configuration.")
	public boolean deleteRole(@GraphQLName("roleId") String roleId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_samlProviderResourceComponentServiceObjects,
			this::_populateResourceContext,
			samlProviderResource -> samlProviderResource.deleteRole(roleId));

		return true;
	}

	@GraphQLField(description = "Patch the SAML SP Provider configuration.")
	public Response patchRole(
			@GraphQLName("roleId") String roleId,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_samlProviderResourceComponentServiceObjects,
			this::_populateResourceContext,
			samlProviderResource -> samlProviderResource.patchRole(
				roleId, object));
	}

	@GraphQLField(description = "Replaces the SAML SP Provider configuration.")
	public Response updateRole(
			@GraphQLName("roleId") String roleId,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_samlProviderResourceComponentServiceObjects,
			this::_populateResourceContext,
			samlProviderResource -> samlProviderResource.putRole(
				roleId, object));
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

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
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

		idpConnectionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(MetadataResource metadataResource)
		throws Exception {

		metadataResource.setContextAcceptLanguage(_acceptLanguage);
		metadataResource.setContextCompany(_company);
		metadataResource.setContextHttpServletRequest(_httpServletRequest);
		metadataResource.setContextHttpServletResponse(_httpServletResponse);
		metadataResource.setContextUriInfo(_uriInfo);
		metadataResource.setContextUser(_user);
		metadataResource.setGroupLocalService(_groupLocalService);
		metadataResource.setRoleLocalService(_roleLocalService);

		metadataResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			SamlProviderResource samlProviderResource)
		throws Exception {

		samlProviderResource.setContextAcceptLanguage(_acceptLanguage);
		samlProviderResource.setContextCompany(_company);
		samlProviderResource.setContextHttpServletRequest(_httpServletRequest);
		samlProviderResource.setContextHttpServletResponse(
			_httpServletResponse);
		samlProviderResource.setContextUriInfo(_uriInfo);
		samlProviderResource.setContextUser(_user);
		samlProviderResource.setGroupLocalService(_groupLocalService);
		samlProviderResource.setRoleLocalService(_roleLocalService);

		samlProviderResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<IdpConnectionResource>
		_idpConnectionResourceComponentServiceObjects;
	private static ComponentServiceObjects<MetadataResource>
		_metadataResourceComponentServiceObjects;
	private static ComponentServiceObjects<SamlProviderResource>
		_samlProviderResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, Sort[]> _sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}