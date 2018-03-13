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

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(
	category = "foundation",
	factoryInstanceLabelAttribute = "configuration.name"
)
@Meta.OCD(
	factory = true,
	id = "com.liferay.oauth2.provider.scope.internal.configuration.ConfigurableScopeMapperConfiguration",
	localization = "content/Language",
	name = "oauth2-configurable-scopemapper-configuration-name"
)
public interface ConfigurableScopeMapperConfiguration {

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
		deflt = "false", description = "default-system-scopemapper-description",
		id = "default", name = "default-system-scopemapper", required = false
	)
	public boolean defaultSystemScopeMapper();

	@Meta.AD(
		deflt = "GET\\,HEAD\\,OPTIONS=everything.readonly,PUT\\,POST\\,PATCH\\,DELETE=everything\\,everything.writeonly",
		description = "mapping-description", id = "mapping", name = "mapping",
		required = false
	)
	public String[] mapping();

	@Meta.AD(
		deflt = "false", description = "passthrough-description",
		id = "passthrough", name = "passthrough", required = false
	)
	public boolean passthrough();

}