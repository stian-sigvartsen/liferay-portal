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

package com.liferay.saml.admin.rest.internal.resource.v1_0;

import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.saml.admin.rest.dto.v1_0.Idp;
import com.liferay.saml.admin.rest.dto.v1_0.IdpConnection;
import com.liferay.saml.admin.rest.dto.v1_0.Provider;
import com.liferay.saml.admin.rest.dto.v1_0.Sp;
import com.liferay.saml.admin.rest.resource.v1_0.IdpConnectionResource;
import com.liferay.saml.admin.rest.resource.v1_0.ProviderResource;

import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import java.util.Collection;

/**
 * @author Stian Sigvartsen
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/provider.properties",
	scope = ServiceScope.PROTOTYPE, service = ProviderResource.class
)
public class ProviderResourceImpl extends BaseProviderResourceImpl {
	@Override
	public Provider getProvider() throws Exception {

		SamlProviderConfiguration samlProviderConfiguration =
			_samlProviderConfigurationHelper.getSamlProviderConfiguration();

		Provider Provider = new Provider();

		Provider.setEnabled(samlProviderConfiguration.enabled());
		Provider.setEntityId(samlProviderConfiguration.entityId());

		Provider.setIdp(_getIdp(samlProviderConfiguration));
		Provider.setSp(_getSp(samlProviderConfiguration));

		return Provider;
	}

	@Override
	public Object getProvider(String roleId) throws Exception {

		SamlProviderConfiguration samlProviderConfiguration =
			_samlProviderConfigurationHelper.getSamlProviderConfiguration();

		if ("sp".equals(roleId)) {
			return _getSp(samlProviderConfiguration);
		}
		else if ("idp".equals(roleId)) {
			return _getIdp(samlProviderConfiguration);
		}

		return null;
	}

	private Idp _getIdp(SamlProviderConfiguration samlProviderConfiguration) {

		Idp idp = new Idp();

		idp.setAuthnRequestSignatureRequired(
			samlProviderConfiguration.authnRequestSignatureRequired());
		idp.setSessionMaximumAge(samlProviderConfiguration.sessionMaximumAge());
		idp.setSessionTimeout(samlProviderConfiguration.sessionTimeout());
		idp.setSignMetadata(samlProviderConfiguration.signMetadata());
		idp.setSslRequired(samlProviderConfiguration.sslRequired());

		return idp;
	}

	private Sp _getSp(SamlProviderConfiguration samlProviderConfiguration)
		throws Exception {

		Sp sp = new Sp();

		sp.setAllowShowingTheLoginPortlet(samlProviderConfiguration.allowShowingTheLoginPortlet());
		sp.setAssertionSignatureRequired(samlProviderConfiguration.assertionSignatureRequired());
		sp.setClockSkew(samlProviderConfiguration.clockSkew());
		sp.setDefaultAssertionLifetime(
			samlProviderConfiguration.defaultAssertionLifetime());
		//sp.setKeyStoreCredentialPassword(samlProviderConfiguration.keyStoreCredentialPassword());
		sp.setLdapImportEnabled(samlProviderConfiguration.ldapImportEnabled());
		sp.setSignAuthnRequest(samlProviderConfiguration.signAuthnRequest());
		sp.setSignMetadata(samlProviderConfiguration.signMetadata());
		sp.setSslRequired(samlProviderConfiguration.sslRequired());

		Page<IdpConnection> page =
			_idpConnectionResource.getIdpConnections(Pagination.of(-1, -1));

		Collection<IdpConnection> collection = page.getItems();

		sp.setIdpConnections(collection.toArray(new IdpConnection[0]));

		return sp;
	}

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	@Reference
	private IdpConnectionResource _idpConnectionResource;
}