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

package com.liferay.portal.security.password.encryptor.internal;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptor;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.DigesterImpl;
import com.liferay.portal.util.PropsValues;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Tomas Polesovsky
 */
public class CompositePasswordEncryptorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		DigesterUtil digesterUtil = new DigesterUtil();

		digesterUtil.setDigester(new DigesterImpl());

		ReflectionTestUtil.setFieldValue(
			_compositePasswordEncryptor, "_serviceTrackerMap",
			_mockServiceTrackerMap);

		ReflectionTestUtil.invoke(
			_compositePasswordEncryptor, "activate",
			new Class<?>[] {BundleContext.class}, _bundleContext);

		_bundleContext.registerService(
			PasswordEncryptor.class, _compositePasswordEncryptor,
			MapUtil.singletonDictionary("composite", "true"));

		_bundleContext.registerService(
			PasswordEncryptor.class, new DefaultPasswordEncryptor(),
			MapUtil.singletonDictionary(
				"type", PasswordEncryptor.TYPE_DEFAULT));
		_bundleContext.registerService(
			PasswordEncryptor.class, new BCryptPasswordEncryptor(),
			MapUtil.singletonDictionary("type", PasswordEncryptor.TYPE_BCRYPT));
		_bundleContext.registerService(
			PasswordEncryptor.class, new CryptPasswordEncryptor(),
			MapUtil.singletonDictionary(
				"type", PasswordEncryptor.TYPE_UFC_CRYPT));
		_bundleContext.registerService(
			PasswordEncryptor.class, new NullPasswordEncryptor(),
			MapUtil.singletonDictionary("type", PasswordEncryptor.TYPE_NONE));
		_bundleContext.registerService(
			PasswordEncryptor.class, new PBKDF2PasswordEncryptor(),
			MapUtil.singletonDictionary("type", PasswordEncryptor.TYPE_PBKDF2));
//		 Test PBKDF2 with arguments that match the test case which has the same algorithm.
//		 Both are available during the test, I checked which one is found while debugging the select() method.
//		_bundleContext.registerService(
//			PasswordEncryptor.class, new PBKDF2PasswordEncryptor(),
//			MapUtil.singletonDictionary("type",
//				PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1/128/720000"));
		_bundleContext.registerService(
			PasswordEncryptor.class, new SSHAPasswordEncryptor(),
			MapUtil.singletonDictionary("type", PasswordEncryptor.TYPE_SSHA));

//		 These are moved to their own respective methods to ensure only one is
//		 available during the test. Otherwise the test will pass during the 2nd
//		 encryption when the arguments are removed from the prefix and would be an
//		 exact match with the encryptor without the arguments.
//				bundleContext.registerService(
//					PasswordEncryptor.class, _testBCPBKDF2PasswordEncryptor,
//					MapUtil.singletonDictionary("type", "BCPBKDF2/16/128"));
//				bundleContext.registerService(
//					PasswordEncryptor.class, _testBCPBKDF2PasswordEncryptor1,
//					MapUtil.singletonDictionary("type", "BCPBKDF2"));
	}

	@After
	public final void tearDown() throws Exception {
		_compositePasswordEncryptor.deactivate();
	}

	@Test
	public void testCustomEncryptorWithParametersWhenMustBeAnExactMatch()
		throws Exception {

		String passwordEncryptorType = "BCPBKDF2/16/128";

		TestBCPBKDF2PasswordEncryptor testBCPBKDF2PasswordEncryptor =
			new TestBCPBKDF2PasswordEncryptor(passwordEncryptorType);

		_bundleContext.registerService(
			PasswordEncryptor.class, testBCPBKDF2PasswordEncryptor,
			MapUtil.singletonDictionary("type", passwordEncryptorType));

		_mockServiceTrackerMap.register(
			passwordEncryptorType, testBCPBKDF2PasswordEncryptor);

		String plainPassword = "password";

		String expectedPassword = PasswordEncryptorUtil.encrypt(
			passwordEncryptorType, plainPassword, (String)null);

		testEncrypt(plainPassword, expectedPassword);

		TestBCPBKDF2PasswordEncryptor passwordEncryptor =
			_mockServiceTrackerMap.getService(passwordEncryptorType);

		Assert.assertNotNull(passwordEncryptor);
		Assert.assertEquals(
			passwordEncryptorType, passwordEncryptor._getType());
	}

	@Test
	public void testCustomEncryptorWithParametersWhenNotAnExactMatch()
		throws Exception {

		String passwordEncryptorType = "BCPBKDF2";

		TestBCPBKDF2PasswordEncryptor testBCPBKDF2PasswordEncryptor =
			new TestBCPBKDF2PasswordEncryptor(passwordEncryptorType);

		_bundleContext.registerService(
			PasswordEncryptor.class, testBCPBKDF2PasswordEncryptor,
			MapUtil.singletonDictionary("type", passwordEncryptorType));

		_mockServiceTrackerMap.register(
			passwordEncryptorType, testBCPBKDF2PasswordEncryptor);

		String plainPassword = "password";

		String expectedPassword = PasswordEncryptorUtil.encrypt(
			passwordEncryptorType + "/16/78", plainPassword, (String)null);

		testEncrypt(plainPassword, expectedPassword);

		TestBCPBKDF2PasswordEncryptor passwordEncryptor =
			_mockServiceTrackerMap.getService(passwordEncryptorType);

		Assert.assertNotNull(passwordEncryptor);
		Assert.assertEquals(
			passwordEncryptorType, passwordEncryptor._getType());
	}

	@Test
	public void testEncryptBCrypt() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_BCRYPT, "password",
			"$2a$10$/ST7LsB.7AAHsn/tlK6hr.nudQaBbJhPX9KfRSSzsn.1ij45lVzaK",
			PasswordEncryptor.TYPE_BCRYPT);
	}

	@Test
	public void testEncryptBCryptWith10Rounds() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_BCRYPT + "/10", "password",
			"$2a$10$JX0uYSs6pSrp05TlQxkmz.hkKGK6Av.KkNCzAYOFugO3qxjAiZleO",
			PasswordEncryptor.TYPE_BCRYPT);
	}

	@Test
	public void testEncryptBCryptWith12Rounds() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_BCRYPT + "/12", "password",
			"$2a$12$2dD/NrqCEBlVgFEkkFCbzOll2a9vrdl8tTTqGosm26wJK1eCtsjnO",
			PasswordEncryptor.TYPE_BCRYPT);
	}

	@Test
	public void testEncryptCRYPT() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_UFC_CRYPT, "password", "SNbUMVY9kKQpY",
			PasswordEncryptor.TYPE_UFC_CRYPT);
	}

	@Test
	public void testEncryptFailure() throws Exception {
		testEncryptFailure(
			"Some Nonexistent Algorithm", StringPool.BLANK, StringPool.BLANK);

		testEncryptFailure(null, null, null);

		testEncryptFailure(null, null, StringPool.BLANK);

		testEncryptFailure(null, StringPool.BLANK, null);

		testEncryptFailure(StringPool.BLANK, null, null);

		testEncryptFailure(StringPool.BLANK, null, StringPool.BLANK);

		testEncryptFailure(StringPool.BLANK, StringPool.BLANK, null);

		testEncryptFailure(null, StringPool.BLANK, StringPool.BLANK);

		testEncryptFailure(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK);

		testEncryptFailure(
			PasswordEncryptor.TYPE_SHA, "password",
			"W6ph5Mm5Pz8GgiULbPgzG37mj9g=");
	}

	@Test
	public void testEncryptMD2() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_MD2, "password", "8DiBqIxuORNfDsxg79YJuQ==",
			PasswordEncryptor.TYPE_MD2);
	}

	@Test
	public void testEncryptMD5() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_MD5, "password", "X03MO1qnZdYdgyfeuILPmQ==",
			PasswordEncryptor.TYPE_MD5);
	}

	@Test
	public void testEncryptNONE() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_NONE, "password", "password",
			PasswordEncryptor.TYPE_NONE);
	}

	@Test
	public void testEncryptPBKDF2() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1", "password",
			"AAAAoAAB9ADJZ16OuMAPPHe2CUbP0HPyXvagoKHumh7iHU3c",
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1");
	}

	@Test
	public void testEncryptPBKDF2With50000Rounds() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1/50000", "password",
			"AAAAoAAAw1B+jxO3UiVsWdBk4B9xGd/Ko3GKHW2afYhuit49",
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1");
	}

	@Test
	public void testEncryptPBKDF2With50000RoundsAnd128Key() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1/128/50000",
			"password", "AAAAoAAAw1AbW1e1Str9wSLWIX5X9swLn+j5/5+m6auSPdva",
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1");
	}

	@Test
	public void testEncryptPBKDF2With720000RoundsAnd128Key() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1/128/720000",
			"password", "AAAAoAAB9ADyaBP3fTtsBh8YlRn1CU7VLYR/mnH7ADMNMz2o",
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1");
	}

	@Test
	public void testEncryptSHA() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_SHA, "password",
			"W6ph5Mm5Pz8GgiULbPgzG37mj9g=", PasswordEncryptor.TYPE_SHA);
	}

	@Test
	public void testEncryptSHA1() throws Exception {
		runTests("SHA-1", "password", "W6ph5Mm5Pz8GgiULbPgzG37mj9g=", "SHA-1");
	}

	@Test
	public void testEncryptSHA256() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_SHA_256, "password",
			"XohImNooBHFR0OVvjcYpJ3NgPQ1qq73WKhHvch0VQtg=",
			PasswordEncryptor.TYPE_SHA_256);
	}

	@Test
	public void testEncryptSHA384() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_SHA_384, "password",
			"qLZLq9CsqRpZvbt3YbQh1PK7OCgNOnW6DyHyvrxFWD1EbFmGYMlM5oDEfRnDB4On",
			PasswordEncryptor.TYPE_SHA_384);
	}

	@Test
	public void testEncryptSSHA() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_SSHA, "password",
			"2EWEKeVpSdd79PkTX5vaGXH5uQ028Smy/H1NmA==",
			PasswordEncryptor.TYPE_SSHA);
	}

	@Test
	public void testEncryptUFCCRYPT() throws Exception {
		runTests(
			PasswordEncryptor.TYPE_UFC_CRYPT, "password", "2lrTlR/pWPUOQ",
			PasswordEncryptor.TYPE_UFC_CRYPT);
	}

	protected void close(String key) {
		_mockServiceTrackerMap.close();
	}

	protected void runTests(
			String algorithm, String plainPassword, String encryptedPassword,
			String prependedAlgorithm)
		throws Exception {

		testEncrypt(algorithm);

		testEncrypt(
			plainPassword,
			StringBundler.concat(
				CharPool.OPEN_CURLY_BRACE, prependedAlgorithm,
				CharPool.CLOSE_CURLY_BRACE, encryptedPassword));

		testLegacyEncrypt(algorithm, plainPassword, encryptedPassword);
	}

	protected void testEncrypt(String algorithm) throws Exception {
		String plainPassword = "password";

		String expectedPassword = PasswordEncryptorUtil.encrypt(
			algorithm, plainPassword, (String)null);

		testEncrypt(plainPassword, expectedPassword);
	}

	protected void testEncrypt(String plainPassword, String expectedPassword)
		throws Exception {

		Assert.assertEquals(
			expectedPassword,
			PasswordEncryptorUtil.encrypt(plainPassword, expectedPassword));
	}

	protected void testEncryptFailure(
		String algorithm, String plainTextPassword, String encryptedPassword) {

		try {
			PasswordEncryptorUtil.encrypt(
				algorithm, plainTextPassword, encryptedPassword);

			Assert.fail();
		}
		catch (Exception exception) {
		}
	}

	protected void testLegacyEncrypt(
			String legacyAlgorithm, String plainPassword,
			String expectedPassword)
		throws Exception {

		String originalLegacyAlgorithm =
			PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY;

		try {
			PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY = legacyAlgorithm;

			Assert.assertEquals(
				expectedPassword,
				PasswordEncryptorUtil.encrypt(plainPassword, expectedPassword));
		}
		finally {
			PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY =
				originalLegacyAlgorithm;
		}
	}

	private final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private final CompositePasswordEncryptor _compositePasswordEncryptor =
		new CompositePasswordEncryptor();
	private final MockServiceTrackerMap _mockServiceTrackerMap =
		new MockServiceTrackerMap();

	@Component(
		property = "type=BCKDF2",
		service = PasswordEncryptor.class
	)
	private static class TestBCPBKDF2PasswordEncryptor
		extends CompositePasswordEncryptor implements PasswordEncryptor {

		@Override
		public String encrypt(
			String algorithm, String plainTextPassword,
			String encryptedPassword, boolean upgradeHashSecurity) {

			return plainTextPassword;
		}

		private TestBCPBKDF2PasswordEncryptor(String type) {
			_type = type;
		}

		private String _getType() {
			return _type;
		}

		private final String _type;

	}

	private final class MockServiceTrackerMap
		implements ServiceTrackerMap<String, TestBCPBKDF2PasswordEncryptor> {

		@Override
		public void close() {
			_passwordEncryptors.clear();
		}

		@Override
		public boolean containsKey(String type) {
			return _passwordEncryptors.containsKey("");
		}

		@Override
		public TestBCPBKDF2PasswordEncryptor getService(String key) {
			return _passwordEncryptors.get(key);
		}

		@Override
		public Set<String> keySet() {
			return _passwordEncryptors.keySet();
		}

		public void register(
			String key, TestBCPBKDF2PasswordEncryptor passwordEncryptor) {

			_passwordEncryptors.put(key, passwordEncryptor);
		}

		@Override
		public Collection<TestBCPBKDF2PasswordEncryptor> values() {
			return _passwordEncryptors.values();
		}

		private final Map<String, TestBCPBKDF2PasswordEncryptor>
			_passwordEncryptors = new HashMap<>();

	}

}