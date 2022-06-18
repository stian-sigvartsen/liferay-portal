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

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.saml.admin.rest.dto.v1_0.Idp;
import com.liferay.saml.admin.rest.dto.v1_0.IdpConnection;
import com.liferay.saml.admin.rest.dto.v1_0.SamlProvider;
import com.liferay.saml.admin.rest.dto.v1_0.Sp;
import com.liferay.saml.admin.rest.dto.v1_0.SpConnection;
import com.liferay.saml.admin.rest.resource.v1_0.SamlProviderResource;
import com.liferay.saml.constants.SamlProviderConfigurationKeys;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlIdpSpConnectionLocalService;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;
import com.liferay.saml.runtime.configuration.SamlConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.runtime.metadata.LocalEntityManager;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.saml.runtime.configuration.SamlConfiguration",
	properties = "OSGI-INF/liferay/rest/v1_0/saml-provider.properties",
	scope = ServiceScope.PROTOTYPE, service = SamlProviderResource.class
)
public class SamlProviderResourceImpl extends BaseSamlProviderResourceImpl {

	@Override
	public SamlProvider getSamlProvider() throws Exception {
		SamlProviderConfiguration samlProviderConfiguration =
			_samlProviderConfigurationHelper.getSamlProviderConfiguration();

		SamlProvider samlProvider = new SamlProvider();

		samlProvider.setEnabled(samlProviderConfiguration.enabled());
		samlProvider.setEntityId(samlProviderConfiguration.entityId());
		samlProvider.setSignMetadata(samlProviderConfiguration.signMetadata());
		samlProvider.setSslRequired(samlProviderConfiguration.sslRequired());

		String role = samlProviderConfiguration.role();

		if (SamlProviderConfigurationKeys.SAML_ROLE_SP.equals(role)) {
			samlProvider.setRole(SamlProvider.Role.SP);
			samlProvider.setSp(_getSp(samlProviderConfiguration));
		}
		else if (SamlProviderConfigurationKeys.SAML_ROLE_IDP.equals(role)) {
			samlProvider.setIdp(_getIdp(samlProviderConfiguration));
			samlProvider.setRole(SamlProvider.Role.IDP);
		}

		return samlProvider;
	}

	@Override
	public Object getRole(String roleId) throws Exception {
		SamlProviderConfiguration samlProviderConfiguration =
			_samlProviderConfigurationHelper.getSamlProviderConfiguration();

		if (Objects.equals(SamlProvider.Role.SP.getValue(), roleId)) {
			return _getSp(samlProviderConfiguration);
		}
		else if (Objects.equals(SamlProvider.Role.IDP.getValue(), roleId)) {
			return _getIdp(samlProviderConfiguration);
		}

		throw new PortalException("Unsupported role: " + roleId);
	}

	@Override
	public Response patchRole(String roleId, Object object) throws Exception {
		if (Objects.equals(SamlProvider.Role.IDP.getValue(), roleId) &&
			(object instanceof Idp)) {

			_setIdpProperties((Idp)object, new UnicodeProperties(), false);
		}
		else if (Objects.equals(SamlProvider.Role.SP.getValue(), roleId) &&
				 (object instanceof Sp)) {

			_setSpProperties((Sp)object, new UnicodeProperties(), false);
		}
		else {
			throw new PortalException("Unsupported role or invalid data");
		}

		return super.patchRole(roleId, object);
	}

	@Override
	public SamlProvider patchSamlProvider(SamlProvider samlProvider)
		throws Exception {

		return _updateSamlProvider(samlProvider, false);
	}

	@Override
	public SamlProvider postSamlProvider(SamlProvider samlProvider)
		throws Exception {

		return _updateSamlProvider(samlProvider, true);
	}

	@Override
	public Response putRole(String roleId, Object object) throws Exception {
		if (Objects.equals(SamlProvider.Role.IDP.getValue(), roleId) &&
			(object instanceof Idp)) {

			_setIdpProperties((Idp)object, new UnicodeProperties(), true);
		}
		else if (Objects.equals(SamlProvider.Role.SP.getValue(), roleId) &&
				 (object instanceof Sp)) {

			_setSpProperties((Sp)object, new UnicodeProperties(), true);
		}
		else {
			throw new PortalException("Unsupported role or invalid data");
		}

		return super.putRole(roleId, object);
	}

	@Override
	public Page<SamlProvider> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return Page.of(Collections.singleton(getSamlProvider()));
	}

	protected void activate(Map<String, Object> properties) {
		_samlConfiguration = ConfigurableUtil.createConfigurable(
			SamlConfiguration.class, properties);
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
			array[i++] = dtoConverter.toDTO(
				new DefaultDTOConverterContext(
					_dtoConverterRegistry, idFunction.apply(t),
					contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
					contextUser));
		}

		return array;
	}

	private Idp _getIdp(SamlProviderConfiguration samlProviderConfiguration)
		throws Exception {

		Idp idp = new Idp();

		idp.setAuthnRequestSignatureRequired(
			samlProviderConfiguration.authnRequestSignatureRequired());
		idp.setDefaultAssertionLifetime(
			samlProviderConfiguration.defaultAssertionLifetime());
		idp.setSessionMaximumAge(samlProviderConfiguration.sessionMaximumAge());
		idp.setSessionTimeout(samlProviderConfiguration.sessionTimeout());

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

		sp.setAllowShowingTheLoginPortlet(
			samlProviderConfiguration.allowShowingTheLoginPortlet());
		sp.setAssertionSignatureRequired(
			samlProviderConfiguration.assertionSignatureRequired());
		sp.setClockSkew(samlProviderConfiguration.clockSkew());
		//		sp.setKeyStoreCredentialPassword(
		//			samlProviderConfiguration.keyStoreCredentialPassword());
		sp.setLdapImportEnabled(samlProviderConfiguration.ldapImportEnabled());
		sp.setSignAuthnRequest(samlProviderConfiguration.signAuthnRequest());

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

	private void _setIdpProperties(
		Idp idp, UnicodeProperties unicodeProperties, boolean setNulls) {

		_setProperty(
			unicodeProperties, "saml.idp.assertion.lifetime",
			_toNullableString(idp.getDefaultAssertionLifetime()), setNulls);
		_setProperty(
			unicodeProperties, "saml.idp.authn.request.signature.required",
			_toNullableString(idp.getAuthnRequestSignatureRequired()),
			setNulls);
		_setProperty(
			unicodeProperties, "saml.idp.session.maximum.age",
			_toNullableString(idp.getSessionMaximumAge()), setNulls);
		_setProperty(
			unicodeProperties, "saml.idp.session.timeout",
			_toNullableString(idp.getSessionTimeout()), setNulls);

		unicodeProperties.put(
			"saml.role", SamlProviderConfigurationKeys.SAML_ROLE_IDP);
	}

	private void _setProperty(
		UnicodeProperties unicodeProperties, String key, String value,
		boolean setNulls) {

		if (value == null) {
			if (setNulls) {
				unicodeProperties.put(key, null);
			}

			return;
		}

		unicodeProperties.put(key, value);
	}

	private void _setSamlProviderProperties(
		SamlProvider samlProvider, UnicodeProperties unicodeProperties,
		boolean setNulls) {

		_setProperty(
			unicodeProperties, "saml.enabled",
			_toNullableString(samlProvider.getEnabled()), setNulls);

		_setProperty(
			unicodeProperties, "saml.entity.id", samlProvider.getEntityId(),
			setNulls);

		_setProperty(
			unicodeProperties, "saml.sign.metadata",
			_toNullableString(samlProvider.getSignMetadata()), setNulls);

		_setProperty(
			unicodeProperties, "saml.ssl.required",
			_toNullableString(samlProvider.getSslRequired()), setNulls);
	}

	private void _setSpProperties(
		Sp sp, UnicodeProperties unicodeProperties, boolean setNulls) {

		_setProperty(
			unicodeProperties, "saml.sp.allow.showing.the.login.portlet",
			_toNullableString(sp.getAllowShowingTheLoginPortlet()), setNulls);
		_setProperty(
			unicodeProperties, "saml.sp.assertion.signature.required",
			_toNullableString(sp.getAssertionSignatureRequired()), setNulls);
		_setProperty(
			unicodeProperties, "saml.sp.clock.skew",
			_toNullableString(sp.getClockSkew()), setNulls);
		_setProperty(
			unicodeProperties, "saml.sp.ldap.import.enabled",
			_toNullableString(sp.getLdapImportEnabled()), setNulls);
		_setProperty(
			unicodeProperties, "saml.sp.sign.authn.request",
			_toNullableString(sp.getSignAuthnRequest()), setNulls);

		unicodeProperties.put(
			"saml.role", SamlProviderConfigurationKeys.SAML_ROLE_SP);
	}

	private String _toNullableString(Object value) {
		if (value == null) {
			return null;
		}

		return String.valueOf(value);
	}

	private SamlProvider _updateSamlProvider(
			SamlProvider samlProvider, boolean setNulls)
		throws Exception {

		// SamlProviderConfiguration samlProviderConfiguration =

		//	_samlProviderConfigurationHelper.getSamlProviderConfiguration();

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.create(
			false
		).build();

		//unicodeProperties.put(
		//	"saml.keystore.credential.password",
		//	_toNullableString(
		//		samlProviderConfiguration.keyStoreCredentialPassword()));
		//unicodeProperties.put(
		//	"saml.keystore.encryption.credential.password",
		//	_toNullableString(
		//

		// 		samlProviderConfiguration.

		//
		//			keyStoreEncryptionCredentialPassword()));

		_setSamlProviderProperties(samlProvider, unicodeProperties, setNulls);

		String entityId = samlProvider.getEntityId();

		if (Validator.isNotNull(entityId) && (entityId.length() > 1024)) {
			throw new PortalException("EntityID too long (Max 1024 characters)");
		}

		if (GetterUtil.getBoolean(samlProvider.getEnabled()) &&
			(_localEntityManager.getLocalEntityCertificate() == null)) {

			throw new PortalException("certificateInvalid");
		}

		SamlProvider currentSamlProvider = getSamlProvider();

		if (samlProvider.getIdp() != null) {
			if (!_validateRoleSelection(
					samlProvider.getEnabled(),
					SamlProvider.Role.IDP.getValue())) {

				throw new PortalException(
					"The Identity Provider role has been disabled. It can be " +
						"re-enabled in system settings.");
			}

			if (samlProvider.getSp() != null
					|| !setNulls && (currentSamlProvider.getSp() != null)) {
				throw new PortalException("Can only configure one of sp & idp roles");
			}

			_setIdpProperties(
				samlProvider.getIdp(), unicodeProperties, setNulls);
		}
		else if (samlProvider.getSp() != null) {
			if (!setNulls && (currentSamlProvider.getIdp() != null)) {
				throw new PortalException("Can only configure one of sp & idp roles");
			}

			_setSpProperties(
				samlProvider.getSp(), unicodeProperties, setNulls);
		}
		else if (GetterUtil.getBoolean(samlProvider.getEnabled()) &&
				 (currentSamlProvider.getRole() == null)) {

			throw new PortalException("Cannot enable the provider without configuring its role");
		}

		_samlProviderConfigurationHelper.updateProperties(unicodeProperties);

		return getSamlProvider();
	}

	private boolean _validateRoleSelection(boolean enabled, String samlRole) {
		if (_samlConfiguration.idpRoleConfigurationEnabled()) {
			return true;
		}

		if (!_samlProviderConfigurationHelper.isRoleIdp() &&
			samlRole.equals(SamlProviderConfigurationKeys.SAML_ROLE_IDP)) {

			return false;
		}

		if (!_samlProviderConfigurationHelper.isEnabled() && enabled &&
			samlRole.equals(SamlProviderConfigurationKeys.SAML_ROLE_IDP)) {

			return false;
		}

		return true;
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private LocalEntityManager _localEntityManager;

	private SamlConfiguration _samlConfiguration;

	@Reference
	private SamlIdpSpConnectionLocalService _samlIdpSpConnectionLocalService;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	@Reference
	private SamlSpIdpConnectionLocalService _samlSpIdpConnectionLocalService;

}