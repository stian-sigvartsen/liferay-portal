package com.liferay.saml.runtime.credential;

import com.liferay.saml.runtime.metadata.LocalEntityManager;

import java.security.KeyStore;

public interface KeyStoreTool {

	public KeyStore.Entry getKeyStoreEntry(
			String entityId, KeyStore keyStore, String certificateKeyPassword,
			LocalEntityManager.CertificateUsage certificateUsage)
		throws SecurityException;
}
