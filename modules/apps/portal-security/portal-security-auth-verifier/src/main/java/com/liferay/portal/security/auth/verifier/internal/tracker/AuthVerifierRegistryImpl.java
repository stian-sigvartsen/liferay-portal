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

package com.liferay.portal.security.auth.verifier.internal.tracker;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierConfiguration;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierRegistry;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auth.AuthVerifierPipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;

import jodd.util.Wildcard;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Stian Sigvartsen
 */
@Component(service = AuthVerifierRegistry.class)
public class AuthVerifierRegistryImpl implements AuthVerifierRegistry {

	public List<AuthVerifierConfiguration> getAuthVerifierConfigurations(
		AccessControlContext accessControlContext) {

		HttpServletRequest httpServletRequest =
			accessControlContext.getRequest();

		List<AuthVerifierConfiguration> authVerifierConfigurations =
			new ArrayList<>();

		String requestURI = httpServletRequest.getRequestURI();

		String contextPath = httpServletRequest.getContextPath();

		requestURI = requestURI.substring(contextPath.length());

		List<AuthVerifierConfiguration> contextPathAuthVerifierConfigurations =
			_authVerifierConfigurations.get(contextPath);

		if (contextPathAuthVerifierConfigurations == null) {
			return authVerifierConfigurations;
		}

		for (AuthVerifierConfiguration authVerifierConfiguration :
				contextPathAuthVerifierConfigurations) {

			authVerifierConfiguration = _mergeAuthVerifierConfiguration(
				authVerifierConfiguration, accessControlContext);

			if (_isMatchingRequestURI(authVerifierConfiguration, requestURI)) {
				authVerifierConfigurations.add(authVerifierConfiguration);
			}
		}

		return authVerifierConfigurations;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, "(objectClass=" + AuthVerifier.class.getName() + ")",
			new AuthVerifierTrackerCustomizer());
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private static boolean _validateAuthVerifierProperties(
		String authVerifierClassName, Properties properties) {

		String[] urlsIncludes = StringUtil.split(
			properties.getProperty("urls.includes"));

		if (urlsIncludes.length == 0) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Auth verifier " + authVerifierClassName +
						" does not have URLs configured");
			}

			return false;
		}

		return true;
	}

	private boolean _isMatchingRequestURI(
		AuthVerifierConfiguration authVerifierConfiguration,
		String requestURI) {

		Properties properties = authVerifierConfiguration.getProperties();

		String[] urlsExcludes = StringUtil.split(
			properties.getProperty("urls.excludes"));

		if ((urlsExcludes.length > 0) &&
			(Wildcard.matchOne(requestURI, urlsExcludes) > -1)) {

			return false;
		}

		String[] urlsIncludes = StringUtil.split(
			properties.getProperty("urls.includes"));

		if (urlsIncludes.length == 0) {
			return false;
		}

		if (Wildcard.matchOne(requestURI, urlsIncludes) > -1) {
			return true;
		}

		return false;
	}

	private AuthVerifierConfiguration _mergeAuthVerifierConfiguration(
		AuthVerifierConfiguration authVerifierConfiguration,
		AccessControlContext accessControlContext) {

		Map<String, Object> settings = accessControlContext.getSettings();

		String authVerifierSettingsKey =
			AuthVerifierPipeline.getAuthVerifierPropertyName(
				authVerifierConfiguration.getAuthVerifierClassName());

		boolean merge = false;

		Set<String> settingsKeys = settings.keySet();

		Iterator<String> iterator = settingsKeys.iterator();

		while (iterator.hasNext() && !merge) {
			String settingsKey = iterator.next();

			if (settingsKey.startsWith(authVerifierSettingsKey) &&
				(settings.get(settingsKey) instanceof String)) {

				merge = true;
			}
		}

		if (!merge) {
			return authVerifierConfiguration;
		}

		AuthVerifierConfiguration mergedAuthVerifierConfiguration =
			new AuthVerifierConfiguration();

		mergedAuthVerifierConfiguration.setAuthVerifier(
			authVerifierConfiguration.getAuthVerifier());

		Properties mergedProperties = new Properties(
			authVerifierConfiguration.getProperties());

		for (Map.Entry<String, Object> entry : settings.entrySet()) {
			String settingsKey = entry.getKey();

			if (settingsKey.startsWith(authVerifierSettingsKey)) {
				Object settingsValue = entry.getValue();

				if (settingsValue instanceof String) {
					String propertiesKey = settingsKey.substring(
						authVerifierSettingsKey.length());

					mergedProperties.setProperty(
						propertiesKey, (String)settingsValue);
				}
			}
		}

		mergedAuthVerifierConfiguration.setProperties(mergedProperties);

		return mergedAuthVerifierConfiguration;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuthVerifierRegistryImpl.class);

	private final Map<String, List<AuthVerifierConfiguration>>
		_authVerifierConfigurations = new HashMap<>();
	private BundleContext _bundleContext;

	@Reference
	private Portal _portal;

	private ServiceTracker<AuthVerifier, AuthVerifierConfigurationPublisher>
		_serviceTracker;

	private class AuthVerifierConfigurationPublisher {

		public AuthVerifierConfigurationPublisher(
			ServiceReference<AuthVerifier> serviceReference,
			AuthVerifier authVerifier) {

			Class<?> authVerifierClass = authVerifier.getClass();

			_authVerifierConfiguration = new AuthVerifierConfiguration();

			_authVerifierConfiguration.setAuthVerifier(authVerifier);
			_authVerifierConfiguration.setAuthVerifierClassName(
				authVerifierClass.getName());

			publish(serviceReference);
		}

		public boolean publish(
			ServiceReference<AuthVerifier> serviceReference) {

			Properties properties = _loadProperties(serviceReference);

			if (!_validateAuthVerifierProperties(
					_authVerifierConfiguration.getAuthVerifierClassName(),
					properties)) {

				unpublish();

				return false;
			}

			_authVerifierConfiguration.setProperties(properties);

			if (_published) {
				return true;
			}

			String servletContextHelperSelectFilter = GetterUtil.getString(
				properties.get("servlet.context.helper.select.filter"));

			if (Validator.isNotNull(servletContextHelperSelectFilter)) {
				_serviceTracker = _openServletContextHelperServiceTracker(
					servletContextHelperSelectFilter, this::mount,
					this::unmount);
			}
			else {
				mount(StringPool.BLANK);
			}

			return _published = true;
		}

		public void unpublish() {
			if (!_published) {
				return;
			}

			if (_serviceTracker != null) {
				_serviceTracker.close();
			}
			else {
				unmount(StringPool.BLANK);
			}
		}

		protected void mount(String contextPath) {
			List<AuthVerifierConfiguration> authVerifierConfigurations =
				_authVerifierConfigurations.computeIfAbsent(
					contextPath,
					cp -> new ArrayList<AuthVerifierConfiguration>());

			authVerifierConfigurations.add(_authVerifierConfiguration);
		}

		protected void unmount(String contextPath) {
			_authVerifierConfigurations.computeIfPresent(
				contextPath,
				(cp, list) -> {
					list.remove(_authVerifierConfiguration);

					return list;
				});
		}

		private Properties _loadProperties(
			ServiceReference<AuthVerifier> serviceReference) {

			Properties properties = new Properties();

			String authVerifierPropertyName =
				AuthVerifierPipeline.getAuthVerifierPropertyName(
					_authVerifierConfiguration.getAuthVerifierClassName());

			for (String key : serviceReference.getPropertyKeys()) {
				if (key.startsWith(authVerifierPropertyName)) {
					properties.setProperty(
						key.substring(authVerifierPropertyName.length()),
						String.valueOf(serviceReference.getProperty(key)));
				}
				else {
					properties.setProperty(
						key, String.valueOf(serviceReference.getProperty(key)));
				}
			}

			return properties;
		}

		private ServiceTracker<ServletContextHelper, String>
			_openServletContextHelperServiceTracker(
				String servletContextHelperSelectFilter,
				Consumer<String> publisher, Consumer<String> unpublisher) {

			String filterString = StringBundler.concat(
				"(&", servletContextHelperSelectFilter, "(",
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "=*)",
				"(objectClass=", ServletContextHelper.class.getName(), "))");

			return ServiceTrackerFactory.open(
				_bundleContext, filterString,
				new ServletContextHelperTrackerCustomizer(
					publisher, unpublisher));
		}

		private final AuthVerifierConfiguration _authVerifierConfiguration;
		private boolean _published;
		private ServiceTracker<ServletContextHelper, String> _serviceTracker;

	}

	private class AuthVerifierTrackerCustomizer
		implements ServiceTrackerCustomizer
			<AuthVerifier, AuthVerifierConfigurationPublisher> {

		@Override
		public AuthVerifierConfigurationPublisher addingService(
			ServiceReference<AuthVerifier> serviceReference) {

			AuthVerifier authVerifier = _bundleContext.getService(
				serviceReference);

			return new AuthVerifierConfigurationPublisher(
				serviceReference, authVerifier);
		}

		@Override
		public void modifiedService(
			ServiceReference<AuthVerifier> serviceReference,
			AuthVerifierConfigurationPublisher
				authVerifierConfigurationPublisher) {

			authVerifierConfigurationPublisher.publish(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<AuthVerifier> serviceReference,
			AuthVerifierConfigurationPublisher
				authVerifierConfigurationPublisher) {

			_bundleContext.ungetService(serviceReference);

			authVerifierConfigurationPublisher.unpublish();
		}

	}

	private class ServletContextHelperTrackerCustomizer
		implements ServiceTrackerCustomizer<ServletContextHelper, String> {

		public ServletContextHelperTrackerCustomizer(
			Consumer<String> publisher, Consumer<String> unpublisher) {

			_publisher = publisher;
			_unpublisher = unpublisher;
		}

		@Override
		public String addingService(
			ServiceReference<ServletContextHelper> serviceReference) {

			String contextPath = _getPathModule(serviceReference);

			_publisher.accept(contextPath);

			return contextPath;
		}

		@Override
		public void modifiedService(
			ServiceReference<ServletContextHelper> serviceReference,
			String contextPath) {

			String newContextPath = _getPathModule(serviceReference);

			if (contextPath.equals(newContextPath)) {
				return;
			}

			_unpublisher.accept(contextPath);
			_publisher.accept(newContextPath);
		}

		@Override
		public void removedService(
			ServiceReference<ServletContextHelper> serviceReference,
			String contextPath) {

			_unpublisher.accept(contextPath);
		}

		private String _getPathModule(
			ServiceReference<ServletContextHelper> serviceReference) {

			String contextPath = GetterUtil.getString(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH));

			return _portal.getPathModule() + contextPath;
		}

		private final Consumer<String> _publisher;
		private final Consumer<String> _unpublisher;

	}

}