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
import com.liferay.saml.admin.rest.client.dto.v1_0.ProviderConfiguration;
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
public class ProviderConfigurationSerDes {

	public static ProviderConfiguration toDTO(String json) {
		ProviderConfigurationJSONParser providerConfigurationJSONParser =
			new ProviderConfigurationJSONParser();

		return providerConfigurationJSONParser.parseToDTO(json);
	}

	public static ProviderConfiguration[] toDTOs(String json) {
		ProviderConfigurationJSONParser providerConfigurationJSONParser =
			new ProviderConfigurationJSONParser();

		return providerConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ProviderConfiguration providerConfiguration) {
		if (providerConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (providerConfiguration.getAllowShowingTheLoginPortlet() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allowShowingTheLoginPortlet\": ");

			sb.append(providerConfiguration.getAllowShowingTheLoginPortlet());
		}

		if (providerConfiguration.getAssertionSignatureRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assertionSignatureRequired\": ");

			sb.append(providerConfiguration.getAssertionSignatureRequired());
		}

		if (providerConfiguration.getAuthnRequestSignatureRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authnRequestSignatureRequired\": ");

			sb.append(providerConfiguration.getAuthnRequestSignatureRequired());
		}

		if (providerConfiguration.getClockSkew() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clockSkew\": ");

			sb.append(providerConfiguration.getClockSkew());
		}

		if (providerConfiguration.getCompanyId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"companyId\": ");

			sb.append(providerConfiguration.getCompanyId());
		}

		if (providerConfiguration.getDefaultAssertionLifetime() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultAssertionLifetime\": ");

			sb.append(providerConfiguration.getDefaultAssertionLifetime());
		}

		if (providerConfiguration.getEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enabled\": ");

			sb.append(providerConfiguration.getEnabled());
		}

		if (providerConfiguration.getEntityId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entityId\": ");

			sb.append("\"");

			sb.append(_escape(providerConfiguration.getEntityId()));

			sb.append("\"");
		}

		if (providerConfiguration.getIdpConnections() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"idpConnections\": ");

			sb.append("[");

			for (int i = 0;
				 i < providerConfiguration.getIdpConnections().length; i++) {

				sb.append(
					String.valueOf(
						providerConfiguration.getIdpConnections()[i]));

				if ((i + 1) <
						providerConfiguration.getIdpConnections().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (providerConfiguration.getKeyStoreCredentialPassword() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keyStoreCredentialPassword\": ");

			sb.append("\"");

			sb.append(
				_escape(providerConfiguration.getKeyStoreCredentialPassword()));

			sb.append("\"");
		}

		if (providerConfiguration.getLdapImportEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ldapImportEnabled\": ");

			sb.append(providerConfiguration.getLdapImportEnabled());
		}

		if (providerConfiguration.getRole() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"role\": ");

			sb.append("\"");

			sb.append(_escape(providerConfiguration.getRole()));

			sb.append("\"");
		}

		if (providerConfiguration.getSamlKeystoreCredentialPassword() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"samlKeystoreCredentialPassword\": ");

			sb.append("\"");

			sb.append(
				_escape(
					providerConfiguration.getSamlKeystoreCredentialPassword()));

			sb.append("\"");
		}

		if (providerConfiguration.getSessionMaximumAge() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sessionMaximumAge\": ");

			sb.append(providerConfiguration.getSessionMaximumAge());
		}

		if (providerConfiguration.getSessionTimeout() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sessionTimeout\": ");

			sb.append(providerConfiguration.getSessionTimeout());
		}

		if (providerConfiguration.getSignAuthnRequest() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"signAuthnRequest\": ");

			sb.append(providerConfiguration.getSignAuthnRequest());
		}

		if (providerConfiguration.getSignMetadata() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"signMetadata\": ");

			sb.append(providerConfiguration.getSignMetadata());
		}

		if (providerConfiguration.getSslRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sslRequired\": ");

			sb.append(providerConfiguration.getSslRequired());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProviderConfigurationJSONParser providerConfigurationJSONParser =
			new ProviderConfigurationJSONParser();

		return providerConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ProviderConfiguration providerConfiguration) {

		if (providerConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (providerConfiguration.getAllowShowingTheLoginPortlet() == null) {
			map.put("allowShowingTheLoginPortlet", null);
		}
		else {
			map.put(
				"allowShowingTheLoginPortlet",
				String.valueOf(
					providerConfiguration.getAllowShowingTheLoginPortlet()));
		}

		if (providerConfiguration.getAssertionSignatureRequired() == null) {
			map.put("assertionSignatureRequired", null);
		}
		else {
			map.put(
				"assertionSignatureRequired",
				String.valueOf(
					providerConfiguration.getAssertionSignatureRequired()));
		}

		if (providerConfiguration.getAuthnRequestSignatureRequired() == null) {
			map.put("authnRequestSignatureRequired", null);
		}
		else {
			map.put(
				"authnRequestSignatureRequired",
				String.valueOf(
					providerConfiguration.getAuthnRequestSignatureRequired()));
		}

		if (providerConfiguration.getClockSkew() == null) {
			map.put("clockSkew", null);
		}
		else {
			map.put(
				"clockSkew",
				String.valueOf(providerConfiguration.getClockSkew()));
		}

		if (providerConfiguration.getCompanyId() == null) {
			map.put("companyId", null);
		}
		else {
			map.put(
				"companyId",
				String.valueOf(providerConfiguration.getCompanyId()));
		}

		if (providerConfiguration.getDefaultAssertionLifetime() == null) {
			map.put("defaultAssertionLifetime", null);
		}
		else {
			map.put(
				"defaultAssertionLifetime",
				String.valueOf(
					providerConfiguration.getDefaultAssertionLifetime()));
		}

		if (providerConfiguration.getEnabled() == null) {
			map.put("enabled", null);
		}
		else {
			map.put(
				"enabled", String.valueOf(providerConfiguration.getEnabled()));
		}

		if (providerConfiguration.getEntityId() == null) {
			map.put("entityId", null);
		}
		else {
			map.put(
				"entityId",
				String.valueOf(providerConfiguration.getEntityId()));
		}

		if (providerConfiguration.getIdpConnections() == null) {
			map.put("idpConnections", null);
		}
		else {
			map.put(
				"idpConnections",
				String.valueOf(providerConfiguration.getIdpConnections()));
		}

		if (providerConfiguration.getKeyStoreCredentialPassword() == null) {
			map.put("keyStoreCredentialPassword", null);
		}
		else {
			map.put(
				"keyStoreCredentialPassword",
				String.valueOf(
					providerConfiguration.getKeyStoreCredentialPassword()));
		}

		if (providerConfiguration.getLdapImportEnabled() == null) {
			map.put("ldapImportEnabled", null);
		}
		else {
			map.put(
				"ldapImportEnabled",
				String.valueOf(providerConfiguration.getLdapImportEnabled()));
		}

		if (providerConfiguration.getRole() == null) {
			map.put("role", null);
		}
		else {
			map.put("role", String.valueOf(providerConfiguration.getRole()));
		}

		if (providerConfiguration.getSamlKeystoreCredentialPassword() == null) {
			map.put("samlKeystoreCredentialPassword", null);
		}
		else {
			map.put(
				"samlKeystoreCredentialPassword",
				String.valueOf(
					providerConfiguration.getSamlKeystoreCredentialPassword()));
		}

		if (providerConfiguration.getSessionMaximumAge() == null) {
			map.put("sessionMaximumAge", null);
		}
		else {
			map.put(
				"sessionMaximumAge",
				String.valueOf(providerConfiguration.getSessionMaximumAge()));
		}

		if (providerConfiguration.getSessionTimeout() == null) {
			map.put("sessionTimeout", null);
		}
		else {
			map.put(
				"sessionTimeout",
				String.valueOf(providerConfiguration.getSessionTimeout()));
		}

		if (providerConfiguration.getSignAuthnRequest() == null) {
			map.put("signAuthnRequest", null);
		}
		else {
			map.put(
				"signAuthnRequest",
				String.valueOf(providerConfiguration.getSignAuthnRequest()));
		}

		if (providerConfiguration.getSignMetadata() == null) {
			map.put("signMetadata", null);
		}
		else {
			map.put(
				"signMetadata",
				String.valueOf(providerConfiguration.getSignMetadata()));
		}

		if (providerConfiguration.getSslRequired() == null) {
			map.put("sslRequired", null);
		}
		else {
			map.put(
				"sslRequired",
				String.valueOf(providerConfiguration.getSslRequired()));
		}

		return map;
	}

	public static class ProviderConfigurationJSONParser
		extends BaseJSONParser<ProviderConfiguration> {

		@Override
		protected ProviderConfiguration createDTO() {
			return new ProviderConfiguration();
		}

		@Override
		protected ProviderConfiguration[] createDTOArray(int size) {
			return new ProviderConfiguration[size];
		}

		@Override
		protected void setField(
			ProviderConfiguration providerConfiguration,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "allowShowingTheLoginPortlet")) {

				if (jsonParserFieldValue != null) {
					providerConfiguration.setAllowShowingTheLoginPortlet(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "assertionSignatureRequired")) {

				if (jsonParserFieldValue != null) {
					providerConfiguration.setAssertionSignatureRequired(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "authnRequestSignatureRequired")) {

				if (jsonParserFieldValue != null) {
					providerConfiguration.setAuthnRequestSignatureRequired(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "clockSkew")) {
				if (jsonParserFieldValue != null) {
					providerConfiguration.setClockSkew(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "companyId")) {
				if (jsonParserFieldValue != null) {
					providerConfiguration.setCompanyId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "defaultAssertionLifetime")) {

				if (jsonParserFieldValue != null) {
					providerConfiguration.setDefaultAssertionLifetime(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "enabled")) {
				if (jsonParserFieldValue != null) {
					providerConfiguration.setEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "entityId")) {
				if (jsonParserFieldValue != null) {
					providerConfiguration.setEntityId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "idpConnections")) {
				if (jsonParserFieldValue != null) {
					providerConfiguration.setIdpConnections(
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
					providerConfiguration.setKeyStoreCredentialPassword(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ldapImportEnabled")) {
				if (jsonParserFieldValue != null) {
					providerConfiguration.setLdapImportEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "role")) {
				if (jsonParserFieldValue != null) {
					providerConfiguration.setRole((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"samlKeystoreCredentialPassword")) {

				if (jsonParserFieldValue != null) {
					providerConfiguration.setSamlKeystoreCredentialPassword(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sessionMaximumAge")) {
				if (jsonParserFieldValue != null) {
					providerConfiguration.setSessionMaximumAge(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sessionTimeout")) {
				if (jsonParserFieldValue != null) {
					providerConfiguration.setSessionTimeout(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "signAuthnRequest")) {
				if (jsonParserFieldValue != null) {
					providerConfiguration.setSignAuthnRequest(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "signMetadata")) {
				if (jsonParserFieldValue != null) {
					providerConfiguration.setSignMetadata(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sslRequired")) {
				if (jsonParserFieldValue != null) {
					providerConfiguration.setSslRequired(
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