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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Generated;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Connection details for a connected SAML IDP",
	value = "IdpConnection"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "IdpConnection")
public class IdpConnection implements Serializable {

	public static IdpConnection toDTO(String json) {
		return ObjectMapperUtil.readValue(IdpConnection.class, json);
	}

	public static IdpConnection unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(IdpConnection.class, json);
	}

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

	@Schema
	public Boolean getForceAuthn() {
		return forceAuthn;
	}

	public void setForceAuthn(Boolean forceAuthn) {
		this.forceAuthn = forceAuthn;
	}

	@JsonIgnore
	public void setForceAuthn(
		UnsafeSupplier<Boolean, Exception> forceAuthnUnsafeSupplier) {

		try {
			forceAuthn = forceAuthnUnsafeSupplier.get();
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
	protected Boolean forceAuthn;

	@Schema
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
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
	protected Long id;

	@Schema
	public Date getMetadataUpdatedDate() {
		return metadataUpdatedDate;
	}

	public void setMetadataUpdatedDate(Date metadataUpdatedDate) {
		this.metadataUpdatedDate = metadataUpdatedDate;
	}

	@JsonIgnore
	public void setMetadataUpdatedDate(
		UnsafeSupplier<Date, Exception> metadataUpdatedDateUnsafeSupplier) {

		try {
			metadataUpdatedDate = metadataUpdatedDateUnsafeSupplier.get();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date metadataUpdatedDate;

	@Schema
	public String getMetadataUrl() {
		return metadataUrl;
	}

	public void setMetadataUrl(String metadataUrl) {
		this.metadataUrl = metadataUrl;
	}

	@JsonIgnore
	public void setMetadataUrl(
		UnsafeSupplier<String, Exception> metadataUrlUnsafeSupplier) {

		try {
			metadataUrl = metadataUrlUnsafeSupplier.get();
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
	protected String metadataUrl;

	@Schema
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
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
	protected String name;

	@Schema
	public String getNameIdFormat() {
		return nameIdFormat;
	}

	public void setNameIdFormat(String nameIdFormat) {
		this.nameIdFormat = nameIdFormat;
	}

	@JsonIgnore
	public void setNameIdFormat(
		UnsafeSupplier<String, Exception> nameIdFormatUnsafeSupplier) {

		try {
			nameIdFormat = nameIdFormatUnsafeSupplier.get();
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
	protected String nameIdFormat;

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
	public Boolean getUnknownUsersAreStrangers() {
		return unknownUsersAreStrangers;
	}

	public void setUnknownUsersAreStrangers(Boolean unknownUsersAreStrangers) {
		this.unknownUsersAreStrangers = unknownUsersAreStrangers;
	}

	@JsonIgnore
	public void setUnknownUsersAreStrangers(
		UnsafeSupplier<Boolean, Exception>
			unknownUsersAreStrangersUnsafeSupplier) {

		try {
			unknownUsersAreStrangers =
				unknownUsersAreStrangersUnsafeSupplier.get();
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
	protected Boolean unknownUsersAreStrangers;

	@Schema
	public String getUserAttributeMappings() {
		return userAttributeMappings;
	}

	public void setUserAttributeMappings(String userAttributeMappings) {
		this.userAttributeMappings = userAttributeMappings;
	}

	@JsonIgnore
	public void setUserAttributeMappings(
		UnsafeSupplier<String, Exception> userAttributeMappingsUnsafeSupplier) {

		try {
			userAttributeMappings = userAttributeMappingsUnsafeSupplier.get();
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
	protected String userAttributeMappings;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof IdpConnection)) {
			return false;
		}

		IdpConnection idpConnection = (IdpConnection)object;

		return Objects.equals(toString(), idpConnection.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		if (assertionSignatureRequired != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assertionSignatureRequired\": ");

			sb.append(assertionSignatureRequired);
		}

		if (clockSkew != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clockSkew\": ");

			sb.append(clockSkew);
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

		if (forceAuthn != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"forceAuthn\": ");

			sb.append(forceAuthn);
		}

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		if (metadataUpdatedDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metadataUpdatedDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(metadataUpdatedDate));

			sb.append("\"");
		}

		if (metadataUrl != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metadataUrl\": ");

			sb.append("\"");

			sb.append(_escape(metadataUrl));

			sb.append("\"");
		}

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		if (nameIdFormat != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nameIdFormat\": ");

			sb.append("\"");

			sb.append(_escape(nameIdFormat));

			sb.append("\"");
		}

		if (signAuthnRequest != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"signAuthnRequest\": ");

			sb.append(signAuthnRequest);
		}

		if (unknownUsersAreStrangers != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unknownUsersAreStrangers\": ");

			sb.append(unknownUsersAreStrangers);
		}

		if (userAttributeMappings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userAttributeMappings\": ");

			sb.append("\"");

			sb.append(_escape(userAttributeMappings));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@Schema(
		accessMode = Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.saml.admin.rest.dto.v1_0.IdpConnection",
		name = "x-class-name"
	)
	public String xClassName;

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