package com.liferay.saml.admin.rest.internal.dto;

import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.saml.admin.rest.dto.v1_0.SpConnection;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;
import com.liferay.saml.persistence.service.SamlIdpSpConnectionLocalService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	//enabled = false,
	property = "dto.class.name=com.liferay.saml.persistence.model.SamlIdpSpConnection",
	service = {SpConnectionDTOConverter.class, DTOConverter.class}
)
public class SpConnectionDTOConverter
	implements DTOConverter<SamlIdpSpConnection, SpConnection> {


	@Override
	public String getContentType() {
		return SpConnection.class.getSimpleName();
	}

	@Override
	public SpConnection toDTO(DTOConverterContext dtoConverterContext) throws Exception {

		SamlIdpSpConnection samlIdpSpConnection =
			_samlIdpSpConnectionLocalService.getSamlIdpSpConnection(
				(long)dtoConverterContext.getId());

		return new SpConnection() {
			{
				enabled = samlIdpSpConnection.isEnabled();
//				entityId = samlSpIdpConnection.getSamlIdpEntityId();
//				id = samlSpIdpConnection.getSamlSpIdpConnectionId();
//				metadataUrl = samlSpIdpConnection.getMetadataUrl();
//				name = samlSpIdpConnection.getName();
//				nameIdFormat = samlSpIdpConnection.getNameIdFormat();
//				assertionSignatureRequired =
//					samlSpIdpConnection.isAssertionSignatureRequired();
//				clockSkew = samlSpIdpConnection.getClockSkew();
//				forceAuthn = samlSpIdpConnection.isForceAuthn();
//				samlSpIdpConnection.getMetadataUpdatedDate();
//				signAuthnRequest = samlSpIdpConnection.isSignAuthnRequest();
//				unknownUsersAreStrangers =
//					samlSpIdpConnection.isUnknownUsersAreStrangers();
//				userAttributeMappings =
//					samlSpIdpConnection.getUserAttributeMappings();
			}
		};
	}

	@Reference
	private SamlIdpSpConnectionLocalService _samlIdpSpConnectionLocalService;
}
