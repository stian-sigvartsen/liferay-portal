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

import com.liferay.portal.kernel.util.LocaleUtil;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.Cookie;

/**
 * @author Stian Sigvartsen
 */
public class CookieUtil {

	public static String generateSetCookieHeaderValue(
		String newName, Cookie cookie, String sameSite) {

		return cookieUtil._generateSetCookieHeaderValue(
			newName, cookie.getValue(), cookie.getMaxAge(), cookie.getDomain(),
			cookie.getPath(), cookie.getSecure(), cookie.isHttpOnly(),
			sameSite);
	}

	public static String generateSetCookieHeaderValue(
		String newName, String value, int maxAge, String domain, String path,
		boolean secure, boolean httpOnly, String sameSite) {

		return cookieUtil._generateSetCookieHeaderValue(
			newName, value, maxAge, domain, path, secure, httpOnly, sameSite);
	}

	protected static final String COOKIE_DATE_PATTERN =
		"EEE, dd-MMM-yyyy HH:mm:ss z";

	protected static CookieUtil cookieUtil = new CookieUtil();

	protected final String ancientDate;
	protected final DateFormat dateFormat;

	private CookieUtil() {
		dateFormat = new SimpleDateFormat(COOKIE_DATE_PATTERN, LocaleUtil.US);

		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		ancientDate = dateFormat.format(new Date(10000));
	}

	/**
	 * Inspired by org.apache.tomcat.util.http.Rfc6265CookieProcessor#generateHeader
	 */
	private String _generateSetCookieHeaderValue(
		String newName, String value, int maxAge, String domain, String path,
		boolean secure, boolean httpOnly, String sameSite) {

		// Can't use StringBuilder due to DateFormat

		StringBuffer header = new StringBuffer();

		// TODO: Name validation takes place in Cookie and cannot be configured
		//       per Context. Moving it to here would allow per Context config
		//       but delay validation until the header is generated. However,
		//       the spec requires an IllegalArgumentException on Cookie
		//       generation.

		header.append(newName);
		header.append('=');

		if ((value != null) && (value.length() > 0)) {
			header.append(value);
		}

		// RFC 6265 prefers Max-Age to Expires but... (see below)

		if (maxAge > -1) {

			// Negative Max-Age is equivalent to no Max-Age

			header.append("; Max-Age=");
			header.append(maxAge);

			// Microsoft IE and Microsoft Edge don't understand Max-Age so send
			// expires as well. Without this, persistent cookies fail with those
			// browsers. See http://tomcat.markmail.org/thread/g6sipbofsjossacn

			// Wdy, DD-Mon-YY HH:MM:SS GMT ( Expires Netscape format )

			header.append("; Expires=");

			// To expire immediately we need to set the time in past

			if (maxAge == 0) {
				header.append(ancientDate);
			}
			else {
				dateFormat.format(
					new Date(System.currentTimeMillis() + maxAge * 1000L),
					header, new FieldPosition(0));
			}
		}

		if ((domain != null) && (domain.length() > 0)) {
			header.append("; Domain=");
			header.append(domain);
		}

		if ((path != null) && (path.length() > 0)) {
			header.append("; Path=");
			header.append(path);
		}

		if (secure) {
			header.append("; Secure");
		}

		if (httpOnly) {
			header.append("; HttpOnly");
		}

		if ((sameSite != null) && (sameSite.length() > 0)) {
			header.append("; SameSite=" + sameSite);
		}

		return header.toString();
	}

}