/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.saml.admin.rest.internal.resource.v1_0;

import com.liferay.saml.admin.rest.dto.v1_0.Idp;
import com.liferay.saml.admin.rest.dto.v1_0.Provider;
import com.liferay.saml.admin.rest.resource.v1_0.ProviderResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Stian Sigvartsen
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/provider.properties",
	scope = ServiceScope.PROTOTYPE, service = ProviderResource.class
)
public class ProviderResourceImpl extends BaseProviderResourceImpl {
	@Override
	public Provider getProvider() throws Exception {

		Provider Provider = new Provider();

		Provider.setEnabled(true);
		Provider.setEntityId("test");

		Provider.setIdp(_getIdp());

		return Provider;
	}

	@Override
	public Object getProvider(String roleId) throws Exception {
		return _getIdp();
	}

	private Idp _getIdp() {

		Idp idp = new Idp();

		return idp;
	}	
}