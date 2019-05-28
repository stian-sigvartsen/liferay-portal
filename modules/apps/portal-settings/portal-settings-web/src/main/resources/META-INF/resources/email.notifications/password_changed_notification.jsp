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

<%
String adminEmailFromName = PrefsPropsUtil.getString(company.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_NAME);
String adminEmailFromAddress = PrefsPropsUtil.getString(company.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);

PortletPreferences companyPortletPreferences = PrefsPropsUtil.getPreferences(company.getCompanyId(), true);

String sectionName = "password-changed-notification";
%>

<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />

<liferay-ui:error key="emailPasswordChangedSubject" message="please-enter-a-valid-subject" />
<liferay-ui:error key="emailPasswordChangedBody" message="please-enter-a-valid-body" />

<liferay-frontend:email-notification-settings
	emailBody='<%= LocalizationUtil.getLocalizationXmlFromPreferences(companyPortletPreferences, renderRequest, "adminEmailPasswordChangedBody", "settings", ContentUtil.get(PortalClassLoaderUtil.getClassLoader(), PropsValues.ADMIN_EMAIL_PASSWORD_CHANGED_BODY)) %>'
	emailParam="adminEmailPasswordChanged"
	emailSubject='<%= LocalizationUtil.getLocalizationXmlFromPreferences(companyPortletPreferences, renderRequest, "adminEmailPasswordChangedSubject", "settings", ContentUtil.get(PortalClassLoaderUtil.getClassLoader(), PropsValues.ADMIN_EMAIL_PASSWORD_CHANGED_SUBJECT)) %>'
	fieldPrefix="settings"
	showEmailEnabled="<%= false %>"
/>

<aui:fieldset cssClass="definition-of-terms email-verification terms" label="definition-of-terms">
	<%@ include file="/email.notifications/definition_of_terms.jspf" %>
</aui:fieldset>

<%
String emailPasswordSentSubject = LocalizationUtil.getLocalizationXmlFromPreferences(portletPreferences, renderRequest, "emailPasswordSentSubject", "preferences", null);
%>

<c:if test="<%= Validator.isNotNull(emailPasswordSentSubject) %>">
	<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="legacy-template-no-longer-used" markupView="lexicon">
		<aui:input checked="<%= false %>" label="discard" name="discardLegacyKey" type="checkbox" value="emailPasswordSentSubject,emailPasswordSentBody" />

		<div class="alert alert-info">
			<liferay-ui:message key="sending-of-passwords-by-email-is-no-longer-supported-the-template-below-is-not-used-and-can-be-discarded" />
		</div>

		<aui:field-wrapper label="subject">
			<liferay-ui:input-localized
				fieldPrefix="settings"
				fieldPrefixSeparator="--"
				name="emailPasswordSentSubject"
				readonly="<%= true %>"
				xml="<%= emailPasswordSentSubject %>"
			/>
		</aui:field-wrapper>

		<aui:field-wrapper label="body">
			<liferay-ui:input-localized
				fieldPrefix="settings"
				fieldPrefixSeparator="--"
				name="emailPasswordSentBody"
				readonly="<%= true %>"
				type="textarea"
				xml='<%= LocalizationUtil.getLocalizationXmlFromPreferences(portletPreferences, renderRequest, "emailPasswordSentBody", "preferences", null) %>'
			/>
		</aui:field-wrapper>
	</aui:fieldset>
</c:if>