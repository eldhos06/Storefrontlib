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
package org.storefrontlibraryfacades.Impl;

import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.impl.DefaultProductFacade;

import java.util.Collection;


/**
 * Default implementation of {@link ProductFacade}.
 * 
 * @param <REF_TARGET>
 *           generic type parameter for the product model type
 */
public class StoreFrontLibProductFacade<REF_TARGET> extends DefaultProductFacade<REF_TARGET>
{
	@Override
	public ProductData getProductForCodeAndOptions(final String code, final Collection<ProductOption> options)
	{
		return super.getProductForCodeAndOptions(code, options);
	}
}
