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

package com.liferay.saml.admin.rest.internal.dto;

import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.saml.admin.rest.dto.v1_0.IdpConnection;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	property = "dto.class.name=com.liferay.saml.persistence.model.SamlSpIdpConnection",
	service = {DTOConverter.class, IdpConnectionDTOConverter.class}
)
public class IdpConnectionDTOConverter
	implements DTOConverter<SamlSpIdpConnection, IdpConnection> {

	@Override
	public String getContentType() {
		return IdpConnection.class.getSimpleName();
	}

	@Override
	public IdpConnection toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		SamlSpIdpConnection samlSpIdpConnection =
			_samlSpIdpConnectionLocalService.getSamlSpIdpConnection(
				(long)dtoConverterContext.getId());

		return new IdpConnection() {
			{
				assertionSignatureRequired =
					samlSpIdpConnection.isAssertionSignatureRequired();
				clockSkew = samlSpIdpConnection.getClockSkew();
				enabled = samlSpIdpConnection.isEnabled();
				entityId = samlSpIdpConnection.getSamlIdpEntityId();
				forceAuthn = samlSpIdpConnection.isForceAuthn();
				id = samlSpIdpConnection.getSamlSpIdpConnectionId();
				metadataUpdatedDate =
					samlSpIdpConnection.getMetadataUpdatedDate();
				metadataUrl = samlSpIdpConnection.getMetadataUrl();
				name = samlSpIdpConnection.getName();
				nameIdFormat = samlSpIdpConnection.getNameIdFormat();
				signAuthnRequest = samlSpIdpConnection.isSignAuthnRequest();
				unknownUsersAreStrangers =
					samlSpIdpConnection.isUnknownUsersAreStrangers();
				userAttributeMappings =
					samlSpIdpConnection.getUserAttributeMappings();
			}
		};
	}

	@Reference
	private SamlSpIdpConnectionLocalService _samlSpIdpConnectionLocalService;

}