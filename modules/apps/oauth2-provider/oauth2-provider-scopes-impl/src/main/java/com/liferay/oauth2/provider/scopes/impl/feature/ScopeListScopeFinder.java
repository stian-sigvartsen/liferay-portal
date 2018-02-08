/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.scopes.impl.feature;

import com.liferay.oauth2.provider.scopes.impl.scopematcher.ChunkScopeMatcherFactory;
import com.liferay.oauth2.provider.scopes.spi.ScopeFinder;
import com.liferay.oauth2.provider.scopes.spi.ScopeMatcher;
import com.liferay.oauth2.provider.scopes.spi.ScopeMatcherFactory;

import java.util.Collection;

class ScopeListScopeFinder implements ScopeFinder {

	private final Collection<String> _scopes;

	public ScopeListScopeFinder(Collection<String> scopes) {
		_scopes = scopes;
	}

	@Override
	public Collection<String> findScopes(ScopeMatcher scopeMatcher) {
		return scopeMatcher.filter(_scopes);
	}

	@Override
	public ScopeMatcherFactory getDefaultScopeMatcherFactory() {
		return new ChunkScopeMatcherFactory();
	}
}
