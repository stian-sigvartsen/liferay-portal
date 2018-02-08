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

package com.liferay.oauth2.provider.scopes.impl.requestscopechecker;

import com.liferay.oauth2.provider.scopes.api.RequiresScope;
import com.liferay.oauth2.provider.scopes.api.ScopeChecker;
import com.liferay.oauth2.provider.scopes.spi.RequestScopeChecker;
import com.liferay.portal.kernel.util.MapUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Request;
import java.lang.reflect.Method;
import java.util.Map;

@Component(service = AnnotationRequestScopeChecker.class)
public class AnnotationRequestScopeChecker implements RequestScopeChecker {

	private boolean _defaultReturn;

	@Activate
	protected void activate(Map<String, Object> properties) {
		_defaultReturn = MapUtil.getBoolean(
			properties, "default.return", false);
	}

	@Override
	public boolean isAllowed(
		ScopeChecker scopeChecker, Request request, ResourceInfo resourceInfo) {

		Method method = resourceInfo.getResourceMethod();

		RequiresScope annotation = method.getAnnotation(RequiresScope.class);

		if (annotation == null) {
			Class<?> resourceClass = resourceInfo.getResourceClass();

			annotation = resourceClass.getAnnotation(RequiresScope.class);
		}

		if (annotation != null) {
			if (annotation.allNeeded()) {
				return scopeChecker.hasAllScopes(annotation.value());
			}
			else {
				return scopeChecker.hasAnyScope(annotation.value());
			}
		}

		return _defaultReturn;
	}

}