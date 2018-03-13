<%--
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
--%>
<%@ include file="/init.jsp" %>
<%@ page import="com.liferay.oauth2.provider.constants.GrantType" %><%@
page import="com.liferay.oauth2.provider.exception.UnsupportedGrantTypeForClientException" %><%@
page import="com.liferay.oauth2.provider.exception.ApplicationNameException" %><%@
page import="com.liferay.oauth2.provider.exception.DuplicateOAuth2ClientIdException" %><%@
page import="com.liferay.oauth2.provider.exception.EmptyClientSecretException" %><%@
page import="com.liferay.oauth2.provider.exception.InvalidHomePageURLException" %><%@
page import="com.liferay.oauth2.provider.exception.InvalidHomePageURLSchemeException" %><%@
page import="com.liferay.oauth2.provider.exception.InvalidPrivacyPolicyURLException" %><%@
page import="com.liferay.oauth2.provider.exception.InvalidPrivacyPolicyURLSchemeException" %><%@
page import="com.liferay.oauth2.provider.exception.InvalidRedirectURIException" %><%@
page import="com.liferay.oauth2.provider.exception.InvalidRedirectURIFragmentException" %><%@
page import="com.liferay.oauth2.provider.exception.InvalidRedirectURIPathException" %><%@
page import="com.liferay.oauth2.provider.exception.InvalidRedirectURISchemeException" %><%@
page import="com.liferay.oauth2.provider.exception.MissingRedirectURIException" %><%@
page import="com.liferay.oauth2.provider.exception.NonEmptyClientSecretException" %><%@
page import="com.liferay.oauth2.provider.model.OAuth2Authorization" %><%@
page import="com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalServiceUtil" %><%@
page import="com.liferay.oauth2.provider.service.OAuth2RefreshTokenLocalServiceUtil" %><%@
page import="com.liferay.oauth2.provider.service.OAuth2TokenLocalServiceUtil" %><%@
page import="com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope" %><%@
page import="com.liferay.oauth2.provider.web.internal.constants.OAuth2AdminWebKeys" %><%@
page import="com.liferay.oauth2.provider.web.internal.display.context.AuthorizationRequestModel"%><%@
page import="com.liferay.oauth2.provider.web.internal.display.context.OAuth2AdminPortletDisplayContext" %><%@
page import="com.liferay.portal.kernel.dao.search.ResultRow" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.security.auth.PrincipalException" %><%@
page import="com.liferay.portal.kernel.util.StringPool" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %>

<%@ page import="java.util.ArrayList" %>

<%
OAuth2AdminPortletDisplayContext oAuth2AdminPortletDisplayContext = (OAuth2AdminPortletDisplayContext)request.getAttribute(OAuth2AdminWebKeys.ADMIN_DISPLAY_CONTEXT);
%>
