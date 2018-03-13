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

package com.liferay.oauth2.provider.rest.tokenprovider;

import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProvider;
import com.liferay.portal.kernel.util.MapUtil;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.Map;

@Component(
	immediate = true,
	property = {
		"token.format=opaque",
		"name=default",
		"access_token.expires_in:Long=600",
		"access_token.key.byte.size:Long=32",
		"refresh_token.expires_in:Long=604800",
		"refresh_token.key.byte.size:Long=32",
	}
)
public class DefaultBearerTokenProvider implements BearerTokenProvider {

	@Override
	public void enrichAccessToken(AccessToken accessToken) {
		String tokenKey = OAuthUtils.generateRandomTokenKey(
			_accessTokenKeyByteSize);

		accessToken.setTokenKey(tokenKey);
		accessToken.setExpiresIn(_accessTokenExpiresIn);
	}

	@Override
	public void enrichRefreshToken(RefreshToken refreshToken) {
		String tokenKey = OAuthUtils.generateRandomTokenKey(
			_accessTokenKeyByteSize);

		refreshToken.setTokenKey(tokenKey);
		refreshToken.setExpiresIn(_refreshTokenExpiresIn);
	}

	@Override
	public boolean isValid(AccessToken accessToken) {
		long expiresIn = accessToken.getExpiresIn() * 1000;
		long issuedAt = accessToken.getIssuedAt() * 1000;

		if (expiresIn < 0) {
			return false;
		}

		if (issuedAt + expiresIn < System.currentTimeMillis()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isValid(RefreshToken refreshToken) {
		return true;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_accessTokenExpiresIn =
			MapUtil.getInteger(properties, "access_token.expires_in",
				_accessTokenExpiresIn);

		_accessTokenKeyByteSize =
			MapUtil.getInteger(properties, "access_token.key.byte.size",
				_accessTokenKeyByteSize);

		_refreshTokenExpiresIn =
			MapUtil.getInteger(properties, "refresh_token.expires_in",
				_refreshTokenExpiresIn);

		_refreshTokenKeyByteSize =
			MapUtil.getInteger(properties, "refresh_token.key.byte.size",
				_refreshTokenKeyByteSize);

	}

	private int _accessTokenExpiresIn = 600;
	private int _accessTokenKeyByteSize = 64;
	private int _refreshTokenExpiresIn = 600;
	private int _refreshTokenKeyByteSize = 64;

}