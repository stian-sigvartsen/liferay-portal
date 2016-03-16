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

<style type="text/css">
	.portlet-flags .form fieldset {
		border-width: 0;
		padding: 0;
		width: 100%;
	}
</style>

<div class="portlet-flags" id="<portlet:namespace />flagsPopup">

	<p><strong><liferay-ui:message key="thank-you-for-your-report" /></strong></p>

	<p>
		<liferay-ui:message arguments="<%= HtmlUtil.escape(company.getName()) %>" key="although-we-cannot-disclose-our-final-decision,-we-do-review-every-report-and-appreciate-your-effort-to-make-sure-x-is-a-safe-environment-for-everyone" translateArguments="<%= false %>" />
	</p>
</div>