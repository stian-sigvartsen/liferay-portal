package com.liferay.oauth2.provider.scopes.spi;

@FunctionalInterface
public interface ScopeFinder {

	public OAuth2Grant findScopes(ScopeMatcher scopeMatcher);

	static ScopeFinder empty(ScopeMatcher matcher) {
		return __ -> OAuth2Grant.NULL;
	}

	public default OAuth2Grant findScopes(String scope) {
		ScopeMatcherFactory scopeMatcherFactory =
			getDefaultScopeMatcherFactory();

		return findScopes(scopeMatcherFactory.create(scope));
	}

	public default ScopeMatcherFactory getDefaultScopeMatcherFactory() {
		return ScopeMatcherFactory.STRICT;
	}

}