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

import com.liferay.saml.admin.rest.client.dto.v1_0.IdpProviderConfiguration;
import com.liferay.saml.admin.rest.client.dto.v1_0.SpConnection;
import com.liferay.saml.admin.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

import javax.annotation.Generated;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
public class IdpProviderConfigurationSerDes {

	public static IdpProviderConfiguration toDTO(String json) {
		IdpProviderConfigurationJSONParser idpProviderConfigurationJSONParser =
			new IdpProviderConfigurationJSONParser();

		return idpProviderConfigurationJSONParser.parseToDTO(json);
	}

	public static IdpProviderConfiguration[] toDTOs(String json) {
		IdpProviderConfigurationJSONParser idpProviderConfigurationJSONParser =
			new IdpProviderConfigurationJSONParser();

		return idpProviderConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		IdpProviderConfiguration idpProviderConfiguration) {

		if (idpProviderConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (idpProviderConfiguration.getAuthnRequestSignatureRequired() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authnRequestSignatureRequired\": ");

			sb.append(
				idpProviderConfiguration.getAuthnRequestSignatureRequired());
		}

		if (idpProviderConfiguration.getEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enabled\": ");

			sb.append(idpProviderConfiguration.getEnabled());
		}

		if (idpProviderConfiguration.getEntityId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entityId\": ");

			sb.append("\"");

			sb.append(_escape(idpProviderConfiguration.getEntityId()));

			sb.append("\"");
		}

		if (idpProviderConfiguration.getRole() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"role\": ");

			sb.append("\"");

			sb.append(idpProviderConfiguration.getRole());

			sb.append("\"");
		}

		if (idpProviderConfiguration.getSessionMaximumAge() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sessionMaximumAge\": ");

			sb.append(idpProviderConfiguration.getSessionMaximumAge());
		}

		if (idpProviderConfiguration.getSessionTimeout() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sessionTimeout\": ");

			sb.append(idpProviderConfiguration.getSessionTimeout());
		}

		if (idpProviderConfiguration.getSignMetadata() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"signMetadata\": ");

			sb.append(idpProviderConfiguration.getSignMetadata());
		}

		if (idpProviderConfiguration.getSpConnections() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"spConnections\": ");

			sb.append("[");

			for (int i = 0;
				 i < idpProviderConfiguration.getSpConnections().length; i++) {

				sb.append(
					String.valueOf(
						idpProviderConfiguration.getSpConnections()[i]));

				if ((i + 1) <
						idpProviderConfiguration.getSpConnections().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (idpProviderConfiguration.getSslRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sslRequired\": ");

			sb.append(idpProviderConfiguration.getSslRequired());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		IdpProviderConfigurationJSONParser idpProviderConfigurationJSONParser =
			new IdpProviderConfigurationJSONParser();

		return idpProviderConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		IdpProviderConfiguration idpProviderConfiguration) {

		if (idpProviderConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (idpProviderConfiguration.getAuthnRequestSignatureRequired() ==
				null) {

			map.put("authnRequestSignatureRequired", null);
		}
		else {
			map.put(
				"authnRequestSignatureRequired",
				String.valueOf(
					idpProviderConfiguration.
						getAuthnRequestSignatureRequired()));
		}

		if (idpProviderConfiguration.getEnabled() == null) {
			map.put("enabled", null);
		}
		else {
			map.put(
				"enabled",
				String.valueOf(idpProviderConfiguration.getEnabled()));
		}

		if (idpProviderConfiguration.getEntityId() == null) {
			map.put("entityId", null);
		}
		else {
			map.put(
				"entityId",
				String.valueOf(idpProviderConfiguration.getEntityId()));
		}

		if (idpProviderConfiguration.getRole() == null) {
			map.put("role", null);
		}
		else {
			map.put("role", String.valueOf(idpProviderConfiguration.getRole()));
		}

		if (idpProviderConfiguration.getSessionMaximumAge() == null) {
			map.put("sessionMaximumAge", null);
		}
		else {
			map.put(
				"sessionMaximumAge",
				String.valueOf(
					idpProviderConfiguration.getSessionMaximumAge()));
		}

		if (idpProviderConfiguration.getSessionTimeout() == null) {
			map.put("sessionTimeout", null);
		}
		else {
			map.put(
				"sessionTimeout",
				String.valueOf(idpProviderConfiguration.getSessionTimeout()));
		}

		if (idpProviderConfiguration.getSignMetadata() == null) {
			map.put("signMetadata", null);
		}
		else {
			map.put(
				"signMetadata",
				String.valueOf(idpProviderConfiguration.getSignMetadata()));
		}

		if (idpProviderConfiguration.getSpConnections() == null) {
			map.put("spConnections", null);
		}
		else {
			map.put(
				"spConnections",
				String.valueOf(idpProviderConfiguration.getSpConnections()));
		}

		if (idpProviderConfiguration.getSslRequired() == null) {
			map.put("sslRequired", null);
		}
		else {
			map.put(
				"sslRequired",
				String.valueOf(idpProviderConfiguration.getSslRequired()));
		}

		return map;
	}

	public static class IdpProviderConfigurationJSONParser
		extends BaseJSONParser<IdpProviderConfiguration> {

		@Override
		protected IdpProviderConfiguration createDTO() {
			return new IdpProviderConfiguration();
		}

		@Override
		protected IdpProviderConfiguration[] createDTOArray(int size) {
			return new IdpProviderConfiguration[size];
		}

		@Override
		protected void setField(
			IdpProviderConfiguration idpProviderConfiguration,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "authnRequestSignatureRequired")) {

				if (jsonParserFieldValue != null) {
					idpProviderConfiguration.setAuthnRequestSignatureRequired(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "enabled")) {
				if (jsonParserFieldValue != null) {
					idpProviderConfiguration.setEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "entityId")) {
				if (jsonParserFieldValue != null) {
					idpProviderConfiguration.setEntityId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "role")) {
				if (jsonParserFieldValue != null) {
					idpProviderConfiguration.setRole(
						IdpProviderConfiguration.Role.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sessionMaximumAge")) {
				if (jsonParserFieldValue != null) {
					idpProviderConfiguration.setSessionMaximumAge(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sessionTimeout")) {
				if (jsonParserFieldValue != null) {
					idpProviderConfiguration.setSessionTimeout(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "signMetadata")) {
				if (jsonParserFieldValue != null) {
					idpProviderConfiguration.setSignMetadata(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "spConnections")) {
				if (jsonParserFieldValue != null) {
					idpProviderConfiguration.setSpConnections(
						Stream.of(
							toStrings((Object[])jsonParserFieldValue)
						).map(
							object -> SpConnectionSerDes.toDTO((String)object)
						).toArray(
							size -> new SpConnection[size]
						));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sslRequired")) {
				if (jsonParserFieldValue != null) {
					idpProviderConfiguration.setSslRequired(
						(Boolean)jsonParserFieldValue);
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