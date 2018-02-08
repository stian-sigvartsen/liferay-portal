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

package com.liferay.oauth2.provider.service.impl;

import com.liferay.oauth2.provider.exception.DuplicateOAuth2ClientIdException;
import com.liferay.oauth2.provider.exception.NoSuchOAuth2ApplicationException;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2RefreshToken;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.model.OAuth2Token;
import com.liferay.oauth2.provider.service.base.OAuth2ApplicationLocalServiceBaseImpl;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * The implementation of the o auth2 application local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2ApplicationLocalServiceBaseImpl
 * @see com.liferay.oauth2.provider.service.OAuth2ApplicationLocalServiceUtil
 */
public class OAuth2ApplicationLocalServiceImpl
	extends OAuth2ApplicationLocalServiceBaseImpl {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link com.liferay.oauth2.provider.service.OAuth2ApplicationLocalServiceUtil} to access the o auth2 application local service.
	 */
	public OAuth2Application addOAuth2Application(
			long userId, String name, String description, String webURL,
			boolean oAuth2ClientConfidential, String oAuth2ClientId, 
			String oAuth2ClientSecret, String oAuth2RedirectURI, 
			ServiceContext serviceContext)
		throws PortalException {

		if (oAuth2ApplicationPersistence.fetchByC_CI(
				serviceContext.getCompanyId(), oAuth2ClientId) != null) {

			throw new DuplicateOAuth2ClientIdException();
		}

		Date now = new Date();

		long oAuth2ApplicationId = counterLocalService.increment(
			OAuth2Application.class.getName());

		OAuth2Application oAuth2Application =
			oAuth2ApplicationPersistence.create(oAuth2ApplicationId);

		oAuth2Application.setUserId(userId);
		oAuth2Application.setName(name);
		oAuth2Application.setDescription(description);
		oAuth2Application.setWebUrl(webURL);
		oAuth2Application.setClientConfidential(oAuth2ClientConfidential);
		oAuth2Application.setClientId(oAuth2ClientId);
		oAuth2Application.setClientSecret(oAuth2ClientSecret);
		oAuth2Application.setRedirectUri(oAuth2RedirectURI);
		oAuth2Application.setCreateDate(now);
		oAuth2Application.setModifiedDate(now);

		// Resources

		resourceLocalService.addResources(
			oAuth2Application.getCompanyId(), 0, oAuth2Application.getUserId(),
			OAuth2Application.class.getName(),
			oAuth2Application.getOAuth2ApplicationId(), false, false, false);

		return oAuth2ApplicationPersistence.update(oAuth2Application);
	}

	@Override
	public OAuth2Application deleteOAuth2Application(long oAuth2ApplicationId)
		throws PortalException {

		Collection<OAuth2Token> oAuth2Tokens =
			oAuth2TokenLocalService.findByApplicationId(oAuth2ApplicationId,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (OAuth2Token oAuth2Token : oAuth2Tokens) {
			Collection<OAuth2ScopeGrant> grants =
				oAuth2ScopeGrantLocalService.findByTokenId(
					oAuth2Token.getOAuth2TokenId());

			for (OAuth2ScopeGrant grant : grants) {
				oAuth2ScopeGrantLocalService.deleteOAuth2ScopeGrant(grant);
			}

			oAuth2TokenLocalService.deleteOAuth2Token(oAuth2Token);
		}

		Collection<OAuth2RefreshToken> refreshTokens =
			oAuth2RefreshTokenLocalService.findByApplication(
				oAuth2ApplicationId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		for (OAuth2RefreshToken refreshToken : refreshTokens) {
			oAuth2RefreshTokenLocalService.deleteOAuth2RefreshToken(
				refreshToken);
		}

		return super.deleteOAuth2Application(oAuth2ApplicationId);
	}

	public OAuth2Application fetchOAuth2Application(
		long companyId, String clientId) {

		return oAuth2ApplicationPersistence.fetchByC_CI(companyId, clientId);
	}

	public OAuth2Application getOAuth2Application(
			long companyId, String clientId)
		throws NoSuchOAuth2ApplicationException {

		return oAuth2ApplicationPersistence.findByC_CI(companyId, clientId);
	}

	public OAuth2Application updateOAuth2Application(
			long userId, long oAuth2ApplicationId, String name, 
			String description, String webURL, boolean oAuth2ClientConfidential,  
			String oAuth2ClientId, String oAuth2ClientSecret, 
			String oAuth2RedirectURI, ServiceContext serviceContext)
		throws PortalException {

		OAuth2Application oAuth2Application =
			oAuth2ApplicationPersistence.findByPrimaryKey(oAuth2ApplicationId);

		Date now = new Date();

		oAuth2Application.setName(name);
		oAuth2Application.setDescription(description);
		oAuth2Application.setWebUrl(webURL);
		oAuth2Application.setClientConfidential(oAuth2ClientConfidential);
		oAuth2Application.setClientId(oAuth2ClientId);
		oAuth2Application.setClientSecret(oAuth2ClientSecret);
		oAuth2Application.setRedirectUri(oAuth2RedirectURI);
		oAuth2Application.setModifiedDate(now);

		return oAuth2ApplicationPersistence.update(oAuth2Application);
	}

	public OAuth2Application updateScopes(
			long oAuth2ApplicationId, List<String> scopes)
		throws NoSuchOAuth2ApplicationException {

		OAuth2Application oAuth2Application =
			oAuth2ApplicationPersistence.findByPrimaryKey(oAuth2ApplicationId);

		Date now = new Date();

		oAuth2Application.setScopesList(scopes);
		oAuth2Application.setModifiedDate(now);

		return oAuth2ApplicationPersistence.update(oAuth2Application);
	}

}