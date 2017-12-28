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

package com.liferay.oauth2.provider.scopes.impl;

import com.liferay.oauth2.provider.scopes.liferay.api.RetentiveOAuth2Grant;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

public class TestOAuth2Grant implements RetentiveOAuth2Grant {

	private final long _companyId;
	private final String _applicationName;
	private final String _bundleSymbolicName;
	private final String _bundleVersion;
	private final String _scope;

	public TestOAuth2Grant(
		long companyId, String applicationName, String bundleSymbolicName,
		String bundleVersion, String scope) {
		_companyId = companyId;

		_applicationName = applicationName;
		_bundleSymbolicName = bundleSymbolicName;
		_bundleVersion = bundleVersion;
		_scope = scope;
	}

	@Override
	public String getApplicationName() {
		return _applicationName;
	}

	@Override
	public String getBundleSymbolicName() {
		return _bundleSymbolicName;
	}

	@Override
	public String getBundleVersion() {
		return _bundleVersion;
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public String getScope() {
		return _scope;
	}
}
