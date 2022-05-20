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

import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.saml.admin.rest.dto.v1_0.Idp;
import com.liferay.saml.admin.rest.dto.v1_0.IdpConnection;
import com.liferay.saml.admin.rest.dto.v1_0.Provider;
import com.liferay.saml.admin.rest.dto.v1_0.Sp;
import com.liferay.saml.admin.rest.dto.v1_0.SpConnection;
import com.liferay.saml.admin.rest.resource.v1_0.ProviderResource;

import com.liferay.saml.persistence.model.SamlIdpSpConnection;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlIdpSpConnectionLocalService;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Stian Sigvartsen
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/provider.properties",
	scope = ServiceScope.PROTOTYPE, service = ProviderResource.class
)
public class ProviderResourceImpl extends BaseProviderResourceImpl {

	@Override
	public Page<Provider> read(
		Filter filter, Pagination pagination, Sort[] sorts,
		Map<String, Serializable> parameters, String search) throws Exception {

		return Page.of(Collections.singleton(getProvider()));
	}

	@Override
	public Provider postProvider(Provider provider) throws Exception {
		return super.postProvider(provider);
	}

	@Override
	public Provider patchProvider(Provider provider) throws Exception {
		return super.patchProvider(provider);
	}

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

	private Idp _getIdp(SamlProviderConfiguration samlProviderConfiguration)
		throws Exception {

		Idp idp = new Idp();

		idp.setAuthnRequestSignatureRequired(
			samlProviderConfiguration.authnRequestSignatureRequired());
		idp.setSessionMaximumAge(samlProviderConfiguration.sessionMaximumAge());
		idp.setSessionTimeout(samlProviderConfiguration.sessionTimeout());
		idp.setSignMetadata(samlProviderConfiguration.signMetadata());
		idp.setSslRequired(samlProviderConfiguration.sslRequired());

		idp.setSpConnections(
			_convert(
				() -> _samlIdpSpConnectionLocalService.getSamlIdpSpConnections(
					contextCompany.getCompanyId()),
				SamlIdpSpConnection::getSamlIdpSpConnectionId,
				(DTOConverter<?, SpConnection>)
					_dtoConverterRegistry.getDTOConverter(
						SamlIdpSpConnection.class.getName()),
				SpConnection[]::new));

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

		sp.setIdpConnections(
			_convert(
				() -> _samlSpIdpConnectionLocalService.getSamlSpIdpConnections(
					contextCompany.getCompanyId()),
				SamlSpIdpConnection::getSamlSpIdpConnectionId,
				(DTOConverter<?, IdpConnection>)
					_dtoConverterRegistry.getDTOConverter(
						SamlSpIdpConnection.class.getName()),
				IdpConnection[]::new));

		return sp;
	}

	private <T, V> V[] _convert(
		Supplier<List<T>> supplier, Function<T, Object> idFunction,
			DTOConverter<?, V> dtoConverter,
			Function<Integer, V[]> arrayFunction)
		throws Exception {

		List<T> list = supplier.get();

		V[] array = arrayFunction.apply(list.size());
		int i = 0;

		for (T t : list) {
			array[i++] =
				dtoConverter.toDTO(
					new DefaultDTOConverterContext(
						_dtoConverterRegistry,
						idFunction.apply(t),
						contextAcceptLanguage.getPreferredLocale(),
						contextUriInfo, contextUser));
		}

		return array;
	}

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	@Reference
	private SamlSpIdpConnectionLocalService _samlSpIdpConnectionLocalService;

	@Reference
	private SamlIdpSpConnectionLocalService _samlIdpSpConnectionLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;
}