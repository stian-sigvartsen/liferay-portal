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
page import="com.liferay.oauth2.provider.exception.DuplicateOAuth2ApplicationClientIdException" %><%@
page import="com.liferay.oauth2.provider.exception.OAuth2ApplicationClientGrantTypeException" %><%@
page import="com.liferay.oauth2.provider.exception.OAuth2ApplicationHomePageURLException" %><%@
page import="com.liferay.oauth2.provider.exception.OAuth2ApplicationHomePageURLSchemeException" %><%@
page import="com.liferay.oauth2.provider.exception.OAuth2ApplicationNameException" %><%@
page import="com.liferay.oauth2.provider.exception.OAuth2ApplicationPrivacyPolicyURLException" %><%@
page import="com.liferay.oauth2.provider.exception.OAuth2ApplicationPrivacyPolicyURLSchemeException" %><%@
page import="com.liferay.oauth2.provider.exception.OAuth2ApplicationRedirectURIException" %><%@
page import="com.liferay.oauth2.provider.exception.OAuth2ApplicationRedirectURIFragmentException" %><%@
page import="com.liferay.oauth2.provider.exception.OAuth2ApplicationRedirectURIMissingException" %><%@
page import="com.liferay.oauth2.provider.exception.OAuth2ApplicationRedirectURIPathException" %><%@
page import="com.liferay.oauth2.provider.exception.OAuth2ApplicationRedirectURISchemeException" %><%@
page import="com.liferay.oauth2.provider.model.OAuth2ApplicationScopeAliases" %><%@
page import="com.liferay.oauth2.provider.model.OAuth2Authorization" %><%@ 
page import="com.liferay.oauth2.provider.scope.spi.application.descriptor.ApplicationDescriptor" %><%@
page import="com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope" %><%@
page import="com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService" %><%@ 
page import="com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalServiceUtil" %><%@
page import="com.liferay.oauth2.provider.service.OAuth2AuthorizationServiceUtil" %><%@ 
page import="com.liferay.oauth2.provider.web.internal.constants.OAuth2AdminWebKeys" %><%@
page import="com.liferay.oauth2.provider.web.internal.display.context.AssignScopesModel"%><%@
page import="com.liferay.oauth2.provider.web.internal.display.context.AuthorizationModel"%><%@
page import="com.liferay.oauth2.provider.web.internal.display.context.OAuth2AdminPortletDisplayContext" %><%@
page import="com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap" %><%@
page import="com.liferay.portal.kernel.dao.search.ResultRow" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.security.auth.PrincipalException" %><%@
page import="com.liferay.portal.kernel.util.StringPool" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %>

<%@ page import="java.util.ArrayList" %><%@ 
page import="java.util.Collections" %><%@
page import="java.util.Comparator"%><%@
page import="java.util.HashSet"%><%@
page import="java.util.Map"%><%@
page import="java.util.Set"%><%@
page import="java.util.stream.Collectors"%>
	
<%
OAuth2AdminPortletDisplayContext oAuth2AdminPortletDisplayContext = (OAuth2AdminPortletDisplayContext)request.getAttribute(OAuth2AdminWebKeys.ADMIN_DISPLAY_CONTEXT);
%>
