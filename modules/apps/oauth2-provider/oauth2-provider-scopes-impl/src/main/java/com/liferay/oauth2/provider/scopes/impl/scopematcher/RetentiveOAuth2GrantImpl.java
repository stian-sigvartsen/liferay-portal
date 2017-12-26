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

package com.liferay.oauth2.provider.scopes.impl.scopematcher;

import com.liferay.oauth2.provider.scopes.liferay.api.RetentiveOAuth2Grant;
import com.liferay.oauth2.provider.scopes.spi.OAuth2Grant;
import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.Locale;

public class RetentiveOAuth2GrantImpl implements RetentiveOAuth2Grant {

	private final long _companyId;
	private final String _applicationName;
	private final OAuth2Grant _oAuth2Grant;
	private final String _bundleSymbolicName;
	private final String _bundleVersion;

	@Override
	public Collection<String> getNames() {
		return _oAuth2Grant.getNames();
	}

	@Override
	public Collection<String> getDescriptions(Locale locale) {
		return _oAuth2Grant.getDescriptions(locale);
	}

	public RetentiveOAuth2GrantImpl(
		long companyId, Bundle bundle, String applicationName,
		OAuth2Grant oAuth2Grant) {

		_companyId = companyId;
		_applicationName = applicationName;
		_oAuth2Grant = oAuth2Grant;

		_bundleSymbolicName = bundle.getSymbolicName();
		_bundleVersion = bundle.getVersion().toString();
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

}
