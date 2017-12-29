package com.liferay.oauth2.provider.scopes.liferay.api;

import com.liferay.oauth2.provider.model.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.model.LiferayOAuth2ScopeExternalIdentifier;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.portal.kernel.model.Company;

import java.util.Collection;

public interface ScopeFinderLocator {

	Collection<LiferayOAuth2Scope> locateScopes(
		Company company, String scope);

	Collection<LiferayOAuth2ScopeExternalIdentifier> listScopes(
		Company company);

}