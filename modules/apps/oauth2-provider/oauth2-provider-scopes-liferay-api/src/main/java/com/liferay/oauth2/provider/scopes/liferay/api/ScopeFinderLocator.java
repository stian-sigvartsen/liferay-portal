package com.liferay.oauth2.provider.scopes.liferay.api;

import com.liferay.portal.kernel.model.Company;

import java.util.Collection;

public interface ScopeFinderLocator {

	Collection<RetentiveOAuth2Grant> locateScopes(
		Company company, String scope);

	Collection<RetentiveOAuth2Grant> listScopes(Company company);
}