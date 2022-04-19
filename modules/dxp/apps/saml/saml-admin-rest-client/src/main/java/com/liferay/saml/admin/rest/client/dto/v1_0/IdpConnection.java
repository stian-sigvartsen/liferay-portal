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
import com.liferay.saml.admin.rest.client.serdes.v1_0.IdpConnectionSerDes;

import java.io.Serializable;

import java.util.Objects;

import javax.annotation.Generated;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
public class IdpConnection implements Cloneable, Serializable {

	public static IdpConnection toDTO(String json) {
		return IdpConnectionSerDes.toDTO(json);
	}

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

	public Boolean getForceAuthn() {
		return forceAuthn;
	}

	public void setForceAuthn(Boolean forceAuthn) {
		this.forceAuthn = forceAuthn;
	}

	public void setForceAuthn(
		UnsafeSupplier<Boolean, Exception> forceAuthnUnsafeSupplier) {

		try {
			forceAuthn = forceAuthnUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean forceAuthn;

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

	public Boolean getUnknownUsersAreStrangers() {
		return unknownUsersAreStrangers;
	}

	public void setUnknownUsersAreStrangers(Boolean unknownUsersAreStrangers) {
		this.unknownUsersAreStrangers = unknownUsersAreStrangers;
	}

	public void setUnknownUsersAreStrangers(
		UnsafeSupplier<Boolean, Exception>
			unknownUsersAreStrangersUnsafeSupplier) {

		try {
			unknownUsersAreStrangers =
				unknownUsersAreStrangersUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean unknownUsersAreStrangers;

	public String getUserAttributeMappings() {
		return userAttributeMappings;
	}

	public void setUserAttributeMappings(String userAttributeMappings) {
		this.userAttributeMappings = userAttributeMappings;
	}

	public void setUserAttributeMappings(
		UnsafeSupplier<String, Exception> userAttributeMappingsUnsafeSupplier) {

		try {
			userAttributeMappings = userAttributeMappingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String userAttributeMappings;

	@Override
	public IdpConnection clone() throws CloneNotSupportedException {
		return (IdpConnection)super.clone();
	}

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
		return IdpConnectionSerDes.toJSON(this);
	}

}