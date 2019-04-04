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

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2ApplicationScopeAliases;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.oauth2.provider.service.base.OAuth2ApplicationScopeAliasesLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.oauth2.provider.model.OAuth2ApplicationScopeAliases",
	service = AopService.class
)
public class OAuth2ApplicationScopeAliasesLocalServiceImpl
	extends OAuth2ApplicationScopeAliasesLocalServiceBaseImpl {

	@Override
	public OAuth2ApplicationScopeAliases addOAuth2ApplicationScopeAliases(
			long companyId, long userId, String userName,
			long oAuth2ApplicationId, List<String> scopeAliasesList)
		throws PortalException {

		if (scopeAliasesList.stream().anyMatch(
				scopeAlias -> scopeAlias.indexOf(' ') > -1)) {

			throw new PortalException(
				"Scope alias cannot contain space character");
		}

		OAuth2Application oAuth2Application =
			oAuth2ApplicationPersistence.findByPrimaryKey(oAuth2ApplicationId);

		long oAuth2ApplicationScopeAliasesId =
			oAuth2Application.getOAuth2ApplicationScopeAliasesId();

		Map<LiferayOAuth2Scope, List<String>> liferayOAuth2ScopesScopeAliases =
			new HashMap<>();

		for (String scopeAlias : ListUtil.sort(scopeAliasesList)) {
			for (LiferayOAuth2Scope liferayOAuth2Scope :
					_scopeLocator.getLiferayOAuth2Scopes(
						companyId, scopeAlias)) {

				List<String> scopeAliases =
					liferayOAuth2ScopesScopeAliases.computeIfAbsent(
						liferayOAuth2Scope, x -> new ArrayList<>());

				scopeAliases.add(scopeAlias);
			}
		}

		if ((oAuth2ApplicationScopeAliasesId > 0) &&
			_hasUpToDateScopeGrants(
				oAuth2ApplicationScopeAliasesId,
				liferayOAuth2ScopesScopeAliases)) {

			return oAuth2ApplicationScopeAliasesPersistence.fetchByPrimaryKey(
				oAuth2ApplicationScopeAliasesId);
		}

		oAuth2ApplicationScopeAliasesId = counterLocalService.increment(
			OAuth2ApplicationScopeAliases.class.getName());

		OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases =
			createOAuth2ApplicationScopeAliases(
				oAuth2ApplicationScopeAliasesId);

		oAuth2ApplicationScopeAliases.setCompanyId(companyId);
		oAuth2ApplicationScopeAliases.setUserId(userId);
		oAuth2ApplicationScopeAliases.setUserName(userName);
		oAuth2ApplicationScopeAliases.setCreateDate(new Date());
		oAuth2ApplicationScopeAliases.setOAuth2ApplicationId(
			oAuth2ApplicationId);

		oAuth2ApplicationScopeAliases =
			oAuth2ApplicationScopeAliasesPersistence.update(
				oAuth2ApplicationScopeAliases);

		createScopeGrants(
			companyId, oAuth2ApplicationScopeAliasesId,
			liferayOAuth2ScopesScopeAliases);

		return oAuth2ApplicationScopeAliases;
	}

	@Override
	public OAuth2ApplicationScopeAliases deleteOAuth2ApplicationScopeAliases(
			long oAuth2ApplicationScopeAliasesId)
		throws PortalException {

		Collection<OAuth2ScopeGrant> oAuth2ScopeGrants =
			_oAuth2ScopeGrantLocalService.getOAuth2ScopeGrants(
				oAuth2ApplicationScopeAliasesId, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		for (OAuth2ScopeGrant oAuth2ScopeGrant : oAuth2ScopeGrants) {
			_oAuth2ScopeGrantLocalService.deleteOAuth2ScopeGrant(
				oAuth2ScopeGrant.getOAuth2ScopeGrantId());
		}

		return oAuth2ApplicationScopeAliasesPersistence.remove(
			oAuth2ApplicationScopeAliasesId);
	}

	@Override
	public List<OAuth2ApplicationScopeAliases>
		getOAuth2ApplicationScopeAliaseses(
			long oAuth2ApplicationId, int start, int end,
			OrderByComparator<OAuth2ApplicationScopeAliases>
				orderByComparator) {

		return oAuth2ApplicationScopeAliasesPersistence.
			findByOAuth2ApplicationId(
				oAuth2ApplicationId, start, end, orderByComparator);
	}

	@Override
	public List<String> getScopeAliasesList(
		long oAuth2ApplicationScopeAliasesId) {

		Collection<OAuth2ScopeGrant> oAuth2ScopeGrants =
			_oAuth2ScopeGrantLocalService.getOAuth2ScopeGrants(
				oAuth2ApplicationScopeAliasesId, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Stream<OAuth2ScopeGrant> stream = oAuth2ScopeGrants.stream();

		Set<String> scopeAliases = stream.flatMap(
			oa2sg -> oa2sg.getScopeAliasesList(
			).stream()
		).collect(
			Collectors.toSet()
		);

		return new ArrayList<>(scopeAliases);
	}

	protected void createScopeGrants(
			long companyId, long oAuth2ApplicationScopeAliasesId,
			Map<LiferayOAuth2Scope, List<String>>
				liferayOAuth2ScopesScopeAliases)
		throws PortalException {

		for (Map.Entry<LiferayOAuth2Scope, List<String>> entry :
				liferayOAuth2ScopesScopeAliases.entrySet()) {

			LiferayOAuth2Scope liferayOAuth2Scope = entry.getKey();

			Bundle bundle = liferayOAuth2Scope.getBundle();

			_oAuth2ScopeGrantLocalService.createOAuth2ScopeGrant(
				companyId, oAuth2ApplicationScopeAliasesId,
				liferayOAuth2Scope.getApplicationName(),
				bundle.getSymbolicName(), liferayOAuth2Scope.getScope(),
				entry.getValue());
		}
	}

	private boolean _hasUpToDateScopeGrants(
		long oAuth2ApplicationScopeAliasesId,
		Map<LiferayOAuth2Scope, List<String>> liferayOAuth2ScopesScopeAliases) {

		Collection<OAuth2ScopeGrant> oAuth2ScopeGrants = new ArrayList<>(
			_oAuth2ScopeGrantLocalService.getOAuth2ScopeGrants(
				oAuth2ApplicationScopeAliasesId, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null));

		if (liferayOAuth2ScopesScopeAliases.size() !=
				oAuth2ScopeGrants.size()) {

			return false;
		}

		for (Map.Entry<LiferayOAuth2Scope, List<String>> entry :
				liferayOAuth2ScopesScopeAliases.entrySet()) {

			LiferayOAuth2Scope liferayOAuth2Scope = entry.getKey();
			List<String> scopeAliases = entry.getValue();
			Bundle bundle = liferayOAuth2Scope.getBundle();

			boolean found = oAuth2ScopeGrants.removeIf(
				oAuth2ScopeGrant -> {
					if (Objects.equals(
							liferayOAuth2Scope.getApplicationName(),
							oAuth2ScopeGrant.getApplicationName()) &&
						Objects.equals(
							bundle.getSymbolicName(),
							oAuth2ScopeGrant.getBundleSymbolicName()) &&
						Objects.equals(
							liferayOAuth2Scope.getScope(),
							oAuth2ScopeGrant.getScope())) {

						List<String> oAuth2ScopeGrantScopeAliases =
							oAuth2ScopeGrant.getScopeAliasesList();

						return oAuth2ScopeGrantScopeAliases.equals(
							scopeAliases);
					}

					return false;
				});

			if (!found) {
				return false;
			}
		}

		return true;
	}

	@Reference
	private OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

	@Reference
	private ScopeLocator _scopeLocator;

}