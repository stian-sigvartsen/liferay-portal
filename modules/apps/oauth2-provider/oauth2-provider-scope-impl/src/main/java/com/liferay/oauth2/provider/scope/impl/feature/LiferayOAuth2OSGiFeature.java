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

package com.liferay.oauth2.provider.scope.impl.feature;

import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.oauth2.provider.scope.spi.application.descriptor.ApplicationDescriptor;
import com.liferay.oauth2.provider.scope.impl.jaxrs.ScopedRequestScopeChecker;
import com.liferay.oauth2.provider.scope.spi.scope.descriptor.ScopeDescriptor;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.oauth2.provider.scope.impl.jaxrs.CompanyRetrieverContainerRequestFilter;
import com.liferay.oauth2.provider.scope.impl.jaxrs.RunnableExecutorContainerResponseFilter;
import com.liferay.oauth2.provider.rest.spi.RequestScopeCheckerFilter;
import com.liferay.oauth2.provider.scope.liferay.api.ScopeContext;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServiceFactoryBean;
import org.apache.cxf.jaxrs.model.ApplicationInfo;
import org.apache.cxf.service.factory.AbstractServiceFactoryBean;
import org.apache.cxf.service.factory.FactoryBeanListener;
import org.apache.cxf.service.factory.FactoryBeanListenerManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.util.tracker.ServiceTracker;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Component(
	immediate = true,
	property = {
		"liferay.extension=OAuth2"
	}
)
@Provider
public class LiferayOAuth2OSGiFeature implements Feature {

	private FactoryBeanListener _factoryBeanListener;
	private BundleContext _bundleContext;

	public LiferayOAuth2OSGiFeature() {
		_factoryBeanListener = new ScopeFinderFactoryBeanListener();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Override
	public boolean configure(FeatureContext context) {
		context.register(
			new CompanyRetrieverContainerRequestFilter(
				_scopeContext::setCompany),
			Priorities.AUTHORIZATION - 10);

		context.register(
			new ScopeContextContainerRequestFilter(_scopeContext),
			Priorities.AUTHORIZATION - 9);

		context.register(
			new ScopedRequestScopeChecker(
				_bundleContext, _scopeChecker,
				() -> _defaultRequestScopeChecker),
			Priorities.AUTHORIZATION - 8);

		context.register(
			new RunnableExecutorContainerResponseFilter(_scopeContext::clear),
			Priorities.AUTHORIZATION - 8);

		_initializedThreadLocal.set(Boolean.TRUE);

		return true;
	}

	@Reference(
		unbind = "restoreBus",
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected void modifyBus(Bus bus) {
		FactoryBeanListenerManager factoryBeanListenerManager =
			bus.getExtension(FactoryBeanListenerManager.class);

		if (factoryBeanListenerManager == null) {
			return;
		}

		factoryBeanListenerManager.addListener(_factoryBeanListener);

	}

	protected void restoreBus(Bus bus) {
		FactoryBeanListenerManager factoryBeanListenerManager =
			bus.getExtension(FactoryBeanListenerManager.class);

		if (factoryBeanListenerManager == null) {
			return;
		}

		factoryBeanListenerManager.removeListener(_factoryBeanListener);
	}

	private class ScopeFinderFactoryBeanListener
		implements FactoryBeanListener {

		public final String APPLICATION_CLASS_NAME =
			Application.class.getName();

		@Override
		public void handleEvent(
			Event ev, AbstractServiceFactoryBean factory, Object... args) {

			if ((factory instanceof JAXRSServiceFactoryBean) &&
				ev.equals(Event.SERVER_CREATED)) {

				if (!_initializedThreadLocal.get()) {
					return;
				}

				_initializedThreadLocal.remove();

				Server server = (Server) args[0];

				Endpoint endpoint = server.getEndpoint();

				ApplicationInfo applicationInfo =
					(ApplicationInfo) endpoint.get(APPLICATION_CLASS_NAME);

				Application application = applicationInfo.getProvider();

				Class<? extends Application> clazz = application.getClass();

				String applicationClassName = clazz.getName();

				Bundle bundle = FrameworkUtil.getBundle(clazz);

				if (bundle == null) {
					return;
				}

				BundleContext bundleContext = bundle.getBundleContext();

				ServiceReference<?> serviceReference =
					findReference(bundleContext, application);

				Map<String, Object> properties = new HashMap<>();

				Object oauth2ScopeCheckerTypeObject =
					serviceReference.getProperty("oauth2.scopechecker.type");

				String oauth2ScopeCheckerType;

				if (oauth2ScopeCheckerTypeObject == null) {
					oauth2ScopeCheckerType = "request.operation";
				}
				else {
					oauth2ScopeCheckerType =
						oauth2ScopeCheckerTypeObject.toString();
				}

				properties.put(
					"oauth2.scopechecker.type", oauth2ScopeCheckerType);

				applicationInfo.setOverridingProps(properties);

				Dictionary<String, Object> serviceProperties = new Hashtable<>();
				for (String property : serviceReference.getPropertyKeys()) {
					if (property.startsWith("service.")) {
						continue;
					}
					serviceProperties.put(property, serviceReference.getProperty(property));
				}
				
				serviceProperties.put("osgi.jaxrs.name", applicationClassName);
				
				if (oauth2ScopeCheckerType.equals("request.operation")) {
					processRequestOperationStrategy(
						bundleContext, endpoint, applicationClassName, 
						serviceProperties);
				}

				if (oauth2ScopeCheckerType.equals("annotations"))
					processAnnotationStrategy(
						bundleContext, (JAXRSServiceFactoryBean) factory,
						endpoint, applicationClassName, serviceProperties);

				registerDescriptors(
					endpoint, bundle, applicationClassName);
			}
		}

		private void registerDescriptors(
			Endpoint endpoint, Bundle bundle, String applicationClassName) {

			String bundleSymbolicName = bundle.getSymbolicName();

			ServiceTracker<ResourceBundleLoader, ResourceBundleLoader>
				serviceTracker = ServiceTrackerFactory.open(
					_bundleContext,
					"(&(objectClass=" + ResourceBundleLoader.class.getName() +
					")(bundle.symbolic.name=" + bundleSymbolicName + ")" +
					"(resource.bundle.base.name=content.Language))");

			ServiceRegistration<?>
				serviceRegistration =
				_bundleContext.registerService(
					new String[]{
						ScopeDescriptor.class.getName(),
						ApplicationDescriptor.class.getName()
					},
					new ApplicationDescriptorsImpl(
						serviceTracker, applicationClassName),
					new Hashtable<String, Object>() {{
						put("osgi.jaxrs.name", applicationClassName);
					}});

			endpoint.addCleanupHook(
				() -> {
					serviceRegistration.unregister();

					serviceTracker.close();
				}
			);
		}

		protected ServiceReference<?> findReference(
			BundleContext bundleContext, Application application) {

			ServiceReference<?>[] serviceReferences;

			try {
				serviceReferences = bundleContext.getAllServiceReferences(
					Application.class.getName(), null);
			}
			catch (InvalidSyntaxException e) {
				throw new RuntimeException(e);
			}

			for (ServiceReference<?> serviceReference : serviceReferences) {
				try {
					Object service = bundleContext.getService(serviceReference);

					if (service == application) {
						return serviceReference;
					}
				}
				finally {
					bundleContext.ungetService(serviceReference);
				}
			}

			return null;
		}

		protected void processRequestOperationStrategy(
			BundleContext bundleContext, Endpoint endpoint,
			String applicationClassName, 
			Dictionary<String, Object> serviceProperties) {

			ServiceRegistration<ScopeFinder> serviceRegistration =
				bundleContext.registerService(
					ScopeFinder.class,
					new CollectionScopeFinder(
						Arrays.asList(
							HttpMethod.DELETE, HttpMethod.GET, HttpMethod.HEAD,
							HttpMethod.OPTIONS, HttpMethod.POST,
							HttpMethod.PUT)),
					serviceProperties);

			endpoint.addCleanupHook(serviceRegistration::unregister);
		}

		protected void processAnnotationStrategy(
			BundleContext bundleContext, JAXRSServiceFactoryBean factory,
			Endpoint endpoint, String applicationClassName, 
			Dictionary<String, Object> serviceProperties) {

			List<Class<?>> resourceClasses = factory.getResourceClasses();

			Bus bus = factory.getBus();

			Collection<String> scopes = new HashSet<>();

			for (Class<?> resourceClass : resourceClasses) {
				scopes.addAll(
					ScopeAnnotationFinder.find(resourceClass, bus));
			}


			ServiceRegistration<ScopeFinder> scopeFinderRegistration =
				bundleContext.registerService(
					ScopeFinder.class, new CollectionScopeFinder(scopes),
					serviceProperties);

			ServiceRegistration<RequestScopeCheckerFilter>
				requestScopeCheckerFilterRegistration =
				bundleContext.registerService(
					RequestScopeCheckerFilter.class,
					_annotationRequestScopeChecker, serviceProperties);

			endpoint.addCleanupHook(
				() -> {
					scopeFinderRegistration.unregister();

					requestScopeCheckerFilterRegistration.unregister();
				});
		}

		private class ApplicationDescriptorsImpl
			implements ScopeDescriptor, ApplicationDescriptor {

			private final ServiceTracker<?, ResourceBundleLoader>
				_serviceTracker;
			private String _applicationClassName;

			public ApplicationDescriptorsImpl(
				ServiceTracker<?, ResourceBundleLoader> serviceTracker,
				String applicationClassName) {

				_serviceTracker = serviceTracker;
				_applicationClassName = applicationClassName;
			}

			@Override
			public String describeScope(String scope, Locale locale) {
				ResourceBundleLoader resourceBundleLoader =
					_serviceTracker.getService();

				if (resourceBundleLoader == null) {
					return _defaultScopeDescriptor.describeScope(scope, locale);
				}

				ResourceBundle resourceBundle =
					resourceBundleLoader.loadResourceBundle(
						LocaleUtil.toLanguageId(locale));

				String key = "oauth2.scope." + scope;

				if (!resourceBundle.containsKey(key)) {
					return _defaultScopeDescriptor.describeScope(
						scope, locale);
				}

				return resourceBundle.getString(key);
			}

			@Override
			public String describeApplication(
				Locale locale) {

				ResourceBundleLoader resourceBundleLoader =
					_serviceTracker.getService();

				ResourceBundle resourceBundle =
					resourceBundleLoader.loadResourceBundle(
						LocaleUtil.toLanguageId(locale));

				String key = "oauth2.application.description." +
					 _applicationClassName;

				return resourceBundle.getString(key);
			}
		}
	}

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private ScopeContext _scopeContext;

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private ScopeChecker _scopeChecker;

	@Reference(
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(type=annotation)"
	)
	private RequestScopeCheckerFilter _annotationRequestScopeChecker;

	@Reference(
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(default=true)"
	)
	private RequestScopeCheckerFilter _defaultRequestScopeChecker;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(default=true)"
	)
	private volatile ScopeDescriptor _defaultScopeDescriptor;

	private static ThreadLocal<Boolean> _initializedThreadLocal =
		ThreadLocal.withInitial(() -> Boolean.FALSE);

}
