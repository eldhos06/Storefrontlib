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
package org.storefrontlibraryfacades.populators;

import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.IndexedPropertyValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseBreadcrumbsPopulator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.Registry;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FacetDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FacetValueDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 */
public class StoreFrontLibSearchResponseBreadcrumbsPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM>
extends SearchResponseBreadcrumbsPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM>
{
	protected SolrSearchQueryData cloneSearchQueryData(final SolrSearchQueryData source)
	{
		final SolrSearchQueryData target = createSearchQueryData();
		target.setFreeTextSearch(source.getFreeTextSearch());
		target.setCategoryCode(source.getCategoryCode());
		target.setPromotionPageIds(source.getPromotionPageIds());
		target.setSort(source.getSort());
		target.setFilterTerms(source.getFilterTerms());
		target.setFilterQueries(source.getFilterQueries());
		return target;
	}
}
