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

<%@ include file="/authorize/init.jsp" %>

<%
AuthorizationModel authorizationModel = oAuth2AuthorizePortletDisplayContext.getAuthorizationModel();

OAuth2Application oAuth2Application = oAuth2AuthorizePortletDisplayContext.getOAuth2Application();

Map<String, String> oAuth2Parameters = oAuth2AuthorizePortletDisplayContext.getOAuth2Parameters();

String replyTo = PortalUtil.escapeRedirect(oAuth2Parameters.get("reply_to"));

String thumbnailURL = StringPool.BLANK;

if (oAuth2Application.getIconFileEntryId() > 0) {
	FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(oAuth2Application.getIconFileEntryId());
	thumbnailURL = DLUtil.getThumbnailSrc(fileEntry, themeDisplay);
}
%>

<style>
	.consent .user-icon, .consent .app-icon {
		height: 64px;
		width: 64px;
	}
	
	.consent .list-group-item {
		border-left: 0;
		border-right: 0;
	}
	
	.consent .list-group :first-child {
		border-top: 0;
	}
	
	.consent .list-group :last-child {
		border-bottom: 0;
	}
</style>

<div class="closed container-fluid-1280 consent">
	<aui:form action="<%= replyTo %>" method="POST" name="fm">		
		<aui:fieldset-group markupView="lexicon">
		
			<div class="panel-body">
				<div style="display: inline-block; position: relative; left: 64px;">
					<liferay-ui:user-portrait
						imageCssClass="user-icon-lg"
						user="<%= user %>"
					/>
				</div>
				<div class="aspect-ratio-bg-cover app-icon" style="display: inline-block; position: relative; left: -64px; background-image:url(<%= thumbnailURL.toString() %>)"></div>	
			</div>

			<c:choose>
				<c:when test="<%= oAuth2Application == null %>">
					<liferay-ui:header title="authorize-oauth2-application" />
				</c:when>
				<c:when test="<%= !oAuth2AuthorizePortletDisplayContext.hasCreateTokenApplicationPermission(oAuth2Application) %>">
					<liferay-ui:header title="you-dont-have-permissions-to-authorize-the-application" />
				</c:when>
				<c:otherwise>
					<div class="panel-body">
						<liferay-ui:header title='<%= LanguageUtil.format(request, "authorize-x", new String[] {oAuth2Application.getName()}) %>' />
						<liferay-ui:message key="application-wants-permissions-to-access" />:
					</div>
					<div class="panel-body">
						<ul class="list-group">
							<%
							for (String applicationName : authorizationModel.getApplicationNames()) {
								String applicationScopeDescription = authorizationModel.getApplicationScopeDescription(applicationName).stream().collect(Collectors.joining(", "));
							%>
								<li class="list-group-item list-group-item-flex"> 
									<div class="autofit-col"> 
										<clay:icon symbol="check" />
									</div>
								
									<div class="autofit-col autofit-col-expand">
										<h4 class="list-group-title text-truncate"><%= HtmlUtil.escape(authorizationModel.getApplicationDescription(applicationName)) %></h4>		
										<p class="list-group-subtitle text-truncate"><%= applicationScopeDescription %></p>
									</div>
								</li>
								<%
							}
							%>
						</ul>
						<p class="list-group-subtitle text-truncate">Application <aui:a href="<%= HtmlUtil.escapeAttribute(oAuth2Application.getPrivacyPolicyURL()) %>" target="_blank">privacy policy</aui:a></p>
					</div>

					<div class="panel-body" style="border-top: 1px solid #e7e7ed;">
						<%
						for (String paramName : oAuth2Parameters.keySet()) {
							if (paramName.equals("reply_to")) {
								continue;
							}
							%>

							<aui:input name="<%= HtmlUtil.escapeAttribute(paramName) %>" type="hidden" useNamespace="<%= false %>" value="<%= oAuth2Parameters.get(paramName) %>" />
	
							<%
						}
						%>

						<aui:input name="oauthDecision" type="hidden" useNamespace="<%= false %>" value="deny" />
		
						<aui:button id="allow" type="button" value="authorize" />
						<aui:button type="submit" value="cancel" />

						<aui:script>
							$('#<portlet:namespace />allow').on(
								'click',
								function() {
									document.<portlet:namespace/>fm.oauthDecision.value='allow';
									document.<portlet:namespace/>fm.submit();
								}
							);
						</aui:script>
		
					</div>
				</c:otherwise>
			</c:choose>
		</aui:fieldset-group>
	</aui:form>
</div>