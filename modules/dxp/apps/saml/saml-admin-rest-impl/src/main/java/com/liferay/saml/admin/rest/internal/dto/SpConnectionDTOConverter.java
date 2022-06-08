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
import com.liferay.saml.admin.rest.dto.v1_0.SpConnection;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;
import com.liferay.saml.persistence.service.SamlIdpSpConnectionLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	//enabled = false,
	property = "dto.class.name=com.liferay.saml.persistence.model.SamlIdpSpConnection",
	service = {DTOConverter.class, SpConnectionDTOConverter.class}
)
public class SpConnectionDTOConverter
	implements DTOConverter<SamlIdpSpConnection, SpConnection> {

	@Override
	public String getContentType() {
		return SpConnection.class.getSimpleName();
	}

	@Override
	public SpConnection toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		SamlIdpSpConnection samlIdpSpConnection =
			_samlIdpSpConnectionLocalService.getSamlIdpSpConnection(
				(long)dtoConverterContext.getId());

		return new SpConnection() {
			{
				enabled = samlIdpSpConnection.isEnabled();
			}
		};
	}

	@Reference
	private SamlIdpSpConnectionLocalService _samlIdpSpConnectionLocalService;

}