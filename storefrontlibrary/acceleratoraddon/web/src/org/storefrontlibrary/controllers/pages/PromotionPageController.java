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

import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.SearchBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController.SearchResultsData;
import de.hybris.platform.acceleratorstorefrontcommons.util.MetaSanitizerUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetRefinement;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.GenericSearchConstants.LOG;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.storefrontlibraryfacades.Impl.StoreFrontLibSolrProductSearchFacade;
import org.storefrontlibraryfacades.model.PromotionPageModel;

import com.sap.security.core.server.csi.XSSEncoder;



@Controller
@RequestMapping(value = "/**/cc")
public class PromotionPageController extends AbstractPromotionPageController
{
	private static final String SEARCH_META_DESCRIPTION_ON = "search.meta.description.on";
	private static final String SEARCH_META_DESCRIPTION_RESULTS = "search.meta.description.results";

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(PromotionPageController.class);

	private static final String PATH_VARIABLE_PATTERN = "{promotionPageid:.*}";
	private static final String FACET_SEPARATOR = ":";

	private static final String NO_RESULTS_CMS_PAGE_ID = "promotionEmptyResultPage";

	@Resource(name = "storeFrontLibProductSearchFacade")
	private StoreFrontLibSolrProductSearchFacade<ProductData> productSearchFacade;

	@Resource(name = "customerLocationService")
	private CustomerLocationService customerLocationService;

	@Resource(name = "cmsComponentService")
	private CMSComponentService cmsComponentService;

	@Resource(name = "cmsPageService")
	private CMSPageService cmsPageService;

	@RequestMapping(value = PATH_VARIABLE_PATTERN, method = RequestMethod.GET, params = "!q")
	public String textSearch(@PathVariable("promotionPageid") final String promotionPageid,
			@RequestParam(value = "text", defaultValue = "") final String searchText, final HttpServletRequest request,
			final Model model) throws CMSItemNotFoundException
	{
		final String searchQry = "";
		String pageId;
		PromotionPageModel page=null;
		if (StringUtils.isNotBlank(promotionPageid))
		{
			final PageableData pageableData = createPageableData(0, getSearchPageSize(), null, ShowMode.Page);

			final SearchStateData searchState = new SearchStateData();
			final SearchQueryData searchQueryData = new SearchQueryData();
			searchState.setQuery(searchQueryData);

			ProductSearchPageData<SearchStateData, ProductData> searchPageData = null;
			try
			{
				LOG.info("Searching for Solr Query");
				searchPageData = encodeSearchPageData(productSearchFacade.promotionPageSearch(promotionPageid, searchState,
						pageableData));
			}
			catch (final ConversionException e) // NOSONAR
			{
				LOG.error(e);
			}
			if (searchPageData == null)
			{
				page = getPage(NO_RESULTS_CMS_PAGE_ID);
				LOG.info("Search result Null");
			}
			else if (searchPageData.getKeywordRedirectUrl() != null)
			{
				LOG.info("Key Word redirecting to :" + searchPageData.getKeywordRedirectUrl());
				return "redirect:" + searchPageData.getKeywordRedirectUrl();
			}
			else if (searchPageData.getPagination().getTotalNumberOfResults() == 0)
			{
				LOG.info("Zero result in search Result.");
				page = getPage(NO_RESULTS_CMS_PAGE_ID);
				model.addAttribute("searchPageData", searchPageData);
			}
			else
			{
				LOG.info("Valid result for search query");
				storeContinueUrl(request);
				populateModel(model, searchPageData, ShowMode.Page);
				page = getPage(promotionPageid);

			}
			model.addAttribute("userLocation", customerLocationService.getUserLocation());
			if (searchPageData != null && page!=null)
			{
				updateBreadcrumbs(page, searchPageData, model);
			}
		}
		else
		{
			LOG.info("Collection Id Seems empty.");
			pageId = NO_RESULTS_CMS_PAGE_ID;
		}
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_FOLLOW);
		final String metaDescription = MetaSanitizerUtil
				.sanitizeDescription(getMessageSource().getMessage(SEARCH_META_DESCRIPTION_RESULTS, null,
						SEARCH_META_DESCRIPTION_RESULTS, getI18nService().getCurrentLocale()) + " " + searchText + " "
						+ getMessageSource().getMessage(SEARCH_META_DESCRIPTION_ON, null, SEARCH_META_DESCRIPTION_ON,
								getI18nService().getCurrentLocale())
						+ " " + getSiteName());
		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(searchQry);
		updatePageTitle(page, model);
		setUpMetaData(model, metaKeywords, metaDescription);
		storeCmsPageInModel(model, page);
		return getViewForPage(page);
	}

	@RequestMapping(value = PATH_VARIABLE_PATTERN, method = RequestMethod.GET, params = "q")
	public String refineSearch(@RequestParam("q") final String searchQuery,
			@PathVariable("promotionPageid") final String promotionPageid,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode,
			@RequestParam(value = "text", required = false) final String searchText, final HttpServletRequest request,
			final Model model) throws CMSItemNotFoundException
	{
		String pageId;
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = performSearch(searchQuery, page, showMode,
				sortCode, getSearchPageSize(), promotionPageid);

		populateModel(model, searchPageData, showMode);
		model.addAttribute("userLocation", customerLocationService.getUserLocation());

		if (searchPageData.getPagination().getTotalNumberOfResults() == 0)
		{
			pageId = NO_RESULTS_CMS_PAGE_ID;
		}
		else
		{
			storeContinueUrl(request);
			pageId = promotionPageid;
		}
		PromotionPageModel cmsPage = getPage(pageId);
		updateBreadcrumbs(cmsPage, searchPageData, model);
		final String metaDescription = MetaSanitizerUtil
				.sanitizeDescription(getMessageSource().getMessage(SEARCH_META_DESCRIPTION_RESULTS, null,
						SEARCH_META_DESCRIPTION_RESULTS, getI18nService().getCurrentLocale()) + " " + searchText + " "
						+ getMessageSource().getMessage(SEARCH_META_DESCRIPTION_ON, null, SEARCH_META_DESCRIPTION_ON,
								getI18nService().getCurrentLocale())
						+ " " + getSiteName());

		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(searchText);
		setUpMetaData(model, metaKeywords, metaDescription);
		
		updatePageTitle(cmsPage, model);
		setUpMetaData(model, metaKeywords, metaDescription);
		storeCmsPageInModel(model, cmsPage);
		return getViewForPage(cmsPage);
	}

	protected ProductSearchPageData<SearchStateData, ProductData> performSearch(final String searchQuery, final int page,
			final ShowMode showMode, final String sortCode, final int pageSize, final String promotionPageid)
	{
		final PageableData pageableData = createPageableData(page, pageSize, sortCode, showMode);

		final SearchStateData searchState = new SearchStateData();
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(searchQuery);
		searchState.setQuery(searchQueryData);

		return encodeSearchPageData(productSearchFacade.promotionPageSearch(promotionPageid, searchState, pageableData));
	}

	@ResponseBody
	@RequestMapping(value = "/results", method = RequestMethod.GET)
	public SearchResultsData<ProductData> jsonSearchResults(@RequestParam("q") final String searchQuery,@PathVariable("promotionPageid") final String promotionPageid,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode) throws CMSItemNotFoundException
	{
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = performSearch(searchQuery, page, showMode,
				sortCode, getSearchPageSize(),promotionPageid);
		final SearchResultsData<ProductData> searchResultsData = new SearchResultsData<>();
		searchResultsData.setResults(searchPageData.getResults());
		searchResultsData.setPagination(searchPageData.getPagination());
		return searchResultsData;
	}

	@ResponseBody
	@RequestMapping(value = "/facets", method = RequestMethod.GET)
	public FacetRefinement<SearchStateData> getFacets(@RequestParam("q") final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode) throws CMSItemNotFoundException
	{
		final SearchStateData searchState = new SearchStateData();
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(searchQuery);
		searchState.setQuery(searchQueryData);

		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = productSearchFacade.textSearch(searchState,
				createPageableData(page, getSearchPageSize(), sortCode, showMode));
		final List<FacetData<SearchStateData>> facets = refineFacets(searchPageData.getFacets(),
				convertBreadcrumbsToFacets(searchPageData.getBreadcrumbs()));
		final FacetRefinement<SearchStateData> refinement = new FacetRefinement<>();
		refinement.setFacets(facets);
		refinement.setCount(searchPageData.getPagination().getTotalNumberOfResults());
		refinement.setBreadcrumbs(searchPageData.getBreadcrumbs());
		return refinement;
	}

	protected <E> List<E> subList(final List<E> list, final int maxElements)
	{
		if (CollectionUtils.isEmpty(list))
		{
			return Collections.emptyList();
		}

		if (list.size() > maxElements)
		{
			return list.subList(0, maxElements);
		}

		return list;
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
   private PromotionPageModel getPage(String pageId) throws CMSItemNotFoundException
   {
   	PromotionPageModel page;
   	Date today=new Date();
		try
		{
			page = (PromotionPageModel) cmsPageService.getPageById(pageId);
			if(!NO_RESULTS_CMS_PAGE_ID.equalsIgnoreCase(pageId))
			{
				if(!(today.equals(page.getPageValidityTo()) ||today.before(page.getPageValidityTo())) || !(today.after(page.getPageValidityFrom()) || today.equals(page.getPageValidityFrom())))
				{
					page = (PromotionPageModel) cmsPageService.getPageById(NO_RESULTS_CMS_PAGE_ID);
				}
			}
		}
		catch (CMSItemNotFoundException e)
		{
   		LOG.error(e);
   		throw e;
		}
		return page;
   }
}
