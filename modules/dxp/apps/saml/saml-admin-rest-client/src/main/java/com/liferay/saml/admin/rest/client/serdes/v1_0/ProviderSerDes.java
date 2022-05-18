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

import com.liferay.saml.admin.rest.client.dto.v1_0.Provider;
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
public class ProviderSerDes {

	public static Provider toDTO(String json) {
		ProviderJSONParser providerJSONParser = new ProviderJSONParser();

		return providerJSONParser.parseToDTO(json);
	}

	public static Provider[] toDTOs(String json) {
		ProviderJSONParser providerJSONParser = new ProviderJSONParser();

		return providerJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Provider provider) {
		if (provider == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (provider.getEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enabled\": ");

			sb.append(provider.getEnabled());
		}

		if (provider.getEntityId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entityId\": ");

			sb.append("\"");

			sb.append(_escape(provider.getEntityId()));

			sb.append("\"");
		}

		if (provider.getIdp() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"idp\": ");

			sb.append(String.valueOf(provider.getIdp()));
		}

		if (provider.getRole() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"role\": ");

			sb.append("\"");

			sb.append(provider.getRole());

			sb.append("\"");
		}

		if (provider.getSp() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sp\": ");

			sb.append(String.valueOf(provider.getSp()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProviderJSONParser providerJSONParser = new ProviderJSONParser();

		return providerJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Provider provider) {
		if (provider == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (provider.getEnabled() == null) {
			map.put("enabled", null);
		}
		else {
			map.put("enabled", String.valueOf(provider.getEnabled()));
		}

		if (provider.getEntityId() == null) {
			map.put("entityId", null);
		}
		else {
			map.put("entityId", String.valueOf(provider.getEntityId()));
		}

		if (provider.getIdp() == null) {
			map.put("idp", null);
		}
		else {
			map.put("idp", String.valueOf(provider.getIdp()));
		}

		if (provider.getRole() == null) {
			map.put("role", null);
		}
		else {
			map.put("role", String.valueOf(provider.getRole()));
		}

		if (provider.getSp() == null) {
			map.put("sp", null);
		}
		else {
			map.put("sp", String.valueOf(provider.getSp()));
		}

		return map;
	}

	public static class ProviderJSONParser extends BaseJSONParser<Provider> {

		@Override
		protected Provider createDTO() {
			return new Provider();
		}

		@Override
		protected Provider[] createDTOArray(int size) {
			return new Provider[size];
		}

		@Override
		protected void setField(
			Provider provider, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "enabled")) {
				if (jsonParserFieldValue != null) {
					provider.setEnabled((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "entityId")) {
				if (jsonParserFieldValue != null) {
					provider.setEntityId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "idp")) {
				if (jsonParserFieldValue != null) {
					provider.setIdp(
						IdpSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "role")) {
				if (jsonParserFieldValue != null) {
					provider.setRole(
						Provider.Role.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sp")) {
				if (jsonParserFieldValue != null) {
					provider.setSp(
						SpSerDes.toDTO((String)jsonParserFieldValue));
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