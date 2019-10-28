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

package com.liferay.multi.factor.authentication.checker.email.otp.web.internal.portlet.action;

import com.liferay.multi.factor.authentication.checker.email.otp.web.internal.checker.EmailOTPMFAChecker;
import com.liferay.multi.factor.authentication.checker.email.otp.web.internal.contants.MFAPortletKeys;
import com.liferay.multi.factor.authentication.checker.email.otp.web.internal.contants.MFAPortletURLFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"javax.portlet.name=" + MFAPortletKeys.MFA_VERIFY_PORTLET,
		"mvc.command.name=/mfa_verify/verify"
	},
	service = MVCActionCommand.class
)
public class MFAVerifyMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long userId = getMFAUserId(actionRequest);

		if (userId == 0) {
			SessionErrors.add(actionRequest, "sessionExpired");

			actionResponse.setRenderParameter("mvcRenderCommandName", "/");
			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		if (_emailOTPMFAChecker.verifyBrowserRequest(
				_portal.getHttpServletRequest(actionRequest),
				_portal.getHttpServletResponse(actionResponse), userId)) {

			sendRedirect(actionRequest, actionResponse);
		}
	}

	private long getMFAUserId(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (themeDisplay.isSignedIn()) {
			return themeDisplay.getUserId();
		}

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(portletRequest));

		HttpSession session = httpServletRequest.getSession();

		Object mfaUserId = session.getAttribute(
			MFAPortletURLFactory.MFA_USER_ID);

		if (mfaUserId == null) {
			return 0;
		}

		return (Long)mfaUserId;
	}

	@Reference
	private EmailOTPMFAChecker _emailOTPMFAChecker;

	@Reference
	private Portal _portal;

}