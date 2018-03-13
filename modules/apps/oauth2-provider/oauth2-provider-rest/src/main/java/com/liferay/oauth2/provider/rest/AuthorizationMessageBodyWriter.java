/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.rest;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import com.liferay.portal.kernel.util.Validator;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.oauth2.common.OAuthAuthorizationData;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Map;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	immediate = true,
	property = "liferay.oauth2.endpoint=true",
	service = Object.class
)
@Produces("text/html")
@Provider
public class AuthorizationMessageBodyWriter
	implements MessageBodyWriter<OAuthAuthorizationData> {

	@Override
	public boolean isWriteable(
		Class<?> aClass, Type type, Annotation[] annotations,
		MediaType mediaType) {

		return aClass.isAssignableFrom(OAuthAuthorizationData.class) &&
			   "text".equalsIgnoreCase(mediaType.getType()) &&
			   "html".equalsIgnoreCase(mediaType.getSubtype());
	}

	@Override
	public long getSize(
		OAuthAuthorizationData oAuthAuthorizationData, Class<?> aClass,
		Type type, Annotation[] annotations, MediaType mediaType) {

		return -1L;
	}

	@Override
	public void writeTo(
		OAuthAuthorizationData oAuthAuthorizationData, Class<?> aClass,
		Type type, Annotation[] annotations, MediaType mediaType,
		MultivaluedMap<String, Object> httpHeaders,
		OutputStream outputStream)
		throws IOException, WebApplicationException {

		String redirect = _oAuth2AuthorizePortalScreenURL;

		if (!_http.hasDomain(redirect)) {
			String portalURL =
				_portal.getPortalURL(_messageContext.getHttpServletRequest());

			redirect = portalURL + redirect;
		}

		redirect = setParameter(
			redirect, OAuthConstants.CLIENT_ID,
			oAuthAuthorizationData.getClientId());

		redirect = setParameter(
			redirect, OAuthConstants.REDIRECT_URI,
			oAuthAuthorizationData.getRedirectUri());

		redirect = setParameter(
			redirect, OAuthConstants.STATE,
			oAuthAuthorizationData.getState());

		redirect = setParameter(
			redirect, OAuthConstants.SCOPE,
			oAuthAuthorizationData.getProposedScope());

		redirect = setParameter(
			redirect, OAuthConstants.CLIENT_AUDIENCE,
			oAuthAuthorizationData.getAudience());

		redirect = setParameter(
			redirect, OAuthConstants.NONCE,
			oAuthAuthorizationData.getNonce());

		redirect = setParameter(
			redirect, OAuthConstants.AUTHORIZATION_CODE_CHALLENGE,
			oAuthAuthorizationData.getClientCodeChallenge());

		redirect = setParameter(
			redirect, OAuthConstants.RESPONSE_TYPE,
			oAuthAuthorizationData.getResponseType());

		redirect = setParameter(
			redirect, OAuthConstants.SESSION_AUTHENTICITY_TOKEN,
			oAuthAuthorizationData.getAuthenticityToken());

		redirect = setParameter(
			redirect, "reply_to", oAuthAuthorizationData.getReplyTo());

		if (redirect.length() > _invokerFilterUriMaxLength) {
			redirect = removeParameter(redirect, OAuthConstants.SCOPE);
		}

		throw new WebApplicationException(
			Response
				.status(Response.Status.FOUND)
				.location(URI.create(redirect))
				.build());
	}

	protected String removeParameter(String url, String name) {
		return _http.removeParameter(url, "oauth2_" + name);
	}

	protected String setParameter(String url, String name, String value) {
		if (Validator.isBlank(value)) {
			return url;
		}

		return _http.addParameter(url, "oauth2_" + name, value);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_oAuth2AuthorizePortalScreenURL =
			MapUtil.getString(
				properties, "oauth2.authorize.portal.screen.url",
				_oAuth2AuthorizePortalScreenURL);

		_invokerFilterUriMaxLength =
			GetterUtil.getInteger(
				PropsUtil.get(PropsKeys.INVOKER_FILTER_URI_MAX_LENGTH),
				_invokerFilterUriMaxLength);

	}

	@Context
	private MessageContext _messageContext;

	@Reference
	private Portal _portal;

	@Reference
	private Http _http;

	@Reference
	private Props _props;

	private int _invokerFilterUriMaxLength = 4000;

	private String _oAuth2AuthorizePortalScreenURL =
		"/group/guest/oauth2-authorize";

}
