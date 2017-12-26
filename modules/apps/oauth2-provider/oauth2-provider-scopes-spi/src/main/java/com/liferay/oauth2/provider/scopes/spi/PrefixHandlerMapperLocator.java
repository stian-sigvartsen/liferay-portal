package com.liferay.oauth2.provider.scopes.spi;

import com.liferay.portal.kernel.model.Company;

public interface PrefixHandlerMapperLocator {

	public PrefixHandlerMapper locateMapper(Company company);

}