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

package com.liferay.oauth2.provider.rest;

import com.liferay.portal.kernel.util.MapUtil;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenGrantHandler;
import org.apache.cxf.rs.security.oauth2.services.AccessTokenService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate=true,
	property = {
		"oauth2.allow.access.token.endpoint=true",
		"oauth2.allow.access.token.endpoint.public.clients=true"
	}
)
public class OAuth2AccessTokenServiceRegistrator {

	@Activate
	public void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		boolean enabled = MapUtil.getBoolean(
			properties, "oauth2.allow.token.introspection.endpoint", true);

		if (enabled) {
			boolean publicClientsEnabled = MapUtil.getBoolean(
				properties,
				"oauth2.allow.token.introspection.endpoint.public.clients",
				true);

			OAuth2AccessTokenService accessTokenService =
				new OAuth2AccessTokenService();

			accessTokenService.setBlockUnsecureRequests(true);
			accessTokenService.setCanSupportPublicClients(publicClientsEnabled);
			accessTokenService.setDataProvider(_liferayOAuthDataProvider);
			accessTokenService.setGrantHandlers(_accessTokenGrantHandlers);

			Hashtable<String, Object> endpointProperties = new Hashtable<>();

			endpointProperties.put("liferay.oauth2.endpoint", true);

			_endpointServiceRegistration = bundleContext.registerService(
				Object.class, accessTokenService,
				endpointProperties);
		}
	}

	@Deactivate
	public void deactivate() {
		if (_endpointServiceRegistration != null) {
			_endpointServiceRegistration.unregister();
		}
	}

	@Reference(
		cardinality = ReferenceCardinality.AT_LEAST_ONE,
		policyOption = ReferencePolicyOption.GREEDY,
		unbind = "removeAccessTokenGrantHandler"
	)
	public void addAccessTokenGrantHandler(
		AccessTokenGrantHandler accessTokenGrantHandler) {

		_accessTokenGrantHandlers.add(accessTokenGrantHandler);
	}

	public void removeAccessTokenGrantHandler(
		AccessTokenGrantHandler accessTokenGrantHandler) {

		_accessTokenGrantHandlers.remove(accessTokenGrantHandler);
	}

	private List<AccessTokenGrantHandler> _accessTokenGrantHandlers =
		new ArrayList<>();

	private ServiceRegistration<Object> _endpointServiceRegistration;

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

	public class OAuth2AccessTokenService extends AccessTokenService {
		@Override
		protected Client authenticateClientIfNeeded(
			MultivaluedMap<String, String> params) {

			Client client = super.authenticateClientIfNeeded(params);

			Map<String, String> clientProperties = client.getProperties();

			HttpServletRequest httpServletRequest =
				getMessageContext().getHttpServletRequest();

			String remoteAddr = httpServletRequest.getRemoteAddr();

			String remoteHost = httpServletRequest.getRemoteHost();

			try {
				InetAddress inetAddress = InetAddress.getByName(remoteAddr);
				remoteHost = inetAddress.getCanonicalHostName();
			}
			catch (UnknownHostException e) {
			}

			clientProperties.put(
				LiferayOAuthDataProvider.CLIENT_REMOTE_ADDR_PROPERTY,
				remoteAddr);

			clientProperties.put(
				LiferayOAuthDataProvider.CLIENT_REMOTE_HOST_PROPERTY,
				remoteHost);

			return client;
		}
	}
}
