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

import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.servlet.HttpSessionWrapper;
import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringBundler;

import java.lang.ref.WeakReference;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Stian Sigvartsen
 */
@Component(
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
		return true;
	}

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return true;
	}

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		Dictionary<String, Object> properties = new HashMapDictionary<>();

		properties.put("key", PropsKeys.SERVLET_SESSION_CREATE_EVENTS);

		_serviceRegistrations.add(
			bundleContext.registerService(
				LifecycleAction.class,
				lifecycleEvent -> {
					HttpSession session = lifecycleEvent.getSession();

					if (_log.isDebugEnabled()) {
						_log.debug("Tracking new session " + session.getId());
					}

					_httpSessions.put(
						session.getId(),
						new WeakReference<HttpSession>(session));
				},
				properties));

		properties = new HashMapDictionary<>();

		properties.put("key", PropsKeys.SERVLET_SESSION_DESTROY_EVENTS);

		_serviceRegistrations.add(
			bundleContext.registerService(
				LifecycleAction.class,
				lifecycleEvent -> {
					HttpSession session = lifecycleEvent.getSession();

					_httpSessions.remove(session.getId());

					if (_log.isDebugEnabled()) {
						_log.debug(
							"Removed tracking of destroyed session " +
								session.getId());
					}
				},
				properties));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistrations.forEach(
			serviceRegistration -> serviceRegistration.unregister());
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
			sessionIdCompatServletRequest, httpServletResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SameSiteCookiesFilter.class);

	private final Map<String, WeakReference<HttpSession>> _httpSessions =
		new HashMap<>();
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

	private class DetectInvalidationHttpSessionWrapper
		extends HttpSessionWrapper {

		@Override
		public void invalidate() {
			_httpSessions.remove(getId());
			super.invalidate();

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Removed tracking of invalidated session " + getId());
			}
		}

		private DetectInvalidationHttpSessionWrapper(HttpSession session) {
			super(session);
		}

	}

	private class SessionIdCompatServletRequest
		extends PersistentHttpServletRequestWrapper {

		public SessionIdCompatServletRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			super(httpServletRequest);

			_httpServletResponse = httpServletResponse;
		}

		public HttpSession getSession() {
			return getSession(true);
		}

		@Override
		public HttpSession getSession(boolean create) {
			HttpSession httpSession = super.getSession(false);

			Cookie[] cookies = super.getCookies();

			String jSessionIdCompatValue = null;

			if ((cookies != null) && (cookies.length > 0)) {
				for (Cookie cookie : cookies) {
					String name = cookie.getName();

					if (!name.equals(_JESSIONIDCOMPAT)) {
						continue;
					}

					if (httpSession == null) {
						WeakReference<HttpSession> weakReference =
							_httpSessions.get(cookie.getValue());

						if (weakReference != null) {
							httpSession = weakReference.get();
						}

						if (httpSession != null) {
							return httpSession;
						}
					}

					jSessionIdCompatValue = cookie.getValue();

					break;
				}
			}

			if ((httpSession == null) && create) {
				httpSession = super.getSession(true);
			}
			else if (httpSession == null) {
				return null;
			}

			String sessionId = httpSession.getId();

			if (!sessionId.equals(jSessionIdCompatValue)) {
				process(httpSession);
			}

			return _getWrappedSession(httpSession);
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
					"Setting JSESSIONID-compat cookie for session " +
						sessionId);
			}

			String headerValue = StringBundler.concat(
				_JESSIONIDCOMPAT, "=", sessionId,
				"; Path=/; HttpOnly; SameSite=None");

			if (isSecure()) {
				headerValue = headerValue.concat("; Secure");
			}

			_httpServletResponse.addHeader("Set-Cookie", headerValue);

			setAttribute(_JESSIONIDCOMPAT_ALREADY_SET, sessionId);
		}

		private HttpSessionWrapper _getWrappedSession(HttpSession httpSession) {
			return new DetectInvalidationHttpSessionWrapper(httpSession);
		}

		private static final String _JESSIONIDCOMPAT = "JSESSIONID-compat";

		private static final String _JESSIONIDCOMPAT_ALREADY_SET =
			"JESSIONIDCOMPAT_ALREADY_SET";

		private final HttpServletResponse _httpServletResponse;

	}

}