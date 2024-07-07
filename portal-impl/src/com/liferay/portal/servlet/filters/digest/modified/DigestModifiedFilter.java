/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2024-07
 */

package com.liferay.portal.servlet.filters.digest.modified;


import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.servlet.filters.password.modified.PasswordModifiedFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @author Nikoletta Buza
 */
public class DigestModifiedFilter  extends BasePortalFilter {

	@Override
	protected void processFilter(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		//  TODO 	- we need to be able to distinguish WebDAV requests --> perhaps
		//				we can use the requestPath
		//			- do we need to be able to handle basic and digest authentication,
		//				do we need to identify them or is it unnecessary at this point?
		//				- if we do, do we need to use a similar approach than in BaseAuthFilter?
		//  			  e.g. if (_digestAuthEnabled) {
		//							httpServletRequest = digestAuth(
		//								httpServletRequest, httpServletResponse);
		//						}
		//						else if (_basicAuthEnabled) {
		//							httpServletRequest = basicAuth(
		//								httpServletRequest, httpServletResponse);
		//						}
		//					--> if most of that code was duplicated, modifying BasicAuthFilter
		//						would make more sense

		if (_isDigestModified(httpServletRequest)) {
			// TODO - do we need to generate challenge?
			//  	--> This is also BaseAuthFilter related code
			//  	+ log msg
		}
		else {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
		}
	}

	private boolean _isDigestModified(HttpServletRequest httpServletRequest) {
		HttpSession httpSession = httpServletRequest.getSession(false);

		// TODO - is this part needed in our scenario?
		if ((httpSession == null) ||
			!httpServletRequest.isRequestedSessionIdValid()) {

			return false;
		}

		try {
			// TODO - we need to retrieve the date when the digest PW was modified from the DB
			Date digestModifiedDate = null;

			if (digestModifiedDate == null) {
				return false;
			}

			// TODO - why do we need to check !httpServletRequest.isRequestedSessionIdValid() twice, should this stay?
			if (!httpServletRequest.isRequestedSessionIdValid() ||
				(httpSession.getCreationTime() <
				 digestModifiedDate.getTime())) {

				return true;
			}

			return false;
		}
		// TODO we will probably need this when the digestModifiedDate is retrieved from the DB
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PasswordModifiedFilter.class);

}