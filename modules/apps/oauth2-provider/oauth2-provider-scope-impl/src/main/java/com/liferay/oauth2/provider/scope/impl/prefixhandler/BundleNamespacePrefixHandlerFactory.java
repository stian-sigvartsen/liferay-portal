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

package com.liferay.oauth2.provider.scope.impl.prefixhandler;

import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandler;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandlerFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

@Component(
	immediate = true, 
	configurationPid = "com.liferay.oauth2.provider.configuration."
		+ "BundleNamespacePrefixHandlerFactory",
	property = {
		"separator=" + StringPool.SLASH
	},
	configurationPolicy = ConfigurationPolicy.REQUIRE
)
public class BundleNamespacePrefixHandlerFactory implements
	PrefixHandlerFactory {
	
	private List<String> _excludedScope = new ArrayList<>();
	private String _separator = StringPool.SLASH;
	private String[] _serviceProperties;
	private boolean _includeBundleSymbolicName;
	
	public BundleNamespacePrefixHandlerFactory() {}
	
	public BundleNamespacePrefixHandlerFactory(
		BundleContext bundleContext, boolean includeBundleSymbolName, 
		String[] serviceProperties, String excludedScope, String separator) {
		
		_init(
			bundleContext, includeBundleSymbolName, serviceProperties, excludedScope, 
			separator);
	}
	
	private void _init(
		BundleContext bundleContext, boolean includeBundleSymbolicName, 
		String[] serviceProperties, String excludedScopeProperty, 
		String separator) {

		_bundleContext = bundleContext;
		
		_excludedScope.addAll(Arrays.asList(
			excludedScopeProperty.split(StringPool.COMMA)));

		_excludedScope.removeIf(Validator::isBlank);
		
		if (Validator.isNotNull(separator)) {
			_separator = separator;
		}
		
		_serviceProperties = serviceProperties;
		_includeBundleSymbolicName = includeBundleSymbolicName;
	}

	@Override
	public PrefixHandler create(Function<String,Object> serviceProperties) {
		
		ArrayList<String> parts = new ArrayList<>(_serviceProperties.length + 1);
		
		if (_includeBundleSymbolicName) {
			long bundleId = Long.parseLong(
				serviceProperties.apply("service.bundleid").toString());
			
			Bundle bundle = _bundleContext.getBundle(bundleId);

			parts.add(bundle.getSymbolicName());
		}
		
		for (String serviceProperty : _serviceProperties) {
			
			if (Validator.isBlank(serviceProperty)) {
				continue;
			}
			
			int modifiersStart = serviceProperty.indexOf(StringPool.SPACE);
			String modifiersString = StringPool.BLANK;
			
			if (modifiersStart > -1) {
				modifiersString = serviceProperty.substring(modifiersStart);
				serviceProperty = serviceProperty.substring(0, modifiersStart);
			}
				
			Object applyResult = 
				serviceProperties.apply(serviceProperty);
			
			if (applyResult != null) {
				parts.add(applyResult.toString());
				continue;
			}
			
			String propertiesFormat = 
				modifiersString.replaceAll(StringPool.SPACE, "\n");
			
			Properties modifiers = new Properties();
	        try {
				modifiers.load(new StringReader(propertiesFormat));
			} 
	        catch (IOException e) {}
	        
			parts.add(
				GetterUtil.getString(
					modifiers.getProperty("default"), 
					StringPool.BLANK));
		}

		PrefixHandler prefixHandler = create(parts.toArray(_EMPTY_STRING_ARRAY));

		return (target) -> {
			if (_excludedScope.contains(target)) {
				return target;
			}

			return prefixHandler.addPrefix(target);
		};
	}
	
	private String[] _EMPTY_STRING_ARRAY = new String[0];

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		boolean includeBundleSymbolicName = MapUtil.getBoolean(
			properties, "includeBundleSymbolicName");
		
		String excludedScopesProperty = MapUtil.getString(
			properties, "excluded.scope");
		
		Object servicePropertyObject = properties.get("serviceProperty");
		String[] serviceProperties;
		
		if (servicePropertyObject instanceof String[]) {
			serviceProperties = (String[])servicePropertyObject;
		}
		else if (servicePropertyObject != null) {
			serviceProperties = Collections.singletonList(
					servicePropertyObject.toString()).toArray(
							_EMPTY_STRING_ARRAY);
		}
		else {
			serviceProperties = _EMPTY_STRING_ARRAY;
		}

		Object separatorObject = properties.get("separator");

		String separator;
		if (Validator.isNotNull(separatorObject)) {
			separator = separatorObject.toString();
		}
		else {
			separator = null;
		}
		
		_init(
			bundleContext, includeBundleSymbolicName, serviceProperties, 
			excludedScopesProperty, separator);
	}

	private BundleContext _bundleContext;

	public PrefixHandler create(String ... prefixes) {
		StringBundler sb = new StringBundler(prefixes.length * 2);

		for (String namespace : prefixes) {
			sb.append(namespace);
			sb.append(_separator);
		}

		return (target) -> sb.toString() + target;
	}

}