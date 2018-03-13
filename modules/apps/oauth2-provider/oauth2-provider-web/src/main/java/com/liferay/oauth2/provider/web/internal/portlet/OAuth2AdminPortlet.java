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

package com.liferay.oauth2.provider.web.internal.portlet;

import com.liferay.oauth2.provider.configuration.OAuth2Configuration;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationService;
import com.liferay.oauth2.provider.service.OAuth2RefreshTokenService;
import com.liferay.oauth2.provider.service.OAuth2TokenService;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2AdminWebKeys;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.oauth2.provider.web.internal.display.context.OAuth2AdminPortletDisplayContext;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringPool;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.configuration.OAuth2Configuration",
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.preferences-company-wide=true",
		"javax.portlet.display-name=OAuth2 Admin",
		"javax.portlet.init-param.portlet-title-based-navigation=true",
		"javax.portlet.init-param.template-path=/admin/",
		"javax.portlet.init-param.view-template=/admin/view.jsp",
		"javax.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_ADMIN_PORTLET,
		"javax.portlet.preferences=classpath:/META-INF/portlet-preferences/default-portlet-preferences.xml",
		"javax.portlet.resource-bundle=content.Language"
	},
	service = Portlet.class
)
public class OAuth2AdminPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		OAuth2AdminPortletDisplayContext oAuth2AdminPortletDisplayContext =
			new OAuth2AdminPortletDisplayContext(_oAuth2Configuration);

		renderRequest.setAttribute(
			OAuth2AdminWebKeys.ADMIN_DISPLAY_CONTEXT,
			oAuth2AdminPortletDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	public void deleteOAuth2Application(
			ActionRequest request, ActionResponse response)
		throws PortalException {

		long oAuth2ApplicationId = ParamUtil.getLong(
			request, "oAuth2ApplicationId");

		try {
			_oAuth2ApplicationService.deleteOAuth2Application(
				oAuth2ApplicationId);
		}
		catch (PortalException pe) {
			SessionErrors.add(request, pe.getClass());
		}
	}

	public void updateOAuth2Application(
			ActionRequest request, ActionResponse response)
		throws PortalException {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			OAuth2Application.class.getName(), request);

		long oAuth2ApplicationId = ParamUtil.getLong(
			request, "oAuth2ApplicationId");

		boolean clientConfidential = ParamUtil.get(
			request, "clientConfidential", false);
				
		String clientId = ParamUtil.get(
			request, "clientId", StringPool.BLANK);

		String clientSecret = ParamUtil.get(
			request, "clientSecret", StringPool.BLANK);

		String name = ParamUtil.get(request, "name", StringPool.BLANK);

		PortletPreferences portletPreferences = request.getPreferences();

		String[] oAuth2Features = StringUtil.split(portletPreferences.getValue(
			"oAuth2Features", StringPool.BLANK));

		List<String> featuresList = new ArrayList<>(oAuth2Features.length);

		for(String feature : oAuth2Features) {
			if (ParamUtil.getBoolean(request, "feature-" + feature, false)) {
				featuresList.add(feature);
			}
		}

		String description = ParamUtil.get(
			request, "description", StringPool.BLANK);

		String homePageURL = ParamUtil.get(
			request, "homePageURL", StringPool.BLANK);

		OAuth2AdminPortletDisplayContext oAuth2AdminPortletDisplayContext =
			new OAuth2AdminPortletDisplayContext(_oAuth2Configuration);

		List<GrantType> oAuth2Grants =
			oAuth2AdminPortletDisplayContext.getOAuth2Grants(
				portletPreferences);

		List<GrantType> allowedGrantTypes = new ArrayList<>(
			oAuth2Grants.size());

		for(GrantType grantType : oAuth2Grants) {
			if (ParamUtil.getBoolean(request, "grant-" + grantType.name())) {
				allowedGrantTypes.add(grantType);
			}
		}

		String privacyPolicyURL = ParamUtil.get(
			request, "privacyPolicyURL", StringPool.BLANK);

		List<String> redirectURIsList = Arrays.asList(
			StringUtil.splitLines(
				ParamUtil.get(request, "redirectURIs", StringPool.BLANK)));

		List<String> scopesList = Collections.emptyList();
		long iconFileEntryId = 0;

		OAuth2Application oAuth2Application = null;
		try {
			if (oAuth2ApplicationId == 0) {
				oAuth2Application =
					_oAuth2ApplicationService.addOAuth2Application(
						allowedGrantTypes, clientConfidential, clientId,
						clientSecret, description, featuresList, homePageURL,
						iconFileEntryId, name, privacyPolicyURL,
						redirectURIsList, scopesList, serviceContext);
			}
			else {
				oAuth2Application =
					_oAuth2ApplicationService.getOAuth2Application(
						oAuth2ApplicationId);

				iconFileEntryId = oAuth2Application.getIconFileEntryId();
				scopesList = oAuth2Application.getScopesList();

				oAuth2Application =
					_oAuth2ApplicationService.updateOAuth2Application(
						oAuth2ApplicationId, allowedGrantTypes,
						clientConfidential, clientId, clientSecret, description,
						featuresList, homePageURL, iconFileEntryId, name,
						privacyPolicyURL, redirectURIsList, scopesList,
						serviceContext);
			}

			UploadPortletRequest uploadPortletRequest =
				_portal.getUploadPortletRequest(request);

			String sourceFileName = uploadPortletRequest.getFileName("icon");

			if (!Validator.isBlank(sourceFileName)) {
				try (InputStream inputStream =
						 uploadPortletRequest.getFileAsStream("icon")) {

					_oAuth2ApplicationService.updateIcon(
						oAuth2Application.getOAuth2ApplicationId(),
						inputStream);
				}
			}
		}
		catch (IOException ioe) {
			_log.error(ioe);

			response.setRenderParameter(
				"mvcPath", "/admin/edit_application.jsp");

		}
		catch (PortalException pe) {
			if (_log.isDebugEnabled()) {
				_log.debug(pe);
			}

			Class<?> peClass = pe.getClass();

			SessionErrors.add(request, peClass.getName(), pe);

			response.setRenderParameter(
				"mvcPath", "/admin/edit_application.jsp");
		}
	}

	public void revokeAuthorizationTokens(
			ActionRequest request, ActionResponse response)
		throws PortalException {

		long oAuth2TokenId = ParamUtil.getLong(request, "oAuth2TokenId");

		long oAuth2RefreshTokenId = ParamUtil.getLong(
			request, "oAuth2RefreshTokenId");

		_oAuth2AuthorizationService.revokeAuthorization(
			oAuth2TokenId, oAuth2RefreshTokenId);
	}


	@Activate
	protected void activate(Map<String, Object> properties) {
		_oAuth2Configuration =
			ConfigurableUtil.createConfigurable(
				OAuth2Configuration.class, properties);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2AdminPortlet.class);

	private OAuth2Configuration _oAuth2Configuration;

	@Reference
	private OAuth2ApplicationService _oAuth2ApplicationService;
	@Reference
	private OAuth2RefreshTokenService _oAuth2RefreshTokenService;
	@Reference
	private OAuth2AuthorizationService _oAuth2AuthorizationService;
	@Reference
	private OAuth2TokenService _oAuth2TokenService;
	@Reference
	private Portal _portal;


}