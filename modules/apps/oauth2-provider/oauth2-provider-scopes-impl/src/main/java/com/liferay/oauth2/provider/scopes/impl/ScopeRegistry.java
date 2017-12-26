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

import com.liferay.oauth2.provider.scopes.liferay.api.RetentiveOAuth2Grant;
import com.liferay.oauth2.provider.scopes.liferay.api.ScopeFinderLocator;
import com.liferay.oauth2.provider.scopes.impl.scopematcher.RetentiveOAuth2GrantImpl;
import com.liferay.oauth2.provider.scopes.spi.OAuth2Grant;
import com.liferay.oauth2.provider.scopes.spi.ScopeFinder;
import com.liferay.oauth2.provider.scopes.spi.ScopeMatcher;
import com.liferay.oauth2.provider.scopes.spi.PrefixHandler;
import com.liferay.oauth2.provider.scopes.spi.PrefixHandlerFactory;
import com.liferay.oauth2.provider.scopes.spi.PrefixHandlerMapper;
import com.liferay.oauth2.provider.scopes.spi.PrefixHandlerMapperLocator;
import com.liferay.oauth2.provider.scopes.spi.ScopeMatcherFactory;
import com.liferay.osgi.service.tracker.collections.ServiceReferenceServiceTuple;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.model.Company;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

@Component(immediate = true, service = ScopeFinderLocator.class)
public class ScopeRegistry
	implements ScopeFinderLocator, PrefixHandlerMapperLocator {

	@Override
	public PrefixHandlerMapper locateMapper(Company company) {
		return propertyHolder -> {
			Object nameProperty = propertyHolder.get("osgi.jaxrs.name");

			String name = nameProperty.toString();

			long companyId = company.getCompanyId();

			PrefixHandlerMapper namespaceAdder =
				_namespacesByCompanyAndName.getService(companyId + '-' + name);

			if (namespaceAdder != null) {
				return namespaceAdder.mapFrom(propertyHolder);
			}

			namespaceAdder = _namespacesByName.getService(name);

			if (namespaceAdder != null) {
				return namespaceAdder.mapFrom(propertyHolder);
			}

			namespaceAdder = _namespacesByCompany.getService(
				Long.toString(companyId));

			if (namespaceAdder != null) {
				return namespaceAdder.mapFrom(propertyHolder);
			}

			return _defaultNamespaceAdderMapper.mapFrom(propertyHolder);
		};
	}

	@Override
	public Collection<RetentiveOAuth2Grant> locateScopes(
		Company company, String scope) {

		Collection<RetentiveOAuth2Grant> grants = new ArrayList<>();

		Set<String> names = _scopeFinderByNameServiceTrackerMap.keySet();

		for (String name : names) {
			ServiceReferenceServiceTuple<?, ScopeFinder> tuple =
				_scopeFinderByNameServiceTrackerMap.getService(name);

			PrefixHandlerMapper namespaceAdderMapper = locateMapper(
				company);

			ServiceReference<?> serviceReference = tuple.getServiceReference();

			PrefixHandler namespaceAdder = namespaceAdderMapper.mapFrom(
				serviceReference::getProperty);

			ScopeFinder scopeFinder = tuple.getService();

			ScopeMatcherFactory scopeMatcherFactory =
				scopeFinder.getDefaultScopeMatcherFactory();

			ScopeMatcher scopeMatcher = scopeMatcherFactory.create(scope);

			OAuth2Grant oAuth2Grant = scopeFinder.findScopes(
				scopeMatcher.prepend(namespaceAdder));

			grants.add(
				new RetentiveOAuth2GrantImpl(
					company.getCompanyId(), serviceReference.getBundle(),
					serviceReference.getProperty("osgi.jaxrs.name").toString(),
					oAuth2Grant));
		}

		return grants;
	}

	@Override
	public Collection<RetentiveOAuth2Grant> listScopes(Company company) {
		Collection<RetentiveOAuth2Grant> grants = new ArrayList<>();

		Set<String> names = _scopeFinderByNameServiceTrackerMap.keySet();

		for (String name : names) {
			ServiceReferenceServiceTuple<?, ScopeFinder> tuple =
				_scopeFinderByNameServiceTrackerMap.getService(name);

			ServiceReference<?> serviceReference =
				tuple.getServiceReference();

			ScopeFinder scopeFinder = tuple.getService();

			OAuth2Grant oAuth2Grant = scopeFinder.findScopes(__ -> true);

			grants.add(
				new RetentiveOAuth2GrantImpl(
					company.getCompanyId(), serviceReference.getBundle(),
					serviceReference.getProperty("osgi.jaxrs.name").toString(),
					oAuth2Grant));
		}

		return grants;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_scopeFinderByNameServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, ScopeFinder.class, "osgi.jaxrs.name",
				new ScopeFinderServiceTupleServiceTrackerCustomizer(
					bundleContext));

		_namespacesByCompany = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, PrefixHandlerMapper.class, "companyId");

		_namespacesByName = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, PrefixHandlerMapper.class, "osgi.jaxrs.name");

		_namespacesByCompanyAndName =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, PrefixHandlerMapper.class,
				"(&(companyId=*)(osgi.jaxrs.name=*))",
				(serviceReference, emitter) -> {
					ServiceReferenceMapper<String, PrefixHandlerMapper>
						companyMapper = new PropertyServiceReferenceMapper<>(
							"companyId");
					ServiceReferenceMapper<String, PrefixHandlerMapper>
						nameMapper = new PropertyServiceReferenceMapper<>(
							"osgi.jaxrs.name");

					companyMapper.map(
						serviceReference,
						key1 -> nameMapper.map(
							serviceReference,
							key2 -> emitter.emit(key1 + "-" + key2)));
				});
	}

	@Reference(
		target = "(default=true)",
		policyOption = ReferencePolicyOption.GREEDY
	)
	private PrefixHandlerMapper _defaultNamespaceAdderMapper;

	@Reference
	private PrefixHandlerFactory _namespaceAdderFactory;

	private ServiceTrackerMap<String, PrefixHandlerMapper>
		_namespacesByCompany;
	private ServiceTrackerMap<String, PrefixHandlerMapper>
		_namespacesByCompanyAndName;
	private ServiceTrackerMap<String, PrefixHandlerMapper> _namespacesByName;
	private ServiceTrackerMap<
		String, ServiceReferenceServiceTuple<?, ScopeFinder>>
			_scopeFinderByNameServiceTrackerMap;

	private static class ScopeFinderServiceTupleServiceTrackerCustomizer
		implements
		ServiceTrackerCustomizer
			<ScopeFinder, ServiceReferenceServiceTuple<?, ScopeFinder>> {

		private BundleContext _bundleContext;

		public ScopeFinderServiceTupleServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public ServiceReferenceServiceTuple<?, ScopeFinder> addingService(
			ServiceReference<ScopeFinder> reference) {

			ScopeFinder scopeFinder = _bundleContext.getService(reference);

			return new ServiceReferenceServiceTuple<>(reference, scopeFinder);
		}

		@Override
		public void modifiedService(
			ServiceReference<ScopeFinder> reference,
			ServiceReferenceServiceTuple<?, ScopeFinder> tuple) {
		}

		@Override
		public void removedService(
			ServiceReference<ScopeFinder> reference,
			ServiceReferenceServiceTuple<?, ScopeFinder> tuple) {

			_bundleContext.ungetService(reference);
		}

	}

}