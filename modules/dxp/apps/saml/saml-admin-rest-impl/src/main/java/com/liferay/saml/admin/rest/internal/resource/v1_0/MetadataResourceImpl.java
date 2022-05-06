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

import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.saml.admin.rest.dto.v1_0.Metadata;
import com.liferay.saml.admin.rest.resource.v1_0.MetadataResource;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;
import com.liferay.saml.util.MetadataUtil;

import java.nio.charset.StandardCharsets;

import javax.ws.rs.BadRequestException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Stian Sigvartsen
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/metadata.properties",
	scope = ServiceScope.PROTOTYPE, service = MetadataResource.class
)
public class MetadataResourceImpl extends BaseMetadataResourceImpl {

	@Override
	public Metadata postIdpConnectionMetadata(
			Long idpConnectionId, MultipartBody multipartBody)
		throws Exception {

		BinaryFile binaryFile = multipartBody.getBinaryFile("file");

		if (binaryFile == null) {
			throw new BadRequestException("No file found in body");
		}

		SamlSpIdpConnection samlSpIdpConnection =
			_samlSpIdpConnectionLocalService.getSamlSpIdpConnection(
				idpConnectionId);

		samlSpIdpConnection.setMetadataXml(
			_metadataUtil.parseMetadataXml(
				binaryFile.getInputStream(),
				samlSpIdpConnection.getSamlIdpEntityId()));

		return _convert(
			_samlSpIdpConnectionLocalService.updateSamlSpIdpConnection(
				samlSpIdpConnection));
	}

	@Override
	public Metadata getIdpConnectionMetadata(Long idpConnectionId)
		throws Exception {

		SamlSpIdpConnection samlSpIdpConnection =
			_samlSpIdpConnectionLocalService.getSamlSpIdpConnection(
				idpConnectionId);

		return _convert(samlSpIdpConnection);
	}

	private String _base64Encode(String str) {
		if (str == null) {
			return null;
		}

		return Base64.encode(str.getBytes(StandardCharsets.UTF_8));
	}

	private Metadata _convert(SamlSpIdpConnection samlSpIdpConnection) {
		return new Metadata() {
			{
				contentUrl = samlSpIdpConnection.getMetadataUrl();
				contentValue = _base64Encode(
					samlSpIdpConnection.getMetadataXml());
				metadataUpdatedDate =
					samlSpIdpConnection.getMetadataUpdatedDate();
			}
		};
	}

	@Reference
	private MetadataUtil _metadataUtil;

	@Reference
	private SamlSpIdpConnectionLocalService _samlSpIdpConnectionLocalService;

}