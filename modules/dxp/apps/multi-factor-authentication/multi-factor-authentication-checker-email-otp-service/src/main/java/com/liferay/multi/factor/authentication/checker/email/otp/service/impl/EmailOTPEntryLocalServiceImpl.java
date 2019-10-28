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

package com.liferay.multi.factor.authentication.checker.email.otp.service.impl;

import com.liferay.multi.factor.authentication.checker.email.otp.model.EmailOTPEntry;
import com.liferay.multi.factor.authentication.checker.email.otp.service.base.EmailOTPEntryLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * The implementation of the email otp entry local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the <code>com.liferay.multi.factor.authentication.checker.email.otp.service.EmailOTPEntryLocalService</code> interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author arthurchan35
 * @see EmailOTPEntryLocalServiceBaseImpl
 */
@Component(
	property = "model.class.name=com.liferay.multi.factor.authentication.checker.email.otp.model.EmailOTPEntry",
	service = AopService.class
)
public class EmailOTPEntryLocalServiceImpl
	extends EmailOTPEntryLocalServiceBaseImpl {

	public EmailOTPEntry addEmailOTPEntry(String emailAddress, long userId)
		throws PortalException {

		EmailOTPEntry emailOTPEntry =
			emailOTPEntryLocalService.fetchEmailOTPEntry(userId);

		if (emailOTPEntry != null) {
			throw new IllegalArgumentException(
				"There is already one Email OTP Entry for user " + userId);
		}

		User user = userLocalService.getUserById(userId);

		long entryId = counterLocalService.increment();

		emailOTPEntry = emailOTPEntryPersistence.create(entryId);

		emailOTPEntry.setCompanyId(user.getCompanyId());
		emailOTPEntry.setUserId(userId);
		emailOTPEntry.setUserName(user.getFullName());
		emailOTPEntry.setCreateDate(new Date());

		emailOTPEntryPersistence.update(emailOTPEntry);

		return emailOTPEntry;
	}

	public List<EmailOTPEntry> getEmailOTPEntriesByUserId(
		long userId, int start, int end,
		OrderByComparator<EmailOTPEntry> orderByComparator) {

		return emailOTPEntryPersistence.findByUserId(
			userId, start, end, orderByComparator);
	}

	public boolean updateFailedAttempt(long userId, String userIP) {
		EmailOTPEntry emailOTPEntry =
			emailOTPEntryLocalService.fetchEmailOTPEntry(userId);

		if (emailOTPEntry == null) {
			return false;
		}

		emailOTPEntry.setFailedAttempts(emailOTPEntry.getFailedAttempts() + 1);
		emailOTPEntry.setLastFailDate(new Date());
		emailOTPEntry.setLastFailIP(userIP);

		emailOTPEntryPersistence.update(emailOTPEntry);

		return true;
	}

	public boolean updateSuccessAttempt(long userId, String userIP) {
		EmailOTPEntry emailOTPEntry =
			emailOTPEntryLocalService.fetchEmailOTPEntry(userId);

		if (emailOTPEntry == null) {
			return false;
		}

		emailOTPEntry.setFailedAttempts(0);
		emailOTPEntry.setLastSuccessDate(new Date());
		emailOTPEntry.setLastSuccessIP(userIP);

		emailOTPEntryPersistence.update(emailOTPEntry);

		return true;
	}

}