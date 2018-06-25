/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package org.storefrontlibraryfacades.setup;

import static org.storefrontlibraryfacades.constants.StorefrontlibraryfacadesConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import org.storefrontlibraryfacades.constants.StorefrontlibraryfacadesConstants;
import org.storefrontlibraryfacades.service.StorefrontlibraryfacadesService;


@SystemSetup(extension = StorefrontlibraryfacadesConstants.EXTENSIONNAME)
public class StorefrontlibraryfacadesSystemSetup
{
	private final StorefrontlibraryfacadesService storefrontlibraryfacadesService;

	public StorefrontlibraryfacadesSystemSetup(final StorefrontlibraryfacadesService storefrontlibraryfacadesService)
	{
		this.storefrontlibraryfacadesService = storefrontlibraryfacadesService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		storefrontlibraryfacadesService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return StorefrontlibraryfacadesSystemSetup.class.getResourceAsStream("/storefrontlibraryfacades/sap-hybris-platform.png");
	}
}
