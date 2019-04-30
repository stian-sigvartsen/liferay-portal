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

<%@ include file="/admin/init.jsp" %>

<%
OAuth2Application oAuth2Application = oAuth2AdminPortletDisplayContext.getOAuth2Application();

SelectUsersDisplayContext selectUsersDisplayContext = new SelectUsersDisplayContext(request, renderRequest, renderResponse);

String clientCredentialsCheckboxName = null;
%>

<aui:model-context bean="<%= oAuth2Application %>" model="<%= OAuth2Application.class %>" />

<aui:fieldset>
	<aui:input helpMessage="application-name-help" label="application-name" name="name" required="<%= true %>" />

	<aui:input helpMessage="home-page-url-help" name="homePageURL" />

	<c:if test="<%= oAuth2Application != null %>">
		<aui:input helpMessage="application-description-help" label="application-description" name="description" />
	</c:if>

	<aui:input helpMessage="redirect-uris-help" label="redirect-uris" name="redirectURIs" required="<%= true %>" />

	<c:if test="<%= oAuth2Application != null %>">
		<aui:input helpMessage="privacy-policy-url-help" name="privacyPolicyURL" />
	</c:if>

	<aui:select name="clientProfile">

		<%
		ClientProfile[] clientProfiles = oAuth2AdminPortletDisplayContext.getSortedClientProfiles();

		for (ClientProfile clientProfile : clientProfiles) {
		%>

			<aui:option label="<%= clientProfile.name() %>" value="<%= clientProfile.id() %>" />

		<%
		}
		%>

	</aui:select>

	<h3 class="sheet-subtitle"><liferay-ui:message key="allowed-grant-types" /></h3>

	<div class="row">
		<div class="col-lg-6">
			<aui:field-wrapper>
				<div id="<portlet:namespace />allowedGrantTypes">

					<%
					List<GrantType> allowedGrantTypesList = new ArrayList<>();

					if (oAuth2Application != null) {
						allowedGrantTypesList = oAuth2Application.getAllowedGrantTypesList();
					}

					List<GrantType> oAuth2Grants = oAuth2AdminPortletDisplayContext.getGrantTypes(portletPreferences);

					for (GrantType grantType : oAuth2Grants) {
						Set<String> cssClasses = new HashSet<>();

						for (ClientProfile clientProfile : ClientProfile.values()) {
							if (clientProfile.grantTypes().contains(grantType)) {
								cssClasses.add("client-profile-" + clientProfile.id());
							}
						}

						String cssClassesStr = StringUtil.merge(cssClasses, StringPool.SPACE);

						boolean checked = false;

						if ((oAuth2Application == null) || allowedGrantTypesList.contains(grantType)) {
							checked = true;
						}

						String name = "grant-" + grantType.name();

						if (grantType.equals(GrantType.CLIENT_CREDENTIALS)) {
							clientCredentialsCheckboxName = name;
						}

						checked = ParamUtil.getBoolean(request, name, checked);

						Map<String, Object> data = new HashMap<>();

						data.put("isredirect", grantType.isRequiresRedirectURI());
						data.put("issupportsconfidentialclients", grantType.isSupportsConfidentialClients());
						data.put("issupportspublicclients", grantType.isSupportsPublicClients());
					%>

						<div class="allowedGrantType <%= cssClassesStr %>">
							<c:choose>
								<c:when test="<%= grantType.equals(GrantType.CLIENT_CREDENTIALS) %>">
									<aui:input checked="<%= checked %>" data="<%= data %>" helpMessage="the-client-will-impersonate-the-default-user" label="<%= grantType.name() %>" name="<%= clientCredentialsCheckboxName %>" type="checkbox" />
								</c:when>
								<c:otherwise>
									<aui:input checked="<%= checked %>" data="<%= data %>" label="<%= grantType.name() %>" name="<%= name %>" type="checkbox" />
								</c:otherwise>
							</c:choose>
						</div>

						<%
						if (grantType.isRequiresRedirectURI()) {
						%>

							<script>
								var allowedAuthorizationTypeCheckbox = document.getElementById('<portlet:namespace /><%= name %>');

								if (allowedAuthorizationTypeCheckbox) {
									allowedAuthorizationTypeCheckbox.addEventListener(
										'click',
										function(event) {
											<portlet:namespace />requiredRedirectURIs();
										}
									);
								}
							</script>

					<%
						}
					}
					%>

				</div>
			</aui:field-wrapper>
		</div>

		<c:if test="<%= clientCredentialsCheckboxName != null %>">
			<div class="col-lg-6" id="<portlet:namespace />userSelection">
				<aui:input name="clientCredentialUserId" type="hidden" />
				<aui:input disabled="<%= true %>" helpMessage="the-user-to-impersonate-when-the-authorization-type-has-no-user-context" label="default-user" name="clientCredentialUserName" type="text" />

				<div class="button-holder">
					<aui:button id="selectUserButton" value="select" />
					<aui:button id="removeUserButton" value="remove" />
				</div>

				<aui:script use="aui-base,aui-io">
					Liferay.Util.toggleBoxes('<portlet:namespace /><%= clientCredentialsCheckboxName %>', '<portlet:namespace />userSelection');

					var removeUserButton = document.getElementById('<portlet:namespace />removeUserButton');

					if (removeUserButton) {
						if (A.one('#<portlet:namespace />clientCredentialUserName').val() == "") {
							removeUserButton.disabled = true;
						}

						removeUserButton.addEventListener(
							'click',
							function(event) {
								A.one('#<portlet:namespace />clientCredentialUserId').val("");
								A.one('#<portlet:namespace />clientCredentialUserName').val("");

								removeUserButton.disabled = true;
							}
						);
					}

					var selectUserButton = document.getElementById('<portlet:namespace />selectUserButton');

					if (selectUserButton) {
						selectUserButton.addEventListener(
							'click',
							function(event) {
								Liferay.Util.selectEntity(
									{
										dialog: {
											modal: true,
											destroyOnHide: true
										},
										eventName: '<%= selectUsersDisplayContext.getEventName() %>',
										id: '<%= selectUsersDisplayContext.getEventName() %>',
										title: '<liferay-ui:message key="users" />',
										uri: '<%= selectUsersDisplayContext.getPortletURL() %>'
									},
									function(event) {
										A.one('#<portlet:namespace />clientCredentialUserId').val(event.userid);
										A.one('#<portlet:namespace />clientCredentialUserName').val(event.screenname);

										removeUserButton.disabled = false;

									}
								);
							}
						);
					}

				</aui:script>
			</div>
		</c:if>
	</div>

	<c:if test="<%= oAuth2Application != null %>">
		<aui:fieldset label="supported-features">
			<aui:field-wrapper>

				<%
				List<String> oAuth2ApplicationFeaturesList = new ArrayList<>();

				if (oAuth2Application != null) {
					oAuth2ApplicationFeaturesList = oAuth2Application.getFeaturesList();
				}

				String[] oAuth2Features = oAuth2AdminPortletDisplayContext.getOAuth2Features(portletPreferences);

				for (String oAuth2Feature : oAuth2Features) {
					boolean checked = false;

					if ((oAuth2Application != null) && oAuth2ApplicationFeaturesList.contains(oAuth2Feature)) {
						checked = true;
					}

					String name = "feature-" + oAuth2Feature;

					checked = ParamUtil.getBoolean(request, name, checked);
				%>

					<div class="supportedFeature">
						<aui:input checked="<%= checked %>" label="<%= HtmlUtil.escape(oAuth2Feature) %>" name="<%= name %>" type="checkbox" />
					</div>

				<%
				}
				%>

			</aui:field-wrapper>
		</aui:fieldset>
	</c:if>
</aui:fieldset>