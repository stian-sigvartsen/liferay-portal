package com.liferay.oauth2.provider.scopes.spi;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public interface OAuth2Grant {
	public static OAuth2Grant NULL = new OAuth2Grant() {
		@Override
		public Collection<String> getNames() {
			return Collections.emptyList();
		}

		@Override
		public Collection<String> getDescriptions(Locale locale) {
			return Collections.emptyList();
		}
	};

	Collection<String> getNames();

	Collection<String> getDescriptions(Locale locale);
}
