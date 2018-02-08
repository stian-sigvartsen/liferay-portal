package com.liferay.oauth2.provider.model;

/**
 * Interface representing the whole information for a scope in a Liferay
 * environment. An external scope name will match several scopes internally.
 */
public interface LiferayAliasedOAuth2Scope extends LiferayOAuth2Scope {

	public String getExternalAlias();

}
