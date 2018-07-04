/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.oauth2.provider.scope.spi.scope.descriptor;

import com.liferay.oauth2.provider.scope.spi.scope.descriptor.ScopeDescriptor;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	immediate = true,
	property = {
		"osgi.jaxrs.name=liferay-oauth2-scope-sample-verbs-qa-app",
		"service.ranking:Integer=" + Integer.MAX_VALUE
	},
	service = ScopeDescriptor.class
)
public class RESTBestPracticeScopeDescriptor implements ScopeDescriptor {

	@Override
	public String describeScope(String scope, Locale locale) {
		String key = "oauth2.scope." + scope;
		ResourceBundle resourceBundle =
			_resourceBundleLoader.loadResourceBundle(locale);

		return ResourceBundleUtil.getString(resourceBundle, key);
	}

	@Reference(
		target = "(bundle.symbolic.name=com.example.oauth2.provider.scope.spi.scope.descriptor)"
	)
	private volatile ResourceBundleLoader _resourceBundleLoader;

}