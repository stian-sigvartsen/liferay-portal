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

package com.liferay.oauth2.provider.scopes.impl.feature;

import com.liferay.oauth2.provider.scopes.impl.jaxrs.OAuth2CompanySetterContainerRequestFilter;
import com.liferay.oauth2.provider.scopes.impl.jaxrs.OAuth2ThreadLocalScopeCheckerContainerResponseFilter;
import com.liferay.oauth2.provider.scopes.spi.OAuth2Grant;
import com.liferay.oauth2.provider.scopes.api.RequiresScope;
import com.liferay.oauth2.provider.scopes.api.ScopeChecker;
import com.liferay.oauth2.provider.scopes.spi.ScopeFinder;
import com.liferay.oauth2.provider.scopes.spi.ScopeMatcher;
import com.liferay.oauth2.provider.scopes.spi.ScopeMatcherFactory;
import com.liferay.oauth2.provider.scopes.api.ScopesDescriptionBundle;
import com.liferay.oauth2.provider.scopes.impl.methodallowedchecker.AnnotationMethodAllowedChecker;
import com.liferay.oauth2.provider.scopes.impl.jaxrs.OAuth2ResourceMethodCheckerContainerRequestFilter;
import com.liferay.oauth2.provider.scopes.impl.jaxrs.ScopeContextBundleContainerRequestFilter;
import com.liferay.oauth2.provider.scopes.liferay.api.ScopeContext;
import com.liferay.portal.kernel.util.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.util.ClassResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Provider
public class LiferayOauth2OSGiFeature implements Feature {

	private ScopeContext _scopeContext;
	private ScopeChecker _scopeChecker;
	private BundleContext _bundleContext;
	private ScopeMatcherFactory _scopeMatcherFactory;
	private Bundle _bundle;
	private String _name;
	private ServiceRegistration<ScopeFinder>
		_serviceRegistration;

	public LiferayOauth2OSGiFeature(
		ScopeContext scopeContext, ScopeChecker scopeChecker,
		ScopeMatcherFactory scopeMatcherFactory, Bundle bundle, String name) {

		_scopeContext = scopeContext;
		_scopeChecker = scopeChecker;
		_scopeMatcherFactory = scopeMatcherFactory;
		_bundle = bundle;
		_name = name;

		_bundleContext = _bundle.getBundleContext();
	}

	@Override
	public boolean configure(FeatureContext context) {
		context.register(
			new OAuth2CompanySetterContainerRequestFilter(_scopeContext),
			Priorities.AUTHORIZATION - 10);

		context.register(
			new ScopeContextBundleContainerRequestFilter(
				_scopeContext, _bundle, _name),
			Priorities.AUTHORIZATION - 9);

		context.register(
			new OAuth2ResourceMethodCheckerContainerRequestFilter(
				new AnnotationMethodAllowedChecker(_scopeChecker, true)),
			Priorities.AUTHORIZATION - 8);

		context.register(
			new OAuth2ThreadLocalScopeCheckerContainerResponseFilter(
				_scopeContext),
			Priorities.AUTHORIZATION - 8);

		ScopeFinder scopeFinder = new AnnotationGathererScopeFinder();

		context.register(scopeFinder, DynamicFeature.class);

		_serviceRegistration = _bundleContext.registerService(
			ScopeFinder.class, scopeFinder,
			new Hashtable<String, Object>(){{
				put("osgi.jaxrs.name", _name);
			}});

		return true;
	}

	public void close() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	private class AnnotationGathererScopeFinder
		implements ScopeFinder, DynamicFeature {

		Collection<String> _scopes = new TreeSet<>();
		Collection<ResourceBundleLoader> resourceBundleLoaders =
			new HashSet<>();

		@Override
		public OAuth2Grant findScopes(ScopeMatcher scopeMatcher) {
			return new DefaultOAuth2Grant(scopeMatcher);
		}

		@Override
		public ScopeMatcherFactory getDefaultScopeMatcherFactory() {
			return _scopeMatcherFactory;
		}

		@Override
		public void configure(
			ResourceInfo resourceInfo, FeatureContext context) {

			Class<?> resourceClass = resourceInfo.getResourceClass();

			ScopesDescriptionBundle scopesDescriptionBundle =
				resourceClass.getAnnotation(ScopesDescriptionBundle.class);

			if (scopesDescriptionBundle != null) {
				resourceBundleLoaders.add(
					new ClassResourceBundleLoader(
						scopesDescriptionBundle.value(), resourceClass));
			}

			Method resourceMethod = resourceInfo.getResourceMethod();

			RequiresScope requiresScope =
				resourceMethod.getAnnotation(RequiresScope.class);

			if (requiresScope != null) {
				_scopes.addAll(Arrays.asList(requiresScope.value()));
			}
		}

		private class DefaultOAuth2Grant implements OAuth2Grant {
			private final ScopeMatcher _scopeMatcher;
			Collection<String> scopes;
			ResourceBundleLoader _resourceBundleLoader;

			public DefaultOAuth2Grant(ScopeMatcher scopeMatcher) {
				_scopeMatcher = scopeMatcher;

				scopes = _scopeMatcher.filter(_scopes);
				_resourceBundleLoader = new AggregateResourceBundleLoader(
					resourceBundleLoaders.toArray(new ResourceBundleLoader[0]));
			}

			@Override
			public Collection<String> getNames() {
				return scopes;
			}

			@Override
			public Collection<String> getDescriptions(Locale locale) {
				Stream<String> stream = scopes.stream();

				ResourceBundle resourceBundle =
					_resourceBundleLoader.loadResourceBundle(locale);

				return stream.map(resourceBundle::getString
				).collect(Collectors.toList());
			}
		}
	}
}
