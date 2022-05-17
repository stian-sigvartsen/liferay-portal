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

package com.liferay.saml.admin.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Generated;

import javax.validation.Valid;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Configuration of the SAML provider's SP capability",
	value = "SpProviderConfiguration"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SpProviderConfiguration")
public class SpProviderConfiguration implements Serializable {

	public static SpProviderConfiguration toDTO(String json) {
		return ObjectMapperUtil.readValue(SpProviderConfiguration.class, json);
	}

	public static SpProviderConfiguration unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			SpProviderConfiguration.class, json);
	}

	@Schema
	public Boolean getAllowShowingTheLoginPortlet() {
		return allowShowingTheLoginPortlet;
	}

	public void setAllowShowingTheLoginPortlet(
		Boolean allowShowingTheLoginPortlet) {

		this.allowShowingTheLoginPortlet = allowShowingTheLoginPortlet;
	}

	@JsonIgnore
	public void setAllowShowingTheLoginPortlet(
		UnsafeSupplier<Boolean, Exception>
			allowShowingTheLoginPortletUnsafeSupplier) {

		try {
			allowShowingTheLoginPortlet =
				allowShowingTheLoginPortletUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean allowShowingTheLoginPortlet;

	@Schema
	public Boolean getAssertionSignatureRequired() {
		return assertionSignatureRequired;
	}

	public void setAssertionSignatureRequired(
		Boolean assertionSignatureRequired) {

		this.assertionSignatureRequired = assertionSignatureRequired;
	}

	@JsonIgnore
	public void setAssertionSignatureRequired(
		UnsafeSupplier<Boolean, Exception>
			assertionSignatureRequiredUnsafeSupplier) {

		try {
			assertionSignatureRequired =
				assertionSignatureRequiredUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean assertionSignatureRequired;

	@Schema
	public Boolean getAuthnRequestSignatureRequired() {
		return authnRequestSignatureRequired;
	}

	public void setAuthnRequestSignatureRequired(
		Boolean authnRequestSignatureRequired) {

		this.authnRequestSignatureRequired = authnRequestSignatureRequired;
	}

	@JsonIgnore
	public void setAuthnRequestSignatureRequired(
		UnsafeSupplier<Boolean, Exception>
			authnRequestSignatureRequiredUnsafeSupplier) {

		try {
			authnRequestSignatureRequired =
				authnRequestSignatureRequiredUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean authnRequestSignatureRequired;

	@Schema
	public Long getClockSkew() {
		return clockSkew;
	}

	public void setClockSkew(Long clockSkew) {
		this.clockSkew = clockSkew;
	}

	@JsonIgnore
	public void setClockSkew(
		UnsafeSupplier<Long, Exception> clockSkewUnsafeSupplier) {

		try {
			clockSkew = clockSkewUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long clockSkew;

	@Schema
	public Integer getDefaultAssertionLifetime() {
		return defaultAssertionLifetime;
	}

	public void setDefaultAssertionLifetime(Integer defaultAssertionLifetime) {
		this.defaultAssertionLifetime = defaultAssertionLifetime;
	}

	@JsonIgnore
	public void setDefaultAssertionLifetime(
		UnsafeSupplier<Integer, Exception>
			defaultAssertionLifetimeUnsafeSupplier) {

		try {
			defaultAssertionLifetime =
				defaultAssertionLifetimeUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer defaultAssertionLifetime;

	@Schema
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@JsonIgnore
	public void setEnabled(
		UnsafeSupplier<Boolean, Exception> enabledUnsafeSupplier) {

		try {
			enabled = enabledUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean enabled;

	@Schema
	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	@JsonIgnore
	public void setEntityId(
		UnsafeSupplier<String, Exception> entityIdUnsafeSupplier) {

		try {
			entityId = entityIdUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String entityId;

	@Schema(description = "Connected SAML IDPs")
	@Valid
	public IdpConnection[] getIdpConnections() {
		return idpConnections;
	}

	public void setIdpConnections(IdpConnection[] idpConnections) {
		this.idpConnections = idpConnections;
	}

	@JsonIgnore
	public void setIdpConnections(
		UnsafeSupplier<IdpConnection[], Exception>
			idpConnectionsUnsafeSupplier) {

		try {
			idpConnections = idpConnectionsUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField(description = "Connected SAML IDPs")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected IdpConnection[] idpConnections;

	@Schema
	public String getKeyStoreCredentialPassword() {
		return keyStoreCredentialPassword;
	}

	public void setKeyStoreCredentialPassword(
		String keyStoreCredentialPassword) {

		this.keyStoreCredentialPassword = keyStoreCredentialPassword;
	}

	@JsonIgnore
	public void setKeyStoreCredentialPassword(
		UnsafeSupplier<String, Exception>
			keyStoreCredentialPasswordUnsafeSupplier) {

		try {
			keyStoreCredentialPassword =
				keyStoreCredentialPasswordUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String keyStoreCredentialPassword;

	@Schema
	public Boolean getLdapImportEnabled() {
		return ldapImportEnabled;
	}

	public void setLdapImportEnabled(Boolean ldapImportEnabled) {
		this.ldapImportEnabled = ldapImportEnabled;
	}

	@JsonIgnore
	public void setLdapImportEnabled(
		UnsafeSupplier<Boolean, Exception> ldapImportEnabledUnsafeSupplier) {

		try {
			ldapImportEnabled = ldapImportEnabledUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean ldapImportEnabled;

	@Schema
	@Valid
	public Role getRole() {
		return role;
	}

	@JsonIgnore
	public String getRoleAsString() {
		if (role == null) {
			return null;
		}

		return role.toString();
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@JsonIgnore
	public void setRole(UnsafeSupplier<Role, Exception> roleUnsafeSupplier) {
		try {
			role = roleUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Role role;

	@Schema
	public String getSamlKeystoreCredentialPassword() {
		return samlKeystoreCredentialPassword;
	}

	public void setSamlKeystoreCredentialPassword(
		String samlKeystoreCredentialPassword) {

		this.samlKeystoreCredentialPassword = samlKeystoreCredentialPassword;
	}

	@JsonIgnore
	public void setSamlKeystoreCredentialPassword(
		UnsafeSupplier<String, Exception>
			samlKeystoreCredentialPasswordUnsafeSupplier) {

		try {
			samlKeystoreCredentialPassword =
				samlKeystoreCredentialPasswordUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String samlKeystoreCredentialPassword;

	@Schema
	public Long getSessionMaximumAge() {
		return sessionMaximumAge;
	}

	public void setSessionMaximumAge(Long sessionMaximumAge) {
		this.sessionMaximumAge = sessionMaximumAge;
	}

	@JsonIgnore
	public void setSessionMaximumAge(
		UnsafeSupplier<Long, Exception> sessionMaximumAgeUnsafeSupplier) {

		try {
			sessionMaximumAge = sessionMaximumAgeUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long sessionMaximumAge;

	@Schema
	public Long getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(Long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	@JsonIgnore
	public void setSessionTimeout(
		UnsafeSupplier<Long, Exception> sessionTimeoutUnsafeSupplier) {

		try {
			sessionTimeout = sessionTimeoutUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long sessionTimeout;

	@Schema
	public Boolean getSignAuthnRequest() {
		return signAuthnRequest;
	}

	public void setSignAuthnRequest(Boolean signAuthnRequest) {
		this.signAuthnRequest = signAuthnRequest;
	}

	@JsonIgnore
	public void setSignAuthnRequest(
		UnsafeSupplier<Boolean, Exception> signAuthnRequestUnsafeSupplier) {

		try {
			signAuthnRequest = signAuthnRequestUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean signAuthnRequest;

	@Schema
	public Boolean getSignMetadata() {
		return signMetadata;
	}

	public void setSignMetadata(Boolean signMetadata) {
		this.signMetadata = signMetadata;
	}

	@JsonIgnore
	public void setSignMetadata(
		UnsafeSupplier<Boolean, Exception> signMetadataUnsafeSupplier) {

		try {
			signMetadata = signMetadataUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean signMetadata;

	@Schema
	public Boolean getSslRequired() {
		return sslRequired;
	}

	public void setSslRequired(Boolean sslRequired) {
		this.sslRequired = sslRequired;
	}

	@JsonIgnore
	public void setSslRequired(
		UnsafeSupplier<Boolean, Exception> sslRequiredUnsafeSupplier) {

		try {
			sslRequired = sslRequiredUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean sslRequired;

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
		StringBundler sb = new StringBundler();

		sb.append("{");

		if (allowShowingTheLoginPortlet != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allowShowingTheLoginPortlet\": ");

			sb.append(allowShowingTheLoginPortlet);
		}

		if (assertionSignatureRequired != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assertionSignatureRequired\": ");

			sb.append(assertionSignatureRequired);
		}

		if (authnRequestSignatureRequired != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authnRequestSignatureRequired\": ");

			sb.append(authnRequestSignatureRequired);
		}

		if (clockSkew != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clockSkew\": ");

			sb.append(clockSkew);
		}

		if (defaultAssertionLifetime != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultAssertionLifetime\": ");

			sb.append(defaultAssertionLifetime);
		}

		if (enabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enabled\": ");

			sb.append(enabled);
		}

		if (entityId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entityId\": ");

			sb.append("\"");

			sb.append(_escape(entityId));

			sb.append("\"");
		}

		if (idpConnections != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"idpConnections\": ");

			sb.append("[");

			for (int i = 0; i < idpConnections.length; i++) {
				sb.append(String.valueOf(idpConnections[i]));

				if ((i + 1) < idpConnections.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (keyStoreCredentialPassword != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keyStoreCredentialPassword\": ");

			sb.append("\"");

			sb.append(_escape(keyStoreCredentialPassword));

			sb.append("\"");
		}

		if (ldapImportEnabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ldapImportEnabled\": ");

			sb.append(ldapImportEnabled);
		}

		if (role != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"role\": ");

			sb.append("\"");

			sb.append(role);

			sb.append("\"");
		}

		if (samlKeystoreCredentialPassword != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"samlKeystoreCredentialPassword\": ");

			sb.append("\"");

			sb.append(_escape(samlKeystoreCredentialPassword));

			sb.append("\"");
		}

		if (sessionMaximumAge != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sessionMaximumAge\": ");

			sb.append(sessionMaximumAge);
		}

		if (sessionTimeout != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sessionTimeout\": ");

			sb.append(sessionTimeout);
		}

		if (signAuthnRequest != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"signAuthnRequest\": ");

			sb.append(signAuthnRequest);
		}

		if (signMetadata != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"signMetadata\": ");

			sb.append(signMetadata);
		}

		if (sslRequired != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sslRequired\": ");

			sb.append(sslRequired);
		}

		sb.append("}");

		return sb.toString();
	}

	@Schema(
		accessMode = Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.saml.admin.rest.dto.v1_0.SpProviderConfiguration",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Role")
	public static enum Role {

		SP("sp");

		@JsonCreator
		public static Role create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Role role : values()) {
				if (Objects.equals(role.getValue(), value)) {
					return role;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
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

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

}