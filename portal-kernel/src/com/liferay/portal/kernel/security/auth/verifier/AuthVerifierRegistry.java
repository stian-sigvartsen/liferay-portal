package com.liferay.portal.kernel.security.auth.verifier;

import java.util.List;

import com.liferay.portal.kernel.security.auth.AccessControlContext;

public interface AuthVerifierRegistry {
	
	public List<AuthVerifierConfiguration> getAuthVerifierConfigurations(
		AccessControlContext accessControlContext);
}
