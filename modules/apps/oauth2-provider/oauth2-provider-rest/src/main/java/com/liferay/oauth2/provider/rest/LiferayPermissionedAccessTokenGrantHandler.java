/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.rest;

import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenGrantHandler;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Tomas Polesovsky
 */
public class LiferayPermissionedAccessTokenGrantHandler
	implements AccessTokenGrantHandler {

	public LiferayPermissionedAccessTokenGrantHandler(
		AccessTokenGrantHandler accessTokenGrantHandler,
		BiFunction<Client, MultivaluedMap<String, String>, Boolean>
			hasPermissionBiFunction) {

		_accessTokenGrantHandler = accessTokenGrantHandler;
		_hasPermissionBiFunction = hasPermissionBiFunction;
	}

	@Override
	public List<String> getSupportedGrantTypes() {
		return _accessTokenGrantHandler.getSupportedGrantTypes();
	}

	@Override
	public ServerAccessToken createAccessToken(
		Client client, MultivaluedMap<String, String> multivaluedMap)
		throws OAuthServiceException {

		if (!_hasPermissionBiFunction.apply(client, multivaluedMap)) {
			throw new OAuthServiceException(
				"User doesn't have permission to create token!");
		}

		return _accessTokenGrantHandler.createAccessToken(
			client, multivaluedMap);
	}

	private AccessTokenGrantHandler _accessTokenGrantHandler;

	private BiFunction<Client, MultivaluedMap<String, String>, Boolean>
		_hasPermissionBiFunction;
}
