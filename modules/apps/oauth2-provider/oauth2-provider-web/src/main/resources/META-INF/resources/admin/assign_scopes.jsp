<%@ page
	import="com.liferay.oauth2.provider.scope.spi.application.descriptor.ApplicationDescriptor" %>
<%@ page
	import="com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap" %><%--
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
<%@ include file="/admin/init.jsp" %>
<%
String redirect = ParamUtil.getString(request, "redirect");

long oAuth2ApplicationId = ParamUtil.getLong(request, "oAuth2ApplicationId");


OAuth2Application oAuth2Application = null;

try {
	oAuth2Application = OAuth2ApplicationServiceUtil.getOAuth2Application(oAuth2ApplicationId);
}
catch (PrincipalException e) {
	%>
		<liferay-ui:error-principal />
	<%

	return;
}

portletDisplay.setShowBackIcon(true);

portletDisplay.setURLBack(redirect);

renderResponse.setTitle(LanguageUtil.get(request, "assign-scopes"));

Map<String, AuthorizationRequestModel> scopes = (Map<String, AuthorizationRequestModel>) request.getAttribute(OAuth2AdminWebKeys.SCOPES);
//Map<String, Set<String>> scopeDescriptions = (Map<String, Set<String>>) request.getAttribute(OAuth2AdminWebKeys.SCOPES_DESCRIPTIONS);

ServiceTrackerMap<String, ApplicationDescriptor> applicationDescriptors = (ServiceTrackerMap<String, ApplicationDescriptor>) request.getAttribute("applicationDescriptors");

List<String> assignedScopes = oAuth2Application.getScopesList();
%>

<style>
	.preview-container {
		background-color: #EEEEEE;
	}
	
	.preview-content {}
	
	.dashed-top {
		height: 8px;
		border-top: 4px dashed #FFFFFF;
		background-color: #EEEEEE;
	}
	
	.dashed-bottom {
		height: 8px;
		border-top: 4px dashed #EEEEEE;
		background-color: #FFFFFF;
	}
</style>

<div class="container-fluid-1280">
	<portlet:actionURL name="/admin/assign_scopes" var="updateScopesURL" />
	<aui:form action="<%= updateScopesURL %>" name="fm">

		<aui:fieldset-group markupView="lexicon">
			<aui:input type="hidden" name="oAuth2ApplicationId" value='<%= oAuth2ApplicationId %>' />

			<%
				for (Map.Entry<String, AuthorizationRequestModel> aliasedScopes : scopes.entrySet()) {
					String alias = aliasedScopes.getKey();
			%>
				<div class="row">
				<div class="col-lg-12">
					<aui:input checked="<%= assignedScopes.contains(alias) %>" label="<%= HtmlUtil.escape(alias) %>" localizeLabel="false" name="<%= "scope_" + HtmlUtil.escapeAttribute(alias) %>" type="checkbox" />
				</div>
				</div>
			<%
					AuthorizationRequestModel authorizationRequestModel = aliasedScopes.getValue();
					for (String appName : authorizationRequestModel.getApplicationNames()) {
					%>
					<div class="row">
					<div class="col-lg-6">
						<ul>
							<%							
							for (String internalScope : authorizationRequestModel.getApplicationInternalScopes(appName)) {
								%>
									<li><%=HtmlUtil.escape(appName)%> -> <%=HtmlUtil.escape(internalScope)%></li>
								<%
							}								
							%>
						</ul>
					</div>
					<div class="col-lg-6">
						<div class="preview-container">
							<div class="dashed-top"></div>
							<div class="preview-content">
							<ul>
								<%
								ApplicationDescriptor applicationDescriptor = applicationDescriptors.getService(appName);

								String applicationDescription;

								if (applicationDescriptor == null) {
									applicationDescription = appName;
								}
								else {
									applicationDescription = applicationDescriptor.describeApplication(locale);
								}
								%>
								<li><%= HtmlUtil.escape(applicationDescription) %></li>
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
							<div class="dashed-bottom"></div>
						</div>
					</div>
					</div>
					<%
					}

				}
			%>
		</aui:fieldset-group>

		<aui:button-row>
			<aui:button cssClass="btn-lg" type="submit" />
			<aui:button cssClass="btn-lg" href="<%= portletDisplay.getURLBack() %>" type="cancel" />
		</aui:button-row>
	</aui:form>
</div>