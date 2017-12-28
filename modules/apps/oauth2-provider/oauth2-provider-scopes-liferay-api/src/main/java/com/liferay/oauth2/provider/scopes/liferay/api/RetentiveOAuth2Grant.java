package com.liferay.oauth2.provider.scopes.liferay.api;

public interface RetentiveOAuth2Grant {

	public String getApplicationName();

	public String getBundleSymbolicName();

	public String getBundleVersion();

	public long getCompanyId();

	public String getScope();

}
