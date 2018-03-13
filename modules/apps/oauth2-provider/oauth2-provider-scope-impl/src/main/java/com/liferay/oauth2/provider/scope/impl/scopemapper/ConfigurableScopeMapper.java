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

package com.liferay.oauth2.provider.scope.impl.scopemapper;

import com.liferay.oauth2.provider.scope.internal.configuration.ConfigurableScopeMapperConfiguration;
import com.liferay.oauth2.provider.scope.spi.scope.mapper.ScopeMapper;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.scope.internal.configuration.ConfigurableScopeMapperConfiguration",
	configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true,
	property = {"default=true"}
)
public class ConfigurableScopeMapper implements ScopeMapper {

	@Override
	public Set<String> map(String scope) {
		Set<String> mappedScopes = _mapping.get(scope);

		Set<String> result = new HashSet<>();

		if (mappedScopes != null) {
			result.addAll(mappedScopes);
		}
		else {
			result.add(scope);
		}

		if (_passtrough) {
			result.add(scope);
		}

		return result;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		ConfigurableScopeMapperConfiguration
			configurableScopeMapperConfiguration =
				ConfigurableUtil.createConfigurable(
					ConfigurableScopeMapperConfiguration.class, properties);

		_passtrough = configurableScopeMapperConfiguration.passthrough();

		for (String mapping : configurableScopeMapperConfiguration.mapping()) {
			String[] mappingParts = StringUtil.split(mapping, StringPool.EQUAL);

			if (mappingParts.length != 2) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid ConfigurableScopeMapperConfiguration " +
							"mapping " + mapping);
				}

				continue;
			}

			String[] keys = StringUtil.split(mappingParts[0]);
			String[] values = StringUtil.split(mappingParts[1]);

			for (String key : keys) {
				Set<String> keyValuesSet = _mapping.computeIfAbsent(
					key, __ -> new HashSet<>());

				Collections.addAll(keyValuesSet, values);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurableScopeMapper.class);

	private final Map<String, Set<String>> _mapping = new HashMap<>();
	private boolean _passtrough;

}