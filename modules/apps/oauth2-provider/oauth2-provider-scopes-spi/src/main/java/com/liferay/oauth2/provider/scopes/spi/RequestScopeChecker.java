package com.liferay.oauth2.provider.scopes.spi;

import com.liferay.oauth2.provider.scopes.api.ScopeChecker;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Request;

public interface RequestScopeChecker {

	public boolean isAllowed(
		ScopeChecker scopeChecker, Request request, ResourceInfo resourceInfo);

}
