/*
 * [y] hybris Platform
 * 
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information of SAP 
 * Hybris ("Confidential Information"). You shall not disclose such 
 * Confidential Information and shall use it only in accordance with the 
 * terms of the license agreement you entered into with SAP Hybris.
 */
package org.storefrontlibraryfacades.service.impl;

import de.hybris.platform.basecommerce.strategies.ActivateBaseSiteInSessionStrategy;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.SearchRestrictionModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cronjob.model.PreparePromotionPageCronJobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.site.BaseSiteService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.storefrontlibraryfacades.Impl.StoreFrontLibSolrProductSearchFacade;
import org.storefrontlibraryfacades.model.PromotionPageModel;
import org.storefrontlibraryfacades.model.SearchQueriesModel;

import com.sap.security.core.server.csi.XSSEncoder;


/**
 *
 */
public class PreparePromotionPageJob extends AbstractJobPerformable<PreparePromotionPageCronJobModel>
{
	private static final Integer INITIAL_MEDIA_COUNT = Integer.valueOf(0);
	private static final String FACET_SEPARATOR = ":";
	private static final Logger LOG = Logger.getLogger(PreparePromotionPageJob.class.getName());

	private CatalogVersionService catalogVersionService;
	private StoreFrontLibSolrProductSearchFacade productSearchFacade;
	private ProductService productService;
	private BaseSiteService baseSiteService;
	private ActivateBaseSiteInSessionStrategy activateBaseSiteInSessionStrategy;
	private SearchRestrictionService searchRestrictionService;
	private TypeService typeService;
	/**
	 * @return the typeService
	 */
	public TypeService getTypeService()
	{
		return typeService;
	}

	/**
	 * @param typeService the typeService to set
	 */
	public void setTypeService(TypeService typeService)
	{
		this.typeService = typeService;
	}

	/**
	 * @return the searchRestrictionService
	 */
	public SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	/**
	 * @param searchRestrictionService
	 *           the searchRestrictionService to set
	 */
	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}

	/**
	 * @return the activateBaseSiteInSessionStrategy
	 */
	ActivateBaseSiteInSessionStrategy getActivateBaseSiteInSessionStrategy()
	{
		return activateBaseSiteInSessionStrategy;
	}

	/**
	 * @param activateBaseSiteInSessionStrategy
	 *           the activateBaseSiteInSessionStrategy to set
	 */
	public void setActivateBaseSiteInSessionStrategy(final ActivateBaseSiteInSessionStrategy activateBaseSiteInSessionStrategy)
	{
		this.activateBaseSiteInSessionStrategy = activateBaseSiteInSessionStrategy;
	}

	/**
	 * @return the baseSiteService
	 */
	BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * @param baseSiteService
	 *           the baseSiteService to set
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * @return the modelService
	 */
	public ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the productSearchFacade
	 */
	public StoreFrontLibSolrProductSearchFacade getProductSearchFacade()
	{
		return productSearchFacade;
	}

	/**
	 * @param productSearchFacade
	 *           the productSearchFacade to set
	 */
	public void setProductSearchFacade(final StoreFrontLibSolrProductSearchFacade productSearchFacade)
	{
		this.productSearchFacade = productSearchFacade;
	}
	@Override
	public PerformResult perform(final PreparePromotionPageCronJobModel cronJob)
	{
		if (cronJob == null)
		{
			LOG.warn("Provided MoveMediaCronJobModel is null");
			return null;
			//new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
		}
		final PageableData pageableData = createPageableData(0, 100, null);
		getBaseSiteService().setCurrentBaseSite(cronJob.getSite(), false);
		getActivateBaseSiteInSessionStrategy().activate(cronJob.getSite());
		getSearchRestrictionService().enableSearchRestrictions();
		sessionService.setAttribute("disableRestrictionGroupInheritance",false);
		//		LOG.debug("Base site " + cronJob.getSite() + " activated.");
		//		sessionService.setAttribute("currentSite", cronJob.getSite());
		//		getCatalogVersionService().addSessionCatalogVersion(cronJob.getContentCatalogVersion());
		//		getCatalogVersionService().addSessionCatalogVersion(cronJob.getProductCatalogVersion());
		ProductSearchPageData<SearchStateData, ProductData> searchPageData = null;
		try
		{
			final List<PromotionPageModel> promotionPages = getAllPromotionPages(cronJob.getContentCatalogVersion());

			for (final PromotionPageModel promotionPage : promotionPages)
			{
				LOG.info("Processing Promotion Page..." + promotionPage.getUid());
				for (final SearchQueriesModel searchQuery : promotionPage.getSearchQueries())
				{
					LOG.info("Searching for Solr Query:" + searchQuery.getSearchQuery());
					LOG.info("Searching for Category:" + searchQuery.getCategoryCode());

					final SearchStateData searchState = new SearchStateData();
					final SearchQueryData searchQueryData = new SearchQueryData();
					if (StringUtils.isNotEmpty(searchQuery.getSearchQuery()))
					{
						searchQueryData.setValue(searchQuery.getSearchQuery());
					}
					searchState.setQuery(searchQueryData);

					if (StringUtils.isNotBlank(searchQuery.getCategoryCode()))
					{
						LOG.info("Performing Category Search...");


						searchPageData = encodeSearchPageData(getProductSearchFacade().categorySearch(searchQuery.getCategoryCode(),
								searchState, pageableData));
					}
					else if (StringUtils.isNotBlank(searchQuery.getSearchQuery()))
					{
						LOG.info("Performing Free Text Search...");
						searchPageData = encodeSearchPageData(getProductSearchFacade().textSearch(searchState, pageableData));
					}
					final Collection<ProductModel> productlist = new ArrayList<ProductModel>();
					for (final ProductData product : searchPageData.getResults())
					{
						final ProductModel productmodel = getProductService().getProductForCode(cronJob.getProductCatalogVersion(),
								product.getCode());
						if (productmodel != null)
						{
							LOG.info("Adding Products..." + productmodel.getCode());
							productlist.add(productmodel);
						}
						promotionPage.setProducts(productlist);
					}
				}
				LOG.info("Saving Promotion Page..." + promotionPage.getUid());
				modelService.save(promotionPage);
			}
		}
		catch (final ConversionException e) // NOSONAR
		{
			LOG.error(e);
		}
		finally
		{
			getSearchRestrictionService().disableSearchRestrictions();
		}
		return null;//new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);		
	}

	protected List<PromotionPageModel> getAllPromotionPages(final CatalogVersionModel catalogVersion)
	{
		final Date today = new Date();
		final StringBuilder QUERY = new StringBuilder("SELECT {" + PromotionPageModel.PK + "} FROM {"
				+ PromotionPageModel._TYPECODE + "}");
		QUERY.append(" Where {catalogVersion}=?catalogVersion");
		try
		{
			final String DATE_FORMAT = " And (?currentDate BETWEEN {pageValidityFrom} AND {pageValidityTo}) ";
			QUERY.append(DATE_FORMAT);

			final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(QUERY.toString());
			searchQuery.addQueryParameter("currentDate", today);
			searchQuery.addQueryParameter("catalogVersion", catalogVersion);
			final SearchResult<PromotionPageModel> processes = flexibleSearchService.search(searchQuery);
			return processes.getResult();
		}
		catch (final Exception e)
		{
			LOG.error(e);
		}
		try
		{

			final String DATE_FORMAT = " And (to_timestamp(?currentDate, 'yyyy-mm-dd hh24:mi:ss') BETWEEN {pageValidityFrom} AND {pageValidityTo}) ";
			QUERY.append(DATE_FORMAT);
			final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(QUERY.toString());
			searchQuery.addQueryParameter("currentDate", today);
			searchQuery.addQueryParameter("catalogVersion", catalogVersion);
			final SearchResult<PromotionPageModel> processes = flexibleSearchService.search(searchQuery);
			return processes.getResult();
		}
		catch (final Exception e)
		{
			LOG.error(e);
		}
		return null;
	}

	protected PageableData createPageableData(final int pageNumber, final int pageSize, final String sortCode)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(pageNumber);
		pageableData.setSort(sortCode);

		pageableData.setPageSize(100);
		return pageableData;
	}

	protected ProductSearchPageData<SearchStateData, ProductData> encodeSearchPageData(
			final ProductSearchPageData<SearchStateData, ProductData> searchPageData)
	{
		final SearchStateData currentQuery = searchPageData.getCurrentQuery();

		if (currentQuery != null)
		{
			try
			{
				final SearchQueryData query = currentQuery.getQuery();
				final String encodedQueryValue = XSSEncoder.encodeHTML(query.getValue());
				query.setValue(encodedQueryValue);
				currentQuery.setQuery(query);
				searchPageData.setCurrentQuery(currentQuery);
				searchPageData.setFreeTextSearch(XSSEncoder.encodeHTML(searchPageData.getFreeTextSearch()));

				final List<FacetData<SearchStateData>> facets = searchPageData.getFacets();
				if (CollectionUtils.isNotEmpty(facets))
				{
					processFacetData(facets);
				}
			}
			catch (final UnsupportedEncodingException e)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Error occured during Encoding the Search Page data values", e);
				}
			}
		}
		return searchPageData;
	}

	protected void processFacetData(final List<FacetData<SearchStateData>> facets) throws UnsupportedEncodingException
	{
		for (final FacetData<SearchStateData> facetData : facets)
		{
			final List<FacetValueData<SearchStateData>> topFacetValueDatas = facetData.getTopValues();
			if (CollectionUtils.isNotEmpty(topFacetValueDatas))
			{
				processFacetDatas(topFacetValueDatas);
			}
			final List<FacetValueData<SearchStateData>> facetValueDatas = facetData.getValues();
			if (CollectionUtils.isNotEmpty(facetValueDatas))
			{
				processFacetDatas(facetValueDatas);
			}
		}
	}

	protected void processFacetDatas(final List<FacetValueData<SearchStateData>> facetValueDatas)
			throws UnsupportedEncodingException
	{
		for (final FacetValueData<SearchStateData> facetValueData : facetValueDatas)
		{
			final SearchStateData facetQuery = facetValueData.getQuery();
			final SearchQueryData queryData = facetQuery.getQuery();
			final String queryValue = queryData.getValue();
			if (StringUtils.isNotBlank(queryValue))
			{
				final String[] queryValues = queryValue.split(FACET_SEPARATOR);
				final StringBuilder queryValueBuilder = new StringBuilder();
				queryValueBuilder.append(XSSEncoder.encodeHTML(queryValues[0]));
				for (int i = 1; i < queryValues.length; i++)
				{
					queryValueBuilder.append(FACET_SEPARATOR).append(queryValues[i]);
				}
				queryData.setValue(queryValueBuilder.toString());
			}
		}
	}

	@Override
	public boolean isAbortable()
	{
		return true;
	}

	/**
	 * @return the catalogVersionService
	 */
	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	/**
	 * @param catalogVersionService
	 *           the catalogVersionService to set
	 */
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
}
