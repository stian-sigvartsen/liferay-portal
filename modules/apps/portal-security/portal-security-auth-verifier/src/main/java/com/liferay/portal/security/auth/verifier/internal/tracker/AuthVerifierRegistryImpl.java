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

@Component
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

	private static Properties _loadProperties(
		ServiceReference<AuthVerifier> serviceReference,
		String authVerifierClassName) {

		Properties properties = new Properties();

		String authVerifierPropertyName =
			AuthVerifierPipeline.getAuthVerifierPropertyName(
				authVerifierClassName);

		for (String key : serviceReference.getPropertyKeys()) {
			if (key.startsWith(authVerifierPropertyName)) {
				//key = key.substring(authVerifierPropertyName.length());
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

	private AuthVerifierConfiguration _buildAuthVerifierConfiguration(
		AuthVerifier authVerifier) {

		Class<?> authVerifierClass = authVerifier.getClass();

		AuthVerifierConfiguration authVerifierConfiguration =
			new AuthVerifierConfiguration();

		authVerifierConfiguration.setAuthVerifier(authVerifier);
		authVerifierConfiguration.setAuthVerifierClassName(
			authVerifierClass.getName());

		return authVerifierConfiguration;
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

	private void _updateAuthVerifierConfiguration(
		AuthVerifierConfiguration authVerifierConfiguration,
		ServiceReference<AuthVerifier> serviceReference) {

		Properties properties = _loadProperties(
			serviceReference,
			authVerifierConfiguration.getAuthVerifierClassName());

		if (!_validateAuthVerifierProperties(
				authVerifierConfiguration.getAuthVerifierClassName(),
				properties)) {

			return;
		}

		authVerifierConfiguration.setProperties(properties);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuthVerifierRegistryImpl.class);

	private final Map<String, List<AuthVerifierConfiguration>>
		_authVerifierConfigurations = new HashMap<>();
	private BundleContext _bundleContext;

	@Reference
	private Portal _portal;

	private ServiceTracker<AuthVerifier, AuthVerifierConfigurationTracker>
		_serviceTracker;

	private interface AuthVerifierConfigurationTracker {

		public void close();

		public AuthVerifierConfiguration getAuthVerifierConfiguration();

	}

	private class AuthVerifierTrackerCustomizer
		implements ServiceTrackerCustomizer
			<AuthVerifier, AuthVerifierConfigurationTracker> {

		@Override
		public AuthVerifierConfigurationTracker addingService(
			ServiceReference<AuthVerifier> serviceReference) {

			AuthVerifier authVerifier = _bundleContext.getService(
				serviceReference);

			return new HttpWhiteboardAuthVerifierConfigurationTracker(
				serviceReference, authVerifier);
		}

		@Override
		public void modifiedService(
			ServiceReference<AuthVerifier> serviceReference,
			AuthVerifierConfigurationTracker authVerifierConfigurationTracker) {

			_updateAuthVerifierConfiguration(
				authVerifierConfigurationTracker.getAuthVerifierConfiguration(),
				serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<AuthVerifier> serviceReference,
			AuthVerifierConfigurationTracker authVerifierConfigurationTracker) {

			_bundleContext.ungetService(serviceReference);

			authVerifierConfigurationTracker.close();
		}

	}

	private class HttpWhiteboardAuthVerifierConfigurationTracker
		implements AuthVerifierConfigurationTracker {

		public HttpWhiteboardAuthVerifierConfigurationTracker(
			ServiceReference<AuthVerifier> serviceReference,
			AuthVerifier authVerifier) {

			AuthVerifierConfiguration authVerifierConfiguration =
				_buildAuthVerifierConfiguration(authVerifier);

			Properties properties = _loadProperties(
				serviceReference,
				authVerifierConfiguration.getAuthVerifierClassName());

			if (!_validateAuthVerifierProperties(
					authVerifierConfiguration.getAuthVerifierClassName(),
					properties)) {

				return;
			}

			_updateAuthVerifierConfiguration(
				authVerifierConfiguration, serviceReference);

			_authVerifierConfiguration = authVerifierConfiguration;

			String servletContextHelperSelectFilter = GetterUtil.getString(
				properties.get("servlet.context.helper.select.filter"));

			if (Validator.isNotNull(servletContextHelperSelectFilter)) {
				_serviceTracker = _openServletContextHelperServiceTracker(
					servletContextHelperSelectFilter,
					authVerifierConfiguration);
			}
			else {
				List<AuthVerifierConfiguration> authVerifierConfigurations =
					_authVerifierConfigurations.computeIfAbsent(
						StringPool.BLANK,
						cp -> new ArrayList<AuthVerifierConfiguration>());

				authVerifierConfigurations.add(authVerifierConfiguration);
			}
		}

		public void close() {
			if (_serviceTracker != null) {
				_serviceTracker.close();
			}
			else {
				_authVerifierConfigurations.computeIfPresent(
					StringPool.BLANK,
					(cp, list) -> {
						list.remove(_authVerifierConfiguration);

						return list;
					});
			}
		}

		public AuthVerifierConfiguration getAuthVerifierConfiguration() {
			return _authVerifierConfiguration;
		}

		private ServiceTracker<ServletContextHelper, String>
			_openServletContextHelperServiceTracker(
				String servletContextHelperSelectFilter,
				AuthVerifierConfiguration authVerifierConfiguration) {

			String filterString = StringBundler.concat(
				"(&", servletContextHelperSelectFilter, "(",
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "=*)",
				"(objectClass=", ServletContextHelper.class.getName(), "))");

			return ServiceTrackerFactory.open(
				_bundleContext, filterString,
				new ServletContextHelperTrackerCustomizer(
					authVerifierConfiguration));
		}

		private AuthVerifierConfiguration _authVerifierConfiguration;
		private ServiceTracker<ServletContextHelper, String> _serviceTracker;

	}

	private class ServletContextHelperTrackerCustomizer
		implements ServiceTrackerCustomizer<ServletContextHelper, String> {

		public ServletContextHelperTrackerCustomizer(
			AuthVerifierConfiguration authVerifierConfiguration) {

			_authVerifierConfiguration = authVerifierConfiguration;
		}

		@Override
		public String addingService(
			ServiceReference<ServletContextHelper> serviceReference) {

			String contextPath = _getPathModule(serviceReference);

			List<AuthVerifierConfiguration> authVerifierConfigurations =
				_authVerifierConfigurations.computeIfAbsent(
					_getPathModule(serviceReference),
					cp -> new ArrayList<AuthVerifierConfiguration>());

			authVerifierConfigurations.add(_authVerifierConfiguration);

			return contextPath;
		}

		@Override
		public void modifiedService(
			ServiceReference<ServletContextHelper> serviceReference,
			String contextPath) {

			List<AuthVerifierConfiguration> authVerifierConfigurations =
				_authVerifierConfigurations.get(contextPath);

			authVerifierConfigurations.remove(_authVerifierConfiguration);

			authVerifierConfigurations = _authVerifierConfigurations.get(
				_getPathModule(serviceReference));

			authVerifierConfigurations.add(_authVerifierConfiguration);
		}

		@Override
		public void removedService(
			ServiceReference<ServletContextHelper> serviceReference,
			String contextPath) {

			List<AuthVerifierConfiguration> authVerifierConfigurations =
				_authVerifierConfigurations.get(contextPath);

			authVerifierConfigurations.remove(_authVerifierConfiguration);
		}

		private String _getPathModule(
			ServiceReference<ServletContextHelper> serviceReference) {

			String contextPath = GetterUtil.getString(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH));

			return _portal.getPathModule() + contextPath;
		}

		private final AuthVerifierConfiguration _authVerifierConfiguration;

	}

}