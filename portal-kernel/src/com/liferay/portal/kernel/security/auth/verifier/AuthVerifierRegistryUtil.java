package com.liferay.portal.kernel.security.auth.verifier;

import java.util.List;

import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceTracker;

public class AuthVerifierRegistryUtil {
	
	public static AuthVerifierRegistry getAuthVerifierRegistry() {
		return _instance._serviceTracker.getService();
	}

	private AuthVerifierRegistryUtil() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceTracker = registry.trackServices(AuthVerifierRegistry.class);

		_serviceTracker.open();
	}
	
	private static final AuthVerifierRegistryUtil _instance = 
		new AuthVerifierRegistryUtil();
	
	private final ServiceTracker<?, AuthVerifierRegistry> _serviceTracker;

	public static List<AuthVerifierConfiguration> 
		getAuthVerifierConfigurations(AccessControlContext accessControlContext) {
		
		AuthVerifierRegistry authVerifierRegistry = getAuthVerifierRegistry();
		
		return authVerifierRegistry.getAuthVerifierConfigurations(accessControlContext);
	}
}
