package com.liferay.oauth2.provider.scopes.liferay.api;

import com.liferay.portal.kernel.model.Company;
import org.osgi.framework.Bundle;

import java.util.Collection;

public interface ScopeContext {

	void setCompany(Company company);

	void setBundle(Bundle bundle);

	void setApplicationName(String applicationName);

	void clear();
}