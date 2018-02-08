/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.scopes.impl;

import static junit.framework.TestCase.assertTrue;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Request;

import com.liferay.oauth2.provider.scopes.impl.requestscopechecker.HttpMethodRequestScopeChecker;
import com.liferay.oauth2.provider.scopes.spi.RequestScopeChecker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class HttpMethodRequestScopeCheckerTest extends PowerMockito {

	@Before
	public void setUp() throws Exception {
		methodAllowedChecker = new HttpMethodRequestScopeChecker();	
		resourceInfo = Mockito.mock(ResourceInfo.class);
	}
	
	@Test
	public void testHttpMethodAllowed() throws NoSuchMethodException {
		TestScopeChecker testScopeChecker = new TestScopeChecker(
			"GET");

		Request request = Mockito.mock(Request.class);
		
		when(
			request.getMethod()
		).thenReturn(
			"GET"
		);
		
		assertTrue(
			methodAllowedChecker.isAllowed(
				testScopeChecker,
				request,
				resourceInfo));
	}

	protected ResourceInfo resourceInfo;
	protected RequestScopeChecker methodAllowedChecker;
}