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

package com.liferay.multi.factor.authentication.checker.email.otp.web.internal;

import com.liferay.multi.factor.authentication.checker.email.otp.web.internal.contants.LoginMFAIntegrationWebKeys;
import com.liferay.petra.encryptor.Encryptor;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.util.Accessor;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.security.Key;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * @author Tomas Polesovsky
 */
public class RequestEncryptionHelper {

	public static Map<String, Object> createStateFromRequestParameters(
		HttpServletRequest httpServletRequest) {

		Map<String, Object> stateMap = new HashMap<>();

		stateMap.put("requestParameters", httpServletRequest.getParameterMap());

		return stateMap;
	}

	public static Map<String, Object> decrypt(
			HttpSession session, String encryptedStateMapJSON)
		throws Exception {

		Map<String, Object> encryptionInfo =
			(Map<String, Object>)session.getAttribute(
				LoginMFAIntegrationWebKeys.ENCRYPTION_INFO);

		String digest = (String)encryptionInfo.get("digest");
		Key key = (Key)encryptionInfo.get("key");

		if (!StringUtil.equals(
				digest, DigesterUtil.digest(encryptedStateMapJSON))) {

			throw new PrincipalException("User sent unverified data");
		}

		String stateMapJSON = Encryptor.decrypt(key, encryptedStateMapJSON);

		Map<String, Object> stateMap = JSONFactoryUtil.looseDeserialize(
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

	public static String encrypt(
			HttpSession session, Map<String, Object> stateMap)
		throws Exception {

		String stateMapJSON = JSONFactoryUtil.looseSerializeDeep(stateMap);

		Key key = Encryptor.generateKey();

		String encryptedStateMapJSON = Encryptor.encrypt(key, stateMapJSON);

		String digest = DigesterUtil.digest(encryptedStateMapJSON);

		Map<String, Object> encryptionInfo = new HashMap<>();

		encryptionInfo.put("digest", digest);
		encryptionInfo.put("key", key);

		session.setAttribute(LoginMFAIntegrationWebKeys.ENCRYPTION_INFO, key);

		return encryptedStateMapJSON;
	}

	public static HttpServletRequest loadRequestParametersFromState(
		HttpServletRequest request, Map<String, Object> stateMap) {

		Map<String, String[]> requestParameters =
			(Map<String, String[]>)stateMap.get("requestParameters");

		request = new HttpServletRequestWrapper(request) {

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

		return request;
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

}