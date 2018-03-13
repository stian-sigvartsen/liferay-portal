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

package com.liferay.oauth2.provider.jsonws;

import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true, property = "sap.entry.oauth2.prefix=OAUTH2_",
	service = SAPEntryScopeRegistry.class
)
public class SAPEntryScopeRegistry {

	@Activate
	public void activate(Map<String, Object> properties) {
		_sapEntryOAuth2Prefix = MapUtil.getString(
			properties, "sap.entry.oauth2.prefix", _sapEntryOAuth2Prefix);
	}

	public List<SAPEntryScope> getSAPEntryScopes(long companyId) {
		return Collections.unmodifiableList(
			_sapEntryCompanyScopes.get(companyId));
	}

	public void registerSAPEntry(SAPEntry sapEntry){
		if (!StringUtil.startsWith(sapEntry.getName(), _sapEntryOAuth2Prefix)) {
			return;
		}

		SAPEntryScope sapEntryScope = new SAPEntryScope(
			sapEntry.getSapEntryId(), sapEntry.getName(),
			getScopeName(sapEntry.getName()),
			sapEntry.getTitle());

		List<SAPEntryScope> sapEntryScopes =
			_sapEntryCompanyScopes.computeIfAbsent(
				sapEntry.getCompanyId(), __ -> new CopyOnWriteArrayList<>());

		sapEntryScopes.add(sapEntryScope);
	}

	public void unregisterSAPEntry(SAPEntry sapEntry){
		List<SAPEntryScope> sapEntryScopes = _sapEntryCompanyScopes.get(
			sapEntry.getCompanyId());

		if (sapEntryScopes == null) {
			throw new IllegalArgumentException(
				"No entries registered in company " + sapEntry.getCompanyId());
		}

		long sapEntryId = sapEntry.getSapEntryId();

		for (Iterator<SAPEntryScope> it = sapEntryScopes.iterator();
			 it.hasNext();) {

			SAPEntryScope sapEntryScope = it.next();

			if (sapEntryScope.getSapEntryId() == sapEntryId) {
				it.remove();

				return;
			}
		}
	}

	public void updateSAPEntry(SAPEntry sapEntry){
		if (!StringUtil.startsWith(sapEntry.getName(), _sapEntryOAuth2Prefix)) {
			unregisterSAPEntry(sapEntry);

			return;
		}

		List<SAPEntryScope> sapEntryScopes = _sapEntryCompanyScopes.get(
			sapEntry.getCompanyId());

		if (sapEntryScopes == null) {
			throw new IllegalArgumentException(
				"No entries registered in company " + sapEntry.getCompanyId());
		}

		long sapEntryId = sapEntry.getSapEntryId();

		for (SAPEntryScope sapEntryScope : sapEntryScopes) {
			if (sapEntryScope.getSapEntryId() == sapEntryId) {
				sapEntryScope.setSapEntryName(sapEntry.getName());
				sapEntryScope.setScopeName(getScopeName(sapEntry.getName()));
				sapEntryScope.setTitle(sapEntry.getTitle());
			}
		}
	}

	protected String getScopeName(String sapEntryName) {
		return sapEntryName.substring(_sapEntryOAuth2Prefix.length());
	}

	private String _sapEntryOAuth2Prefix = "OAUTH2_";

	private Map<Long, List<SAPEntryScope>> _sapEntryCompanyScopes =
		new ConcurrentHashMap<>();

}
