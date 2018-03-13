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

package com.liferay.oauth2.provider.scope.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.util.StringPool;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(
	category = "foundation",
	factoryInstanceLabelAttribute = "configuration.name"
)
@Meta.OCD(
	factory = true,
	id = "com.liferay.oauth2.provider.scope.internal.configuration.BundlePrefixHandlerFactoryConfiguration",
	localization = "content/Language",
	name = "oauth2-bundle-prefix-handler-factory-configuration-name"
)
public interface BundlePrefixHandlerFactoryConfiguration {

	@Meta.AD(
		deflt = "", description = "configuration-name-description",
		id = "configuration.name", name = "configuration-name", required = false
	)
	public String configurationName();

	@Meta.AD(
		deflt = "", description = "osgi-jaxrs-name-description",
		id = "osgi.jaxrs.name", name = "osgi-jaxrs-name", required = false
	)
	public String osgiJAXRSName();

	@Meta.AD(
		deflt = "false",
		description = "default-system-prefixhandlerfactory-description",
		id = "default", name = "default-system-prefixhandlerfactory",
		required = false
	)
	public boolean defaultSystemPrefixHandlerFactory();

	@Meta.AD(
		deflt = "true",
		description = "include-bundle-symbolic-name-description",
		id = "include.bundle.symbolic.name",
		name = "include-bundle-symbolic-name", required = false
	)
	public boolean includeBundleSymbolicName();

	@Meta.AD(
		deflt = "", description = "excluded-scopes-description",
		id = "excluded.scopes", name = "excluded-scopes", required = false
	)
	public String[] excludedScopes();

	@Meta.AD(
		deflt = "osgi.jaxrs.name",
		description = "service-properties-description",
		id = "service.properties", name = "service-properties", required = false
	)
	public String[] serviceProperties();

	@Meta.AD(
		deflt = StringPool.SLASH, description = "separator-description",
		id = "separator", name = "separator", required = false
	)
	public String separator();

}