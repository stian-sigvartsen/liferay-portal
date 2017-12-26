package com.liferay.oauth2.provider.scopes.spi;

@FunctionalInterface
public interface PropertyGetter {

	Object get(String propertyName);

}