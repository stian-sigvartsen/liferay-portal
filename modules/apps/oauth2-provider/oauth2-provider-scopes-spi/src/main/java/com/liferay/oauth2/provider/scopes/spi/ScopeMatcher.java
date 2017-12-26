package com.liferay.oauth2.provider.scopes.spi;

import java.util.Collection;
import java.util.stream.Collectors;

public interface ScopeMatcher {

	public boolean match(String name);

	public default Collection<String> filter(Collection<String> names) {
		return names.stream().filter(this::match).collect(Collectors.toList());
	}

	public default ScopeMatcher prepend(PrefixHandler namespaceAdder) {
		return localName -> match(namespaceAdder.addPrefix(localName));
	}

	public static ScopeMatcher NULL = __ -> false;

}
