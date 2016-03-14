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

package com.liferay.flags.service.impl;

import com.liferay.flags.messaging.FlagsRequest;
import com.liferay.flags.service.base.FlagsEntryServiceBaseImpl;
import com.liferay.portal.kernel.exception.EmailAddressException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RateLimitExceededException;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.spring.extender.service.ServiceReference;

import java.util.Date;

/**
 * @author Julio Camarero
 */
public class FlagsEntryServiceImpl extends FlagsEntryServiceBaseImpl {

	@Override
	public void addEntry(
			String className, long classPK, String reporterEmailAddress,
			long reportedUserId, String contentTitle, String contentURL,
			String reason, ServiceContext serviceContext)
		throws PortalException {

		if (!Validator.isEmailAddress(reporterEmailAddress)) {
			throw new EmailAddressException();
		}

		checkRateLimiting(className, classPK, serviceContext);

		FlagsRequest flagsRequest = new FlagsRequest(
			className, classPK, reporterEmailAddress, reportedUserId,
			contentTitle, contentURL, reason, serviceContext);

		MessageBusUtil.sendMessage(DestinationNames.FLAGS, flagsRequest);
	}

	protected void checkRateLimiting(
			String className, long classPK, ServiceContext serviceContext)
		throws PortalException {

		long companyId = serviceContext.getCompanyId();

		Ticket throttleTicket = _ticketLocalService.fetchTicket(
			className, classPK, TicketConstants.TYPE_FLAG);

		if (Validator.isNotNull(throttleTicket) &&
			!throttleTicket.isExpired()) {

			throw new RateLimitExceededException(
				"{className=\"" + className + "\"," + classPK + "," +
					TicketConstants.TYPE_FLAG + "}");
		}
		else {
			Date expirationDate = new Date(
				System.currentTimeMillis() + getFlagRateLimit(companyId));

			throttleTicket = _ticketLocalService.addDistinctTicket(
				companyId, className, classPK, TicketConstants.TYPE_FLAG, null,
				expirationDate, serviceContext);
		}
	}

	private long getFlagRateLimit(long companyId) {
		return 60000;
	}

	@ServiceReference(type = TicketLocalService.class)
	private transient TicketLocalService _ticketLocalService;

}