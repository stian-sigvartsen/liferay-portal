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

package com.liferay.oauth2.provider.scope.impl.scopematcher;

import com.liferay.oauth2.provider.scope.spi.scope.matcher.ScopeMatcher;
import com.liferay.oauth2.provider.scope.spi.scope.matcher.ScopeMatcherFactory;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.impl.scope.ChunkScopeMatcherFactory",
	property = {"default=true", "separator=" + StringPool.PERIOD, "type=chunks"}
)
public class ChunkScopeMatcherFactory implements ScopeMatcherFactory {

	@Override
	public ScopeMatcher create(String input) {
		String[] inputParts = StringUtil.split(input, _separator);

		if (inputParts.length == 0) {
			return ScopeMatcher.NONE;
		}

		return new ChunkScopeMatcher(input, inputParts);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		Object separator = properties.get("separator");

		_separator = separator.toString();

		if (Validator.isNull(_separator)) {
			throw new IllegalArgumentException(
				"separator property can't be null");
		}
	}

	private String _separator = StringPool.PERIOD;

	private class ChunkScopeMatcher implements ScopeMatcher {

		@Override
		public boolean match(String name) {
			String[] scopeParts = StringUtil.split(name, _separator);

			if (scopeParts.length < _inputParts.length) {
				return false;
			}

			for (int i = 0; i < _inputParts.length; i++) {
				String inputPart = _inputParts[i];

				if (inputPart.equals(scopeParts[i])) {
					continue;
				}

				return false;
			}

			return true;
		}

		private ChunkScopeMatcher(String input, String[] inputParts) {
			_input = input;
			_inputParts = inputParts;
		}

		private final String _input;
		private final String[] _inputParts;

	}

}