package com.example.oauth2.provider.scope.spi.scope.descriptor;

import com.liferay.oauth2.provider.scope.spi.application.descriptor.ApplicationDescriptor;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	property = {
		"osgi.jaxrs.name=liferay-oauth2-scope-sample-verbs-qa-app",
		"service.ranking:Integer=" + Integer.MAX_VALUE
	},
	service = ApplicationDescriptor.class
)
public class ExampleApplicationDescriptor implements ApplicationDescriptor {

	@Override
	public String describeApplication(Locale locale) {
		ResourceBundle resourceBundle =
			_resourceBundleLoader.loadResourceBundle(locale);

		String key = "oauth2.application.description." + _osgiJaxrsName;

		return ResourceBundleUtil.getString(resourceBundle, key);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_osgiJaxrsName = MapUtil.getString(properties, "osgi.jaxrs.name");
	}

	private String _osgiJaxrsName;

	@Reference(
		target = "(bundle.symbolic.name=com.example.oauth2.provider.scope.spi.scope.descriptor)"
	)
	private ResourceBundleLoader _resourceBundleLoader;

}