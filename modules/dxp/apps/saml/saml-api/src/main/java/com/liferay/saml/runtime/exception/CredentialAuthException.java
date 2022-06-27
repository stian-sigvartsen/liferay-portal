package com.liferay.saml.runtime.exception;

import com.liferay.saml.runtime.exception.EntityIdException;

import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;

public class CredentialAuthException extends GeneralSecurityException {

	public CredentialAuthException
		(GeneralSecurityException generalSecurityException) {
		super(generalSecurityException);

		Throwable throwable = _getCauseThrowable(
			generalSecurityException, KeyStoreException.class);

		if (throwable != null) {
			Throwable unrecoverableKeyThrowable =
				_getCauseThrowable(
					throwable, UnrecoverableKeyException.class);

			if (unrecoverableKeyThrowable != null) {
				_status = Status.KEYSTORE_PASSWORD_INCORRECT;
			}
			else {
				_status = Status.KEYSTORE_EXCEPTION;
			}
		}
		else {
			throwable = _getCauseThrowable(
				generalSecurityException, UnrecoverableKeyException.class);

			if (throwable != null) {
				_status = Status.CREDENTIAL_PASSWORD_INCORRECT;
			}
			else {
				throwable = _getCauseThrowable(
					generalSecurityException, EntityIdException.class);

				if (throwable != null) {
					_status = Status.NOT_FOUND;
				}
				else {
					_status = Status.UNKNOWN_EXCEPTION;
				}
			}
		}
	}

	public CredentialAuthException(Status status) {
		_status = status;
	}

	public Status getStatus() {
		return _status;
	}

	public enum Status {

		CREDENTIAL_PASSWORD_INCORRECT, KEYSTORE_EXCEPTION,
		KEYSTORE_PASSWORD_INCORRECT, UNKNOWN_EXCEPTION, NOT_FOUND
	}

	private Throwable _getCauseThrowable(
		Throwable throwable, Class<?> exceptionType) {

		if (throwable == null) {
			return null;
		}

		Throwable causeThrowable = throwable.getCause();

		while (causeThrowable != null) {
			if (exceptionType.isInstance(causeThrowable)) {
				return causeThrowable;
			}

			causeThrowable = causeThrowable.getCause();
		}

		return null;
	}

	@Override
	public String getMessage() {
		return _status.name();
	}

	private final Status _status;
}
