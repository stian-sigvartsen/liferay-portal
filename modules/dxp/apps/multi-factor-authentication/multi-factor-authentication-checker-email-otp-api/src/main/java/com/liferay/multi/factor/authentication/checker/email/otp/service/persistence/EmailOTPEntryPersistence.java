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

package com.liferay.multi.factor.authentication.checker.email.otp.service.persistence;

import com.liferay.multi.factor.authentication.checker.email.otp.exception.NoSuchEmailOTPEntryException;
import com.liferay.multi.factor.authentication.checker.email.otp.model.EmailOTPEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the email otp entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see EmailOTPEntryUtil
 * @generated
 */
@ProviderType
public interface EmailOTPEntryPersistence
	extends BasePersistence<EmailOTPEntry> {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link EmailOTPEntryUtil} to access the email otp entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the email otp entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching email otp entries
	 */
	public java.util.List<EmailOTPEntry> findByUserId(long userId);

	/**
	 * Returns a range of all the email otp entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>EmailOTPEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of email otp entries
	 * @param end the upper bound of the range of email otp entries (not inclusive)
	 * @return the range of matching email otp entries
	 */
	public java.util.List<EmailOTPEntry> findByUserId(
		long userId, int start, int end);

	/**
	 * Returns an ordered range of all the email otp entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>EmailOTPEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of email otp entries
	 * @param end the upper bound of the range of email otp entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching email otp entries
	 */
	public java.util.List<EmailOTPEntry> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EmailOTPEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the email otp entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>EmailOTPEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of email otp entries
	 * @param end the upper bound of the range of email otp entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching email otp entries
	 */
	public java.util.List<EmailOTPEntry> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EmailOTPEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first email otp entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching email otp entry
	 * @throws NoSuchEmailOTPEntryException if a matching email otp entry could not be found
	 */
	public EmailOTPEntry findByUserId_First(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator<EmailOTPEntry>
				orderByComparator)
		throws NoSuchEmailOTPEntryException;

	/**
	 * Returns the first email otp entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching email otp entry, or <code>null</code> if a matching email otp entry could not be found
	 */
	public EmailOTPEntry fetchByUserId_First(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator<EmailOTPEntry>
			orderByComparator);

	/**
	 * Returns the last email otp entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching email otp entry
	 * @throws NoSuchEmailOTPEntryException if a matching email otp entry could not be found
	 */
	public EmailOTPEntry findByUserId_Last(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator<EmailOTPEntry>
				orderByComparator)
		throws NoSuchEmailOTPEntryException;

	/**
	 * Returns the last email otp entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching email otp entry, or <code>null</code> if a matching email otp entry could not be found
	 */
	public EmailOTPEntry fetchByUserId_Last(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator<EmailOTPEntry>
			orderByComparator);

	/**
	 * Returns the email otp entries before and after the current email otp entry in the ordered set where userId = &#63;.
	 *
	 * @param entryId the primary key of the current email otp entry
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next email otp entry
	 * @throws NoSuchEmailOTPEntryException if a email otp entry with the primary key could not be found
	 */
	public EmailOTPEntry[] findByUserId_PrevAndNext(
			long entryId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<EmailOTPEntry>
				orderByComparator)
		throws NoSuchEmailOTPEntryException;

	/**
	 * Removes all the email otp entries where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public void removeByUserId(long userId);

	/**
	 * Returns the number of email otp entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching email otp entries
	 */
	public int countByUserId(long userId);

	/**
	 * Caches the email otp entry in the entity cache if it is enabled.
	 *
	 * @param emailOTPEntry the email otp entry
	 */
	public void cacheResult(EmailOTPEntry emailOTPEntry);

	/**
	 * Caches the email otp entries in the entity cache if it is enabled.
	 *
	 * @param emailOTPEntries the email otp entries
	 */
	public void cacheResult(java.util.List<EmailOTPEntry> emailOTPEntries);

	/**
	 * Creates a new email otp entry with the primary key. Does not add the email otp entry to the database.
	 *
	 * @param entryId the primary key for the new email otp entry
	 * @return the new email otp entry
	 */
	public EmailOTPEntry create(long entryId);

	/**
	 * Removes the email otp entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the email otp entry
	 * @return the email otp entry that was removed
	 * @throws NoSuchEmailOTPEntryException if a email otp entry with the primary key could not be found
	 */
	public EmailOTPEntry remove(long entryId)
		throws NoSuchEmailOTPEntryException;

	public EmailOTPEntry updateImpl(EmailOTPEntry emailOTPEntry);

	/**
	 * Returns the email otp entry with the primary key or throws a <code>NoSuchEmailOTPEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the email otp entry
	 * @return the email otp entry
	 * @throws NoSuchEmailOTPEntryException if a email otp entry with the primary key could not be found
	 */
	public EmailOTPEntry findByPrimaryKey(long entryId)
		throws NoSuchEmailOTPEntryException;

	/**
	 * Returns the email otp entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the email otp entry
	 * @return the email otp entry, or <code>null</code> if a email otp entry with the primary key could not be found
	 */
	public EmailOTPEntry fetchByPrimaryKey(long entryId);

	/**
	 * Returns all the email otp entries.
	 *
	 * @return the email otp entries
	 */
	public java.util.List<EmailOTPEntry> findAll();

	/**
	 * Returns a range of all the email otp entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>EmailOTPEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of email otp entries
	 * @param end the upper bound of the range of email otp entries (not inclusive)
	 * @return the range of email otp entries
	 */
	public java.util.List<EmailOTPEntry> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the email otp entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>EmailOTPEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of email otp entries
	 * @param end the upper bound of the range of email otp entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of email otp entries
	 */
	public java.util.List<EmailOTPEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EmailOTPEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the email otp entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>EmailOTPEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of email otp entries
	 * @param end the upper bound of the range of email otp entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of email otp entries
	 */
	public java.util.List<EmailOTPEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EmailOTPEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the email otp entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of email otp entries.
	 *
	 * @return the number of email otp entries
	 */
	public int countAll();

}