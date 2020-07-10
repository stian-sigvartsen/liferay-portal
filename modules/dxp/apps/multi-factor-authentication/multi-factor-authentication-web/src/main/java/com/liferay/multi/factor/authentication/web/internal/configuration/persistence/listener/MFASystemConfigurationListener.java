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

package com.liferay.multi.factor.authentication.web.internal.configuration.persistence.listener;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.multi.factor.authentication.email.otp.configuration.MFAEmailOTPConfiguration;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Dictionary;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marta Medio
 */
@Component(
	immediate = true,
	property = "model.class.name=com.liferay.multi.factor.authentication.web.internal.system.configuration.MFASystemConfiguration",
	service = ConfigurationModelListener.class
)
public class MFASystemConfigurationListener
	implements ConfigurationModelListener {

	@Override
	public void onAfterSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		try {
			boolean mfaDisableGlobally = GetterUtil.getBoolean(
				properties.get("disableGlobally"));

			for (Company company : _companyLocalService.getCompanies()) {
				long companyId = company.getCompanyId();

				MFAEmailOTPConfiguration mfaEmailOTPConfiguration =
					ConfigurationProviderUtil.getCompanyConfiguration(
						MFAEmailOTPConfiguration.class, companyId);

				if (mfaEmailOTPConfiguration.enabled()) {
					_sendNotificationToInstanceAdministrators(
						companyId, mfaDisableGlobally);
				}
			}
		}
		catch (ConfigurationException configurationException) {
			_log.error(
				"Unable to obtain multi-factor authentication configuration",
				configurationException);

			throw new ConfigurationModelListenerException(
				configurationException.getMessage(),
				ConfigurationException.class,
				MFASystemConfigurationListener.class, properties);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to send multi-factor authentication notification",
				portalException);

			throw new ConfigurationModelListenerException(
				portalException.getMessage(), PortalException.class,
				MFASystemConfigurationListener.class, properties);
		}
	}

	private void _sendNotificationToInstanceAdministrators(
			long companyId, boolean mfaDisableGlobally)
		throws PortalException {

		Role role = _roleLocalService.getRole(
			companyId, RoleConstants.ADMINISTRATOR);

		List<User> administratorUsers = _userLocalService.getRoleUsers(
			role.getRoleId());

		for (User user : administratorUsers) {
			long userId = user.getUserId();

			JSONObject notificationEventJSONObject = JSONUtil.put(
				"classPK", ConfigurationAdminPortletKeys.INSTANCE_SETTINGS
			).put(
				"mfaDisableGlobally", mfaDisableGlobally
			).put(
				"userId", userId
			);

			_userNotificationEventLocalService.sendUserNotificationEvents(
				userId, ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
				UserNotificationDeliveryConstants.TYPE_WEBSITE, false,
				notificationEventJSONObject);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MFASystemConfigurationListener.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}