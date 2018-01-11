package com.liferay.oauth2.provider.scopes.liferay.api;

import com.liferay.oauth2.provider.model.LiferayOAuth2ScopeInternalIdentifier;

import java.util.Collection;

/**
 * This interface allows to access the ScopeFinders with <i>per-company</i>
 * configurations in a Liferay environment.
 */
public interface ScopeFinderLocator {

	/**
	 * Returns a list of the scope internal idetifiers that the different
	 * registered {@link com.liferay.oauth2.provider.scopes.spi.ScopeFinder}
	 * have reported to check.
	 * @return a collection of scope internal identifiers registered.
	 */
	Collection<LiferayOAuth2ScopeInternalIdentifier> listScopes();

}