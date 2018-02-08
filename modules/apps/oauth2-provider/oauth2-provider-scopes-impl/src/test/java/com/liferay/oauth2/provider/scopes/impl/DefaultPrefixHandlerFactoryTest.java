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

import static org.junit.Assert.assertEquals;


import java.util.Arrays;

import com.liferay.oauth2.provider.scopes.impl.prefixhandler.DefaultPrefixHandlerFactory;
import com.liferay.oauth2.provider.scopes.spi.PrefixHandler;
import com.liferay.oauth2.provider.scopes.spi.PrefixHandlerFactory;
import org.junit.Test;

public class DefaultPrefixHandlerFactoryTest {

	@Test
	public void testMerge() {
		PrefixHandlerFactory defaultPrefixHandlerFactory =
			new DefaultPrefixHandlerFactory();

		PrefixHandler prefixHandler1 = defaultPrefixHandlerFactory.create(
			"HELLO");
		PrefixHandler prefixHandler2 = defaultPrefixHandlerFactory.create(
			"BEAUTIFUL");
		PrefixHandler prefixHandler3 = defaultPrefixHandlerFactory.create(
			"COLOURFUL");

		PrefixHandler prefixHandler = PrefixHandler.merge(
			Arrays.asList(prefixHandler1, prefixHandler2, prefixHandler3));

		String prefixed = prefixHandler.addPrefix("WORLD");

		assertEquals("HELLO/BEAUTIFUL/COLOURFUL/WORLD", prefixed);
	}

	@Test
	public void testCreateWithSeveralStrings() {
		DefaultPrefixHandlerFactory defaultPrefixHandlerFactory =
			new DefaultPrefixHandlerFactory();

		PrefixHandler prefixHandler = defaultPrefixHandlerFactory.create(
			"HELLO", "BEAUTIFUL", "COLOURFUL");

		assertEquals("HELLO/BEAUTIFUL/COLOURFUL/WORLD", prefixHandler.addPrefix(
			"WORLD"));
	}

	@Test
	public void testRemovePrefix() {
		DefaultPrefixHandlerFactory defaultPrefixHandlerFactory =
			new DefaultPrefixHandlerFactory();

		PrefixHandler prefixHandler = defaultPrefixHandlerFactory.create(
			"TEST");

		assertEquals("HELLO", prefixHandler.removePrefix("HELLO"));
		assertEquals("HELLO", prefixHandler.removePrefix("TEST/HELLO"));
		assertEquals("TEST/HELLO", prefixHandler.removePrefix(
			"TEST/TEST/HELLO"));

		prefixHandler = prefixHandler.append(prefixHandler);

		assertEquals("HELLO", prefixHandler.removePrefix(
			"TEST/TEST/HELLO"));

		prefixHandler = defaultPrefixHandlerFactory.create("TEST");

		prefixHandler = prefixHandler.append(
			defaultPrefixHandlerFactory.create("TEST2"));

		assertEquals("HELLO", prefixHandler.removePrefix(
			"TEST/TEST2/HELLO"));

		assertEquals("TEST2/TEST/HELLO", prefixHandler.removePrefix(
			"TEST2/TEST/HELLO"));

		prefixHandler = defaultPrefixHandlerFactory.create("TEST");

		prefixHandler = prefixHandler.prepend(
			defaultPrefixHandlerFactory.create("TEST2"));

		assertEquals("HELLO", prefixHandler.removePrefix(
			"TEST2/TEST/HELLO"));

		assertEquals("TEST/TEST2/HELLO", prefixHandler.removePrefix(
			"TEST/TEST2/HELLO"));

	}

}