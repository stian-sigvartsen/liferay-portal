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

package com.liferay.oauth2.provider.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.oauth2.provider.model.OAuth2Application;

import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.util.HashUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing OAuth2Application in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2Application
 * @generated
 */
@ProviderType
public class OAuth2ApplicationCacheModel implements CacheModel<OAuth2Application>,
	Externalizable {
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof OAuth2ApplicationCacheModel)) {
			return false;
		}

		OAuth2ApplicationCacheModel oAuth2ApplicationCacheModel = (OAuth2ApplicationCacheModel)obj;

		if (oAuth2ApplicationId.equals(
					oAuth2ApplicationCacheModel.oAuth2ApplicationId)) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, oAuth2ApplicationId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(23);

		sb.append("{oAuth2ApplicationId=");
		sb.append(oAuth2ApplicationId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", applicationSecret=");
		sb.append(applicationSecret);
		sb.append(", confidential=");
		sb.append(confidential);
		sb.append(", description=");
		sb.append(description);
		sb.append(", name=");
		sb.append(name);
		sb.append(", webUrl=");
		sb.append(webUrl);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public OAuth2Application toEntityModel() {
		OAuth2ApplicationImpl oAuth2ApplicationImpl = new OAuth2ApplicationImpl();

		if (oAuth2ApplicationId == null) {
			oAuth2ApplicationImpl.setOAuth2ApplicationId("");
		}
		else {
			oAuth2ApplicationImpl.setOAuth2ApplicationId(oAuth2ApplicationId);
		}

		oAuth2ApplicationImpl.setCompanyId(companyId);
		oAuth2ApplicationImpl.setUserId(userId);

		if (userName == null) {
			oAuth2ApplicationImpl.setUserName("");
		}
		else {
			oAuth2ApplicationImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			oAuth2ApplicationImpl.setCreateDate(null);
		}
		else {
			oAuth2ApplicationImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			oAuth2ApplicationImpl.setModifiedDate(null);
		}
		else {
			oAuth2ApplicationImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (applicationSecret == null) {
			oAuth2ApplicationImpl.setApplicationSecret("");
		}
		else {
			oAuth2ApplicationImpl.setApplicationSecret(applicationSecret);
		}

		oAuth2ApplicationImpl.setConfidential(confidential);

		if (description == null) {
			oAuth2ApplicationImpl.setDescription("");
		}
		else {
			oAuth2ApplicationImpl.setDescription(description);
		}

		if (name == null) {
			oAuth2ApplicationImpl.setName("");
		}
		else {
			oAuth2ApplicationImpl.setName(name);
		}

		if (webUrl == null) {
			oAuth2ApplicationImpl.setWebUrl("");
		}
		else {
			oAuth2ApplicationImpl.setWebUrl(webUrl);
		}

		oAuth2ApplicationImpl.resetOriginalValues();

		return oAuth2ApplicationImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		oAuth2ApplicationId = objectInput.readUTF();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		applicationSecret = objectInput.readUTF();

		confidential = objectInput.readBoolean();
		description = objectInput.readUTF();
		name = objectInput.readUTF();
		webUrl = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		if (oAuth2ApplicationId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(oAuth2ApplicationId);
		}

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		if (applicationSecret == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(applicationSecret);
		}

		objectOutput.writeBoolean(confidential);

		if (description == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(description);
		}

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (webUrl == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(webUrl);
		}
	}

	public String oAuth2ApplicationId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String applicationSecret;
	public boolean confidential;
	public String description;
	public String name;
	public String webUrl;
}