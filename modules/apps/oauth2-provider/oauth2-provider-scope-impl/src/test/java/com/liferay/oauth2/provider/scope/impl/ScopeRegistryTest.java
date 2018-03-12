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

package com.liferay.oauth2.provider.scope.impl;

import com.liferay.oauth2.provider.scope.impl.scopematcher.StrictScopeMatcherFactory;
import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scope.liferay.ScopedServiceTrackerMap;
import com.liferay.oauth2.provider.scope.spi.scope.matcher.ScopeMatcherFactory;
import com.liferay.osgi.service.tracker.collections.ServiceReferenceServiceTuple;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandler;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandlerFactory;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.oauth2.provider.scope.spi.scope.mapper.ScopeMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.ServiceReference;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RunWith(PowerMockRunner.class)
public class ScopeRegistryTest extends PowerMockito {

	private final long _companyId = 1;
	private final String _applicationName = "com.liferay.test1";	
	
	protected final Set<String> scopesSet1 = 
		new HashSet<>(Arrays.asList(new String[] {"everything", "everything.readonly"}));
	
	protected final Set<String> scopedSet2 = 
		new HashSet<>(Arrays.asList(new String[] {"GET", "POST"}));

	@Before
	public void setUp() 
		throws IllegalArgumentException, IllegalAccessException {}

	private interface CompanyAndKeyRegistrator<T> {
		public void register(Long companyId, String key, T service);
	}
	
	private interface CompanyAndKeyConfigurator<T> {
		public void configure(CompanyAndKeyRegistrator<T> registrator);
	}
	
	private interface KeyRegistrator<T> {
		public void register(String key, T service);
	}
	
	private interface KeyConfigurator<T> {
		public void configure(KeyRegistrator<T> registrator);
	}

	@Test
	public void testPrefixHandlerFactoryByNameAndCompany() throws Exception {
	
		String applicationName2 = "com.liferay.test2";

		PrefixHandler defaultPrefixHandler = (target) -> "default/" + target;
		
		ScopeFinder scopeFinder = () -> scopesSet1;
		
		ScopeRegistry scopeRegistry = new Builder()
			.withPrefixHandlerFactories(
				(propertyAccessor) -> defaultPrefixHandler, 
				(registrator) -> {})
			.withScopeFinders((registrator) -> {				
					registrator.register(_companyId, _applicationName, scopeFinder);
					registrator.register(_companyId, applicationName2, scopeFinder);
				})
			.build();
		
		Collection<String> application1ScopeAliases = 
			scopeRegistry.getScopeAliases(
				_companyId, _applicationName);

		Collection<String> application2ScopeAliases = 
			scopeRegistry.getScopeAliases(
				_companyId, applicationName2);
		
		for (String scope : scopesSet1) {
			
			assertThat(
				application1ScopeAliases, 
				hasItem(defaultPrefixHandler.addPrefix(scope)));

			assertThat(
				application2ScopeAliases, 
				hasItem(defaultPrefixHandler.addPrefix(scope)));
		}
		
		PrefixHandler appPrefixHandler = (target) -> "app/" + target;
		PrefixHandler companyPrefixHandler = (target) -> "company/" + target;
		
		scopeRegistry = new Builder()
			.withPrefixHandlerFactories(
				(propertyAccessor) -> defaultPrefixHandler, 
				(registrator) -> {
					registrator.register(null, _applicationName, (propertyAccessor) -> appPrefixHandler);
					registrator.register(_companyId, null, (propertyAccessor) -> companyPrefixHandler);
				})
			.withScopeFinders((registrator) -> {
					registrator.register(_companyId, _applicationName, scopeFinder);
					registrator.register(_companyId, applicationName2, scopeFinder);
				})			
			.build();
		
		application1ScopeAliases = 
			scopeRegistry.getScopeAliases(
				_companyId, _applicationName);

		application2ScopeAliases = 
			scopeRegistry.getScopeAliases(
				_companyId, applicationName2);

		for (String scope : scopesSet1) {
			
			assertThat(
					application1ScopeAliases, 
				hasItem(appPrefixHandler.addPrefix(scope)));

			assertThat(
				application2ScopeAliases, 
				hasItem(companyPrefixHandler.addPrefix(scope)));
		}
	}
	
	@Test
	public void testScopeFinderByName() throws Exception {
		
		String applicationName2 = "com.liferay.test2";
	
		ScopeFinder application1ScopeFinder = () -> scopesSet1;
		ScopeFinder application2ScopeFinder = () -> scopedSet2;
		
		ScopeRegistry scopeRegistry = new Builder()
			.withScopeFinders((registrator) -> {
					registrator.register(_companyId, _applicationName, application1ScopeFinder);
					registrator.register(_companyId, applicationName2, application2ScopeFinder);
				})
			.build();
		
		Collection<String> application1ScopeAliases = 
			scopeRegistry.getScopeAliases(
				_companyId, _applicationName);
	
		Collection<String> application2ScopesAliasesDefault = 
			scopeRegistry.getScopeAliases(
				_companyId, applicationName2);
		
		for (String scope : scopesSet1) {
			
			assertThat(
				application1ScopeAliases, 
				hasItem(scope));
		}
	
		for (String scope : scopedSet2) {
	
			assertThat(
				application2ScopesAliasesDefault, 
				hasItem(scope));
		}
		
		assertNotEquals(application1ScopeAliases, application2ScopesAliasesDefault);
	}

	@Test
	public void testScopeMapperByNameAndCompany() throws Exception {
		
		String applicationName2 = "com.liferay.test2";

		ScopeMapper defaultScopeMapper = ScopeMapper.PASSTHROUGH_SCOPEMAPPER;
		
		ScopeFinder scopeFinder = () -> scopesSet1;
		
		ScopeRegistry scopeRegistry = new Builder()
			.withScopeMappers(
				defaultScopeMapper, 
				(registrator) -> {})
			.withScopeFinders((registrator) -> {				
					registrator.register(_companyId, _applicationName, scopeFinder);
					registrator.register(_companyId, applicationName2, scopeFinder);
				})
			.build();
		
		Collection<String> application1ScopeAliases = 
			scopeRegistry.getScopeAliases(
				_companyId, _applicationName);

		Collection<String> application2ScopeAliases = 
			scopeRegistry.getScopeAliases(
				_companyId, applicationName2);
		
		for (String scope : scopesSet1) {
			
			assertThat(
				application1ScopeAliases, 
				hasItem(scope));

			assertThat(
				application2ScopeAliases, 
				hasItem(scope));
		}
		
		
		ScopeMapper appScopeMapper = (scope) -> Collections.singleton("app/" + scope);
		ScopeMapper companyScopeMapper = (scope) -> Collections.singleton("company/" + scope);
		
		scopeRegistry = new Builder()
			.withScopeMappers(
				defaultScopeMapper, 
				(registrator) -> {
					registrator.register(null, _applicationName, appScopeMapper);
					registrator.register(_companyId, null, companyScopeMapper);
				})
			.withScopeFinders((registrator) -> {
					registrator.register(_companyId, _applicationName, scopeFinder);
					registrator.register(_companyId, applicationName2, scopeFinder);
				})
			.build();
		
		Collection<String> application1ScopesAliases = 
			scopeRegistry.getScopeAliases(
				_companyId, _applicationName);

		Collection<String> application2ScopesAliases = 
			scopeRegistry.getScopeAliases(
				_companyId, applicationName2);

		for (String scope : scopesSet1) {
			
			assertThat(
				application1ScopesAliases, 
				hasItems(appScopeMapper.map(scope).toArray(new String[0])));

			assertThat(
				application2ScopesAliases, 
				hasItems(companyScopeMapper.map(scope).toArray(new String[0])));
		}
	}

	@Test
	public void testScopeMatcherByCompany() throws Exception {
		
		String applicationName2 = "com.liferay.test2";

		ScopeFinder service = () -> scopesSet1;
		
		Set<String> matchScopes = Collections.singleton("everything.readonly");
		
		ScopeMatcherFactory explicitScopeMatcherFactory = 
			(scopeAlias) -> 
				(scope) -> 
					scope.equals(scopeAlias) && matchScopes.contains(scope);
		
		ScopeRegistry scopeRegistry = new Builder()
			.withScopeFinders((registrator) -> {				
					registrator.register(_companyId, _applicationName, service);
					registrator.register(_companyId, applicationName2, service);
				})
			.withScopeMatcherFactories(
				(scopeAlias) -> scopeAlias::equals,
				(registrator) -> {
					registrator.register(Long.toString(_companyId), explicitScopeMatcherFactory);
				})
			.build();
		
		Collection<LiferayOAuth2Scope> matchedLiferayOAuth2Scopes = 
			scopeRegistry.getLiferayOAuth2Scopes(
				_companyId, "everything", _applicationName);
		
		Set<String> matchedScopes = _getScopes(matchedLiferayOAuth2Scopes);
		
		assertFalse(matchedScopes.contains("everything"));

		matchedLiferayOAuth2Scopes = 
			scopeRegistry.getLiferayOAuth2Scopes(
				_companyId, "everything.readonly", _applicationName);
		
		matchedScopes = _getScopes(matchedLiferayOAuth2Scopes);
		
		assertTrue(matchedScopes.contains("everything.readonly"));
	}

	@Test
	public void testScopeMatcherIsolatedFromPrefixHanderFactory() throws Exception {

		PrefixHandlerFactory testPrefixHandlerFactory = 
			(propertyAccessor) -> (target) -> "test/" + target;
		
		final ScopeMatcherFactory scopeMatcherFactory =
			Mockito.spy(new StrictScopeMatcherFactory());
		
		ScopeRegistry scopeRegistry = new Builder()
			.withPrefixHandlerFactories(
				(propertyAccessor) -> PrefixHandler.PASSTHROUGH_PREFIXHANDLER, 
				(registrator) -> {
					registrator.register(_companyId, _applicationName, testPrefixHandlerFactory);
				})
			.withScopeMatcherFactories(
				(scopeAlias) -> scopeAlias::equals,
				(registrator) -> { 
					registrator.register(Long.toString(_companyId), scopeMatcherFactory);
				})
			.withScopeFinders((registrator) -> {
				registrator.register(
					_companyId, _applicationName, () -> scopesSet1);
				})			
			.build();

		Collection<LiferayOAuth2Scope> matchedLiferayOAuth2Scopes = 
			scopeRegistry.getLiferayOAuth2Scopes(
				_companyId, "test/everything", _applicationName);

		Mockito.verify(scopeMatcherFactory, atLeast(1)).create("everything");
		
		Set<String> matchedScopes = _getScopes(matchedLiferayOAuth2Scopes);
		
		assertTrue(matchedScopes.contains("everything"));		
	}
	
	private Set<String> _getScopes(Collection<LiferayOAuth2Scope> liferayOAuth2Scopes) {
		return
			liferayOAuth2Scopes.stream().flatMap(
				liferayOAuth2Scope -> 
					Collections.singleton(
						liferayOAuth2Scope.getScope()).stream()
			).collect(
				Collectors.toSet()
			);
	}
	
	private class Builder {
		
		private boolean _prefixHandlerFactoriesInitialized = false;
		private boolean _scopeFindersInitialized = false;
		private boolean _scopeMappersInitialized = false;
		private boolean _scopeMatcherFactoriesInitialized = false;
		
		private ScopeRegistry _scopeRegistry = new ScopeRegistry();
		
		public Builder withScopeFinders(CompanyAndKeyConfigurator<ScopeFinder> configurator) 
			throws IllegalArgumentException, IllegalAccessException {
			
			ServiceTrackerMap<String, List<ServiceReferenceServiceTuple<?, ScopeFinder>>> 
				scopeFinderByNameServiceTrackerMap = 
					Mockito.mock(ServiceTrackerMap.class);

			_scopeRegistry.setScopeFinderByNameServiceTrackerMap(
				scopeFinderByNameServiceTrackerMap);

			ScopedServiceTrackerMap<ScopeFinder> scopedScopeFinder = 
				Mockito.mock(ScopedServiceTrackerMap.class);

			_scopeRegistry.setScopedScopeFinders(scopedScopeFinder);

			configurator.configure((companyId, applicationName, service) -> {
				
				List<ServiceReferenceServiceTuple<?, ScopeFinder>> tuples =
					new ArrayList<>();
				
				ServiceReference<?> serviceReference = 
					Mockito.mock(ServiceReference.class);
		
				tuples.add(
					new ServiceReferenceServiceTuple(
						serviceReference, 
						service
					));
				
				tuples.add(
					new ServiceReferenceServiceTuple(
						serviceReference, 
						service
					));
				
				when(
					scopeFinderByNameServiceTrackerMap.getService(applicationName)
				).thenReturn(
					tuples
				);
				
				when(
					scopedScopeFinder.getService(companyId, applicationName)
				).thenReturn(
					service
				);				
			});
			
			_scopeFindersInitialized = true;
			
			return this;
		}

		public Builder withScopeMappers(
			ScopeMapper defaultScopeMapper, 
			CompanyAndKeyConfigurator<ScopeMapper> configurator) throws IllegalAccessException {
			
			ScopedServiceTrackerMap<ScopeMapper> scopedScopeMapper = 
				prepareScopeServiceTrackerMapMock(defaultScopeMapper, configurator);

			_scopeRegistry.setDefaultScopeMapper(defaultScopeMapper);

			_scopeRegistry.setScopedScopeMapper(scopedScopeMapper);

			_scopeMappersInitialized = true;
			return this;
		}
	
		public Builder withScopeMatcherFactories(
			ScopeMatcherFactory defaultScopeMatcherFactory,
			KeyConfigurator<ScopeMatcherFactory> configurator) throws IllegalArgumentException, IllegalAccessException {
			
			ServiceTrackerMap<String, ScopeMatcherFactory> 
				scopeMatcherFactoriesServiceTrackerMap = 
					Mockito.mock(ServiceTrackerMap.class);

			_scopeRegistry.setDefaultScopeMatcherFactory(
				defaultScopeMatcherFactory);

			_scopeRegistry.setScopedScopeMatcherFactories(
				scopeMatcherFactoriesServiceTrackerMap);

			configurator.configure((companyId, service) -> {
				when(
					scopeMatcherFactoriesServiceTrackerMap.getService(companyId)
				).thenReturn(
					service
				);			
			});
			
			_scopeMatcherFactoriesInitialized = true;
			
			return this;
		}
		
		public Builder withPrefixHandlerFactories(
			PrefixHandlerFactory defaultPrefixHandlerFactory, 
			CompanyAndKeyConfigurator<PrefixHandlerFactory> configurator) throws IllegalAccessException {
			
			ScopedServiceTrackerMap<PrefixHandlerFactory> scopedPrefixHandlerFactories = 
				prepareScopeServiceTrackerMapMock(defaultPrefixHandlerFactory, configurator);

			_scopeRegistry.setDefaultPrefixHandlerFactory(
				defaultPrefixHandlerFactory);

			_scopeRegistry.setScopedPrefixHandlerFactories(
				scopedPrefixHandlerFactories);

			_prefixHandlerFactoriesInitialized = true;
			return this;
		}

		public ScopeRegistry build() throws IllegalAccessException {
						
			if (!_scopeMatcherFactoriesInitialized) {
				withScopeMatcherFactories(
					(scopeAlias) -> scopeAlias::equals, 
					(registrator) -> {});
			}
									
			if (!_prefixHandlerFactoriesInitialized) {
				withPrefixHandlerFactories(
					(propertyAccessor) -> PrefixHandler.PASSTHROUGH_PREFIXHANDLER, 
					(registrator -> {}));
			}
			
			if (!_scopeMappersInitialized) {
				withScopeMappers(
					ScopeMapper.PASSTHROUGH_SCOPEMAPPER, 
					(registrator -> {}));
			}
			
			if (!_scopeFindersInitialized) {
				withScopeFinders((registrator) -> {});
			}
			
			return _scopeRegistry;
		}

		private <T> ScopedServiceTrackerMap<T> prepareScopeServiceTrackerMapMock(
			T defaultService, CompanyAndKeyConfigurator<T> configurator) {
			
			ScopedServiceTrackerMap<T> scopedServiceTrackerMap = 
				Mockito.mock(ScopedServiceTrackerMap.class);
	
			TestScopedServiceTrackerMap<T> testScopedServiceTrackerMap =
				new TestScopedServiceTrackerMap<>(defaultService);
			
		    Answer<PrefixHandlerFactory> answer = new Answer() {
		        public Object answer(InvocationOnMock invocation) throws Throwable {
		        	
		            long companyId = invocation.getArgumentAt(0, Long.class);
		            String key = invocation.getArgumentAt(1, String.class);
		            
		            return testScopedServiceTrackerMap.getService(companyId, key);
		        }
		    };
			
			when(
				scopedServiceTrackerMap.getService(anyLong(), anyString())
			).thenAnswer(
				answer
			);
			
			configurator.configure((companyId, applicationName, service) -> {
				testScopedServiceTrackerMap.setService(companyId, applicationName, service);
			});
			
			return scopedServiceTrackerMap;
		}

	}
}
