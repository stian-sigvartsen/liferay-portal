package com.liferay.oauth2.provider.scopes.spi;

import java.util.Collections;
import java.util.Set;

public interface ScopeMapper {

	public Set<String> map(String scope);

	public static ScopeMapper NULL = Collections::singleton;

}
