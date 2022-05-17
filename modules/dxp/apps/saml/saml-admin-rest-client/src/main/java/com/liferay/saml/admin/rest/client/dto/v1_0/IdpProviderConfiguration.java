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
import com.liferay.saml.admin.rest.client.serdes.v1_0.IdpProviderConfigurationSerDes;

import java.io.Serializable;

import java.util.Objects;

import javax.annotation.Generated;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
public class IdpProviderConfiguration implements Cloneable, Serializable {

	public static IdpProviderConfiguration toDTO(String json) {
		return IdpProviderConfigurationSerDes.toDTO(json);
	}

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

	public SpConnection[] getSpConnections() {
		return spConnections;
	}

	public void setSpConnections(SpConnection[] spConnections) {
		this.spConnections = spConnections;
	}

	public void setSpConnections(
		UnsafeSupplier<SpConnection[], Exception> spConnectionsUnsafeSupplier) {

		try {
			spConnections = spConnectionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected SpConnection[] spConnections;

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
	public IdpProviderConfiguration clone() throws CloneNotSupportedException {
		return (IdpProviderConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof IdpProviderConfiguration)) {
			return false;
		}

		IdpProviderConfiguration idpProviderConfiguration =
			(IdpProviderConfiguration)object;

		return Objects.equals(toString(), idpProviderConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return IdpProviderConfigurationSerDes.toJSON(this);
	}

	public static enum Role {

		IDP("idp");

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