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

import com.liferay.saml.admin.rest.client.dto.v1_0.BaseSamlConnection;
import com.liferay.saml.admin.rest.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class BaseSamlConnectionSerDes {

	public static BaseSamlConnection toDTO(String json) {
		BaseSamlConnectionJSONParser baseSamlConnectionJSONParser =
			new BaseSamlConnectionJSONParser();

		return baseSamlConnectionJSONParser.parseToDTO(json);
	}

	public static BaseSamlConnection[] toDTOs(String json) {
		BaseSamlConnectionJSONParser baseSamlConnectionJSONParser =
			new BaseSamlConnectionJSONParser();

		return baseSamlConnectionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(BaseSamlConnection baseSamlConnection) {
		if (baseSamlConnection == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (baseSamlConnection.getEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enabled\": ");

			sb.append(baseSamlConnection.getEnabled());
		}

		if (baseSamlConnection.getEntityId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entityId\": ");

			sb.append("\"");

			sb.append(_escape(baseSamlConnection.getEntityId()));

			sb.append("\"");
		}

		if (baseSamlConnection.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(baseSamlConnection.getId());
		}

		if (baseSamlConnection.getMetadataUpdatedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metadataUpdatedDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					baseSamlConnection.getMetadataUpdatedDate()));

			sb.append("\"");
		}

		if (baseSamlConnection.getMetadataUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metadataUrl\": ");

			sb.append("\"");

			sb.append(_escape(baseSamlConnection.getMetadataUrl()));

			sb.append("\"");
		}

		if (baseSamlConnection.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(baseSamlConnection.getName()));

			sb.append("\"");
		}

		if (baseSamlConnection.getNameIdFormat() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nameIdFormat\": ");

			sb.append("\"");

			sb.append(_escape(baseSamlConnection.getNameIdFormat()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		BaseSamlConnectionJSONParser baseSamlConnectionJSONParser =
			new BaseSamlConnectionJSONParser();

		return baseSamlConnectionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		BaseSamlConnection baseSamlConnection) {

		if (baseSamlConnection == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (baseSamlConnection.getEnabled() == null) {
			map.put("enabled", null);
		}
		else {
			map.put("enabled", String.valueOf(baseSamlConnection.getEnabled()));
		}

		if (baseSamlConnection.getEntityId() == null) {
			map.put("entityId", null);
		}
		else {
			map.put(
				"entityId", String.valueOf(baseSamlConnection.getEntityId()));
		}

		if (baseSamlConnection.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(baseSamlConnection.getId()));
		}

		if (baseSamlConnection.getMetadataUpdatedDate() == null) {
			map.put("metadataUpdatedDate", null);
		}
		else {
			map.put(
				"metadataUpdatedDate",
				liferayToJSONDateFormat.format(
					baseSamlConnection.getMetadataUpdatedDate()));
		}

		if (baseSamlConnection.getMetadataUrl() == null) {
			map.put("metadataUrl", null);
		}
		else {
			map.put(
				"metadataUrl",
				String.valueOf(baseSamlConnection.getMetadataUrl()));
		}

		if (baseSamlConnection.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(baseSamlConnection.getName()));
		}

		if (baseSamlConnection.getNameIdFormat() == null) {
			map.put("nameIdFormat", null);
		}
		else {
			map.put(
				"nameIdFormat",
				String.valueOf(baseSamlConnection.getNameIdFormat()));
		}

		return map;
	}

	public static class BaseSamlConnectionJSONParser
		extends BaseJSONParser<BaseSamlConnection> {

		@Override
		protected BaseSamlConnection createDTO() {
			return new BaseSamlConnection();
		}

		@Override
		protected BaseSamlConnection[] createDTOArray(int size) {
			return new BaseSamlConnection[size];
		}

		@Override
		protected void setField(
			BaseSamlConnection baseSamlConnection, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "enabled")) {
				if (jsonParserFieldValue != null) {
					baseSamlConnection.setEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "entityId")) {
				if (jsonParserFieldValue != null) {
					baseSamlConnection.setEntityId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					baseSamlConnection.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "metadataUpdatedDate")) {

				if (jsonParserFieldValue != null) {
					baseSamlConnection.setMetadataUpdatedDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "metadataUrl")) {
				if (jsonParserFieldValue != null) {
					baseSamlConnection.setMetadataUrl(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					baseSamlConnection.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "nameIdFormat")) {
				if (jsonParserFieldValue != null) {
					baseSamlConnection.setNameIdFormat(
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