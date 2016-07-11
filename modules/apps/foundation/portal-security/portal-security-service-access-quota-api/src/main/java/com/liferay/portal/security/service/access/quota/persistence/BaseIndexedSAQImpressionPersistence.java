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

package com.liferay.portal.security.service.access.quota.persistence;

import com.liferay.portal.security.service.access.quota.metric.SAQContextMatcher;
import com.liferay.portal.security.service.access.quota.metric.SAQMetricMatcher;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Stian Sigvartsen
 */
public abstract class BaseIndexedSAQImpressionPersistence
	implements SAQImpressionPersistence {

	public abstract Iterator<SAQImpression> findImpressionsMatchingMetric(
		long companyId, String metric, SAQMetricMatcher metricMatcher);

	public abstract Iterator<SAQImpression> findAllImpressions(long companyId);

	public abstract int getImpressionsCount(
		long companyId, long expiryIntervalMillis);

	public abstract Iterator<String> findAllMetricValues(
		long companyId, String metricName);

	public Iterator<SAQImpression> findImpressions(
		long companyId, SAQContextMatcher contextMetricsMatcher) {

		return new SAQImpressionsIterator(companyId, contextMetricsMatcher);
	}

	private class SAQImpressionsIterator
		implements Iterator<SAQImpression>, SAQMetricMatcher {

		public SAQImpressionsIterator(
			long companyId, SAQContextMatcher contextMetricsMatcher) {

			_companyId = companyId;
			_contextMetricsMatcher = contextMetricsMatcher;

			_currentMetric = null;
			_impressionKeys = new HashSet<>();
			_impressionsMatchingMetricIterator = null;
			_metricsIterator = contextMetricsMatcher.getMetrics().iterator();
			_next = null;
		}

		@Override
		public boolean hasNext() {
			if (_next != null) {
				return true;
			}

			while (true) {
				if ((_impressionsMatchingMetricIterator != null) &&
					_prepareUniqueNextMatchingMetricImpression(
						_impressionsMatchingMetricIterator)) {

					return true;
				}
				else {
					if (!_metricsIterator.hasNext()) {
						return false;
					}

					_currentMetric = _metricsIterator.next();

					_impressionsMatchingMetricIterator =
						findImpressionsMatchingMetric(
							_companyId, _currentMetric, this);
				}
			}
		}

		@Override
		public boolean matches(String metricValue) {
			return _contextMetricsMatcher.matches(_currentMetric, metricValue);
		}

		@Override
		public SAQImpression next() {
			if (hasNext()) {
				SAQImpression next = _next;

				_next = null;

				return next;
			}
			else {
				throw new NoSuchElementException();
			}
		}

		@Override
		public void remove() {
			throw new RuntimeException("Not implemented");
		}

		private boolean _prepareUniqueNext(
			Iterator<SAQImpression> impressionsIterator) {

			if (_next != null) {
				return true;
			}

			while (impressionsIterator.hasNext()) {
				SAQImpression next = impressionsIterator.next();

				if (_impressionKeys.add(next.getKey())) {
					_next = next;

					return true;
				}
			}

			return false;
		}

		private boolean _prepareUniqueNextMatchingMetricImpression(
			Iterator<SAQImpression> impressionsIterator) {

			if (_prepareUniqueNext(impressionsIterator)) {
				return true;
			}
			else {
				_impressionsMatchingMetricIterator = null;

				return false;
			}
		}

		private final long _companyId;
		private final SAQContextMatcher _contextMetricsMatcher;
		private String _currentMetric;
		private final Set<String> _impressionKeys;
		private Iterator<SAQImpression> _impressionsMatchingMetricIterator;
		private final Iterator<String> _metricsIterator;
		private SAQImpression _next;

	}

}