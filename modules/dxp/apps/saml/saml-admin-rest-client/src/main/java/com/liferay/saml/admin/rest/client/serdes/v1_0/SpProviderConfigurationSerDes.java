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
import com.liferay.saml.admin.rest.client.dto.v1_0.SpProviderConfiguration;
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
public class SpProviderConfigurationSerDes {

	public static SpProviderConfiguration toDTO(String json) {
		SpProviderConfigurationJSONParser spProviderConfigurationJSONParser =
			new SpProviderConfigurationJSONParser();

		return spProviderConfigurationJSONParser.parseToDTO(json);
	}

	public static SpProviderConfiguration[] toDTOs(String json) {
		SpProviderConfigurationJSONParser spProviderConfigurationJSONParser =
			new SpProviderConfigurationJSONParser();

		return spProviderConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		SpProviderConfiguration spProviderConfiguration) {

		if (spProviderConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (spProviderConfiguration.getAllowShowingTheLoginPortlet() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allowShowingTheLoginPortlet\": ");

			sb.append(spProviderConfiguration.getAllowShowingTheLoginPortlet());
		}

		if (spProviderConfiguration.getAssertionSignatureRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assertionSignatureRequired\": ");

			sb.append(spProviderConfiguration.getAssertionSignatureRequired());
		}

		if (spProviderConfiguration.getAuthnRequestSignatureRequired() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authnRequestSignatureRequired\": ");

			sb.append(
				spProviderConfiguration.getAuthnRequestSignatureRequired());
		}

		if (spProviderConfiguration.getClockSkew() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clockSkew\": ");

			sb.append(spProviderConfiguration.getClockSkew());
		}

		if (spProviderConfiguration.getDefaultAssertionLifetime() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultAssertionLifetime\": ");

			sb.append(spProviderConfiguration.getDefaultAssertionLifetime());
		}

		if (spProviderConfiguration.getEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enabled\": ");

			sb.append(spProviderConfiguration.getEnabled());
		}

		if (spProviderConfiguration.getEntityId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entityId\": ");

			sb.append("\"");

			sb.append(_escape(spProviderConfiguration.getEntityId()));

			sb.append("\"");
		}

		if (spProviderConfiguration.getIdpConnections() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"idpConnections\": ");

			sb.append("[");

			for (int i = 0;
				 i < spProviderConfiguration.getIdpConnections().length; i++) {

				sb.append(
					String.valueOf(
						spProviderConfiguration.getIdpConnections()[i]));

				if ((i + 1) <
						spProviderConfiguration.getIdpConnections().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (spProviderConfiguration.getKeyStoreCredentialPassword() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keyStoreCredentialPassword\": ");

			sb.append("\"");

			sb.append(
				_escape(
					spProviderConfiguration.getKeyStoreCredentialPassword()));

			sb.append("\"");
		}

		if (spProviderConfiguration.getLdapImportEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ldapImportEnabled\": ");

			sb.append(spProviderConfiguration.getLdapImportEnabled());
		}

		if (spProviderConfiguration.getRole() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"role\": ");

			sb.append("\"");

			sb.append(spProviderConfiguration.getRole());

			sb.append("\"");
		}

		if (spProviderConfiguration.getSamlKeystoreCredentialPassword() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"samlKeystoreCredentialPassword\": ");

			sb.append("\"");

			sb.append(
				_escape(
					spProviderConfiguration.
						getSamlKeystoreCredentialPassword()));

			sb.append("\"");
		}

		if (spProviderConfiguration.getSessionMaximumAge() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sessionMaximumAge\": ");

			sb.append(spProviderConfiguration.getSessionMaximumAge());
		}

		if (spProviderConfiguration.getSessionTimeout() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sessionTimeout\": ");

			sb.append(spProviderConfiguration.getSessionTimeout());
		}

		if (spProviderConfiguration.getSignAuthnRequest() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"signAuthnRequest\": ");

			sb.append(spProviderConfiguration.getSignAuthnRequest());
		}

		if (spProviderConfiguration.getSignMetadata() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"signMetadata\": ");

			sb.append(spProviderConfiguration.getSignMetadata());
		}

		if (spProviderConfiguration.getSslRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sslRequired\": ");

			sb.append(spProviderConfiguration.getSslRequired());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SpProviderConfigurationJSONParser spProviderConfigurationJSONParser =
			new SpProviderConfigurationJSONParser();

		return spProviderConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SpProviderConfiguration spProviderConfiguration) {

		if (spProviderConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (spProviderConfiguration.getAllowShowingTheLoginPortlet() == null) {
			map.put("allowShowingTheLoginPortlet", null);
		}
		else {
			map.put(
				"allowShowingTheLoginPortlet",
				String.valueOf(
					spProviderConfiguration.getAllowShowingTheLoginPortlet()));
		}

		if (spProviderConfiguration.getAssertionSignatureRequired() == null) {
			map.put("assertionSignatureRequired", null);
		}
		else {
			map.put(
				"assertionSignatureRequired",
				String.valueOf(
					spProviderConfiguration.getAssertionSignatureRequired()));
		}

		if (spProviderConfiguration.getAuthnRequestSignatureRequired() ==
				null) {

			map.put("authnRequestSignatureRequired", null);
		}
		else {
			map.put(
				"authnRequestSignatureRequired",
				String.valueOf(
					spProviderConfiguration.
						getAuthnRequestSignatureRequired()));
		}

		if (spProviderConfiguration.getClockSkew() == null) {
			map.put("clockSkew", null);
		}
		else {
			map.put(
				"clockSkew",
				String.valueOf(spProviderConfiguration.getClockSkew()));
		}

		if (spProviderConfiguration.getDefaultAssertionLifetime() == null) {
			map.put("defaultAssertionLifetime", null);
		}
		else {
			map.put(
				"defaultAssertionLifetime",
				String.valueOf(
					spProviderConfiguration.getDefaultAssertionLifetime()));
		}

		if (spProviderConfiguration.getEnabled() == null) {
			map.put("enabled", null);
		}
		else {
			map.put(
				"enabled",
				String.valueOf(spProviderConfiguration.getEnabled()));
		}

		if (spProviderConfiguration.getEntityId() == null) {
			map.put("entityId", null);
		}
		else {
			map.put(
				"entityId",
				String.valueOf(spProviderConfiguration.getEntityId()));
		}

		if (spProviderConfiguration.getIdpConnections() == null) {
			map.put("idpConnections", null);
		}
		else {
			map.put(
				"idpConnections",
				String.valueOf(spProviderConfiguration.getIdpConnections()));
		}

		if (spProviderConfiguration.getKeyStoreCredentialPassword() == null) {
			map.put("keyStoreCredentialPassword", null);
		}
		else {
			map.put(
				"keyStoreCredentialPassword",
				String.valueOf(
					spProviderConfiguration.getKeyStoreCredentialPassword()));
		}

		if (spProviderConfiguration.getLdapImportEnabled() == null) {
			map.put("ldapImportEnabled", null);
		}
		else {
			map.put(
				"ldapImportEnabled",
				String.valueOf(spProviderConfiguration.getLdapImportEnabled()));
		}

		if (spProviderConfiguration.getRole() == null) {
			map.put("role", null);
		}
		else {
			map.put("role", String.valueOf(spProviderConfiguration.getRole()));
		}

		if (spProviderConfiguration.getSamlKeystoreCredentialPassword() ==
				null) {

			map.put("samlKeystoreCredentialPassword", null);
		}
		else {
			map.put(
				"samlKeystoreCredentialPassword",
				String.valueOf(
					spProviderConfiguration.
						getSamlKeystoreCredentialPassword()));
		}

		if (spProviderConfiguration.getSessionMaximumAge() == null) {
			map.put("sessionMaximumAge", null);
		}
		else {
			map.put(
				"sessionMaximumAge",
				String.valueOf(spProviderConfiguration.getSessionMaximumAge()));
		}

		if (spProviderConfiguration.getSessionTimeout() == null) {
			map.put("sessionTimeout", null);
		}
		else {
			map.put(
				"sessionTimeout",
				String.valueOf(spProviderConfiguration.getSessionTimeout()));
		}

		if (spProviderConfiguration.getSignAuthnRequest() == null) {
			map.put("signAuthnRequest", null);
		}
		else {
			map.put(
				"signAuthnRequest",
				String.valueOf(spProviderConfiguration.getSignAuthnRequest()));
		}

		if (spProviderConfiguration.getSignMetadata() == null) {
			map.put("signMetadata", null);
		}
		else {
			map.put(
				"signMetadata",
				String.valueOf(spProviderConfiguration.getSignMetadata()));
		}

		if (spProviderConfiguration.getSslRequired() == null) {
			map.put("sslRequired", null);
		}
		else {
			map.put(
				"sslRequired",
				String.valueOf(spProviderConfiguration.getSslRequired()));
		}

		return map;
	}

	public static class SpProviderConfigurationJSONParser
		extends BaseJSONParser<SpProviderConfiguration> {

		@Override
		protected SpProviderConfiguration createDTO() {
			return new SpProviderConfiguration();
		}

		@Override
		protected SpProviderConfiguration[] createDTOArray(int size) {
			return new SpProviderConfiguration[size];
		}

		@Override
		protected void setField(
			SpProviderConfiguration spProviderConfiguration,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "allowShowingTheLoginPortlet")) {

				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setAllowShowingTheLoginPortlet(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "assertionSignatureRequired")) {

				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setAssertionSignatureRequired(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "authnRequestSignatureRequired")) {

				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setAuthnRequestSignatureRequired(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "clockSkew")) {
				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setClockSkew(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "defaultAssertionLifetime")) {

				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setDefaultAssertionLifetime(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "enabled")) {
				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "entityId")) {
				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setEntityId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "idpConnections")) {
				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setIdpConnections(
						Stream.of(
							toStrings((Object[])jsonParserFieldValue)
						).map(
							object -> IdpConnectionSerDes.toDTO((String)object)
						).toArray(
							size -> new IdpConnection[size]
						));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "keyStoreCredentialPassword")) {

				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setKeyStoreCredentialPassword(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ldapImportEnabled")) {
				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setLdapImportEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "role")) {
				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setRole(
						SpProviderConfiguration.Role.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"samlKeystoreCredentialPassword")) {

				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setSamlKeystoreCredentialPassword(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sessionMaximumAge")) {
				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setSessionMaximumAge(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sessionTimeout")) {
				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setSessionTimeout(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "signAuthnRequest")) {
				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setSignAuthnRequest(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "signMetadata")) {
				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setSignMetadata(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sslRequired")) {
				if (jsonParserFieldValue != null) {
					spProviderConfiguration.setSslRequired(
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