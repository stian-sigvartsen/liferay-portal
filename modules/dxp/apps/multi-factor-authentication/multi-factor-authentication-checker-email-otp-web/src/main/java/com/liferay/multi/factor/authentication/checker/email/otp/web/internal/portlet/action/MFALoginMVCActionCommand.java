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
import com.liferay.multi.factor.authentication.checker.email.otp.web.internal.configuration.EmailOTPConfiguration;
import com.liferay.multi.factor.authentication.checker.email.otp.web.internal.contants.LoginPortletKeys;
import com.liferay.multi.factor.authentication.checker.email.otp.web.internal.contants.MFAPortletURLFactory;
import com.liferay.petra.encryptor.Encryptor;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.kernel.util.Accessor;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.security.Key;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.ActionURL;
import javax.portlet.filter.ActionRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"javax.portlet.name=" + LoginPortletKeys.FAST_LOGIN,
		"javax.portlet.name=" + LoginPortletKeys.LOGIN,
		"mvc.command.name=/login/login", "service.ranking:Integer=1"
	},
	service = MVCActionCommand.class
)
public class MFALoginMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		EmailOTPConfiguration emailOTPConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				EmailOTPConfiguration.class,
				_portal.getCompanyId(actionRequest));

		if (!emailOTPConfiguration.enabled()) {
			_loginMVCActionCommand.processAction(actionRequest, actionResponse);

			return;
		}

		String state = ParamUtil.getString(actionRequest, "state");

		if (!Validator.isBlank(state)) {
			actionRequest = _loadRequestFromState(actionRequest, state);
		}

		String login = ParamUtil.getString(actionRequest, "login");
		String password = ParamUtil.getString(actionRequest, "password");

		if (!Validator.isBlank(login) && !Validator.isBlank(password)) {
			HttpServletRequest request = _portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

			long userId =
				AuthenticatedSessionManagerUtil.getAuthenticatedUserId(
					request, login, password, null);

			if (userId > 0) {
				if (!_emailOTPMFAChecker.isBrowserVerified(request, userId)) {
					_redirectToVerify(userId, actionRequest, actionResponse);

					return;
				}
			}
		}

		_loginMVCActionCommand.processAction(actionRequest, actionResponse);
	}

	private Map<String, Object> _decrypt(
			HttpSession session, String encryptedStateMapJSON)
		throws Exception {

		String digest = (String)session.getAttribute("digest");

		if (!StringUtil.equals(
				DigesterUtil.digest(encryptedStateMapJSON), digest)) {

			throw new PrincipalException("User sent unverified data");
		}

		Key key = (Key)session.getAttribute("key");

		String stateMapJSON = Encryptor.decrypt(key, encryptedStateMapJSON);

		Map<String, Object> stateMap = _jsonFactory.looseDeserialize(
			stateMapJSON, Map.class);

		Map<String, Object> requestParameters =
			(Map<String, Object>)stateMap.get("requestParameters");

		for (Map.Entry<String, Object> entry : requestParameters.entrySet()) {
			Object value = entry.getValue();

			if (value instanceof List) {
				entry.setValue(
					ListUtil.toArray((List<Object>)value, _STRING_ACCESSOR));
			}
		}

		return stateMap;
	}

	private String _encrypt(HttpSession session, Map<String, Object> stateMap)
		throws Exception {

		String stateMapJSON = _jsonFactory.looseSerializeDeep(stateMap);

		Key key = Encryptor.generateKey();

		String encryptedStateMapJSON = Encryptor.encrypt(key, stateMapJSON);

		session.setAttribute(
			"digest", DigesterUtil.digest(encryptedStateMapJSON));

		session.setAttribute("key", key);

		return encryptedStateMapJSON;
	}

	private ActionRequest _loadRequestFromState(
			ActionRequest actionRequest, String encryptedStateMap)
		throws Exception {

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		Map<String, Object> stateMap = _decrypt(
			httpServletRequest.getSession(), encryptedStateMap);

		Map<String, String[]> requestParameters =
			(Map<String, String[]>)stateMap.get("requestParameters");

		ActionRequestWrapper actionRequestWrapper = new ActionRequestWrapper(
			actionRequest) {

			@Override
			public String getParameter(String name) {
				return MapUtil.getString(requestParameters, name, null);
			}

			@Override
			public Map<String, String[]> getParameterMap() {
				return new HashMap<>(requestParameters);
			}

			@Override
			public Enumeration<String> getParameterNames() {
				return new Vector(
					requestParameters.keySet()
				).elements();
			}

			@Override
			public String[] getParameterValues(String name) {
				return requestParameters.get(name);
			}

		};

		return actionRequestWrapper;
	}

	private void _redirectToVerify(
			long userId, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		Map<String, Object> stateMap = new HashMap<>();

		stateMap.put("requestParameters", actionRequest.getParameterMap());

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		String state = _encrypt(httpServletRequest.getSession(), stateMap);

		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(actionResponse);

		ActionURL actionURL = liferayPortletResponse.createActionURL();

		actionURL.setParameter(ActionRequest.ACTION_NAME, "/login/login");
		actionURL.setParameter("state", state);

		LiferayPortletURL verifyURL = _mfaPortletURLFactory.createVerifyURL(
			httpServletRequest, actionURL.toString(), userId);

		actionRequest.setAttribute(WebKeys.REDIRECT, verifyURL.toString());
	}

	private static final Accessor<Object, String> _STRING_ACCESSOR =
		new Accessor<Object, String>() {

			@Override
			public String get(Object object) {
				return String.valueOf(object);
			}

			@Override
			public Class<String> getAttributeClass() {
				return String.class;
			}

			@Override
			public Class<Object> getTypeClass() {
				return Object.class;
			}

		};

	@Reference
	private EmailOTPMFAChecker _emailOTPMFAChecker;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(
		target = "(component.name=com.liferay.login.web.internal.portlet.action.LoginMVCActionCommand)"
	)
	private MVCActionCommand _loginMVCActionCommand;

	@Reference
	private MFAPortletURLFactory _mfaPortletURLFactory;

	@Reference
	private Portal _portal;

}