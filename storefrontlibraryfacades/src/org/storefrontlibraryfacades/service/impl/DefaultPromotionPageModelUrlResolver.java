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

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;
import de.hybris.platform.site.BaseSiteService;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.storefrontlibraryfacades.model.PromotionPageModel;


/**
 * URL resolver for CategoryModel instances. The pattern could be of the form: /{category-path}/c/{category-code}
 */
public class DefaultPromotionPageModelUrlResolver extends AbstractUrlResolver<PromotionPageModel>
{
	private final String CACHE_KEY = DefaultPromotionPageModelUrlResolver.class.getName();

	private BaseSiteService baseSiteService;
	private String pattern;

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected String getPattern()
	{
		return pattern;
	}

	@Required
	public void setPattern(final String pattern)
	{
		this.pattern = pattern;
	}

	@Override
	protected String getKey(final PromotionPageModel source)
	{
		return CACHE_KEY + "." + source.getPk().toString();
	}

	@Override
	protected String resolveInternal(final PromotionPageModel source)
	{
		// Work out values

		// Replace pattern values
		String url = getPattern();
		if (url.contains("{baseSite-uid}"))
		{
			url = url.replace("{baseSite-uid}", urlEncode(getBaseSiteUid().toString()));
		}
		if (url.contains("{promotion-path}"))
		{
			final String categoryPath = buildPathString(source.getName());
			url = url.replace("{promotion-path}", categoryPath);
		}
		if (url.contains("{promotion-code}"))
		{
			final String uid = urlEncode(source.getUid()).replaceAll("\\+", "%20");
			url = url.replace("{promotion-code}", uid);
		}
				return url;

	}

	protected CharSequence getBaseSiteUid()
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
		if (currentBaseSite == null)
		{
			return "{baseSiteUid}";
		}
		else
		{
			return currentBaseSite.getUid();
		}
	}

	protected String buildPathString(final String name)
	{
		String url;
		url = name.replaceAll("[^\\w/-]", "_");
		//TISSTRT-1297
		if (url.contains("--"))
		{
			url = url.replaceAll("--", "-");
		}
		return url;
	}
}
