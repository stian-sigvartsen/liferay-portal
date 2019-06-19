package com.liferay.oauth2.provider.service;

import java.util.Collections;
import java.util.List;

public interface OAuth2ApplicationScopeAliasesBuilder {
	public OAuth2ScopeGrantBuilder forApplication(String applicationName);
	
	public interface OAuth2ScopeGrantBuilder {

		public default OAuth2ScopeGrantBuilder assignScope(String scope) {
			return assignScope(scope, Collections.singletonList(scope));
		}

		public OAuth2ScopeGrantBuilder assignScope(String scope, List<String> scopeAliases);

		public default OAuth2ScopeGrantBuilder assignScope(
			String scope, String scopeAlias) {

			return assignScope(scope, Collections.singletonList(scopeAlias));
		}

	}

}
