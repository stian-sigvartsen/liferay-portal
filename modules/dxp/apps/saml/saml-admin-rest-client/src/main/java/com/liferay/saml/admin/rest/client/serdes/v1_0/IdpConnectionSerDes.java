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

import com.liferay.saml.admin.rest.client.dto.v1_0.IdpConnection;
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
public class IdpConnectionSerDes {

	public static IdpConnection toDTO(String json) {
		IdpConnectionJSONParser idpConnectionJSONParser =
			new IdpConnectionJSONParser();

		return idpConnectionJSONParser.parseToDTO(json);
	}

	public static IdpConnection[] toDTOs(String json) {
		IdpConnectionJSONParser idpConnectionJSONParser =
			new IdpConnectionJSONParser();

		return idpConnectionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(IdpConnection idpConnection) {
		if (idpConnection == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (idpConnection.getAssertionSignatureRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assertionSignatureRequired\": ");

			sb.append(idpConnection.getAssertionSignatureRequired());
		}

		if (idpConnection.getClockSkew() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clockSkew\": ");

			sb.append(idpConnection.getClockSkew());
		}

		if (idpConnection.getForceAuthn() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"forceAuthn\": ");

			sb.append(idpConnection.getForceAuthn());
		}

		if (idpConnection.getSignAuthnRequest() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"signAuthnRequest\": ");

			sb.append(idpConnection.getSignAuthnRequest());
		}

		if (idpConnection.getUnknownUsersAreStrangers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unknownUsersAreStrangers\": ");

			sb.append(idpConnection.getUnknownUsersAreStrangers());
		}

		if (idpConnection.getUserAttributeMappings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userAttributeMappings\": ");

			sb.append("\"");

			sb.append(_escape(idpConnection.getUserAttributeMappings()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		IdpConnectionJSONParser idpConnectionJSONParser =
			new IdpConnectionJSONParser();

		return idpConnectionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(IdpConnection idpConnection) {
		if (idpConnection == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (idpConnection.getAssertionSignatureRequired() == null) {
			map.put("assertionSignatureRequired", null);
		}
		else {
			map.put(
				"assertionSignatureRequired",
				String.valueOf(idpConnection.getAssertionSignatureRequired()));
		}

		if (idpConnection.getClockSkew() == null) {
			map.put("clockSkew", null);
		}
		else {
			map.put("clockSkew", String.valueOf(idpConnection.getClockSkew()));
		}

		if (idpConnection.getForceAuthn() == null) {
			map.put("forceAuthn", null);
		}
		else {
			map.put(
				"forceAuthn", String.valueOf(idpConnection.getForceAuthn()));
		}

		if (idpConnection.getSignAuthnRequest() == null) {
			map.put("signAuthnRequest", null);
		}
		else {
			map.put(
				"signAuthnRequest",
				String.valueOf(idpConnection.getSignAuthnRequest()));
		}

		if (idpConnection.getUnknownUsersAreStrangers() == null) {
			map.put("unknownUsersAreStrangers", null);
		}
		else {
			map.put(
				"unknownUsersAreStrangers",
				String.valueOf(idpConnection.getUnknownUsersAreStrangers()));
		}

		if (idpConnection.getUserAttributeMappings() == null) {
			map.put("userAttributeMappings", null);
		}
		else {
			map.put(
				"userAttributeMappings",
				String.valueOf(idpConnection.getUserAttributeMappings()));
		}

		return map;
	}

	public static class IdpConnectionJSONParser
		extends BaseJSONParser<IdpConnection> {

		@Override
		protected IdpConnection createDTO() {
			return new IdpConnection();
		}

		@Override
		protected IdpConnection[] createDTOArray(int size) {
			return new IdpConnection[size];
		}

		@Override
		protected void setField(
			IdpConnection idpConnection, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "assertionSignatureRequired")) {

				if (jsonParserFieldValue != null) {
					idpConnection.setAssertionSignatureRequired(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "clockSkew")) {
				if (jsonParserFieldValue != null) {
					idpConnection.setClockSkew(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "forceAuthn")) {
				if (jsonParserFieldValue != null) {
					idpConnection.setForceAuthn((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "signAuthnRequest")) {
				if (jsonParserFieldValue != null) {
					idpConnection.setSignAuthnRequest(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "unknownUsersAreStrangers")) {

				if (jsonParserFieldValue != null) {
					idpConnection.setUnknownUsersAreStrangers(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "userAttributeMappings")) {

				if (jsonParserFieldValue != null) {
					idpConnection.setUserAttributeMappings(
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