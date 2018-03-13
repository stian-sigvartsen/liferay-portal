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

package com.liferay.oauth2.provider.model;

import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Tomas Polesovsky
 */
public class OAuth2Authorization extends OAuth2AuthorizationModel {

	public Date getAccessTokenExpirationDate() {
		return _accessTokenExpirationDate;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public long getoAuth2ApplicationId() {
		return _oAuth2ApplicationId;
	}

	public long getoAuth2RefreshTokenId() {
		return _oAuth2RefreshTokenId;
	}

	public long getoAuth2TokenId() {
		return _oAuth2TokenId;
	}

	public Date getRefreshTokenExpirationDate() {
		return _refreshTokenExpirationDate;
	}

	public String getRemoteIPInfo() {
		return _remoteIPInfo;
	}

	public String getScopes() {
		return _scopes;
	}

	public List<String> getScopesList() {
		return Arrays.asList(StringUtil.split(getScopes(), StringPool.SPACE));
	}

	public long getUserId() {
		return _userId;
	}

	public String getUserName() {
		return _userName;
	}

	public void setAccessTokenExpirationDate(Date accessTokenExpirationDate) {
		_accessTokenExpirationDate = accessTokenExpirationDate;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public void setoAuth2ApplicationId(long oAuth2ApplicationId) {
		_oAuth2ApplicationId = oAuth2ApplicationId;
	}

	public void setoAuth2RefreshTokenId(long oAuth2RefreshTokenId) {
		_oAuth2RefreshTokenId = oAuth2RefreshTokenId;
	}

	public void setoAuth2TokenId(long oAuth2TokenId) {
		_oAuth2TokenId = oAuth2TokenId;
	}

	public void setRefreshTokenExpirationDate(Date refreshTokenExpirationDate) {
		_refreshTokenExpirationDate = refreshTokenExpirationDate;
	}

	public void setRemoteIPInfo(String remoteIPInfo) {
		_remoteIPInfo = remoteIPInfo;
	}

	public void setScopes(String scopes) {
		_scopes = scopes;
	}

	public void setScopesList(List<String> scopesList) {
		String scopes = StringUtil.merge(scopesList, StringPool.SPACE);

		setScopes(scopes);
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	private Date _accessTokenExpirationDate;
	private long _companyId;
	private Date _createDate;
	private long _oAuth2ApplicationId;
	private long _oAuth2RefreshTokenId;
	private long _oAuth2TokenId;
	private Date _refreshTokenExpirationDate;
	private String _remoteIPInfo;
	private String _scopes;
	private long _userId;
	private String _userName;

}