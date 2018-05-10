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
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.web.internal.display.context.AuthorizationModel.ApplicationDescriptor;
import com.liferay.oauth2.provider.web.internal.display.context.AuthorizationModel.ApplicationScopeDescriptor;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

import java.util.Collection;
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
public class AssignScopesModel {

	public AssignScopesModel(
		long companyId, ScopeLocator scopeLocator,
		ApplicationDescriptor applicationDescriptor,
		ApplicationScopeDescriptor applicationScopeDescriptor) {

		Collection<String> scopeAliases = scopeLocator.getScopeAliases(
			companyId);

		for (String scopeAlias : scopeAliases) {
			AuthorizationModel authorizationModel = new AuthorizationModel(
				scopeAliases.size(), applicationDescriptor,
				applicationScopeDescriptor);

			Collection<LiferayOAuth2Scope> liferayOAuth2Scopes =
				scopeLocator.getLiferayOAuth2Scopes(companyId, scopeAlias);

			for (LiferayOAuth2Scope liferayOAuth2Scope : liferayOAuth2Scopes) {
				authorizationModel.addLiferayOAuth2Scope(liferayOAuth2Scope);
			}

			_authorizationModelsRelations.put(
				authorizationModel,
				new Relations(
					Collections.singleton(scopeAlias), new HashSet<>()));

			Set<String> applicationNames =
				authorizationModel.getApplicationNames();

			if (applicationNames.size() == 1) {
				Stream<String> applicationNamesStream =
					applicationNames.stream();

				String applicationName = applicationNamesStream.findAny().get();

				Set<AuthorizationModel> ownedAuthorizationModels =
					_applicationNamesOwnedAuthorizationModels.computeIfAbsent(
						applicationName, __ -> new HashSet<>());

				ownedAuthorizationModels.add(authorizationModel);

				continue;
			}

			_globalAuthorizationModels.add(authorizationModel);

			for (String applicationName : applicationNames) {
				Set<AuthorizationModel> globalAuthroizationModels =
					_applicationNamesGlobalAuthorizationModels.computeIfAbsent(
						applicationName, appName -> new HashSet<>());

				globalAuthroizationModels.add(authorizationModel);
			}
		}

		_applicationDescriptor = applicationDescriptor;
	}

	public Map<AuthorizationModel, Relations>
		buildAssignmentModelForApplication(String applicationName) {

		Map<AuthorizationModel, Relations>
			assignmentModelAuthorizationModelsRelations = new HashMap<>();

		Set<AuthorizationModel> globalAuthorizationModels =
			_applicationNamesGlobalAuthorizationModels.get(applicationName);

		Set<AuthorizationModel> ownedAuthorizationModels =
			_applicationNamesOwnedAuthorizationModels.get(applicationName);

		Map<AuthorizationModel, Relations> ownedEntries;

		if (ownedAuthorizationModels != null) {
			ownedEntries = getAuthorizationModelsRelations(
				ownedAuthorizationModels);
		}
		else {
			ownedEntries = Collections.emptyMap();
		}

		if (globalAuthorizationModels == null) {
			return ownedEntries;
		}

		assignmentModelAuthorizationModelsRelations.putAll(ownedEntries);

		for (AuthorizationModel globalAuthorizationModel :
				globalAuthorizationModels) {

			AuthorizationModel reduceToSpecficApplications =
				globalAuthorizationModel.reduceToSpecficApplications(
					Collections.singleton(applicationName));

			for (Map.Entry<AuthorizationModel, Relations> ownedEntry :
					ownedEntries.entrySet()) {

				if (globalAuthorizationModel.contains(ownedEntry.getKey())) {
					Relations ownedEntryRelations = ownedEntry.getValue();

					ownedEntryRelations._masterAuthorizationModels.add(
						globalAuthorizationModel);
				}

				reduceToSpecficApplications =
					reduceToSpecficApplications.subtract(ownedEntry.getKey());
			}

			Relations relations =
				assignmentModelAuthorizationModelsRelations.computeIfAbsent(
					reduceToSpecficApplications,
					authorizationModel -> new Relations());

			relations._masterAuthorizationModels.add(globalAuthorizationModel);
		}

		return _normalize(assignmentModelAuthorizationModelsRelations);
	}

	public String getApplicationDescription(String applicationName) {
		return _applicationDescriptor.describe(
			CompanyThreadLocal.getCompanyId(), applicationName);
	}

	public Set<String> getApplicationNames() {
		Set<String> applicationNames = new HashSet<>();

		applicationNames.addAll(
			_applicationNamesOwnedAuthorizationModels.keySet());
		applicationNames.addAll(
			_applicationNamesGlobalAuthorizationModels.keySet());

		return applicationNames;
	}

	public Map<String, String> getApplicationNamesDescriptions() {
		Set<String> applicationNames = getApplicationNames();

		Stream<String> applicationNamesStream = applicationNames.stream();

		return applicationNamesStream.collect(
			Collectors.toMap(
				Function.identity(),
				applicationName -> getApplicationDescription(applicationName)));
	}

	public Map<AuthorizationModel, Relations>
		getGlobalAuthorizationModelsRelations() {

		Stream<AuthorizationModel> authorizationModelsStream =
			_globalAuthorizationModels.stream();

		return authorizationModelsStream.filter(
			_authorizationModelsRelations::containsKey
		).collect(
			Collectors.toMap(
				Function.identity(), _authorizationModelsRelations::get)
		);
	}

	public class Relations {

		public Relations() {
			_scopeAliases = new HashSet<>();
			_masterAuthorizationModels = new HashSet<>();
		}

		public Relations(
			Set<String> scopeAliases,
			Set<AuthorizationModel> masterAuthorizationModels) {

			_scopeAliases = new HashSet<>(scopeAliases);
			_masterAuthorizationModels = new HashSet<>(
				masterAuthorizationModels);
		}

		public boolean equals(Object obj2) {
			if (!(obj2 instanceof Relations)) {
				return false;
			}

			Relations relations2 = (Relations)obj2;

			if (((_scopeAliases == null) &&
				 (relations2._scopeAliases != null)) ||
				((_scopeAliases != null) &&
				 !_scopeAliases.equals(relations2._scopeAliases))) {

				return false;
			}

			if (((_masterAuthorizationModels == null) &&
				 (relations2._masterAuthorizationModels != null)) ||
				((_masterAuthorizationModels != null) &&
				 !_masterAuthorizationModels.equals(
					 relations2._masterAuthorizationModels))) {

				return false;
			}

			return true;
		}

		public Map<AuthorizationModel, Relations>
			getMasterAuthorizationModelsRelations() {

			return getAuthorizationModelsRelations(_masterAuthorizationModels);
		}

		public Set<String> getMasterAuthorizationModelsScopeAliases() {
			Map<AuthorizationModel, Relations>
				masterAuthorizationModelsRelations =
					getMasterAuthorizationModelsRelations();

			Collection<Relations> masterAuthorizationModelRelations =
				masterAuthorizationModelsRelations.values();

			Stream<Relations> masterAuthorizationModelRelationsStream =
				masterAuthorizationModelRelations.stream();

			Set<String> masterScopeAliases =
				masterAuthorizationModelRelationsStream.flatMap(
					relations -> relations.getScopeAliases().stream()
				).collect(
					Collectors.toSet()
				);

			return masterScopeAliases;
		}

		public Set<String> getScopeAliases() {
			return _scopeAliases;
		}

		@Override
		public int hashCode() {
			return _masterAuthorizationModels.hashCode();
		}

		private Set<AuthorizationModel> _masterAuthorizationModels;
		private Set<String> _scopeAliases;

	}

	protected Map<AuthorizationModel, Relations>
		getAuthorizationModelsRelations(Set<AuthorizationModel>
		authorizationModels) {

		Stream<AuthorizationModel> authorizationModelsStream =
			authorizationModels.stream();

		return authorizationModelsStream.filter(
			_authorizationModelsRelations::containsKey
		).collect(
			Collectors.toMap(
				Function.identity(), _authorizationModelsRelations::get)
		);
	}

	private <K, V> Map<V, K> _invertMap(Map<K, V> map) {
		Map<V, K> ret = new HashMap<>(map.size());

		for (Map.Entry<K, V> entry : map.entrySet()) {
			ret.put(entry.getValue(), entry.getKey());
		}

		return ret;
	}

	private Map<AuthorizationModel, Relations> _normalize(
		Map<AuthorizationModel, Relations> authorizationModelsRelations) {

		Map<AuthorizationModel, Relations>
			combinedAuthorizationModelsRelations = new HashMap<>();

		for (Map.Entry<AuthorizationModel, Relations>
				authorizationModelsRelationsEntry :
					authorizationModelsRelations.entrySet()) {

			Relations authorizationModelRelations =
				authorizationModelsRelationsEntry.getValue();

			Set<String> scopeAliases =
				authorizationModelRelations.getScopeAliases();

			AuthorizationModel authorizationModel =
				authorizationModelsRelationsEntry.getKey();

			// Preserve AuthorizationModels that are assigned an alias

			if ((scopeAliases != null) && !scopeAliases.isEmpty()) {
				combinedAuthorizationModelsRelations.put(
					authorizationModel, authorizationModelRelations);
				continue;
			}

			// Reduce other AuthorizationModels down to individual
			// application scopes. But keep the master AuthorizationModel
			// relations of each original AuthorizationModel

			Set<AuthorizationModel> applicationScopeAuthorizationModels =
				authorizationModel.splitByApplicationScopes();

			for (AuthorizationModel applicationScopeAuthorizationModel :
					applicationScopeAuthorizationModels) {

				Relations combinedRelations =
					combinedAuthorizationModelsRelations.computeIfAbsent(
						applicationScopeAuthorizationModel,
						__ -> new Relations());

				combinedRelations._masterAuthorizationModels.addAll(
					authorizationModelRelations._masterAuthorizationModels);
			}
		}

		HashMap<Relations, AuthorizationModel> relationsAuthorizationModels =
			new HashMap<>();

		// Finally merge those by identical master AuthorizationModel relations

		for (Map.Entry<AuthorizationModel, Relations>
				entry : combinedAuthorizationModelsRelations.entrySet()) {

			relationsAuthorizationModels.compute(
				entry.getValue(),
				(key, existingValue) -> {
					if (existingValue != null) {
						return existingValue.add(entry.getKey());
					}
					else {
						return entry.getKey();
					}
				});
		}

		return _invertMap(relationsAuthorizationModels);
	}

	private final ApplicationDescriptor _applicationDescriptor;
	private Map<String, Set<AuthorizationModel>>
		_applicationNamesGlobalAuthorizationModels = new HashMap<>();
	private Map<String, Set<AuthorizationModel>>
		_applicationNamesOwnedAuthorizationModels = new HashMap<>();
	private Map<AuthorizationModel, Relations> _authorizationModelsRelations =
		new HashMap<>();
	private Set<AuthorizationModel> _globalAuthorizationModels =
		new HashSet<>();

}