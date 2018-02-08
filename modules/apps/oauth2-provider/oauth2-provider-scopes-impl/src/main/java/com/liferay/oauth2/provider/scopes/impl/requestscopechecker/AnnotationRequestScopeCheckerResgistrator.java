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

package com.liferay.oauth2.provider.scopes.impl.requestscopechecker;

import com.liferay.oauth2.provider.scopes.spi.RequestScopeChecker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import javax.ws.rs.core.Application;
import java.util.Hashtable;

@Component(immediate = true)
public class AnnotationRequestScopeCheckerResgistrator {

	private ServiceTracker<Application, ServiceRegistration<?>>
		_serviceTracker;

	@Activate
	protected void activate(BundleContext bundleContext)
		throws InvalidSyntaxException {

		Filter filter = bundleContext.createFilter(
			"(&(objectClass=" + Application.class.getName() + ")" +
			"(oauth2.scopechecker.type=annotations))");

		_serviceTracker = new ServiceTracker<>(
			bundleContext, filter,
			new ServiceTrackerCustomizer<Application, ServiceRegistration<?>>() {

				@Override
				public ServiceRegistration<?> addingService(
					ServiceReference<Application> serviceReference) {

					Application application =
						bundleContext.getService(serviceReference);

					if (application == null) {
						return null;
					}

					try {
						Class<? extends Application> applicationClass =
							application.getClass();

						String applicationClassName =
							applicationClass.getName();

						Hashtable<String, Object> properties =
							new Hashtable<>();

						properties.put(
							"osgi.jaxrs.name", applicationClassName);

						return bundleContext.registerService(
							RequestScopeChecker.class,
							_annotationRequestScopeChecker, properties);
					}
					finally {
						bundleContext.ungetService(serviceReference);
					}
				}

				@Override
				public void modifiedService(
					ServiceReference<Application> reference,
					ServiceRegistration<?> service) {


				}

				@Override
				public void removedService(
					ServiceReference<Application> reference,
					ServiceRegistration<?> serviceRegistration) {

					serviceRegistration.unregister();
				}
			});

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	@Reference
	AnnotationRequestScopeChecker _annotationRequestScopeChecker;
}
