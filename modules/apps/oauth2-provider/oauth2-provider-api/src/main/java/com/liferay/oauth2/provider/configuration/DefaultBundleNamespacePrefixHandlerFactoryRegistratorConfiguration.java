package com.liferay.oauth2.provider.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.util.StringPool;

@ExtendedObjectClassDefinition(
	category = "foundation"
)
@Meta.OCD(
	id = "com.liferay.oauth2.provider.configuration.DefaultBundleNamespacePrefixHandlerFactoryRegistratorConfiguration",
	localization = "content/Language", name = "oauth2-default-bundlenamespace-prefixhandlerfactory-configuration-name"
)
public interface DefaultBundleNamespacePrefixHandlerFactoryRegistratorConfiguration {

	@Meta.AD(
		deflt = "osgi.jaxrs.name",
		required = false
	)
	public String[] serviceProperty();

	@Meta.AD(
		deflt = "", id = "exclued.scope",
		required = false
	)
	public String excludedScope();

	@Meta.AD(
		deflt = StringPool.SLASH, required = false
	)
	public String separator();
	
	@Meta.AD(
		deflt = "true",
		required = false
	)
	public boolean enabled();
	
	@Meta.AD(
		deflt = "true",
		required = false
	)
	public boolean includeBundleSymbolicName();
}