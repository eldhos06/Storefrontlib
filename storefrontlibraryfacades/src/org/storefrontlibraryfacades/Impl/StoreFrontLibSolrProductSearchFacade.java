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

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.search.solrfacetsearch.impl.DefaultSolrProductSearchFacade;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.threadcontext.ThreadContextService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;


/**
 * Solr implementation of {@link ProductSearchFacade}.
 * 
 * @param <ITEM>
 *           type extending {@link ProductData}
 */
public class StoreFrontLibSolrProductSearchFacade<ITEM extends ProductData> extends DefaultSolrProductSearchFacade<ITEM>
{
	public ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData> promotionPageSearch(final String promotionPageid,
			final SearchStateData searchState, final PageableData pageableData)
	{
		Assert.notNull(searchState, "SearchStateData must not be null.");

		return getThreadContextService()
				.executeInContext(
						new ThreadContextService.Executor<ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData>, ThreadContextService.Nothing>()
						{
							@Override
							public ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData> execute()
							{
								return getProductCategorySearchPageConverter().convert(
										getProductSearchService().searchAgain(decodeCollectionState(searchState, promotionPageid), pageableData));
							}
						});
	}

	protected SolrSearchQueryData decodeCollectionState(final SearchStateData searchState, final String collectionId)
	{
		SolrSearchQueryData searchQueryData;
		if (searchState != null)
		{
			searchQueryData = getSearchQueryDecoder().convert(searchState.getQuery());
		}
		else
		{
			searchQueryData = new SolrSearchQueryData();
			searchQueryData.setFilterTerms(Collections.<SolrSearchQueryTermData> emptyList());
		}
		if (collectionId != null)
		{
			setCollectionFilter(searchQueryData,collectionId);
		}

		return searchQueryData;
	}
	protected final void setCollectionFilter(final SolrSearchQueryData searchQueryData, final String promotionPageid)
	{
		boolean collectionExist;
		List<SolrSearchQueryTermData> solrSearchQueryDatalist;
		solrSearchQueryDatalist = searchQueryData.getFilterTerms();
		SolrSearchQueryTermData solrSearchQueryTermData;
		collectionExist=false;
		searchQueryData.setPromotionPageIds(promotionPageid);
		if(CollectionUtils.isNotEmpty(solrSearchQueryDatalist))
		{
   		for( SolrSearchQueryTermData SolrSearchQueryTermData: solrSearchQueryDatalist)
   		{
   			if(SolrSearchQueryTermData.getKey().equals("promotionPageid") && SolrSearchQueryTermData.getKey().equals(promotionPageid))
   			{
   				collectionExist=true;
   				break;
   			}
   		}
		}else
		{
			solrSearchQueryDatalist=new ArrayList<SolrSearchQueryTermData>(); 
		}
		if(!collectionExist)
		{	
				solrSearchQueryTermData = new SolrSearchQueryTermData();
				solrSearchQueryTermData.setKey("promotionPageid");
				solrSearchQueryTermData.setValue(promotionPageid);
				solrSearchQueryDatalist.add(solrSearchQueryTermData);
				searchQueryData.setFilterTerms(solrSearchQueryDatalist);
		}
	}
}
