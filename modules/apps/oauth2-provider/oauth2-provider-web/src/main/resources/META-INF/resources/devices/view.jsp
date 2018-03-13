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

<%@ include file="/devices/init.jsp" %>
<%
String orderByCol = ParamUtil.getString(request, "orderByCol", "createDate");
String orderByType = ParamUtil.getString(request, "orderByType", "asc");

%>
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
			orderColumns='<%= new String[] {"createDate", "application-name"} %>'
			portletURL="<%= PortletURLUtil.clone(portletURL, liferayPortletResponse) %>"
		/>
	</liferay-frontend:management-bar-filters>
</liferay-frontend:management-bar>

<div class="container-fluid-1280">
	<liferay-ui:search-container
		emptyResultsMessage="no-devices-were-found"
		iteratorURL="<%= portletURL %>"
		total="<%= OAuth2AuthorizationServiceUtil.countByUserId() %>">

		<%
			OrderByComparator orderByComparator = null;

			if (orderByCol.equals("createDate")) {
				orderByComparator = OrderByComparatorFactoryUtil.create("OAuth2Authorization", "createDate", orderByType.equals("asc"));
			}
			else if (orderByCol.equals("application-name")) {
				orderByComparator = OrderByComparatorFactoryUtil.create("OAuth2Authorization", "oAuth2ApplicationId", orderByType.equals("asc"));
			}
		%>

		<liferay-ui:search-container-results
			results="<%= OAuth2AuthorizationServiceUtil.findByUserId(searchContainer.getStart(), searchContainer.getEnd(), orderByComparator) %>"/>

		<liferay-ui:search-container-row
			className="com.liferay.oauth2.provider.model.OAuth2Authorization"
			escapedModel="true"
			modelVar="oAuth2Authorization">

			<%
				String applicationDescription = StringPool.BLANK;
				String applicationName = StringPool.BLANK;
				String thumbnailURL = StringPool.BLANK;

				try {
					OAuth2Application oAuth2Application = OAuth2ApplicationServiceUtil.getOAuth2Application(oAuth2Authorization.getoAuth2ApplicationId());
					applicationDescription = oAuth2Application.getDescription();
					applicationName = oAuth2Application.getName();

					if (oAuth2Application.getIconFileEntryId() > 0) {
						FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(oAuth2Application.getIconFileEntryId());
						thumbnailURL = DLUtil.getThumbnailSrc(fileEntry, themeDisplay);
					}
				}
				catch (PortalException e) {
					// user has no longer access to the application
				}

			%>
			<liferay-ui:search-container-column-image name="icon"
				src="<%= thumbnailURL %>" />

			<liferay-ui:search-container-column-text
				name="application-name" value="<%= HtmlUtil.escape(applicationName) %>"/>

			<liferay-ui:search-container-column-text
				name="application-description" value="<%= HtmlUtil.escape(applicationDescription) %>"/>

			<liferay-ui:search-container-column-date
				property="createDate" />

			<liferay-ui:search-container-column-date
				property="accessTokenExpirationDate" />

			<liferay-ui:search-container-column-date
				property="refreshTokenExpirationDate" />

			<liferay-ui:search-container-column-text
				property="remoteIPInfo" />

			<liferay-ui:search-container-column-text
				name="scopes">

				<%= String.valueOf(oAuth2Authorization.getScopesList().size()) %>

				<liferay-ui:icon-help message="<%= HtmlUtil.escapeAttribute(oAuth2Authorization.getScopes()) %>" />

			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-jsp
				align="right"
				path="/devices/authorization_actions.jsp" />

		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
			searchContainer="<%= searchContainer %>"/>

	</liferay-ui:search-container>

</div>

