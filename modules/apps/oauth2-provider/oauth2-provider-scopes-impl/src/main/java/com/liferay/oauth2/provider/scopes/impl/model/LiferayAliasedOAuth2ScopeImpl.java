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

package com.liferay.oauth2.provider.scopes.impl.model;

import com.liferay.oauth2.provider.model.LiferayAliasedOAuth2Scope;
import org.osgi.framework.Bundle;

public class LiferayAliasedOAuth2ScopeImpl implements LiferayAliasedOAuth2Scope{

	private final String _applicationName;
	private final Bundle _bundle;
	private final String _scope;
	private final String _externalAlias;

	public LiferayAliasedOAuth2ScopeImpl(
		String applicationName, Bundle bundle, String scope,
		String externalAlias) {

		_applicationName = applicationName;
		_bundle = bundle;
		_scope = scope;
		_externalAlias = externalAlias;
	}

	@Override
	public Bundle getBundle() {
		return _bundle;
	}

	@Override
	public String getApplicationName() {
		return _applicationName;
	}

	@Override
	public String getScope() {
		return _scope;
	}

	@Override
	public String getExternalAlias() {
		return _externalAlias;
	}

}
