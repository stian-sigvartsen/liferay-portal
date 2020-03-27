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

package com.liferay.portal.language.extender.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.ListIterator;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Stian Sigvartsen
 */
@RunWith(Arquillian.class)
public class LanguageExtenderTest {

	@Before
	public void setUp() throws Exception {
		_bundleActivator = new LanguageExtenderTestPreparatorBundleActivator();

		_bundle = FrameworkUtil.getBundle(LanguageExtenderTest.class);

		_bundleContext = _bundle.getBundleContext();

		_bundleActivator.start(_bundleContext);
	}

	@After
	public void tearDown() throws Exception {
		_bundleActivator.stop(_bundleContext);
	}

	@Test
	public void test() {
		String servletContextName = "language-test";

		ResourceBundleLoader resourceBundleLoader =
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByServletContextName(servletContextName);

		ResourceBundle resourceBundle = resourceBundleLoader.loadResourceBundle(
			new Locale("en", "US"));

		Assert.assertEquals(
			"1", ResourceBundleUtil.getString(resourceBundle, "test.key1"));
		Assert.assertEquals(
			"2", ResourceBundleUtil.getString(resourceBundle, "test.key2"));
		Assert.assertEquals(
			"3", ResourceBundleUtil.getString(resourceBundle, "test.key3"));

		Assert.assertEquals(
			"1",
			ResourceBundleUtil.getString(resourceBundle, "test.collision1"));
		Assert.assertEquals(
			"3",
			ResourceBundleUtil.getString(resourceBundle, "test.collision2"));
	}

	public static class LanguageExtenderTestPreparatorBundleActivator
		implements BundleActivator {

		@Override
		public void start(BundleContext bundleContext) throws Exception {
			_bundleContext = bundleContext;
			autoCloseables = new ArrayList<>();

			try {
				prepareTest();
			}
			catch (Exception exception) {
				_cleanUp();

				throw new RuntimeException(exception);
			}
		}

		@Override
		public void stop(BundleContext bundleContext) throws Exception {
			_cleanUp();
		}

		protected void prepareTest() throws Exception {
			_registerResourceBundleLoader(
				"language.test.symbolic.name.1",
				createResourceBundle("test.key1", "1", "test.collision1", "1"));

			_registerResourceBundleLoader(
				"language.test.symbolic.name.2",
				createResourceBundle("test.key2", "2", "test.collision2", "2"));

			_registerResourceBundleLoader(
				"language.test.symbolic.name.3",
				createResourceBundle(
					"test.key3", "3", "test.collision1", "3", "test.collision2",
					"3"));
		}

		protected ArrayList<AutoCloseable> autoCloseables;

		private void _cleanUp() {
			ListIterator<AutoCloseable> listIterator =
				autoCloseables.listIterator(autoCloseables.size());

			while (listIterator.hasPrevious()) {
				AutoCloseable previousAutoCloseable = listIterator.previous();

				try {
					previousAutoCloseable.close();
				}
				catch (Exception exception) {
					_log.error(exception, exception);
				}
			}
		}

		private void _registerResourceBundleLoader(
			String bundleSymbolicName, ResourceBundle resourceBundle) {

			Dictionary<String, Object> properties;
			properties = new HashMapDictionary<>();

			properties.put("service.ranking", Integer.MIN_VALUE);

			properties.put("bundle.symbolic.name", bundleSymbolicName);
			properties.put("resource.bundle.base.name", "content.Language");

			ServiceRegistration<ResourceBundleLoader> serviceRegistration =
				_bundleContext.registerService(
					ResourceBundleLoader.class, locale -> resourceBundle,
					properties);

			autoCloseables.add(serviceRegistration::unregister);
		}

		private static final Log _log = LogFactoryUtil.getLog(
			LanguageExtenderTestPreparatorBundleActivator.class);

		private BundleContext _bundleContext;

	}

	protected static ResourceBundle createResourceBundle(
		final String... keysAndValues) {

		if ((keysAndValues.length % 2) != 0) {
			throw new RuntimeException(
				"Keys and values length is not an even number");
		}

		return new ListResourceBundle() {

			@Override
			protected Object[][] getContents() {
				Object[][] contents = new Object[keysAndValues.length / 2][];

				for (int i = 0; i < contents.length; i++) {
					contents[i] = new Object[] {
						keysAndValues[i * 2], keysAndValues[i * 2 + 1]
					};
				}

				return contents;
			}

		};
	}

	private Bundle _bundle;
	private BundleActivator _bundleActivator;
	private BundleContext _bundleContext;

}