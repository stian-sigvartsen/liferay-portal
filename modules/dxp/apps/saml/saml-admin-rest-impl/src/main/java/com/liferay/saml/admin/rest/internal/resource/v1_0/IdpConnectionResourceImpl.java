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

package com.liferay.saml.admin.rest.internal.resource.v1_0;

import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.TransformUtil;
import com.liferay.saml.admin.rest.dto.v1_0.IdpConnection;
import com.liferay.saml.admin.rest.resource.v1_0.IdpConnectionResource;

import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlSpAuthRequestLocalService;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import java.util.List;

/**
 * @author Stian Sigvartsen
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/idp-connection.properties",
	scope = ServiceScope.PROTOTYPE, service = IdpConnectionResource.class
)
public class IdpConnectionResourceImpl extends BaseIdpConnectionResourceImpl {

	@Override
	public IdpConnection getIdpConnection(
		Long idpConnectionId) throws Exception {

		SamlSpIdpConnection samlSpIdpConnection =
			_samlSpIdpConnectionLocalService.getSamlSpIdpConnection(
				idpConnectionId);

		return _convert(samlSpIdpConnection);
	}

	private IdpConnection _convert(SamlSpIdpConnection samlSpIdpConnection) {
		return new IdpConnection() {
			{
				enabled = samlSpIdpConnection.getEnabled();
				entityId = samlSpIdpConnection.getSamlIdpEntityId();
				id = samlSpIdpConnection.getSamlSpIdpConnectionId();
				metadataUrl = samlSpIdpConnection.getMetadataUrl();
				name = samlSpIdpConnection.getName();
				nameIdFormat = samlSpIdpConnection.getNameIdFormat();
				assertionSignatureRequired = samlSpIdpConnection.getAssertionSignatureRequired();
				clockSkew = samlSpIdpConnection.getClockSkew();
				forceAuthn = samlSpIdpConnection.getForceAuthn();
				samlSpIdpConnection.getMetadataUpdatedDate();
				signAuthnRequest = samlSpIdpConnection.getSignAuthnRequest();
				unknownUsersAreStrangers = samlSpIdpConnection.getUnknownUsersAreStrangers();
				userAttributeMappings = samlSpIdpConnection.getUserAttributeMappings();
			}
		};
	}

	@Override
	public Page<IdpConnection> getIdpConnections(Pagination pagination)
		throws Exception {

		List<SamlSpIdpConnection> samlSpIdpConnections =
			_samlSpIdpConnectionLocalService.getSamlSpIdpConnections(
				contextCompany.getCompanyId(), pagination.getStartPosition(),
				pagination.getEndPosition());

		return Page.of(
			TransformUtil.transform(samlSpIdpConnections, this::_convert),
			pagination, samlSpIdpConnections.size());
	}

	@Reference
	private SamlSpIdpConnectionLocalService _samlSpIdpConnectionLocalService;
}