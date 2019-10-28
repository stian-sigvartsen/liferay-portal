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

package com.liferay.multi.factor.authentication.checker.email.otp.web.internal.portlet;

import com.liferay.multi.factor.authentication.checker.email.otp.web.internal.contants.MFAPortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.InterruptedPortletRequestWhitelistUtil;
import com.liferay.portal.util.PropsValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.portlet.Portlet;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.application-type=full-page-application",
		"com.liferay.portlet.css-class-wrapper=portlet-mfa-verify",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.preferences-company-wide=true",
		"javax.portlet.display-name=Multi Factor Authentication Portlet",
		"javax.portlet.init-param.mvc-command-names-default-views=/mfa_verify/view",
		"javax.portlet.init-param.portlet-title-based-navigation=true",
		"javax.portlet.init-param.template-path=/META-INF/resources/mfa_verify/",
		"javax.portlet.name=" + MFAPortletKeys.MFA_VERIFY_PORTLET,
		"javax.portlet.resource-bundle=content.Language",
		"portlet.add.default.resource.check.whitelist=" + MFAPortletKeys.MFA_VERIFY_PORTLET
	},
	service = Portlet.class
)
public class MFAVerifyPortlet extends MVCPortlet {

	@Activate
	protected void activate(BundleContext bundleContext) {
		List<String> portletInterruptedRequestWhitelistList = new ArrayList(
			Arrays.asList(PropsValues.PORTLET_INTERRUPTED_REQUEST_WHITELIST));

		portletInterruptedRequestWhitelistList.add(
			MFAPortletKeys.MFA_VERIFY_PORTLET);

		PropsValues.PORTLET_INTERRUPTED_REQUEST_WHITELIST =
			portletInterruptedRequestWhitelistList.toArray(new String[0]);

		_interruptedPortletRequestWhitelistUtil.
			resetPortletInvocationWhitelist();
	}

	@Deactivate
	protected void deactivate() {
		List<String> portletInterruptedRequestWhitelistList = new ArrayList(
			Arrays.asList(PropsValues.PORTLET_INTERRUPTED_REQUEST_WHITELIST));

		portletInterruptedRequestWhitelistList.add(
			MFAPortletKeys.MFA_VERIFY_PORTLET);

		PropsValues.PORTLET_INTERRUPTED_REQUEST_WHITELIST =
			portletInterruptedRequestWhitelistList.toArray(new String[0]);

		_interruptedPortletRequestWhitelistUtil.
			resetPortletInvocationWhitelist();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MFAVerifyPortlet.class);

	@Reference
	private InterruptedPortletRequestWhitelistUtil
		_interruptedPortletRequestWhitelistUtil;

}