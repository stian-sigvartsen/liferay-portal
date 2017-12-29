package com.liferay.oauth2.provider.model;

import org.osgi.framework.Bundle;

public interface LiferayOAuth2ScopeExternalIdentifier {

	public Bundle getBundle();

	public String getApplicationName();

	public String getScopeExternalIdentifier();
}
