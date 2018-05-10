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

package com.liferay.oauth2.provider.web.internal.display.context;

import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Stian Sigvartsen
 */
public class AuthorizationModel {

	public AuthorizationModel(
		int expectedLiferayOAuth2ScopeCount,
		ApplicationDescriptor applicationDescriptor,
		ApplicationScopeDescriptor applicationScopeDescriptor) {

		_applicationScopes = new HashMap<>(expectedLiferayOAuth2ScopeCount);
		_applicationDescriptor = applicationDescriptor;
		_applicationScopeDescriptor = applicationScopeDescriptor;
	}

	public AuthorizationModel(
		Map<String, Set<String>> applicationScopes,
		ApplicationDescriptor applicationDescriptor,
		ApplicationScopeDescriptor applicationScopeDescriptor) {

		_applicationScopes = applicationScopes;
		_applicationDescriptor = applicationDescriptor;
		_applicationScopeDescriptor = applicationScopeDescriptor;
	}

	public AuthorizationModel add(AuthorizationModel model2) {
		Map<String, Set<String>> combinedApplicationScopesMap = new HashMap<>(
			_applicationScopes);

		for (Map.Entry<String, Set<String>> entry :
				model2._applicationScopes.entrySet()) {

			combinedApplicationScopesMap.compute(
				entry.getKey(),
				(key, existingValue) -> {
					HashSet<String> newValue = new HashSet<>(entry.getValue());

					if (existingValue != null) {
						newValue.addAll(existingValue);
					}

					return newValue;
				});
		}

		AuthorizationModel authorizationModel = new AuthorizationModel(
			combinedApplicationScopesMap, _applicationDescriptor,
			_applicationScopeDescriptor);

		return authorizationModel;
	}

	public void addLiferayOAuth2Scope(LiferayOAuth2Scope liferayOAuth2Scope) {
		String applicationName = liferayOAuth2Scope.getApplicationName();

		Set<String> applicationScopes = _applicationScopes.computeIfAbsent(
			applicationName, __ -> new HashSet<>());

		String internalScope = liferayOAuth2Scope.getScope();

		applicationScopes.add(internalScope);
	}

	public boolean contains(AuthorizationModel model2) {
		for (String applicationName : model2.getApplicationNames()) {
			if (!_applicationScopes.containsKey(applicationName)) {
				return false;
			}

			Set<String> applicationScopes = _applicationScopes.get(
				applicationName);

			if (!applicationScopes.containsAll(
					model2._getApplicationScopes(applicationName))) {

				return false;
			}
		}

		return true;
	}

	@Override
	public boolean equals(Object obj2) {
		if (!(obj2 instanceof AuthorizationModel)) {
			return false;
		}

		AuthorizationModel arm2 = (AuthorizationModel)obj2;

		if (!getApplicationNames().equals(arm2.getApplicationNames())) {
			return false;
		}

		for (String applicationName : getApplicationNames()) {
			if (!_getApplicationScopes(
					applicationName
						).equals(
							arm2._getApplicationScopes(applicationName)
								)) {

				return false;
			}
		}

		return true;
	}

	public String getApplicationDescription(String applicationName) {
		return _applicationDescriptor.describe(
			CompanyThreadLocal.getCompanyId(), applicationName);
	}

	public Set<String> getApplicationNames() {
		return _applicationScopes.keySet();
	}

	public Map<String, String> getApplicationNamesDescriptions() {
		Set<String> keySet = _applicationScopes.keySet();

		Stream<String> stream = keySet.stream();

		return stream.collect(
			Collectors.toMap(
				Function.identity(),
				applicationName -> getApplicationDescription(applicationName)));
	}

	public Set<String> getApplicationScopeDescription(String applicationName) {
		Set<String> applicationScopeDescription = new HashSet<>();

		Set<String> applicationScopes = _applicationScopes.get(applicationName);

		for (String applicationScope : applicationScopes) {
			applicationScopeDescription.add(
				_applicationScopeDescriptor.describe(
					CompanyThreadLocal.getCompanyId(), applicationName,
					applicationScope));
		}

		return applicationScopeDescription;
	}

	@Override
	public int hashCode() {
		return getApplicationNames().hashCode();
	}

	public AuthorizationModel reduceToSpecficApplications(
		Set<String> applicationNames) {

		Map<String, Set<String>> remainingApplicationScopesMap =
			new HashMap<>();

		for (String applicationName : applicationNames) {
			remainingApplicationScopesMap.put(
				applicationName, _applicationScopes.get(applicationName));
		}

		AuthorizationModel authorizationModel = new AuthorizationModel(
			remainingApplicationScopesMap, _applicationDescriptor,
			_applicationScopeDescriptor);

		return authorizationModel;
	}

	public Set<AuthorizationModel> splitByApplicationScopes() {
		Set<AuthorizationModel> split = new HashSet<>();

		for (Map.Entry<String, Set<String>> applicationScopesEntry :
				_applicationScopes.entrySet()) {

			for (String scope : applicationScopesEntry.getValue()) {
				Map<String, Set<String>> applicationScopesMap = new HashMap<>();

				applicationScopesMap.put(
					applicationScopesEntry.getKey(),
					Collections.singleton(scope));

				AuthorizationModel authorizationModel = new AuthorizationModel(
					applicationScopesMap, _applicationDescriptor,
					_applicationScopeDescriptor);

				split.add(authorizationModel);
			}
		}

		return split;
	}

	public AuthorizationModel subtract(AuthorizationModel model2) {
		Map<String, Set<String>> remainingApplicationScopesMap =
			new HashMap<>();

		for (Map.Entry<String, Set<String>> entry :
				_applicationScopes.entrySet()) {

			Set<String> model2ApplicationNames = model2.getApplicationNames();

			if (!model2ApplicationNames.contains(entry.getKey())) {
				remainingApplicationScopesMap.put(
					entry.getKey(), entry.getValue());
			}
			else {
				Set<String> remainingApplicationScopes = new HashSet<>(
					entry.getValue());

				Set<String> model2ApplicationInternalScopes =
					model2._getApplicationScopes(entry.getKey());

				remainingApplicationScopes.removeAll(
					model2ApplicationInternalScopes);

				remainingApplicationScopesMap.put(
					entry.getKey(), remainingApplicationScopes);
			}
		}

		AuthorizationModel authorizationModel = new AuthorizationModel(
			remainingApplicationScopesMap, _applicationDescriptor,
			_applicationScopeDescriptor);

		return authorizationModel;
	}

	public interface ApplicationDescriptor {

		public String describe(long companyId, String applicationName);

	}

	public interface ApplicationScopeDescriptor {

		public String describe(
			long companyId, String applicationName, String internalScope);

	}

	private Set<String> _getApplicationScopes(String applicationName) {
		return _applicationScopes.get(applicationName);
	}

	private final ApplicationDescriptor _applicationDescriptor;
	private final ApplicationScopeDescriptor _applicationScopeDescriptor;
	private final Map<String, Set<String>> _applicationScopes;

}