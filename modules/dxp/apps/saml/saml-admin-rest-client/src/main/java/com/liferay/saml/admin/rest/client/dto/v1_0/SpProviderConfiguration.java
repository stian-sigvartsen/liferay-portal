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

package com.liferay.saml.admin.rest.client.dto.v1_0;

import com.liferay.saml.admin.rest.client.function.UnsafeSupplier;
import com.liferay.saml.admin.rest.client.serdes.v1_0.SpProviderConfigurationSerDes;

import java.io.Serializable;

import java.util.Objects;

import javax.annotation.Generated;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
public class SpProviderConfiguration implements Cloneable, Serializable {

	public static SpProviderConfiguration toDTO(String json) {
		return SpProviderConfigurationSerDes.toDTO(json);
	}

	public Boolean getAllowShowingTheLoginPortlet() {
		return allowShowingTheLoginPortlet;
	}

	public void setAllowShowingTheLoginPortlet(
		Boolean allowShowingTheLoginPortlet) {

		this.allowShowingTheLoginPortlet = allowShowingTheLoginPortlet;
	}

	public void setAllowShowingTheLoginPortlet(
		UnsafeSupplier<Boolean, Exception>
			allowShowingTheLoginPortletUnsafeSupplier) {

		try {
			allowShowingTheLoginPortlet =
				allowShowingTheLoginPortletUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean allowShowingTheLoginPortlet;

	public Boolean getAssertionSignatureRequired() {
		return assertionSignatureRequired;
	}

	public void setAssertionSignatureRequired(
		Boolean assertionSignatureRequired) {

		this.assertionSignatureRequired = assertionSignatureRequired;
	}

	public void setAssertionSignatureRequired(
		UnsafeSupplier<Boolean, Exception>
			assertionSignatureRequiredUnsafeSupplier) {

		try {
			assertionSignatureRequired =
				assertionSignatureRequiredUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean assertionSignatureRequired;

	public Boolean getAuthnRequestSignatureRequired() {
		return authnRequestSignatureRequired;
	}

	public void setAuthnRequestSignatureRequired(
		Boolean authnRequestSignatureRequired) {

		this.authnRequestSignatureRequired = authnRequestSignatureRequired;
	}

	public void setAuthnRequestSignatureRequired(
		UnsafeSupplier<Boolean, Exception>
			authnRequestSignatureRequiredUnsafeSupplier) {

		try {
			authnRequestSignatureRequired =
				authnRequestSignatureRequiredUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean authnRequestSignatureRequired;

	public Long getClockSkew() {
		return clockSkew;
	}

	public void setClockSkew(Long clockSkew) {
		this.clockSkew = clockSkew;
	}

	public void setClockSkew(
		UnsafeSupplier<Long, Exception> clockSkewUnsafeSupplier) {

		try {
			clockSkew = clockSkewUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long clockSkew;

	public Integer getDefaultAssertionLifetime() {
		return defaultAssertionLifetime;
	}

	public void setDefaultAssertionLifetime(Integer defaultAssertionLifetime) {
		this.defaultAssertionLifetime = defaultAssertionLifetime;
	}

	public void setDefaultAssertionLifetime(
		UnsafeSupplier<Integer, Exception>
			defaultAssertionLifetimeUnsafeSupplier) {

		try {
			defaultAssertionLifetime =
				defaultAssertionLifetimeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer defaultAssertionLifetime;

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setEnabled(
		UnsafeSupplier<Boolean, Exception> enabledUnsafeSupplier) {

		try {
			enabled = enabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean enabled;

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public void setEntityId(
		UnsafeSupplier<String, Exception> entityIdUnsafeSupplier) {

		try {
			entityId = entityIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String entityId;

	public IdpConnection[] getIdpConnections() {
		return idpConnections;
	}

	public void setIdpConnections(IdpConnection[] idpConnections) {
		this.idpConnections = idpConnections;
	}

	public void setIdpConnections(
		UnsafeSupplier<IdpConnection[], Exception>
			idpConnectionsUnsafeSupplier) {

		try {
			idpConnections = idpConnectionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected IdpConnection[] idpConnections;

	public String getKeyStoreCredentialPassword() {
		return keyStoreCredentialPassword;
	}

	public void setKeyStoreCredentialPassword(
		String keyStoreCredentialPassword) {

		this.keyStoreCredentialPassword = keyStoreCredentialPassword;
	}

	public void setKeyStoreCredentialPassword(
		UnsafeSupplier<String, Exception>
			keyStoreCredentialPasswordUnsafeSupplier) {

		try {
			keyStoreCredentialPassword =
				keyStoreCredentialPasswordUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String keyStoreCredentialPassword;

	public Boolean getLdapImportEnabled() {
		return ldapImportEnabled;
	}

	public void setLdapImportEnabled(Boolean ldapImportEnabled) {
		this.ldapImportEnabled = ldapImportEnabled;
	}

	public void setLdapImportEnabled(
		UnsafeSupplier<Boolean, Exception> ldapImportEnabledUnsafeSupplier) {

		try {
			ldapImportEnabled = ldapImportEnabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean ldapImportEnabled;

	public Role getRole() {
		return role;
	}

	public String getRoleAsString() {
		if (role == null) {
			return null;
		}

		return role.toString();
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setRole(UnsafeSupplier<Role, Exception> roleUnsafeSupplier) {
		try {
			role = roleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Role role;

	public String getSamlKeystoreCredentialPassword() {
		return samlKeystoreCredentialPassword;
	}

	public void setSamlKeystoreCredentialPassword(
		String samlKeystoreCredentialPassword) {

		this.samlKeystoreCredentialPassword = samlKeystoreCredentialPassword;
	}

	public void setSamlKeystoreCredentialPassword(
		UnsafeSupplier<String, Exception>
			samlKeystoreCredentialPasswordUnsafeSupplier) {

		try {
			samlKeystoreCredentialPassword =
				samlKeystoreCredentialPasswordUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String samlKeystoreCredentialPassword;

	public Long getSessionMaximumAge() {
		return sessionMaximumAge;
	}

	public void setSessionMaximumAge(Long sessionMaximumAge) {
		this.sessionMaximumAge = sessionMaximumAge;
	}

	public void setSessionMaximumAge(
		UnsafeSupplier<Long, Exception> sessionMaximumAgeUnsafeSupplier) {

		try {
			sessionMaximumAge = sessionMaximumAgeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long sessionMaximumAge;

	public Long getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(Long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public void setSessionTimeout(
		UnsafeSupplier<Long, Exception> sessionTimeoutUnsafeSupplier) {

		try {
			sessionTimeout = sessionTimeoutUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long sessionTimeout;

	public Boolean getSignAuthnRequest() {
		return signAuthnRequest;
	}

	public void setSignAuthnRequest(Boolean signAuthnRequest) {
		this.signAuthnRequest = signAuthnRequest;
	}

	public void setSignAuthnRequest(
		UnsafeSupplier<Boolean, Exception> signAuthnRequestUnsafeSupplier) {

		try {
			signAuthnRequest = signAuthnRequestUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean signAuthnRequest;

	public Boolean getSignMetadata() {
		return signMetadata;
	}

	public void setSignMetadata(Boolean signMetadata) {
		this.signMetadata = signMetadata;
	}

	public void setSignMetadata(
		UnsafeSupplier<Boolean, Exception> signMetadataUnsafeSupplier) {

		try {
			signMetadata = signMetadataUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean signMetadata;

	public Boolean getSslRequired() {
		return sslRequired;
	}

	public void setSslRequired(Boolean sslRequired) {
		this.sslRequired = sslRequired;
	}

	public void setSslRequired(
		UnsafeSupplier<Boolean, Exception> sslRequiredUnsafeSupplier) {

		try {
			sslRequired = sslRequiredUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean sslRequired;

	@Override
	public SpProviderConfiguration clone() throws CloneNotSupportedException {
		return (SpProviderConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SpProviderConfiguration)) {
			return false;
		}

		SpProviderConfiguration spProviderConfiguration =
			(SpProviderConfiguration)object;

		return Objects.equals(toString(), spProviderConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SpProviderConfigurationSerDes.toJSON(this);
	}

	public static enum Role {

		SP("sp");

		public static Role create(String value) {
			for (Role role : values()) {
				if (Objects.equals(role.getValue(), value) ||
					Objects.equals(role.name(), value)) {

					return role;
				}
			}

			return null;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private Role(String value) {
			_value = value;
		}

		private final String _value;

	}

}