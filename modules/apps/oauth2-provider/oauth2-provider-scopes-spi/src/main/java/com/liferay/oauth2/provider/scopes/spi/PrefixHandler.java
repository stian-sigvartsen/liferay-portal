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

package com.liferay.oauth2.provider.scopes.spi;

import com.liferay.portal.kernel.util.StringPool;

import java.util.Collection;

public interface PrefixHandler {

	public static PrefixHandler merge(
		Collection<PrefixHandler> namespaceAdders) {

		PrefixHandler namespaceAdder = NULL_HANDLER;

		for (PrefixHandler na : namespaceAdders) {
			namespaceAdder = namespaceAdder.append(na);
		}

		return namespaceAdder;
	}

	public String addPrefix(String string);

	public default String removePrefix(String prefixed) {
		String namespace = addPrefix(StringPool.BLANK);

		if (!prefixed.startsWith(namespace)) {
			return prefixed;
		}

		return namespace.substring(namespace.length());
	}

	public default PrefixHandler append(PrefixHandler prefixHandler) {
		return string -> addPrefix(prefixHandler.addPrefix(string));
	}

	public default PrefixHandler prepend(PrefixHandler prefixHandler) {
		return string -> prefixHandler.addPrefix(addPrefix(string));
	}

	static PrefixHandler NULL_HANDLER = new PrefixHandler() {

		@Override
		public String addPrefix(String string) {
			return string;
		}

		@Override
		public PrefixHandler append(PrefixHandler prefixHandler) {
			return prefixHandler;
		}

		@Override
		public PrefixHandler prepend(PrefixHandler prefixHandler) {
			return prefixHandler;
		}

	};


}