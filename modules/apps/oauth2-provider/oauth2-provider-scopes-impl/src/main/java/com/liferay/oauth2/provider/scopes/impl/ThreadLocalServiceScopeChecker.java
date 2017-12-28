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

import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.scopes.api.ScopeChecker;
import com.liferay.oauth2.provider.scopes.liferay.api.RetentiveOAuth2Grant;
import com.liferay.oauth2.provider.scopes.liferay.api.ScopeContext;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.oauth2.provider.service.OAuth2TokenLocalService;
import com.liferay.oauth2.provider.service.persistence.OAuth2ScopeGrantPK;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.StringPool;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Collection;
import java.util.Collections;

@Component(service = {ScopeChecker.class, ScopeContext.class})
public class ThreadLocalServiceScopeChecker
	implements ScopeChecker, ScopeContext {

	ThreadLocal<Collection<RetentiveOAuth2Grant>> _allowedScopesThreadLocal =
		ThreadLocal.withInitial(Collections::emptySet);

	ThreadLocal<Long> _companyIdThreadLocal = ThreadLocal.withInitial(() -> 0L);
	ThreadLocal<String> _bundleSymbolicName = ThreadLocal.withInitial(
		() -> StringPool.BLANK);
	ThreadLocal<String> _bundleVersion = ThreadLocal.withInitial(
		() -> StringPool.BLANK);
	ThreadLocal<String> _applicationName = ThreadLocal.withInitial(
		() -> StringPool.BLANK);
	ThreadLocal<String> _tokenString = ThreadLocal.withInitial(
		() -> StringPool.BLANK);

	@Override
	public boolean hasScope(String scope) {
		OAuth2ScopeGrant oAuth2ScopeGrant =
			_oAuth2ScopeGrantLocalService.fetchOAuth2ScopeGrant(
				new OAuth2ScopeGrantPK(
					_applicationName.get(), _bundleSymbolicName.get(),
					_bundleVersion.get(), scope, _tokenString.get()));

		return oAuth2ScopeGrant != null;
	}

	public void setAllowedScopes(Collection<RetentiveOAuth2Grant> allowedScopes) {
		_allowedScopesThreadLocal.set(allowedScopes);
	}

	@Override
	public void setCompany(Company company) {
		_companyIdThreadLocal.set(company.getCompanyId());
	}

	@Override
	public void setBundle(Bundle bundle) {
		_bundleSymbolicName.set(bundle.getSymbolicName());
		_bundleVersion.set(bundle.getVersion().toString());
	}

	@Override
	public void setApplicationName(String applicationName) {
		_applicationName.set(applicationName);
	}

	@Override
	public String getTokenString() {
		return _tokenString.get();
	}

	@Override
	public void setTokenString(String tokenString) {
		_tokenString.set(tokenString);
	}

	@Override
	public void clear() {
		_applicationName.remove();
		_bundleSymbolicName.remove();
		_bundleVersion.remove();
		_companyIdThreadLocal.remove();
	}

	@Reference
	OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

}