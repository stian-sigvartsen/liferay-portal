package com.liferay.oauth2.provider.web.internal.display.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

/**
 * @author Stian Sigvartsen
 */
public class AuthorizationRequestModel {
	
	public AuthorizationRequestModel(
		int expectedLiferayOAuth2ScopeCount, 
		ApplicationScopeDescriptor applicationScopeDescriptor) {
		
		_applicationScopes = new HashMap<>(expectedLiferayOAuth2ScopeCount);
		_applicationDescriptions = new HashMap<>(expectedLiferayOAuth2ScopeCount);
		
		_applicationScopeDescriptor = applicationScopeDescriptor;
	}

	public void addLiferayOAuth2Scope(LiferayOAuth2Scope liferayOAuth2Scope) {
		String applicationName = liferayOAuth2Scope.getApplicationName();

		Set<String> applicationScopes =
			_applicationScopes.computeIfAbsent(
				applicationName, __ -> new HashSet<>());

		String internalScope = liferayOAuth2Scope.getScope();

		applicationScopes.add(internalScope);

		Set<String> applicationDescription =
			_applicationDescriptions.computeIfAbsent(
				applicationName, __ -> new HashSet<>());

		applicationDescription.add(
			_applicationScopeDescriptor.describe(
				CompanyThreadLocal.getCompanyId(),
				applicationName,
				internalScope));
	}

	public Set<String> getApplicationNames() {
		return _applicationScopes.keySet();
	}

	public Set<String> getApplicationInternalScopes(String applicationName) {
		return _applicationScopes.get(applicationName);
	}

	public Set<String> getApplicationScopeDescription(String applicationName) {
		return _applicationDescriptions.get(applicationName);
	}

	private Map<String, Set<String>> _applicationScopes;
	private Map<String, Set<String>> _applicationDescriptions;
	private ApplicationScopeDescriptor _applicationScopeDescriptor;

	public interface ApplicationScopeDescriptor {
		public String describe(
			long companyId, String applicationName, String internalScope);
	}

}
