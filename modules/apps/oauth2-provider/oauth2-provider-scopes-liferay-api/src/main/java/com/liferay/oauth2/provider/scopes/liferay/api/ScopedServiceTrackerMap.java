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

package com.liferay.oauth2.provider.scopes.liferay.api;

import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapListener;
import com.liferay.portal.kernel.util.StringBundler;
import org.osgi.framework.BundleContext;

import java.util.List;
import java.util.function.Supplier;

public class ScopedServiceTrackerMap<T> {

	private Supplier<T> _defaultServiceSupplier;
	private Runnable _onChange;

	public ScopedServiceTrackerMap(
		BundleContext bundleContext, Class<T> clazz, String property,
		Supplier<T> defaultServiceSupplier) {

		this(bundleContext, clazz, property, defaultServiceSupplier, () -> {});
	}

	public ScopedServiceTrackerMap(
		BundleContext bundleContext, Class<T> clazz, String property,
		Supplier<T> defaultServiceSupplier, Runnable onChange) {

		_defaultServiceSupplier = defaultServiceSupplier;
		_onChange = onChange;

		_servicesByCompany = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, clazz,
			"(&(companyId=*)(!(" + property + "=*)))",
			new PropertyServiceReferenceMapper<>("companyId"),
			new ServiceTrackerMapListenerImpl<>());

		_servicesByKey = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, clazz,
			"(&(" + property + "=*)(!(companyId=*)))",
			new PropertyServiceReferenceMapper<>(property),
			new ServiceTrackerMapListenerImpl<>());

		_servicesByCompanyAndKey =
			ServiceTrackerMapFactory.openMultiValueMap(
				bundleContext, clazz,
				"(&(companyId=*)(" + property + "=*))",
				(serviceReference, emitter) -> {
					ServiceReferenceMapper<Long, T>
						companyMapper = new PropertyServiceReferenceMapper<>(
							"companyId");
					ServiceReferenceMapper<String, T>
						nameMapper = new PropertyServiceReferenceMapper<>(
							property);

					companyMapper.map(
						serviceReference,
						key1 -> nameMapper.map(
							serviceReference,
							key2 -> emitter.emit(
								String.join("-", Long.toString(key1), key2))));
				},
				new ServiceTrackerMapListenerImpl<>());
	}

	public T getService(long companyId, String key) {
		List<T> services = _servicesByCompanyAndKey.getService(
			String.join("-", Long.toString(companyId), key));

		if (services != null && !services.isEmpty()) {
			return services.get(0);
		}

		services = _servicesByKey.getService(key);

		if (services != null && !services.isEmpty()) {
			return services.get(0);
		}

		services = _servicesByCompany.getService(companyId);

		if (services != null && !services.isEmpty()) {
			return services.get(0);
		}

		return _defaultServiceSupplier.get();
	}

	public void close() {
		_servicesByCompany.close();
		_servicesByCompanyAndKey.close();
		_servicesByKey.close();
	}

	private ServiceTrackerMap<Long, List<T>> _servicesByCompany;
	private ServiceTrackerMap<String, List<T>> _servicesByCompanyAndKey;
	private ServiceTrackerMap<String, List<T>> _servicesByKey;

	private class ServiceTrackerMapListenerImpl<I, T>
		implements ServiceTrackerMapListener<I, T, List<T>> {
		@Override
		public void keyEmitted(
			ServiceTrackerMap<I, List<T>> serviceTrackerMap,
			I key,
			T service, List<T> content) {

			_onChange.run();
		}

		@Override
		public void keyRemoved(
			ServiceTrackerMap<I, List<T>> serviceTrackerMap,
			I key,
			T service, List<T> content) {

			_onChange.run();
		}
	}
}