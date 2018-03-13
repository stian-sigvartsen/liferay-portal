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

package com.liferay.oauth2.provider.web.internal.portlet.action;

import com.liferay.oauth2.provider.service.OAuth2ApplicationService;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"javax.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_ADMIN_PORTLET,
		"mvc.command.name=/admin/assign_scopes"
	}
)
public class AssignScopesMVCActionCommand implements MVCActionCommand {

	@Override
	public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		List<String> scopes = new ArrayList<>();
		for (String parameterName : actionRequest.getParameterMap().keySet()) {
			if (parameterName.startsWith("scope_") &&
				ParamUtil.getBoolean(actionRequest, parameterName)) {
				scopes.add(parameterName.substring("scope_".length()));
			}
		}

		long oAuth2ApplicationId = ParamUtil.getLong(
			actionRequest, "oAuth2ApplicationId");

		try {
			_oAuth2ApplicationService.updateScopes(oAuth2ApplicationId, scopes);
		}
		catch (PortalException e) {
			if (_log.isDebugEnabled()) {
				_log.warn(
					"Unable to load OAuth2Application with id " +
					oAuth2ApplicationId);
			}
		}

		return true;
	}


	private static final Log _log = LogFactoryUtil.getLog(
		AssignScopesMVCActionCommand.class);

	@Reference
	private OAuth2ApplicationService _oAuth2ApplicationService;

}