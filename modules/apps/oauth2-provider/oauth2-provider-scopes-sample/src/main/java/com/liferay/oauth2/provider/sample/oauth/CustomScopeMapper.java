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

package com.liferay.oauth2.provider.sample.oauth;


import com.liferay.oauth2.provider.scopes.spi.ScopeMapper;
import com.liferay.portal.kernel.util.StringPool;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, property = "companyId=20101")
public class CustomScopeMapper implements ScopeMapper{

	@Override
	public String map(String scope) {
		switch (scope) {
			case "everything": return "todo";
			case "everything.readonly": return "todo.sololectura";
		}

		return scope;
	}
}
