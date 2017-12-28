package com.liferay.oauth2.provider.scopes.spi;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

@FunctionalInterface
public interface ScopeFinder {

	public Collection<String> findScopes(ScopeMatcher scopeMatcher);

	static ScopeFinder empty(ScopeMatcher matcher) {
		return __ -> Collections.emptyList();
	}

	public default Collection<String> findScopes(String scope) {
		ScopeMatcherFactory scopeMatcherFactory =
			getDefaultScopeMatcherFactory();

		return findScopes(scopeMatcherFactory.create(scope));
	}

	public default ScopeMatcherFactory getDefaultScopeMatcherFactory() {
		return ScopeMatcherFactory.STRICT;
	}

	public default String describe(
		Collection<String> scopes, Locale locale) {

		return String.join(", ", scopes);
	}

}