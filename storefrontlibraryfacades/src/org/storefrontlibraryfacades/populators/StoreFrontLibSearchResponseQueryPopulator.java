
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

import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.IndexedPropertyValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseQueryPopulator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;

import java.util.ArrayList;
import java.util.List;


/**
 */
public class StoreFrontLibSearchResponseQueryPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, SEARCH_QUERY_TYPE, SEARCH_RESULT_TYPE, ITEM> extends SearchResponseQueryPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, SEARCH_QUERY_TYPE, SEARCH_RESULT_TYPE, ITEM>
{
	@Override
	protected SolrSearchQueryData buildSearchQueryData(
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, IndexedProperty, SEARCH_QUERY_TYPE, IndexedTypeSort, SEARCH_RESULT_TYPE> source)
	{
		SolrSearchQueryData solrSearchQueryData= buildSearchQueryData(source.getRequest().getSearchText(), source.getRequest().getSearchQueryData().getCategoryCode(),
				source.getRequest().getCurrentSort(), source.getRequest().getIndexedPropertyValues(),
				source.getRequest().getSearchQueryData().getSearchQueryContext());
		solrSearchQueryData.setPromotionPageIds(source.getRequest().getSearchQueryData().getPromotionPageIds());
		return solrSearchQueryData;
	}
}
