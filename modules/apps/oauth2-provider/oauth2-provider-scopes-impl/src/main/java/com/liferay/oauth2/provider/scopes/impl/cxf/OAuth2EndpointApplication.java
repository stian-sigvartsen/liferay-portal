/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.scopes.impl.cxf;

import org.apache.cxf.rs.security.oauth2.provider.AccessTokenGrantHandler;
import org.apache.cxf.rs.security.oauth2.provider.OAuthJSONProvider;
import org.apache.cxf.rs.security.oauth2.provider.SubjectCreator;
import org.apache.cxf.rs.security.oauth2.services.AccessTokenService;
import org.apache.cxf.rs.security.oauth2.services.AuthorizationCodeGrantService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

@Component(
	immediate = true,
	service = Application.class
)
@ApplicationPath("/")
public class OAuth2EndpointApplication extends Application {

	@Activate
	public void activate(BundleContext bundleContext) throws IOException {
		ServiceReference<ConfigurationAdmin> serviceReference =
			bundleContext.getServiceReference(ConfigurationAdmin.class);

		try {
			ConfigurationAdmin configurationAdmin = bundleContext.getService(
				serviceReference);

			_cxfConfiguration = configurationAdmin.createFactoryConfiguration(
				"com.liferay.portal.remote.cxf.common.configuration." +
				"CXFEndpointPublisherConfiguration",
				"?");

			Dictionary<String, Object> properties = new Hashtable<>();

			properties.put("contextPath", "/oauth2");

			_cxfConfiguration.update(properties);

			_restConfiguration = configurationAdmin.createFactoryConfiguration(
				"com.liferay.portal.remote.rest.extender.configuration." +
				"RestExtenderConfiguration",
				"?");

			properties = new Hashtable<>();

			properties.put("contextPaths", new String[]{"/oauth2"});
			properties.put(
				"jaxRsApplicationFilterStrings",
				new String[]{"(component.name="+ getClass().getName()+")"});

			_restConfiguration.update(properties);
		}
		finally {
			bundleContext.ungetService(serviceReference);
		}
	}

	@Deactivate
	public void deactivate(){
		try {
			_cxfConfiguration.delete();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		try {
			_restConfiguration.delete();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<Object> getSingletons() {
		AuthorizationCodeGrantService authorizationCodeGrantService =
			new AuthorizationCodeGrantService();

		authorizationCodeGrantService.setDataProvider(
			_liferayOAuthDataProvider);

		authorizationCodeGrantService.setSubjectCreator(_subjectCreator);

		AccessTokenService accessTokenService = new AccessTokenService();

		accessTokenService.setBlockUnsecureRequests(true);
		accessTokenService.setDataProvider(_liferayOAuthDataProvider);

		accessTokenService.setGrantHandlers(_accessTokenGrantHandlers);

		return new HashSet<>(
			Arrays.asList(
				authorizationCodeGrantService, accessTokenService,
				_authorizationMessageBodyWriter));
	}

	@Override
	public Set<Class<?>> getClasses() {
		return Collections.singleton(OAuthJSONProvider.class);
	}

	@Reference(
		cardinality = ReferenceCardinality.AT_LEAST_ONE,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private List<AccessTokenGrantHandler> _accessTokenGrantHandlers;

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

	@Reference
	private AuthorizationMessageBodyWriter _authorizationMessageBodyWriter;

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private SubjectCreator _subjectCreator;

	private Configuration _cxfConfiguration;
	private Configuration _restConfiguration;

}