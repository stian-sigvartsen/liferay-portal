<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */
--%>

<%@ include file="/init.jsp" %>

<portlet:actionURL name="/login/verify_mfa" var="verifyMFAActionURL">
	<portlet:param name="mvcRenderCommandName" value="/login/verify_mfa" />
</portlet:actionURL>

<aui:form action="<%= verifyMFAActionURL %>" cssClass="container-fluid-1280 sign-in-form" method="post" name="fm">
	<aui:input name="encryptedParameterMapJSON" type="hidden" value='<%= ParamUtil.getString(renderRequest, "encryptedParameterMapJSON") %>' />
	<aui:input name="redirect" type="hidden" value='<%= ParamUtil.getString(renderRequest, "redirect") %>' />

	<liferay-ui:error key="mfaFailed" message="multi-factor-authentication-failed" />

	<liferay-util:dynamic-include key="com.liferay.multi.factor.authentication.integration.login.web#/verify_mfa.jsp" />

	<aui:button-row>
		<aui:button type="submit" value="submit" />
	</aui:button-row>
</aui:form>