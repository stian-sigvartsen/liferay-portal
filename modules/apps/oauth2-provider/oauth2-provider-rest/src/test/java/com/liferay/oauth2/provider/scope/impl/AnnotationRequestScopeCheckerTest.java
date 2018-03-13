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

package com.liferay.oauth2.provider.scope.impl;

import com.liferay.oauth2.provider.rest.requestscopechecker.AnnotationRequestScopeChecker;

import com.liferay.oauth2.provider.scope.RequiresScope;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Request;

@RunWith(PowerMockRunner.class)
public class AnnotationRequestScopeCheckerTest extends PowerMockito {
	
	@Before
	public void setUp() throws Exception {
		annotationRequestScopeChecker = new AnnotationRequestScopeChecker();		
		request = Mockito.mock(Request.class);
	}
	
	@Test
	public void testIsAllowed() throws NoSuchMethodException {
		TestScopeChecker testScopeChecker = new TestScopeChecker("READ");
		
		ResourceInfo resourceInfo = Mockito.mock(ResourceInfo.class);
		
		when(
			resourceInfo.getResourceMethod()
		).thenReturn(
			TestEndpointSample.class.getMethod("hello", new Class<?>[] {})
		);
		
		assertTrue(
			annotationRequestScopeChecker.isAllowed(
				testScopeChecker, request, resourceInfo));
	}

	@Test
	public void testMethodAllowed() throws NoSuchMethodException {
		TestScopeChecker testScopeChecker = new TestScopeChecker("WRITE");

		ResourceInfo resourceInfo = Mockito.mock(ResourceInfo.class);
		
		when(
			resourceInfo.getResourceMethod()
		).thenReturn(
			TestEndpointSample.class.getMethod("modify", new Class<?>[] {})
		);
		
		assertTrue(
			annotationRequestScopeChecker.isAllowed(
				testScopeChecker, request, resourceInfo));
	}

	@Test
	public void testMethodAllowedWithMultipleAllRequired()
		throws NoSuchMethodException {

		TestScopeChecker testScopeChecker = new TestScopeChecker(
			"READ", "WRITE");

		ResourceInfo resourceInfo = Mockito.mock(ResourceInfo.class);
		
		when(
			resourceInfo.getResourceMethod()
		).thenReturn(
			TestEndpointSample.class.getMethod("requiresAll", new Class<?>[] {})
		);
		
		assertTrue(
			annotationRequestScopeChecker.isAllowed(
				testScopeChecker, request, resourceInfo));
	}

	@Test
	public void testMethodAllowedWithMultipleSomeRequired()
		throws NoSuchMethodException {

		TestScopeChecker testScopeChecker = new TestScopeChecker("READ");

		ResourceInfo resourceInfo = Mockito.mock(ResourceInfo.class);
		
		when(
			resourceInfo.getResourceMethod()
		).thenReturn(
			TestEndpointSample.class.getMethod("requiresAny", new Class<?>[] {})
		);
		
		assertTrue(
			annotationRequestScopeChecker.isAllowed(
				testScopeChecker, request, resourceInfo));

		testScopeChecker = new TestScopeChecker("WRITE");

		assertTrue(
			annotationRequestScopeChecker.isAllowed(
				testScopeChecker, request, resourceInfo));

		testScopeChecker = new TestScopeChecker("RANDOM");

		assertFalse(
			annotationRequestScopeChecker.isAllowed(
				testScopeChecker, request, resourceInfo));
	}

	@Test
	public void testMethodAllowedWithMultipleAllRequiredAndNotGranted()
		throws NoSuchMethodException {

		TestScopeChecker testScopeChecker = new TestScopeChecker(
			"READ", "WRITE");

		ResourceInfo resourceInfo = Mockito.mock(ResourceInfo.class);
		
		when(
			resourceInfo.getResourceMethod()
		).thenReturn(
			TestEndpointSample.class.getMethod("requiresTooMany", new Class<?>[] {})
		);
		
		assertFalse(
			annotationRequestScopeChecker.isAllowed(
				testScopeChecker, request, resourceInfo));
	}

	private static class TestEndpointSample {

		@RequiresScope("READ")
		public String hello() {
			return "hello";
		}

		@RequiresScope("WRITE")
		public void modify() {

		}

		@RequiresScope({"READ", "WRITE"})
		public void requiresAll() {

		}

		@RequiresScope({"READ", "WRITE", "NOTGRANTED"})
		public void requiresTooMany() {

		}

		@RequiresScope(
			value = {"READ", "WRITE"},
			allNeeded = false)
		public void requiresAny() {

		}
	}

	protected AnnotationRequestScopeChecker annotationRequestScopeChecker;
	protected Request request;
}