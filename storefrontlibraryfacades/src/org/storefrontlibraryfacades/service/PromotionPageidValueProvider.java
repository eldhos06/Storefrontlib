/**
 *
 */
package org.storefrontlibraryfacades.service;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.storefrontlibraryfacades.model.PromotionPageModel;


/**
 * @author TCS
 * 
 */
//Index collectionId for a PcmProductVariantModel
public class PromotionPageidValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider, Serializable
{
	private FieldNameProvider fieldNameProvider;

	/**
	 * @return Collection<fieldValues>
	 * @param indexConfig
	 *           ,indexedProperty,model
	 * @description: It returns the collectionId of a specific variant product
	 * 
	 */
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		final ProductModel productModel = (ProductModel) model;
		if (productModel == null)
		{
			return Collections.emptyList();
		}
		//	final List<RichAttributeModel> richAttributeModel = (List<RichAttributeModel>) productModel.getRichAttribute();

		final List<PromotionPageModel> promotionPages = (List<PromotionPageModel>) productModel.getPromotionPage();
		final List<String> promotionPageids = new ArrayList<String>();
		//if (shopByLookModels != null && shopByLookModels.size() > 0)
		if (CollectionUtils.isNotEmpty(promotionPages))
		{
			//Fetch occasion Id in all the Variants
			for (final PromotionPageModel promotionPage : promotionPages)
			{

				if (promotionPage.getUid() != null)
				{
					promotionPageids.add(promotionPage.getUid());
				}
			}
		}
		final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();

		{
			//add field values
			fieldValues.addAll(createFieldValue(promotionPageids, indexedProperty));
		}
		//return the field values
		return fieldValues;


	}

	/**
	 * @return fieldValues
	 * @param collectionIds
	 *           ,indexedProperty
	 * @description: It create collectionIds field with adding index property
	 * 
	 */
	protected List<FieldValue> createFieldValue(final List<String> collectionIds, final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, null);
		for (final String fieldName : fieldNames)
		{
			fieldValues.add(new FieldValue(fieldName, collectionIds));
		}
		return fieldValues;

	}

	@Required
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}
}
