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

package com.liferay.portal.security.auth;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierConfiguration;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierRegistryUtil;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 * @author Peter Fellwock
 */
public class AuthVerifierPipeline {

	public static final String AUTH_TYPE = "auth.type";

	public static String getAuthVerifierPropertyName(String className) {
		String simpleClassName = StringUtil.extractLast(
			className, StringPool.PERIOD);

		return PropsKeys.AUTH_VERIFIER.concat(
			simpleClassName
		).concat(
			StringPool.PERIOD
		);
	}

	public static AuthVerifierResult verifyRequest(
			AccessControlContext accessControlContext)
		throws PortalException {

		return _instance._verifyRequest(accessControlContext);
	}

	private AuthVerifierResult _createGuestVerificationResult(
			AccessControlContext accessControlContext)
		throws PortalException {

		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		authVerifierResult.setState(AuthVerifierResult.State.UNSUCCESSFUL);

		HttpServletRequest httpServletRequest =
			accessControlContext.getRequest();

		long defaultUserId = UserLocalServiceUtil.getDefaultUserId(
			PortalUtil.getCompanyId(httpServletRequest));

		authVerifierResult.setUserId(defaultUserId);

		return authVerifierResult;
	}

	private Map<String, Object> _mergeSettings(
		Properties properties, Map<String, Object> settings) {

		Map<String, Object> mergedSettings = new HashMap<>(settings);

		if (properties != null) {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				mergedSettings.put((String)entry.getKey(), entry.getValue());
			}
		}

		return mergedSettings;
	}

	private AuthVerifierResult _verifyRequest(
			AccessControlContext accessControlContext)
		throws PortalException {

		if (accessControlContext == null) {
			throw new IllegalArgumentException(
				"Access control context is null");
		}
		
		for (AuthVerifierConfiguration authVerifierConfiguration :
				AuthVerifierRegistryUtil.getAuthVerifierConfigurations(
					accessControlContext)) {

			AuthVerifierResult authVerifierResult = null;

			AuthVerifier authVerifier =
				authVerifierConfiguration.getAuthVerifier();

			Properties properties = authVerifierConfiguration.getProperties();

			try {
				authVerifierResult = authVerifier.verify(
					accessControlContext, properties);
			}
			catch (Exception e) {
				if (_log.isDebugEnabled()) {
					Class<?> authVerifierClass = authVerifier.getClass();

					_log.debug("Skipping " + authVerifierClass.getName(), e);
				}

				continue;
			}

			if (authVerifierResult == null) {
				Class<?> authVerifierClass = authVerifier.getClass();

				_log.error(
					"Auth verifier " + authVerifierClass.getName() +
						" did not return an auth verifier result");

				continue;
			}

			if (authVerifierResult.getState() !=
					AuthVerifierResult.State.NOT_APPLICABLE) {

				User user = UserLocalServiceUtil.fetchUser(
					authVerifierResult.getUserId());

				if ((user == null) || !user.isActive()) {
					if (_log.isDebugEnabled()) {
						Class<?> authVerifierClass = authVerifier.getClass();

						if (user == null) {
							_log.debug(
								StringBundler.concat(
									"Auth verifier ",
									authVerifierClass.getName(),
									" returned null user",
									authVerifierResult.getUserId()));
						}
						else {
							_log.debug(
								StringBundler.concat(
									"Auth verifier ",
									authVerifierClass.getName(),
									" returned inactive user",
									authVerifierResult.getUserId()));
						}
					}

					continue;
				}

				Map<String, Object> settings = _mergeSettings(
					properties, authVerifierResult.getSettings());

				settings.put(AUTH_TYPE, authVerifier.getAuthType());

				authVerifierResult.setSettings(settings);

				return authVerifierResult;
			}
		}

		return _createGuestVerificationResult(accessControlContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuthVerifierPipeline.class);

	private static final AuthVerifierPipeline _instance =
		new AuthVerifierPipeline();

}