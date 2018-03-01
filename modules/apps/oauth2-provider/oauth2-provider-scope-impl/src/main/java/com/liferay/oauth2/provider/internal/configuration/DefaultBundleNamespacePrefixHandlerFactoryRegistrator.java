package com.liferay.oauth2.provider.internal.configuration;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.liferay.oauth2.provider.configuration.DefaultBundleNamespacePrefixHandlerFactoryRegistratorConfiguration;
import com.liferay.oauth2.provider.scope.impl.prefixhandler.BundleNamespacePrefixHandlerFactory;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandlerFactory;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.GetterUtil;

@Component(
	immediate = true,
	configurationPid = 
		"com.liferay.oauth2.provider.configuration."
			+ "DefaultBundleNamespacePrefixHandlerFactoryRegistratorConfiguration"
)
public class DefaultBundleNamespacePrefixHandlerFactoryRegistrator {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {
		
		boolean enabled = 
			GetterUtil.getBoolean(properties.get("enabled"), true);
		
		if (enabled) {
			
			DefaultBundleNamespacePrefixHandlerFactoryRegistratorConfiguration configuration = 
				ConfigurableUtil.createConfigurable(
					DefaultBundleNamespacePrefixHandlerFactoryRegistratorConfiguration.class, properties);
			
			Hashtable<String, Object> prefixHandlerFactoryProperties = new Hashtable<>();
			
			prefixHandlerFactoryProperties.put("default", true);
			prefixHandlerFactoryProperties.put("service.ranking", 0);
			
			_defaultPrefixHandlerFactoryServiceRegistration = 
				bundleContext.registerService(
					PrefixHandlerFactory.class, 
					new BundleNamespacePrefixHandlerFactory(
						bundleContext, configuration.includeBundleSymbolicName(), 
						configuration.serviceProperty(), 
						configuration.excludedScope(), configuration.separator()), 
					prefixHandlerFactoryProperties);
		}
	}
	
	@Deactivate
	protected void deactivate() {
		if (_defaultPrefixHandlerFactoryServiceRegistration != null) {
			_defaultPrefixHandlerFactoryServiceRegistration.unregister();
		}
	}
	
	private ServiceRegistration<PrefixHandlerFactory> _defaultPrefixHandlerFactoryServiceRegistration;
}
