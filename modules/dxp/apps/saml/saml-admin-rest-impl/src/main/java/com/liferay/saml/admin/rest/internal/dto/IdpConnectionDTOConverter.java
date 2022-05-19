package com.liferay.saml.admin.rest.internal.dto;

import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.saml.admin.rest.dto.v1_0.IdpConnection;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	//enabled = false,
	property = "dto.class.name=com.liferay.saml.persistence.model.SamlSpIdpConnection",
	service = {IdpConnectionDTOConverter.class, DTOConverter.class}
)
public class IdpConnectionDTOConverter
	implements DTOConverter<SamlSpIdpConnection, IdpConnection> {


	@Override
	public String getContentType() {
		return IdpConnection.class.getSimpleName();
	}

	@Override
	public IdpConnection toDTO(DTOConverterContext dtoConverterContext) throws Exception {

		SamlSpIdpConnection samlSpIdpConnection =
			_samlSpIdpConnectionLocalService.getSamlSpIdpConnection(
				(long)dtoConverterContext.getId());

		return new IdpConnection() {
			{
				enabled = samlSpIdpConnection.isEnabled();
				entityId = samlSpIdpConnection.getSamlIdpEntityId();
				id = samlSpIdpConnection.getSamlSpIdpConnectionId();
				metadataUrl = samlSpIdpConnection.getMetadataUrl();
				name = samlSpIdpConnection.getName();
				nameIdFormat = samlSpIdpConnection.getNameIdFormat();
				assertionSignatureRequired =
					samlSpIdpConnection.isAssertionSignatureRequired();
				clockSkew = samlSpIdpConnection.getClockSkew();
				forceAuthn = samlSpIdpConnection.isForceAuthn();
				samlSpIdpConnection.getMetadataUpdatedDate();
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
