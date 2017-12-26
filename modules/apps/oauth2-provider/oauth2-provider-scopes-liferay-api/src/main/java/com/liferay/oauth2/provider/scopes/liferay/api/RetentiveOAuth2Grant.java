package com.liferay.oauth2.provider.scopes.liferay.api;

import com.liferay.oauth2.provider.scopes.spi.OAuth2Grant;

public interface RetentiveOAuth2Grant extends OAuth2Grant {

	public String getApplicationName();

	public String getBundleSymbolicName();

	public String getBundleVersion();

	public long getCompanyId();

}
