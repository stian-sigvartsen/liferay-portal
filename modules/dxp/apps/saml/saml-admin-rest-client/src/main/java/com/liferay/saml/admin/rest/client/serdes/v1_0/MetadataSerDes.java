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

import com.liferay.saml.admin.rest.client.dto.v1_0.Metadata;
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
public class MetadataSerDes {

	public static Metadata toDTO(String json) {
		MetadataJSONParser metadataJSONParser = new MetadataJSONParser();

		return metadataJSONParser.parseToDTO(json);
	}

	public static Metadata[] toDTOs(String json) {
		MetadataJSONParser metadataJSONParser = new MetadataJSONParser();

		return metadataJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Metadata metadata) {
		if (metadata == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (metadata.getContentUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentUrl\": ");

			sb.append("\"");

			sb.append(_escape(metadata.getContentUrl()));

			sb.append("\"");
		}

		if (metadata.getContentValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentValue\": ");

			sb.append("\"");

			sb.append(_escape(metadata.getContentValue()));

			sb.append("\"");
		}

		if (metadata.getMetadataUpdatedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metadataUpdatedDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					metadata.getMetadataUpdatedDate()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MetadataJSONParser metadataJSONParser = new MetadataJSONParser();

		return metadataJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Metadata metadata) {
		if (metadata == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (metadata.getContentUrl() == null) {
			map.put("contentUrl", null);
		}
		else {
			map.put("contentUrl", String.valueOf(metadata.getContentUrl()));
		}

		if (metadata.getContentValue() == null) {
			map.put("contentValue", null);
		}
		else {
			map.put("contentValue", String.valueOf(metadata.getContentValue()));
		}

		if (metadata.getMetadataUpdatedDate() == null) {
			map.put("metadataUpdatedDate", null);
		}
		else {
			map.put(
				"metadataUpdatedDate",
				liferayToJSONDateFormat.format(
					metadata.getMetadataUpdatedDate()));
		}

		return map;
	}

	public static class MetadataJSONParser extends BaseJSONParser<Metadata> {

		@Override
		protected Metadata createDTO() {
			return new Metadata();
		}

		@Override
		protected Metadata[] createDTOArray(int size) {
			return new Metadata[size];
		}

		@Override
		protected void setField(
			Metadata metadata, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "contentUrl")) {
				if (jsonParserFieldValue != null) {
					metadata.setContentUrl((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentValue")) {
				if (jsonParserFieldValue != null) {
					metadata.setContentValue((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "metadataUpdatedDate")) {

				if (jsonParserFieldValue != null) {
					metadata.setMetadataUpdatedDate(
						toDate((String)jsonParserFieldValue));
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