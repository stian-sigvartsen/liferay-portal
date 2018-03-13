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

<%@include file="/admin/init.jsp"%>

<%
String orderByCol = ParamUtil.getString(request, "orderByCol", "name");
String orderByType = ParamUtil.getString(request, "orderByType", "asc");
%>

<aui:nav-bar cssClass="navbar-no-collapse" markupView="lexicon">
	<aui:nav collapsible="<%= false %>" cssClass="navbar-nav">
		<aui:nav-item label="oauth2-applications" selected="<%= true %>" />
	</aui:nav>
</aui:nav-bar>

<liferay-portlet:renderURL varImpl="portletURL" />

<liferay-frontend:management-bar>
	<liferay-frontend:management-bar-buttons>
		<liferay-frontend:management-bar-display-buttons
			displayViews='<%= new String[] {"list"} %>'
			portletURL="<%= PortletURLUtil.clone(portletURL, liferayPortletResponse) %>"
			selectedDisplayStyle="list"
		/>
	</liferay-frontend:management-bar-buttons>

	<liferay-frontend:management-bar-filters>
		<liferay-frontend:management-bar-navigation
			navigationKeys='<%= new String[] {"all"} %>'
			portletURL="<%= PortletURLUtil.clone(portletURL, liferayPortletResponse) %>"
		/>

		<liferay-frontend:management-bar-sort
			orderByCol="<%= orderByCol %>"
			orderByType="<%= orderByType %>"
			orderColumns='<%= new String[] {"name"} %>'
			portletURL="<%= PortletURLUtil.clone(portletURL, liferayPortletResponse) %>"
		/>
	</liferay-frontend:management-bar-filters>
</liferay-frontend:management-bar>

<div class="closed container-fluid-1280">
	<liferay-ui:search-container
		emptyResultsMessage="no-applications-were-found"
		total="<%= OAuth2ApplicationServiceUtil.getOAuth2ApplicationsCount(themeDisplay.getCompanyId()) %>">

		<%
			OrderByComparator orderByComparator = null;

			if (orderByCol.equals("name")) {
				orderByComparator = OrderByComparatorFactoryUtil.create("OAuth2Application", "name", orderByType.equals("asc"));
			}
		%>

	    <liferay-ui:search-container-results
	        results="<%= OAuth2ApplicationServiceUtil.getOAuth2Applications(
	            themeDisplay.getCompanyId(), searchContainer.getStart(), searchContainer.getEnd(), orderByComparator) %>" />
	
	    <liferay-ui:search-container-row
	        className="com.liferay.oauth2.provider.model.OAuth2Application"
			escapedModel="true"
			modelVar="oAuth2Application">


			<%
				String thumbnailURL = StringPool.BLANK;

				try {
					if (oAuth2Application.getIconFileEntryId() > 0) {
						FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(oAuth2Application.getIconFileEntryId());
						thumbnailURL = DLUtil.getThumbnailSrc(fileEntry, themeDisplay);
					}
				}
				catch (PortalException e) {
					// user has no longer access to the application
				}

			%>
			<liferay-ui:search-container-column-image
				name="icon"
				src="<%= thumbnailURL %>" />

			<liferay-ui:search-container-column-text
				property="name" />
	        
	        <liferay-ui:search-container-column-text
				property="description" />

			<liferay-ui:search-container-column-text
				name="granted-authorizations"
				value="<%= String.valueOf(OAuth2AuthorizationLocalServiceUtil.countByApplicationId(themeDisplay.getCompanyId(), oAuth2Application.getOAuth2ApplicationId())) %>" />

			<liferay-ui:search-container-column-text
				name="scopes">

				<%= String.valueOf(oAuth2Application.getScopesList().size()) %>

				<liferay-ui:icon-help message="<%= HtmlUtil.escapeAttribute(oAuth2Application.getScopes()) %>" />

			</liferay-ui:search-container-column-text>

	        <liferay-ui:search-container-column-jsp
	            align="right" 
	            path="/admin/application_actions.jsp" />
	
	    </liferay-ui:search-container-row>
	
	    <liferay-ui:search-iterator 
    			markupView="lexicon"
    			searchContainer="<%= searchContainer %>"/>
	</liferay-ui:search-container>


	<c:if test="<%= oAuth2AdminPortletDisplayContext.hasAddApplicationPermission() %>">
		<portlet:renderURL var="addOAuth2ApplicationURL">
			<portlet:param name="mvcPath" value="/admin/edit_application.jsp" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
		</portlet:renderURL>

		<liferay-frontend:add-menu>
			<liferay-frontend:add-menu-item title='<%= LanguageUtil.get(request, "add-o-auth2-application") %>' url="<%= addOAuth2ApplicationURL.toString() %>" />
		</liferay-frontend:add-menu>
	</c:if>
	
</div>