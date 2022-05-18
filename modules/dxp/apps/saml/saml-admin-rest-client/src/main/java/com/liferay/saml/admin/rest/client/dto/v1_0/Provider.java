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
import com.liferay.saml.admin.rest.client.serdes.v1_0.ProviderSerDes;

import java.io.Serializable;

import java.util.Objects;

import javax.annotation.Generated;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
public class Provider implements Cloneable, Serializable {

	public static Provider toDTO(String json) {
		return ProviderSerDes.toDTO(json);
	}

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

	public Idp getIdp() {
		return idp;
	}

	public void setIdp(Idp idp) {
		this.idp = idp;
	}

	public void setIdp(UnsafeSupplier<Idp, Exception> idpUnsafeSupplier) {
		try {
			idp = idpUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Idp idp;

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

	public Sp getSp() {
		return sp;
	}

	public void setSp(Sp sp) {
		this.sp = sp;
	}

	public void setSp(UnsafeSupplier<Sp, Exception> spUnsafeSupplier) {
		try {
			sp = spUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Sp sp;

	@Override
	public Provider clone() throws CloneNotSupportedException {
		return (Provider)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Provider)) {
			return false;
		}

		Provider provider = (Provider)object;

		return Objects.equals(toString(), provider.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ProviderSerDes.toJSON(this);
	}

	public static enum Role {

		IDP("idp"), SP("sp");

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