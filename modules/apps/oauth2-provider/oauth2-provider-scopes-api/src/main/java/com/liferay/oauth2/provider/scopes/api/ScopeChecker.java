package com.liferay.oauth2.provider.scopes.api;

import com.liferay.oauth2.provider.scopes.api.exceptions.ScopeNotGrantedException;

public interface ScopeChecker {

	public boolean hasScope(String scope);

	public default void requireScope(String scope)
		throws ScopeNotGrantedException {

		if (!hasScope(scope)) {
			throw new ScopeNotGrantedException(scope);
		}
	}

}