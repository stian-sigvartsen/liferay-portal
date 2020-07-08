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

package com.liferay.multi.factor.authentication.web.internal.notifications;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseModelUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marta Medio
 */
@Component(
	immediate = true,
	property = "javax.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
	service = UserNotificationHandler.class
)
public class MFASystemConfigurationUserNotificationHandler
	extends BaseModelUserNotificationHandler {

	public MFASystemConfigurationUserNotificationHandler() {
		setPortletId(ConfigurationAdminPortletKeys.INSTANCE_SETTINGS);
	}

	@Override
	protected String getBody(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			userNotificationEvent.getPayload());

		boolean mfaDisableGlobally = jsonObject.getBoolean(
			"mfaDisableGlobally");

		String body;

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			serviceContext.getLocale(), getClass());

		if (mfaDisableGlobally) {
			body = LanguageUtil.get(
				resourceBundle,
				"multi-factor-authentication-disabled-system-settings-wont-" +
					"work-for-any-instance");
		}
		else {
			body = LanguageUtil.get(
				resourceBundle,
				"multi-factor-authentication-enabled-for-all-instances");
		}

		String title = LanguageUtil.get(
			resourceBundle, "multi-factor-authentication");

		return StringUtil.replace(
			getBodyTemplate(), new String[] {"[$BODY$]", "[$TITLE$]"},
			new String[] {body, title});
	}

}