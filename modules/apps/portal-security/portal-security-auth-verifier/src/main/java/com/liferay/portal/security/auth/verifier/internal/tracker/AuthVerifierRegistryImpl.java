package com.liferay.portal.security.auth.verifier.internal.tracker;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
import com.liferay.portal.security.auth.AuthVerifierPipeline;

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

import jodd.util.Wildcard;

@Component
public class AuthVerifierRegistryImpl implements AuthVerifierRegistry {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;
		
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, "(&(objectClass=" + AuthVerifier.class.getName() 
				+ ")(servlet.context.helper.select.filter=*))",
			new AuthVerifierTrackerCustomizer());
		
		_portalServiceTracker = ServiceTrackerFactory.open(
			bundleContext, "(&(objectClass=" + AuthVerifier.class.getName() 
				+ ")(!(servlet.context.helper.select.filter=*)))",
			new PortalAuthVerifierTrackerCustomizer());
	}
	
	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
		_portalServiceTracker.close();
	}
	
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
				properties.setProperty(key, String.valueOf(serviceReference.getProperty(key)));
			}
		}
				
		return properties;
	}

	private static boolean _validate(
		AuthVerifierConfiguration authVerifierConfiguration) {
	
		Properties properties = authVerifierConfiguration.getProperties();
	
		String[] urlsIncludes = StringUtil.split(
			properties.getProperty("urls.includes"));
	
		if (urlsIncludes.length == 0) {
			if (_log.isWarnEnabled()) {
				String authVerifierClassName =
					authVerifierConfiguration.getAuthVerifierClassName();
	
				_log.warn(
					"Auth verifier " + authVerifierClassName +
						" does not have URLs configured");
			}
	
			return false;
		}
	
		return true;
	}

	private AuthVerifierConfiguration _buildAuthVerifierConfiguration(
			ServiceReference<AuthVerifier> serviceReference) {
		
		AuthVerifier authVerifier = _bundleContext.getService(serviceReference);
	
		if (authVerifier == null) {
			return null;
		}
	
		Class<?> authVerifierClass = authVerifier.getClass();
		
		AuthVerifierConfiguration authVerifierConfiguration =
			new AuthVerifierConfiguration();
	
		authVerifierConfiguration.setAuthVerifier(authVerifier);
		authVerifierConfiguration.setAuthVerifierClassName(
			authVerifierClass.getName());
		authVerifierConfiguration.setProperties(
			_loadProperties(serviceReference, authVerifierClass.getName()));
	
		if (!_validate(authVerifierConfiguration)) {
			return null;
		}
		
		return authVerifierConfiguration;
	}

	@Reference
	private Portal _portal;
	
	private BundleContext _bundleContext;
	
	private Map<String, List<AuthVerifierConfiguration>> 
		_authVerifierConfigurations = new HashMap<>();	

	private ServiceTracker<
		AuthVerifier, 
		Map.Entry<AuthVerifierConfiguration, ServiceTracker<ServletContextHelper, String>>>
			_serviceTracker;
	
	private ServiceTracker<AuthVerifier, AuthVerifierConfiguration> 
		_portalServiceTracker;
	
	private static final Log _log = LogFactoryUtil.getLog(
		AuthVerifierRegistryImpl.class);

	private class ServletContextHelperTrackerCustomizer
		implements ServiceTrackerCustomizer<ServletContextHelper, String> {

		public ServletContextHelperTrackerCustomizer(
			AuthVerifierConfiguration authVerifierConfiguration) {
			
			_authVerifierConfiguration = authVerifierConfiguration;
		}
		
		private String _getPathModule(
			ServiceReference<ServletContextHelper> serviceReference) {
			
			return _portal.getPathModule() +
				GetterUtil.getString(
					serviceReference.getProperty(
						"osgi.http.whiteboard.context.path"));
		}
		
		@Override
		public String addingService(
			ServiceReference<ServletContextHelper> serviceReference) {
			
			String contextPath = _getPathModule(serviceReference);
			
			List<AuthVerifierConfiguration> authVerifierConfigurations = 
				_authVerifierConfigurations.computeIfAbsent(
					_getPathModule(serviceReference), (cp) -> new ArrayList<AuthVerifierConfiguration>());
			
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
			
			authVerifierConfigurations = 
				_authVerifierConfigurations.get(
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
		
		private AuthVerifierConfiguration _authVerifierConfiguration;
	}

	private class AuthVerifierTrackerCustomizer
		implements ServiceTrackerCustomizer<
			AuthVerifier, 
			Map.Entry<AuthVerifierConfiguration, ServiceTracker<ServletContextHelper, String>>> {
	
		@Override
		public Map.Entry<AuthVerifierConfiguration, ServiceTracker<ServletContextHelper, String>> 
				addingService(
			ServiceReference<AuthVerifier> serviceReference) {
	
			AuthVerifierConfiguration authVerifierConfiguration = 
				_buildAuthVerifierConfiguration(serviceReference);
	
			//_authVerifierConfigurations.add(0, authVerifierConfiguration);

			ServiceTracker<ServletContextHelper, String> serviceTracker = 
				_openServletContextHelperServiceTracker(
					serviceReference, authVerifierConfiguration);
			
			return new AbstractMap.SimpleEntry<>(
				authVerifierConfiguration, serviceTracker);
		}

		@Override
		public void modifiedService(
			ServiceReference<AuthVerifier> serviceReference,
			Map.Entry<
				AuthVerifierConfiguration, 
				ServiceTracker<ServletContextHelper, String>> 
					entry) {
	
			ServiceTracker<ServletContextHelper, String> serviceTracker = 
				entry.getValue();
			
			serviceTracker.close();
						
			AuthVerifierConfiguration authVerifierConfiguration = 
				entry.getKey();

			AuthVerifier authVerifier = _bundleContext.getService(serviceReference);
			
			Class<?> authVerifierClass = authVerifier.getClass();
			
			authVerifierConfiguration.setProperties(
				_loadProperties(serviceReference, authVerifierClass.getName()));
			
			if (!_validate(authVerifierConfiguration)) {
				return;
			}
			
			serviceTracker = 
				_openServletContextHelperServiceTracker(
					serviceReference, authVerifierConfiguration);
			
			entry.setValue(serviceTracker);
			
			authVerifierConfiguration.setProperties(
				_loadProperties(
					serviceReference,
					authVerifierConfiguration.getAuthVerifierClassName()));	
		}
	
		@Override
		public void removedService(
			ServiceReference<AuthVerifier> serviceReference,
			Map.Entry<
				AuthVerifierConfiguration, 
				ServiceTracker<ServletContextHelper, String>> 
					entry) {
	
			_bundleContext.ungetService(serviceReference);
			
			ServiceTracker<ServletContextHelper, String> serviceTracker = 
				entry.getValue();
			
			serviceTracker.close();			
		}
	
		private ServiceTracker<ServletContextHelper, String> 
			_openServletContextHelperServiceTracker(
				ServiceReference<AuthVerifier> serviceReference, 
				AuthVerifierConfiguration authVerifierConfiguration) {
			
			String servletContextHelperSelectFilter = 
				GetterUtil.getString(
					serviceReference.getProperty(
						"servlet.context.helper.select.filter"));
	
			String filterString = StringBundler.concat(
				"(&", servletContextHelperSelectFilter, "(", 
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "=*)",
				"(objectClass=", ServletContextHelper.class.getName(), "))");
			
			ServiceTracker<ServletContextHelper, String> 
				serviceTracker = ServiceTrackerFactory.open(
					_bundleContext, filterString,
					new ServletContextHelperTrackerCustomizer(authVerifierConfiguration));
			
			return serviceTracker;
		}
	
	}
	
	private class PortalAuthVerifierTrackerCustomizer
		implements ServiceTrackerCustomizer<AuthVerifier, AuthVerifierConfiguration> {
	
		@Override
		public AuthVerifierConfiguration addingService(
			ServiceReference<AuthVerifier> serviceReference) {
	
			AuthVerifierConfiguration authVerifierConfiguration = 
				_buildAuthVerifierConfiguration(serviceReference);

			List<AuthVerifierConfiguration> authVerifierConfigurations = 
				_authVerifierConfigurations.computeIfAbsent(
					StringPool.BLANK, (cp) -> new ArrayList<AuthVerifierConfiguration>());
			
			authVerifierConfigurations.add(authVerifierConfiguration);
			
			return authVerifierConfiguration;
		}
	
		@Override
		public void modifiedService(
			ServiceReference<AuthVerifier> serviceReference,
			AuthVerifierConfiguration authVerifierConfiguration) {
		
			List<AuthVerifierConfiguration> authVerifierConfigurations = 
				_authVerifierConfigurations.get(StringPool.BLANK);
			
			if (authVerifierConfigurations == null) {
				return;
			}
			
			Iterator<AuthVerifierConfiguration> iterator = 
				authVerifierConfigurations.iterator();
			
			while (iterator.hasNext()) {
				AuthVerifierConfiguration authVerifierConfiguration2 = iterator.next();
				if (authVerifierConfiguration == authVerifierConfiguration2) {
					
					AuthVerifier authVerifier = authVerifierConfiguration.getAuthVerifier();
					Class<?> authVerifierClass = authVerifier.getClass();
					
					authVerifierConfiguration.setProperties(
						_loadProperties(serviceReference, authVerifierClass.getName()));

					if (!_validate(authVerifierConfiguration)) {
						iterator.remove();
						return;
					}
				}
			}
			
			if (_validate(authVerifierConfiguration)) {
				authVerifierConfigurations.add(authVerifierConfiguration);
			}			
		}
	
		@Override
		public void removedService(
			ServiceReference<AuthVerifier> serviceReference,
			AuthVerifierConfiguration authVerifierConfiguration) {
	
			_bundleContext.ungetService(serviceReference);
			
			List<AuthVerifierConfiguration> authVerifierConfigurations = 
				_authVerifierConfigurations.get(StringPool.BLANK);
			
			authVerifierConfigurations.remove(authVerifierConfiguration);
		}
	}	
}
