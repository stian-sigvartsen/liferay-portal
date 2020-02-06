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

package com.liferay.portal.samesite.cookies.support.internal.servlet.filter;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;
import com.liferay.portal.kernel.servlet.PortalSessionContext;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.samesite.cookies.support.internal.configuration.SameSiteCookiesConfiguration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.portal.samesite.cookies.support.internal.configuration.SameSiteCookiesConfiguration",
	immediate = true,
	property = {
		"after-filter=Session Id Filter", "servlet-context-name=",
		"servlet-filter-name=SameSite Cookies Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class SameSiteCookiesFilter extends BaseFilter {

	@Override
	public boolean isFilterEnabled() {
		return _sameSiteCookiesConfiguration.enabled();
	}

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return _sameSiteCookiesConfiguration.enabled();
	}

	@Activate
	protected void activate(
			BundleContext bundleContext, Map<String, Object> properties)
		throws Exception {

		_sameSiteCookiesConfiguration = ConfigurableUtil.createConfigurable(
			SameSiteCookiesConfiguration.class, properties);

		_cookieNames = new HashSet<>(
			Arrays.asList(_sameSiteCookiesConfiguration.cookieNames()));
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		SessionIdCompatServletRequest sessionIdCompatServletRequest =
			new SessionIdCompatServletRequest(
				httpServletRequest, httpServletResponse);

		filterChain.doFilter(
			sessionIdCompatServletRequest,
			new CookieCompatServletResponse(httpServletResponse));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SameSiteCookiesFilter.class);

	private Set<String> _cookieNames;
	private SameSiteCookiesConfiguration _sameSiteCookiesConfiguration;

	private class CookieCompatServletResponse
		extends HttpServletResponseWrapper {

		public CookieCompatServletResponse(
			HttpServletResponse httpServletResponse) {

			super(httpServletResponse);
		}

		@Override
		public void addCookie(Cookie cookie) {
			if (_cookieNames.contains(cookie.getName())) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Sending compatibility cookie: ", cookie.getName(),
							"=", cookie.getValue()));
				}

				addHeader(
					"Set-Cookie",
					CookieUtil.generateSetCookieHeaderValue(
						cookie.getName() + "-compat", cookie, "None"));
			}

			super.addCookie(cookie);
		}

	}

	private class SessionIdCompatServletRequest
		extends PersistentHttpServletRequestWrapper {

		public SessionIdCompatServletRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			super(httpServletRequest);

			getCookies();

			_httpServletResponse = httpServletResponse;
		}

		@Override
		public Cookie[] getCookies() {
			if (_cookies != null) {
				return _cookies;
			}

			Cookie[] cookies = super.getCookies();

			if (cookies == null) {
				return null;
			}

			Map<String, String> compatCookieValues = new HashMap<>();

			Map<String, Cookie> finalCookies = new HashMap<>();

			for (Cookie cookie : cookies) {
				String name = cookie.getName();

				String processedCookieName = processCookieName(name);

				if (name.equals(processedCookieName)) {
					finalCookies.put(name, cookie);
				}
				else {
					compatCookieValues.put(
						processedCookieName, cookie.getValue());
				}
			}

			_jsessionidCompatReceived = compatCookieValues.get("JSESSIONID");

			Set<String> keySet = compatCookieValues.keySet();

			keySet.removeIf(finalCookies.keySet()::contains);

			if (!compatCookieValues.isEmpty() && _log.isDebugEnabled()) {
				_log.debug(
					"Compatibility cookies activated: " + compatCookieValues);
			}

			compatCookieValues.forEach(
				(name, value) -> finalCookies.put(
					name, new Cookie(name, value)));

			Collection<Cookie> finalCookiesValues = finalCookies.values();

			_cookies = finalCookiesValues.toArray(new Cookie[0]);

			return _cookies;
		}

		public HttpSession getSession() {
			return getSession(true);
		}

		@Override
		public HttpSession getSession(boolean create) {
			if (!_sameSiteCookiesConfiguration.sessionCookie()) {
				return super.getSession(create);
			}

			HttpSession httpSession = super.getSession(false);

			if ((httpSession == null) && (_jsessionidCompatReceived != null)) {
				httpSession = PortalSessionContext.get(
					_jsessionidCompatReceived);

				if ((httpSession != null) && _isSessionValid(httpSession)) {
					return httpSession;
				}
			}

			if ((httpSession == null) && create) {
				httpSession = super.getSession(true);
			}
			else if (httpSession == null) {
				return null;
			}

			String sessionId = httpSession.getId();

			if (!sessionId.equals(_jsessionidCompatReceived)) {
				process(httpSession);
			}

			return httpSession;
		}

		public String processCookieName(String compatCookieName) {
			int index = compatCookieName.lastIndexOf(
				"-compat", compatCookieName.length() - 7);

			if (index == -1) {
				return compatCookieName;
			}

			String origName = compatCookieName.substring(0, index);

			if (_cookieNames.contains(origName)) {
				return origName;
			}

			return compatCookieName;
		}

		protected void process(HttpSession session) {
			String sessionId = session.getId();

			Object jsessionIdCompatAlreadySet = getAttribute(
				_JESSIONIDCOMPAT_ALREADY_SET);

			if (sessionId.equals(jsessionIdCompatAlreadySet)) {
				return;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Sending compatibility cookie for session " + sessionId);
			}

			_httpServletResponse.addHeader(
				"Set-Cookie",
				CookieUtil.generateSetCookieHeaderValue(
					_JESSIONIDCOMPAT, sessionId, -1, null, "/", isSecure(),
					true, "None"));

			setAttribute(_JESSIONIDCOMPAT_ALREADY_SET, sessionId);
		}

		private boolean _isSessionValid(HttpSession httpSession) {
			try {
				httpSession.isNew();

				return true;
			}
			catch (IllegalStateException illegalStateException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Session ", httpSession.getId(),
							" referenced from a compatibility cookie is ",
							"invalid"));
				}

				return false;
			}
		}

		private static final String _JESSIONIDCOMPAT = "JSESSIONID-compat";

		private static final String _JESSIONIDCOMPAT_ALREADY_SET =
			"JESSIONIDCOMPAT_ALREADY_SET";

		private Cookie[] _cookies;
		private final HttpServletResponse _httpServletResponse;
		private String _jsessionidCompatReceived;

	}

}