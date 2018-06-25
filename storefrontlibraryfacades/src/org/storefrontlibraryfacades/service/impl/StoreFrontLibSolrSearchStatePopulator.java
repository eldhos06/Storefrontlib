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
package org.storefrontlibraryfacades.service.impl;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.search.solrfacetsearch.converters.populator.SolrSearchStatePopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.storefrontlibraryfacades.model.PromotionPageModel;


/**
 */
public class StoreFrontLibSolrSearchStatePopulator extends SolrSearchStatePopulator
{
	private static final Logger LOG = Logger.getLogger(StoreFrontLibSolrSearchStatePopulator.class);

	private UrlResolver<PromotionPageModel> promotionModelUrlResolver;
	private CMSPageService cmsPageService;
	/**
	 * @return the cmsPageService
	 */
	public CMSPageService getCmsPageService()
	{
		return cmsPageService;
	}

	/**
	 * @param cmsPageService the cmsPageService to set
	 */
	public void setCmsPageService(CMSPageService cmsPageService)
	{
		this.cmsPageService = cmsPageService;
	}

	public UrlResolver<PromotionPageModel> getPromotionModelUrlResolver()
	{
		return promotionModelUrlResolver;
	}

	public void setPromotionModelUrlResolver(UrlResolver<PromotionPageModel> promotionModelUrlResolver)
	{
		this.promotionModelUrlResolver = promotionModelUrlResolver;
	}

	@Override
	public void populate(final SolrSearchQueryData source, final SearchStateData target)
	{
		target.setQuery(getSearchQueryConverter().convert(source));

		if (source.getCategoryCode() != null)
		{
			populateCategorySearchUrl(source, target);
		}
		else if (source.getPromotionPageIds() != null){
			populatePromotionUrl(source, target);
		}
		else
		{
			populateFreeTextSearchUrl(source, target);
		}
	}

	protected void populatePromotionUrl(final SolrSearchQueryData source, final SearchStateData target)
	{
		target.setUrl(getPromotionUrl(source) + buildUrlQueryString(source, target));
	}

	protected String getPromotionUrl(final SolrSearchQueryData source)
	{
		PromotionPageModel page=null;
		try
		{
			page = (PromotionPageModel) getCmsPageService().getPageById(source.getPromotionPageIds());
		}
		catch (CMSItemNotFoundException e)
		{
			LOG.error(e);
		}
		return getPromotionModelUrlResolver().resolve(page);
	}
}
