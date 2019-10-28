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

package com.liferay.multi.factor.authentication.checker.email.otp.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(
	category = "mfa", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.multi.factor.authentication.checker.email.otp.web.internal.configuration.EmailOTPConfiguration",
	localization = "content/Language", name = "email-otp-configuration-name"
)
public interface EmailOTPConfiguration {

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		deflt = "30", description = "resend-email-timeout-description",
		name = "resend-email-timeout", required = false
	)
	public long resendEmailTimeout();

	@Meta.AD(
		deflt = "-1", description = "validation-expiration-time-description",
		name = "validation-expiration-time", required = false
	)
	public long validationExpirationTime();

	@Meta.AD(
		deflt = "noreply@liferay.com",
		description = "email-template-from-description",
		name = "email-template-from", required = false
	)
	public String emailTemplateFrom();

	@Meta.AD(
		deflt = "Test Test",
		description = "email-template-from-name-description",
		name = "email-template-from-name", required = false
	)
	public String emailTemplateFromName();

	@Meta.AD(
		deflt = "${resource:com/liferay/multi/factor/authentication/checker/email/otp/web/internal/configuration/email_subject.tmpl}",
		description = "email-template-subject-description",
		name = "email-template-subject", required = false
	)
	public String emailTemplateSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/multi/factor/authentication/checker/email/otp/web/internal/configuration/email_body.tmpl}",
		description = "email-template-body-description",
		name = "email-template-body", required = false
	)
	public String emailTemplateBody();

	@Meta.AD(
		deflt = "6", description = "otp-size-description", name = "otp-size",
		required = false
	)
	public int otpSize();

}