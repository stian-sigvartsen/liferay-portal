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

package com.liferay.saml.admin.rest.internal.graphql.servlet.v1_0;

import com.liferay.portal.vulcan.graphql.servlet.ServletData;
import com.liferay.saml.admin.rest.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.saml.admin.rest.internal.graphql.query.v1_0.Query;
import com.liferay.saml.admin.rest.resource.v1_0.IdpConnectionResource;
import com.liferay.saml.admin.rest.resource.v1_0.MetadataResource;
import com.liferay.saml.admin.rest.resource.v1_0.ProviderConfigurationResource;

import javax.annotation.Generated;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Component(immediate = true, service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setIdpConnectionResourceComponentServiceObjects(
			_idpConnectionResourceComponentServiceObjects);
		Mutation.setMetadataResourceComponentServiceObjects(
			_metadataResourceComponentServiceObjects);

		Query.setIdpConnectionResourceComponentServiceObjects(
			_idpConnectionResourceComponentServiceObjects);
		Query.setProviderConfigurationResourceComponentServiceObjects(
			_providerConfigurationResourceComponentServiceObjects);
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/saml-admin-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<IdpConnectionResource>
		_idpConnectionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<MetadataResource>
		_metadataResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ProviderConfigurationResource>
		_providerConfigurationResourceComponentServiceObjects;

}