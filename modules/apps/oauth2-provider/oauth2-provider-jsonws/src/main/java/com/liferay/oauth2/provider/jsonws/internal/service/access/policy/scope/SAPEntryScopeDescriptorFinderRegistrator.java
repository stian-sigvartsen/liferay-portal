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

package com.liferay.oauth2.provider.jsonws.internal.service.access.policy.scope;

import com.liferay.oauth2.provider.jsonws.internal.constants.OAuth2JSONWSConstants;
import com.liferay.oauth2.provider.scope.spi.scope.descriptor.ScopeDescriptor;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = Constants.SERVICE_PID + "=com.liferay.oauth2.provider.jsonws.internal.configuration.OAuth2JSONWSConfiguration",
	service = {
		ManagedServiceFactory.class,
		SAPEntryScopeDescriptorFinderRegistrator.class
	}
)
public class SAPEntryScopeDescriptorFinderRegistrator
	implements ManagedServiceFactory {

	@Override
	public void deleted(String pid) {
		ServiceRegistration<ScopeFinder> serviceRegistration =
			_serviceRegistrations.remove(pid);

		serviceRegistration.unregister();
	}

	@Override
	public String getName() {
		return SAPEntryScopeDescriptorFinderRegistrator.class.getName() +
			" Factory";
	}

	public List<SAPEntryScope> getRegisteredSAPEntryScopes(long companyId) {
		List<SAPEntryScope> registeredSAPEntryScopes =
			_sapEntryScopesCache.computeIfAbsent(
				companyId, this::_getRegisteredSAPEntryScopes);

		return new ArrayList<>(registeredSAPEntryScopes);
	}

	public void register(long companyId) {
		synchronized (_sapScopeFinderServiceReferences) {
			for (ServiceReference<ScopeFinder> scopeFinderServiceReference :
					_sapScopeFinderServiceReferences) {

				doRegister(companyId, scopeFinderServiceReference);
			}
		}
	}

	public void unregister(long companyId) {
		if (!_companyIdsManagedServiceRegistrations.containsKey(companyId)) {
			return;
		}

		synchronized (_sapScopeFinderServiceReferences) {
			for (ServiceReference<ScopeFinder> scopeFinderServiceReference :
					_sapScopeFinderServiceReferences) {

				doUnregister(companyId, scopeFinderServiceReference);
			}
		}
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> properties)
		throws ConfigurationException {

		Dictionary<String, Object> scopeFinderProperties =
			new HashMapDictionary<>();

		scopeFinderProperties.put(
			"osgi.jaxrs.name", properties.get("osgi.jaxrs.name"));
		scopeFinderProperties.put(
			OAuth2JSONWSConstants.OAUTH2_REMOVE_SAP_ENTRY_OAUTH2_PREFIX,
			properties.get(
				OAuth2JSONWSConstants.OAUTH2_REMOVE_SAP_ENTRY_OAUTH2_PREFIX));
		scopeFinderProperties.put(
			OAuth2JSONWSConstants.OAUTH2_SAP_ENTRY_OAUTH2_PREFIX,
			properties.get(
				OAuth2JSONWSConstants.OAUTH2_SAP_ENTRY_OAUTH2_PREFIX));
		scopeFinderProperties.put("sap.scope.finder", Boolean.TRUE);

		ServiceRegistration<ScopeFinder> serviceRegistration =
			_serviceRegistrations.put(
				pid,
				_bundleContext.registerService(
					ScopeFinder.class, () -> Collections.emptySet(),
					scopeFinderProperties));

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(&(!(company.id=*))(osgi.jaxrs.name=*)(" + OAuth2JSONWSConstants.OAUTH2_SAP_ENTRY_OAUTH2_PREFIX + "=*))"
	)
	protected void addScopeFinder(
		ServiceReference<ScopeFinder> scopeFinderServiceReference) {

		synchronized (_sapScopeFinderServiceReferences) {
			_sapScopeFinderServiceReferences.add(scopeFinderServiceReference);

			for (long companyId :
					_companyIdsManagedServiceRegistrations.keySet()) {

				doRegister(companyId, scopeFinderServiceReference);
			}
		}
	}

	@Deactivate
	protected void deactivate() {
		for (ServiceRegistration<ScopeFinder> serviceRegistration :
				_serviceRegistrations.values()) {

			serviceRegistration.unregister();
		}

		for (long companyId : _companyIdsManagedServiceRegistrations.keySet()) {
			unregister(companyId);
		}
	}

	protected void doRegister(
		long companyId,
		ServiceReference<ScopeFinder> scopeFinderServiceReference) {

		String sapEntryPrefix = (String)scopeFinderServiceReference.getProperty(
			OAuth2JSONWSConstants.OAUTH2_SAP_ENTRY_OAUTH2_PREFIX);

		boolean sapEntryRemovePrefix = GetterUtil.getBoolean(
			scopeFinderServiceReference.getProperty(
				OAuth2JSONWSConstants.OAUTH2_REMOVE_SAP_ENTRY_OAUTH2_PREFIX),
			true);

		String osgiJaxrsName = (String)scopeFinderServiceReference.getProperty(
			"osgi.jaxrs.name");

		List<SAPEntryScope> sapEntryScopes = loadSAPEntryScopes(
			companyId, sapEntryPrefix, sapEntryRemovePrefix);

		Map<ServiceReference<ScopeFinder>, ManagedServiceRegistration>
			companyRegisteredServices =
				_companyIdsManagedServiceRegistrations.computeIfAbsent(
					companyId, cid -> new ConcurrentHashMap<>());

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		properties.put("companyId", String.valueOf(companyId));
		properties.put("osgi.jaxrs.name", osgiJaxrsName);

		SAPEntryScopeDescriptorFinder sapEntryScopeDescriptorFinder =
			new SAPEntryScopeDescriptorFinder(sapEntryScopes);

		companyRegisteredServices.compute(
			scopeFinderServiceReference,
			(sfsr, msr) -> {
				if (msr != null) {
					ServiceRegistration<ScopeFinder> serviceRegistration =
						msr._serviceRegistration;

					serviceRegistration.unregister();
				}

				return new ManagedServiceRegistration(
					(ServiceRegistration<ScopeFinder>)
						_bundleContext.registerService(
							new String[] {
								ScopeDescriptor.class.getName(),
								ScopeFinder.class.getName()
							},
							sapEntryScopeDescriptorFinder, properties),
					sapEntryScopes);
			});

		_sapEntryScopesCache.remove(companyId);
	}

	protected void doUnregister(
		long companyId,
		ServiceReference<ScopeFinder> scopeFinderServiceReference) {

		if (!_companyIdsManagedServiceRegistrations.containsKey(companyId)) {
			return;
		}

		Map<ServiceReference<ScopeFinder>, ManagedServiceRegistration>
			serviceRegistrations = _companyIdsManagedServiceRegistrations.get(
				companyId);

		ManagedServiceRegistration managedServiceRegistration =
			serviceRegistrations.remove(scopeFinderServiceReference);

		if (managedServiceRegistration != null) {
			ServiceRegistration<ScopeFinder> serviceRegistration =
				managedServiceRegistration._serviceRegistration;

			serviceRegistration.unregister();
		}

		_sapEntryScopesCache.remove(companyId);
	}

	protected List<SAPEntryScope> loadSAPEntryScopes(
		long companyId, String sapEntryPrefix, boolean sapEntryRemovePrefix) {

		List<SAPEntry> sapEntries = _sapEntryLocalService.getCompanySAPEntries(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Stream<SAPEntry> stream = sapEntries.stream();

		return stream.filter(
			sapEntry -> StringUtil.startsWith(
				sapEntry.getName(), sapEntryPrefix)
		).filter(
			SAPEntry::isEnabled
		).map(
			sapEntry -> new SAPEntryScope(
				sapEntry,
				_parseScope(sapEntry, sapEntryPrefix, sapEntryRemovePrefix))
		).collect(
			Collectors.toList()
		);
	}

	protected void removeScopeFinder(
		ServiceReference<ScopeFinder> scopeFinderServiceReference) {

		synchronized (_sapScopeFinderServiceReferences) {
			_sapScopeFinderServiceReferences.remove(
				scopeFinderServiceReference);

			for (long companyId :
					_companyIdsManagedServiceRegistrations.keySet()) {

				doUnregister(companyId, scopeFinderServiceReference);
			}
		}
	}

	private List<SAPEntryScope> _getRegisteredSAPEntryScopes(long companyId) {
		Map<ServiceReference<ScopeFinder>, ManagedServiceRegistration>
			serviceReferencesManagedServiceRegistrations =
				_companyIdsManagedServiceRegistrations.get(companyId);

		if (serviceReferencesManagedServiceRegistrations == null) {
			return Collections.emptyList();
		}

		Collection<ManagedServiceRegistration> managedServiceRegistrations =
			serviceReferencesManagedServiceRegistrations.values();

		Stream<ManagedServiceRegistration> stream =
			managedServiceRegistrations.stream();

		return stream.map(
			msr -> msr._sapEntryScopes
		).flatMap(
			list -> list.stream()
		).collect(
			Collectors.toList()
		);
	}

	private String _parseScope(
		SAPEntry sapEntry, String sapEntryPrefix,
		boolean sapEntryRemovePrefix) {

		String sapEntryName = sapEntry.getName();

		if (!sapEntryRemovePrefix) {
			return sapEntryName;
		}

		return sapEntryName.substring(sapEntryPrefix.length());
	}

	private BundleContext _bundleContext;
	private final Map
		<Long, Map<ServiceReference<ScopeFinder>, ManagedServiceRegistration>>
			_companyIdsManagedServiceRegistrations = new ConcurrentHashMap<>();

	@Reference
	private SAPEntryLocalService _sapEntryLocalService;

	private final Map<Long, List<SAPEntryScope>> _sapEntryScopesCache =
		new ConcurrentHashMap<>();
	private final List<ServiceReference<ScopeFinder>>
		_sapScopeFinderServiceReferences = new LinkedList<>();
	private final Map<String, ServiceRegistration<ScopeFinder>>
		_serviceRegistrations = new ConcurrentHashMap<>();

	private static class ManagedServiceRegistration {

		private ManagedServiceRegistration(
			ServiceRegistration<ScopeFinder> serviceRegistration,
			List<SAPEntryScope> sapEntryScopes) {

			_serviceRegistration = serviceRegistration;
			_sapEntryScopes = sapEntryScopes;
		}

		private final List<SAPEntryScope> _sapEntryScopes;
		private final ServiceRegistration<ScopeFinder> _serviceRegistration;

	}

}