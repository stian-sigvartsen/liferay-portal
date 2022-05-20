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

package com.liferay.saml.admin.rest.client.serdes.v1_0;

import com.liferay.saml.admin.rest.client.dto.v1_0.Credentials;
import com.liferay.saml.admin.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Generated;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
public class CredentialsSerDes {

	public static Credentials toDTO(String json) {
		CredentialsJSONParser credentialsJSONParser =
			new CredentialsJSONParser();

		return credentialsJSONParser.parseToDTO(json);
	}

	public static Credentials[] toDTOs(String json) {
		CredentialsJSONParser credentialsJSONParser =
			new CredentialsJSONParser();

		return credentialsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Credentials credentials) {
		if (credentials == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (credentials.getKeyStoreCredentialPassword() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keyStoreCredentialPassword\": ");

			sb.append("\"");

			sb.append(_escape(credentials.getKeyStoreCredentialPassword()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CredentialsJSONParser credentialsJSONParser =
			new CredentialsJSONParser();

		return credentialsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Credentials credentials) {
		if (credentials == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (credentials.getKeyStoreCredentialPassword() == null) {
			map.put("keyStoreCredentialPassword", null);
		}
		else {
			map.put(
				"keyStoreCredentialPassword",
				String.valueOf(credentials.getKeyStoreCredentialPassword()));
		}

		return map;
	}

	public static class CredentialsJSONParser
		extends BaseJSONParser<Credentials> {

		@Override
		protected Credentials createDTO() {
			return new Credentials();
		}

		@Override
		protected Credentials[] createDTOArray(int size) {
			return new Credentials[size];
		}

		@Override
		protected void setField(
			Credentials credentials, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "keyStoreCredentialPassword")) {

				if (jsonParserFieldValue != null) {
					credentials.setKeyStoreCredentialPassword(
						(String)jsonParserFieldValue);
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			Class<?> valueClass = value.getClass();

			if (value instanceof Map) {
				sb.append(_toJSON((Map)value));
			}
			else if (valueClass.isArray()) {
				Object[] values = (Object[])value;

				sb.append("[");

				for (int i = 0; i < values.length; i++) {
					sb.append("\"");
					sb.append(_escape(values[i]));
					sb.append("\"");

					if ((i + 1) < values.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(entry.getValue()));
				sb.append("\"");
			}
			else {
				sb.append(String.valueOf(entry.getValue()));
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

}