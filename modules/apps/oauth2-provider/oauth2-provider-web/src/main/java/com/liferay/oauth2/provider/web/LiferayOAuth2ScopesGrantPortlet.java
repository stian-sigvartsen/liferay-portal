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

package com.liferay.oauth2.provider.web;

import com.liferay.oauth2.provider.model.LiferayOAuth2ScopeInternalIdentifier;
import com.liferay.oauth2.provider.scopes.liferay.api.ScopeFinderLocator;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

@Component(
	immediate = true,
	property = {
		"javax.portlet.name=com.liferay.oauth2.provider.web.LiferayOAuth2ScopesGrantPortlet",
		"javax.portlet.display-name=OAuth2 Grant Scopes Portlet"
	},
	service = Portlet.class
)
public class LiferayOAuth2ScopesGrantPortlet extends MVCPortlet {

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		OutputStream portletOutputStream =
			renderResponse.getPortletOutputStream();

		PrintWriter printWriter = new PrintWriter(portletOutputStream, true);

		printWriter.println("Hello world");

		for (LiferayOAuth2ScopeInternalIdentifier
			identifier : _scopeFinderLocator.listScopes()) {

			printWriter.print(identifier.getApplicationName());
			printWriter.print(":");
			printWriter.print(identifier.getScopeInternalIdentifier());
			printWriter.println("<br>");
		}
	}

	@Reference
	private ScopeFinderLocator _scopeFinderLocator;
}
