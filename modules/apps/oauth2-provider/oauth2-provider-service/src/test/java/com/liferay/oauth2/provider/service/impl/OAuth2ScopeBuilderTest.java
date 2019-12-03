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

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;

import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.service.OAuth2Scope;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.commons.compress.utils.Sets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;

import org.osgi.framework.Bundle;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Stian Sigvartsen
 */
@RunWith(PowerMockRunner.class)
public class OAuth2ScopeBuilderTest extends PowerMockito {

	@Before
	public void setUp() {
		_oAuth2ApplicationScopeAliasesLocalServiceImpl =
			new OAuth2ApplicationScopeAliasesLocalServiceImpl();

		ScopeLocator scopeLocator = Mockito.mock(ScopeLocator.class);

		when(
			scopeLocator.getLiferayOAuth2Scope(
				anyLong(), anyString(), anyString())
		).thenAnswer(
			invocation -> {
				Object[] arguments = invocation.getArguments();

				return new TestLiferayOAuth2Scope(
					(String)arguments[1], (String)arguments[2]);
			}
		);

		_oAuth2ApplicationScopeAliasesLocalServiceImpl.setScopeLocator(
			scopeLocator);
	}

	@Test
	public void testApplicationIsolation() {
		String applicationName1 = "Test.Application1";
		String applicationName2 = "Test.Application2";

		String[] scopes = {"everything.read", "everything.write"};

		String scopeAlias = "everything";

		BiFunction<String, String, String> scopeAliasFunction =
			(applicationName, scope) ->
				applicationName + StringPool.PERIOD + scope;

		Map<LiferayOAuth2Scope, Set<String>> liferayOAuth2ScopesScopeAliases =
			_exerciseBuilder(
				builder -> {
					builder.forApplication(
						applicationName1,
						applicationScopeAssigner ->
							applicationScopeAssigner.assignScope(
								scopes
							).mapToScopeAlias(
								scopeAliasFunction.apply(
									applicationName1, scopeAlias)
							));

					builder.forApplication(
						applicationName2,
						applicationScopeAssigner ->
							applicationScopeAssigner.assignScope(
								scopes
							).mapToScopeAlias(
								scopeAliasFunction.apply(
									applicationName2, scopeAlias)
							));
				});

		liferayOAuth2ScopesScopeAliases.forEach(
			(key, value) -> {
				Assert.assertThat(value, not(hasItems(scopes)));
				Assert.assertEquals(1, value.size());
				Assert.assertThat(
					value,
					hasItems(
						scopeAliasFunction.apply(
							key.getApplicationName(), scopeAlias)));
			});
	}

	@Test
	public void testApplicationIsolationWithScopeAliases() {
		String[] scopes = {"everything.read", "everything.write"};

		Map<String, Set<String>> applicationScopeAlias =
			HashMapBuilder.<String, Set<String>>put(
				"Test.Application1", Sets.newHashSet("application1.everything")
			).put(
				"Test.Application2", Sets.newHashSet("application2.everything")
			).build();

		Map<LiferayOAuth2Scope, Set<String>> liferayOAuth2ScopesScopeAliases =
			_exerciseBuilder(
				builder -> applicationScopeAlias.forEach(
					(applicationName, scopeAliases) -> builder.forApplication(
						applicationName,
						applicationScopeAssigner ->
							applicationScopeAssigner.assignScope(
								scopes
							).mapToScopeAlias(
								scopeAliases
							))));

		liferayOAuth2ScopesScopeAliases.forEach(
			(key, value) -> {
				Assert.assertThat(value, not(hasItems(scopes)));
				Assert.assertEquals(
					value, applicationScopeAlias.get(key.getApplicationName()));
			});
	}

	@Test
	public void testMapToScopeAlias() {
		String applicationName = "Test.Application";

		String[] scopes = {"everything.read", "everything.write"};

		String scopeAlias = "everything";

		Map<LiferayOAuth2Scope, Set<String>> liferayOAuth2ScopesScopeAliases =
			_exerciseBuilder(
				builder -> builder.forApplication(
					applicationName,
					applicationScopeAssigner ->
						applicationScopeAssigner.assignScope(
							scopes
						).mapToScopeAlias(
							scopeAlias
						)));

		liferayOAuth2ScopesScopeAliases.forEach(
			(key, value) -> {
				Assert.assertThat(value, not(hasItems(scopes)));
				Assert.assertEquals(1, value.size());
				Assert.assertThat(value, hasItems(scopeAlias));
			});
	}

	@Test
	public void testMultipleSeparateAssignmentsToSameScopeAlias() {
		String applicationName = "Test.Application";

		Set<String> scopes = Sets.newHashSet(
			"everything.read", "everything.write");

		Collection<String> scopeAlias = Collections.singleton("everything");

		Map<LiferayOAuth2Scope, Set<String>> liferayOAuth2ScopesScopeAliases =
			_exerciseBuilder(
				builder -> builder.forApplication(
					applicationName,
					applicationScopeAssigner -> {
						applicationScopeAssigner.assignScope(
							"everything.read"
						).mapToScopeAlias(
							scopeAlias
						);

						applicationScopeAssigner.assignScope(
							"everything.write"
						).mapToScopeAlias(
							scopeAlias
						);
					}));

		Assert.assertEquals(
			liferayOAuth2ScopesScopeAliases.toString(), 2,
			liferayOAuth2ScopesScopeAliases.size());

		liferayOAuth2ScopesScopeAliases.forEach(
			(key, value) -> {
				Assert.assertEquals(scopeAlias, value);

				scopes.remove(key.getScope());
			});

		Assert.assertEquals(scopes.toString(), 0, scopes.size());
	}

	@Test
	public void testNoSpecifiedScopeAlias() {
		String applicationName = "Test.Application";

		String[] scopesArray = {"everything.read", "everything.write"};

		Set<String> scopes = Sets.newHashSet(scopesArray);

		// Test assigning scopes using Collection

		Map<LiferayOAuth2Scope, Set<String>> liferayOAuth2ScopesScopeAliases1 =
			_exerciseBuilder(
				builder -> builder.forApplication(
					applicationName,
					applicationScopeAssigner ->
						applicationScopeAssigner.assignScope(scopes)));

		// Test that the resulting scope aliases all map to all scopes

		liferayOAuth2ScopesScopeAliases1.forEach(
			(key, value) -> Assert.assertEquals(value, scopes));

		// Repeat using VarArgs

		Map<LiferayOAuth2Scope, Set<String>> liferayOAuth2ScopesScopeAliases2 =
			_exerciseBuilder(
				builder -> builder.forApplication(
					applicationName,
					applicationScopeAssigner ->
						applicationScopeAssigner.assignScope(scopesArray)));

		// Assert the result is identical

		Assert.assertEquals(
			liferayOAuth2ScopesScopeAliases2.toString(),
			liferayOAuth2ScopesScopeAliases1.size(),
			liferayOAuth2ScopesScopeAliases2.size());

		Set<Map.Entry<LiferayOAuth2Scope, Set<String>>> entrySet =
			liferayOAuth2ScopesScopeAliases1.entrySet();

		Stream<Map.Entry<LiferayOAuth2Scope, Set<String>>> stream =
			entrySet.stream();

		Assert.assertTrue(
			stream.allMatch(
				entry -> Objects.equals(
					entry.getValue(),
					liferayOAuth2ScopesScopeAliases2.get(entry.getKey()))));

		// Test separate calls result in each scope mapping to a different scope
		// alias

		liferayOAuth2ScopesScopeAliases1 = _exerciseBuilder(
			builder -> builder.forApplication(
				applicationName,
				applicationScopeAssigner -> {
					applicationScopeAssigner.assignScope(scopesArray[0]);
					applicationScopeAssigner.assignScope(scopesArray[1]);
				}));

		liferayOAuth2ScopesScopeAliases1.forEach(
			(key, value) -> Assert.assertEquals(
				Collections.singleton(key.getScope()), value));
	}

	public class TestLiferayOAuth2Scope implements LiferayOAuth2Scope {

		public TestLiferayOAuth2Scope(String applicationName, String scope) {
			_applicationName = applicationName;
			_scope = scope;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			LiferayOAuth2Scope liferayOAuth2Scope = (LiferayOAuth2Scope)obj;

			if (Objects.equals(
					getApplicationName(),
					liferayOAuth2Scope.getApplicationName()) &&
				Objects.equals(getScope(), liferayOAuth2Scope.getScope())) {

				return true;
			}

			return false;
		}

		@Override
		public String getApplicationName() {
			return _applicationName;
		}

		@Override
		public Bundle getBundle() {
			return null;
		}

		@Override
		public String getScope() {
			return _scope;
		}

		@Override
		public int hashCode() {
			return Objects.hash(_applicationName, _scope);
		}

		private final String _applicationName;
		private final String _scope;

	}

	private Map<LiferayOAuth2Scope, Set<String>> _exerciseBuilder(
		Consumer<OAuth2Scope.Builder> builderConsumer) {

		Map<LiferayOAuth2Scope, List<String>> liferayOAuth2ScopesScopeAliases =
			new HashMap<>();

		OAuth2Scope.Builder builder =
			_oAuth2ApplicationScopeAliasesLocalServiceImpl.
				new OAuth2ScopeBuilderImpl(
					_companyId, liferayOAuth2ScopesScopeAliases);

		builderConsumer.accept(builder);

		Map<LiferayOAuth2Scope, Set<String>>
			liferayOAuth2ScopesScopeAliasesSet = new HashMap<>();

		liferayOAuth2ScopesScopeAliases.forEach(
			(key, value) -> liferayOAuth2ScopesScopeAliasesSet.put(
				key, new HashSet<>(value)));

		return liferayOAuth2ScopesScopeAliasesSet;
	}

	private long _companyId;
	private OAuth2ApplicationScopeAliasesLocalServiceImpl
		_oAuth2ApplicationScopeAliasesLocalServiceImpl;

}