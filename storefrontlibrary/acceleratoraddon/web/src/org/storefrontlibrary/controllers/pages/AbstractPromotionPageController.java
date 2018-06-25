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
package org.storefrontlibrary.controllers.pages;


import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.SearchBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.ui.Model;


/**
 */
public abstract class AbstractPromotionPageController extends AbstractSearchPageController
{
	@Resource(name = "searchBreadcrumbBuilder")
	private SearchBreadcrumbBuilder searchBreadcrumbBuilder;

	protected void updatePageTitle(final AbstractPageModel page, final Model model)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(page.getTitle()));
		model.addAttribute("pageHeading", page.getTitle());
	}

	protected void updateBreadcrumbs(final AbstractPageModel page,
			ProductSearchPageData<SearchStateData, ProductData> searchPageData, final Model model)
	{
		model.addAttribute(
				WebConstants.BREADCRUMBS_KEY,
				searchBreadcrumbBuilder.getBreadcrumbs(null, page.getTitle(),
						CollectionUtils.isEmpty(searchPageData.getBreadcrumbs())));
	}
}
