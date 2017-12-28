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

package com.liferay.oauth2.provider.scopes.impl;

import com.liferay.oauth2.provider.scopes.impl.scopematcher.ChunkScopeMatcherFactory;
import com.liferay.oauth2.provider.scopes.impl.prefixhandler.DefaultPrefixHandlerFactory;
import com.liferay.oauth2.provider.scopes.spi.OAuth2Grant;
import com.liferay.oauth2.provider.scopes.spi.ScopeFinder;
import com.liferay.oauth2.provider.scopes.spi.ScopeMapper;
import com.liferay.oauth2.provider.scopes.spi.ScopeMatcher;
import com.liferay.oauth2.provider.scopes.spi.PrefixHandler;
import com.liferay.oauth2.provider.scopes.spi.PrefixHandlerFactory;
import com.liferay.oauth2.provider.scopes.spi.ScopeMatcherFactory;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScopeMatcherTest {

	@Test
	public void testFindScopes() {
		ScopeFinder testScopeFinder = new TestHierarchyScopeFinder();

		assertEquals(
			Arrays.asList("RO"),
			testScopeFinder.findScopes("RO"::equals).getNames());

		assertEquals(
			Arrays.asList("RO", "RW"),
			testScopeFinder.findScopes("RW"::equals).getNames());
	}

	@Test
	public void testFindScopesWithMapper() {
		ScopeFinder testScopeFinder = new TestHierarchyScopeFinder();

		ScopeMatcher scopeMatcher = ScopeMatcherFactory.STRICT.create("RO2");

		scopeMatcher = scopeMatcher.withMapper(new TestScopeMapper());

		assertEquals(
			Arrays.asList("RO"),
			testScopeFinder.findScopes(scopeMatcher).getNames());

		scopeMatcher = ScopeMatcherFactory.STRICT.create("RW2");

		scopeMatcher = scopeMatcher.withMapper(new TestScopeMapper());

		assertEquals(
			Arrays.asList("RO", "RW"),
			testScopeFinder.findScopes(scopeMatcher).getNames());
	}

	@Test
	public void testFindScopesWithNamespace() {
		ScopeFinder testScopeFinder = new TestHierarchyScopeFinder();

		PrefixHandlerFactory namespaceAdderFactory =
			new DefaultPrefixHandlerFactory();

		PrefixHandler namespaceAdder = namespaceAdderFactory.create("TEST");

		ScopeMatcher scopeMatcher = "TEST_RO"::equals;

		assertEquals(
			Arrays.asList("RO"),
			testScopeFinder.findScopes(
				scopeMatcher.prepend(namespaceAdder)).getNames());

		scopeMatcher = "TEST_RW"::equals;

		assertEquals(
			Arrays.asList("RO", "RW"),
			testScopeFinder.findScopes(
				scopeMatcher.prepend(namespaceAdder)).getNames());
	}

	@Test
	public void testFindScopesWithMultipleNamespaces() {
		ScopeFinder testScopeFinder = new TestHierarchyScopeFinder();

		PrefixHandlerFactory namespaceAdderFactory =
			new DefaultPrefixHandlerFactory();

		PrefixHandler namespaceAdder = namespaceAdderFactory.create("TEST");

		PrefixHandler nestedNamespaceAdder = namespaceAdderFactory.create(
			"NESTED");

		namespaceAdder = namespaceAdder.append(nestedNamespaceAdder);

		ScopeMatcher scopeMatcher = "TEST_NESTED_RO"::equals;

		assertEquals(
			Arrays.asList("RO"),
			testScopeFinder.findScopes(
				scopeMatcher.prepend(namespaceAdder)).getNames());

		scopeMatcher = "TEST_NESTED_RW"::equals;

		assertEquals(
			Arrays.asList("RO", "RW"),
			testScopeFinder.findScopes(
				scopeMatcher.prepend(namespaceAdder)).getNames());
	}

	@Test
	public void testFindScopesWithPrefixMatcher() {
		ScopeFinder scopeFinder = new TestTestAllScopeFinder(
			"everything", "everything.readonly");

		assertEquals(
			Arrays.asList("everything", "everything.readonly"),
			scopeFinder.findScopes(s -> s.startsWith("everything")).getNames());

		assertEquals(
			Arrays.asList("everything.readonly"),
			scopeFinder.findScopes(
				s -> s.startsWith("everything.readonly")).getNames());
	}

	@Test
	public void testFindScopesWithPrefixMatcherAndNamespace() {
		ScopeFinder scopeFinder = new TestTestAllScopeFinder(
			"everything", "everything.readonly");

		PrefixHandlerFactory namespaceAdderFactory =
			namespace -> localName -> namespace + "/" + localName;

		PrefixHandler liferayNamespaceAdder =
			namespaceAdderFactory.create("http://www.liferay.com");

		PrefixHandler namespaceAdder =
			liferayNamespaceAdder.append(namespaceAdderFactory.create("apio"));

		ScopeMatcher scopeMatcher = new ChunkScopeMatcherFactory().create(
			"http://www.liferay.com/apio/everything");

		assertEquals(
			Arrays.asList("everything", "everything.readonly"),
			scopeFinder.findScopes(
				scopeMatcher.prepend(namespaceAdder)).getNames());

		scopeMatcher = new ChunkScopeMatcherFactory().create(
			"http://www.liferay.com/apio/everything.readonly");

		assertEquals(
			Arrays.asList("everything.readonly"),
			scopeFinder.findScopes(
				scopeMatcher.prepend(namespaceAdder)).getNames());
	}

	@Test
	public void testScopeFinderReturnAll() {
		String[] strings =
			{"everything", "everything.readonly", "another"};

		ScopeFinder scopeFinder = new TestTestAllScopeFinder(strings);

		OAuth2Grant grant = scopeFinder.findScopes(__ -> true);

		assertTrue(grant.getNames().containsAll(Arrays.asList(strings)));
	}

	public static class TestHierarchyScopeFinder implements ScopeFinder {

		@Override
		public OAuth2Grant findScopes(ScopeMatcher scopeMatcher) {
			Set<String> strings = new TreeSet<>();

			if (scopeMatcher.match("RO")) {
				strings.add("RO");
			}
			if (scopeMatcher.match("RW")) {
				strings.addAll(Arrays.asList("RO", "RW"));
			}
			return new TestOAuth2Grant(
				0, "Test", "test", "0.0.0", strings.toArray(new String[]{}));
		}
	}

	public static class TestTestAllScopeFinder implements ScopeFinder {

		private final Set<String> _scopes;

		public TestTestAllScopeFinder(String ... scopes) {
			_scopes = new TreeSet<>(Arrays.asList(scopes));
		}

		@Override
		public OAuth2Grant findScopes(ScopeMatcher scopeMatcher) {
			return new OAuth2Grant() {
				List<String> strings =
					_scopes.stream().filter(scopeMatcher::match).collect(
						Collectors.toList());

				@Override
				public Collection<String> getNames() {
					return strings;
				}

				@Override
				public Collection<String> getDescriptions(Locale locale) {
					return strings;
				}
			};
		}
	}

	private static class TestScopeMapper implements ScopeMapper {

		@Override
		public String map(String s) {
			switch (s) {
				case "RW":
					return "RW2";
				case "RO":
					return "RO2";
			}

			return s;
		}
	}
}
