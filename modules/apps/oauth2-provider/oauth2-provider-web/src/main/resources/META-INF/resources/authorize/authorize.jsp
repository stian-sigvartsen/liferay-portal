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

<%@include file="/authorize/init.jsp"%>

<%
AuthorizationRequestModel authorizationRequestModel = oAuth2AuthorizePortletDisplayContext.getAuthorizationRequestModel();

OAuth2Application oAuth2Application = oAuth2AuthorizePortletDisplayContext.getOAuth2Application();

Map<String, String> oAuth2Parameters = oAuth2AuthorizePortletDisplayContext.getOAuth2Parameters();

String replyTo = PortalUtil.escapeRedirect(oAuth2Parameters.get("reply_to"));
%>
<c:choose>
	<c:when test="<%= oAuth2Application == null %>">
		<liferay-ui:header title="authorize-oauth2-application" />
	</c:when>
	<c:when test="<%= !oAuth2AuthorizePortletDisplayContext.hasCreateTokenApplicationPermission(oAuth2Application) %>">
		<liferay-ui:header title="you-dont-have-permissions-to-authorize-the-application" />
	</c:when>
    <c:otherwise>
	    <liferay-ui:header title="<%= LanguageUtil.format(request, "authorize-x", new String[]{oAuth2Application.getName()}) %>" />

	    <h4>
		    <liferay-ui:message key="description" />
	    </h4>
	    <div>
		    <%= HtmlUtil.escape(oAuth2Application.getDescription() )%>
	    </div>

	    <h4>
		    <liferay-ui:message key="application-will-have-access-to" />:
	    </h4>
	    <div>
		    <ul>
				<%
				for (String appName : authorizationRequestModel.getApplicationNames()) {
				%>
					<div>
					<ul>
						<li><%= HtmlUtil.escape(appName) %></li>
						<ul>
						<%
						for (String description : authorizationRequestModel.getApplicationScopeDescription(appName)) {
							%>
							<li><%= HtmlUtil.escape(description) %></li>
							<%
						}
						%>
						</ul>
					</ul>
					</div>
				<%
				}
			    %>
		    </ul>
	    </div>

	    <div class="closed container-fluid-1280">
		    <aui:form action="<%= replyTo %>" method="GET" name="fm">
				<%
					for (String paramName : oAuth2Parameters.keySet()) {
						if (paramName.equals("reply_to")) {
							continue;
						}
				%>
					<aui:input name="<%= HtmlUtil.escapeAttribute(paramName) %>" type="hidden" useNamespace="false" value="<%= oAuth2Parameters.get(paramName) %>" />
				<%
					}
				%>

				<aui:input name="oauthDecision" type="hidden" useNamespace="false" value="deny" />

			    <aui:fieldset>
				    <aui:button-row>
					    <aui:button type="button" value="allow" id="allow"/>
					    <aui:button type="submit" value="deny" />
				    </aui:button-row>
		        </aui:fieldset>
	        </aui:form>
        </div>

        <aui:script>
	        $('#<portlet:namespace />allow').on(
		        'click',
		        function() {
			        document.<portlet:namespace/>fm.oauthDecision.value='allow';
			        document.<portlet:namespace/>fm.submit();
		        }
	        );
        </aui:script>
    </c:otherwise>
</c:choose>
