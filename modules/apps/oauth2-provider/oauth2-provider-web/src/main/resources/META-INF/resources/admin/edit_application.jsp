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
String redirect = ParamUtil.getString(request, "redirect");

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

long oAuth2ApplicationId = ParamUtil.getLong(request, "oAuth2ApplicationId", -1);

OAuth2Application oAuth2Application = null;

if (oAuth2ApplicationId > -1) {
		oAuth2Application = OAuth2ApplicationServiceUtil.getOAuth2Application(oAuth2ApplicationId);
}

String headerTitle = (oAuth2Application == null) ? LanguageUtil.get(request, "add-o-auth2-application") : LanguageUtil.format(request, "edit-x", oAuth2Application.getName(), false);

renderResponse.setTitle(headerTitle);
%>

<div class="container-fluid-1280">
	<portlet:actionURL name='<%= oAuth2Application == null ? "updateOAuth2Application" : "updateOAuth2Application" %>' var="editOAuth2ApplicationURL" />

	<aui:form action="<%= editOAuth2ApplicationURL %>" name="fm">
		<liferay-ui:error exception="<%= ApplicationNameException.class %>" message="missing-application-name" focusField="name"/>
		<liferay-ui:error exception="<%= DuplicateOAuth2ClientIdException.class %>" message="client-id-already-exists" focusField="clientId"/>
		<liferay-ui:error exception="<%= EmptyClientSecretException.class %>" message="client-secret-is-missing" focusField="clientSecret"/>
		<liferay-ui:error exception="<%= InvalidHomePageURLException.class %>" message="home-page-url-is-invalid" focusField="homePageURL"/>
		<liferay-ui:error exception="<%= InvalidHomePageURLSchemeException.class %>" message="home-page-url-scheme-is-invalid" focusField="homePageURL"/>
		<liferay-ui:error exception="<%= InvalidPrivacyPolicyURLException.class %>" message="privacy-policy-url-is-invalid" focusField="privacyPolicyURL">
			<liferay-ui:message key="redirect-uri-is-missing-for-grant-type-x" arguments="<%= HtmlUtil.escape(((MissingRedirectURIException)errorException).getMessage()) %>" />
		</liferay-ui:error>
		<liferay-ui:error exception="<%= InvalidPrivacyPolicyURLSchemeException.class %>" focusField="privacyPolicyURL">
			<liferay-ui:message key="privacy-policy-url-scheme-is-invalid" arguments="<%= HtmlUtil.escape(((MissingRedirectURIException)errorException).getMessage()) %>" />
		</liferay-ui:error>
		<liferay-ui:error exception="<%= InvalidRedirectURIException.class %>" focusField="redirectURIs">
			<liferay-ui:message key="redirect-uri-x-is-invalid" arguments="<%= HtmlUtil.escape(((InvalidRedirectURIException)errorException).getMessage()) %>" />
		</liferay-ui:error>
		<liferay-ui:error exception="<%= InvalidRedirectURIFragmentException.class %>" focusField="redirectURIs">
			<liferay-ui:message key="redirect-uri-x-fragment-is-invalid" arguments="<%= HtmlUtil.escape(((InvalidRedirectURIFragmentException)errorException).getMessage()) %>" />
		</liferay-ui:error>
		<liferay-ui:error exception="<%= InvalidRedirectURIPathException.class %>" focusField="redirectURIs">
			<liferay-ui:message key="redirect-uri-x-path-is-invalid" arguments="<%= HtmlUtil.escape(((InvalidRedirectURIPathException)errorException).getMessage()) %>" />
		</liferay-ui:error>
		<liferay-ui:error exception="<%= InvalidRedirectURISchemeException.class %>" focusField="redirectURIs">
			<liferay-ui:message key="redirect-uri-x-scheme-is-invalid" arguments="<%= HtmlUtil.escape(((InvalidRedirectURISchemeException)errorException).getMessage()) %>" />
		</liferay-ui:error>
		<liferay-ui:error exception="<%= MissingRedirectURIException.class %>" focusField="redirectURIs">
			<liferay-ui:message key="redirect-uri-is-missing-for-grant-type-x" arguments="<%= HtmlUtil.escape(((MissingRedirectURIException)errorException).getMessage()) %>" />
		</liferay-ui:error>
		<liferay-ui:error exception="<%= NonEmptyClientSecretException.class %>" message="client-secret-must-be-empty" focusField="clientSecret"/>
		<liferay-ui:error exception="<%= UnsupportedGrantTypeForClientException.class %>" focusField="clientConfidential">
			<liferay-ui:message key="grant-type-x-is-unsupported-for-this-client-type" arguments="<%= HtmlUtil.escape(((UnsupportedGrantTypeForClientException)errorException).getMessage()) %>" />
		</liferay-ui:error>

		<aui:model-context bean="<%= oAuth2Application %>" model="<%= OAuth2Application.class %>" />

		<aui:fieldset-group markupView="lexicon">
			<aui:input type="hidden" name="oAuth2ApplicationId"
				value='<%= oAuth2Application == null ? "" : oAuth2Application.getOAuth2ApplicationId() %>'
			/>

			<aui:fieldset>
				<aui:input name="name" required="true" />
				<aui:field-wrapper>
					<aui:input label="icon" name="icon" type="file" inlineField="true"/>
					<c:if test="<%= oAuth2Application != null && oAuth2Application.getIconFileEntryId() > 0%>">
						<%
							String thumbnailURL = StringPool.BLANK;

							try {
								FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(oAuth2Application.getIconFileEntryId());
								thumbnailURL = DLUtil.getThumbnailSrc(fileEntry, themeDisplay);
							}
							catch (PortalException e) {
								// user has no longer access to the application
							}
						%>
						<img src="<%= thumbnailURL %>" width="64" />
					</c:if>
				</aui:field-wrapper>
				<aui:input name="description" type="textarea" />
				<aui:input name="homePageURL" />
				<aui:input name="privacyPolicyURL" />
				<aui:input name="clientConfidential" type="checkbox" value="<%= oAuth2Application == null ? true : oAuth2Application.getClientConfidential() %>"/>
				<aui:input name="clientId" required="true" />
				<aui:input name="clientSecret" />
				<aui:input label="redirect-uris" name="redirectURIs" helpMessage="redirect-uris-help" />
				<aui:fieldset label="allowed-grant-types">
					<%
						List<GrantType> oAuth2Grants = oAuth2AdminPortletDisplayContext.getOAuth2Grants(portletPreferences);

						for (GrantType grantType : oAuth2Grants) {
							String grantTypeName = grantType.name();
					%>
					<aui:input name="<%= "grant-" + grantTypeName %>" type="checkbox" label="<%= grantTypeName %>" checked="<%= oAuth2Application != null && oAuth2Application.getAllowedGrantTypesList().contains(grantType) %>" />
					<%
						}
					%>
				</aui:fieldset>
				<aui:fieldset label="supported-features">
					<%
						String[] oAuth2Features = StringUtil.split(portletPreferences.getValue("oAuth2Features", StringPool.BLANK));

						for (String oAuth2Feature : oAuth2Features) {
							String escapedOAuth2Feature = HtmlUtil.escapeAttribute(oAuth2Feature);
					%>
					<aui:input name="<%= "feature-" + escapedOAuth2Feature %>" type="checkbox" label="<%= escapedOAuth2Feature %>" checked="<%= oAuth2Application != null && oAuth2Application.getFeaturesList().contains(oAuth2Feature) %>" />
					<%
						}
					%>
				</aui:fieldset>
			</aui:fieldset>
		</aui:fieldset-group>

		<aui:button-row>
			<aui:button cssClass="btn-lg" type="submit" />
			<aui:button cssClass="btn-lg" href="<%= portletDisplay.getURLBack() %>" type="cancel" />
		</aui:button-row>
	</aui:form>
</div>