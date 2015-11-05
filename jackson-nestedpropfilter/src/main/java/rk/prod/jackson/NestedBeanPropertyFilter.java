package rk.prod.jackson;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

/**
 * NestedBeanPropertyFilter contains all property filters per class
 * contained in a json root entity class
 * An instance of this class should be cached per entity class and 
 * all filterable nested properties as a key
 * 
 * FYI: A class has a different meaning in the context of Jackson i.e.,
 * a class that has getters and setters and can generate nested attributes
 * for Jackson
 */
public class NestedBeanPropertyFilter extends SimpleBeanPropertyFilter {
	
	private Map<Class<?>, SimpleBeanPropertyFilter> classLevelBeanPropertyFilter
			= new HashMap<Class<?>, SimpleBeanPropertyFilter>();

	/**
	 * Construct filter based on many nested properties as follows
	 * prop1, classAObj.class1Obj.prop2, classAObj.class2Obj, prop3 
	 * @param properties
	 */
	public NestedBeanPropertyFilter(Class<?> clazz, final String ... properties){
		/*classLevelBeanPropertyFilter.put(RecommendedOfferResponseVO.class,
				SimpleBeanPropertyFilter.filterOutAllExcept("offer"));
		classLevelBeanPropertyFilter.put(OfferVO.class,
				SimpleBeanPropertyFilter.filterOutAllExcept(
					"activationDate", "activationLimit", "brandName", "canonicalHierarchy.categoryLevel1", "canonicalHierarchy.categoryLevel2", "canonicalHierarchy.categoryLevel3", "canonicalHierarchy.categoryLevel4", "canonicalHierarchy.categoryLevel5", "clearingHouse.id", "clearingHouse.name", "companyName", "daysLeft", "discountType", "errorCode", "hasExtendedRequirements", "hierarchy.categoryLevel1", "hierarchy.categoryLevel2", "hierarchy.categoryLevel3", "hierarchy.categoryLevel4", "hierarchy.categoryLevel5", "isAutoActivated", "isOfferPinned", "isWeightVolume", "manufacturerCode", "manufacturerId", "offerActiveDate", "offerActiveTimezoneInMills", "offerAssociationCode", "offerCode", "offerConditions.categoryName", "offerConditions.minBasketValue", "offerConditions.minQty", "offerConditions.minQtyDescription", "offerConditions.minTripCount", "offerConditions.offerType", "offerConditions.redemptionFrequency", "offerConditions.redemptionLimit", "offerConditions.shopXtimes", "offerDescription", "offerDisclaimer", "offerExpiryDate", "offerExpiryDateFormatted", "offerFeaturedText", "offerFinePrint", "offerGS1", "offerId", "offerImage.offerImage1", "offerImage.offerImage2", "offerItems.ean", "offerItems.familyCode", "offerItems.gtin", "offerItems.itemCode", "offerItems.price", "offerItems.qty", "offerItems.upc", "offerItems.upc12", "offerRedemptionStartDate", "offerRewards.categoryName", "offerRewards.offerValue", "offerRewards.rewardQuantity", "offerShutoffDate", "offerSource", "offerSourceList.bytes", "offerSourceList.empty", "offerSummary", "offerUPC", "offerUpdateDate", "offerValue", "offeredInZipCodes.bytes", "offeredInZipCodes.empty", "omsCid", "purchaseUnit", "redemptionDate", "requirements.categoryName", "requirements.conditions.purchaseConditions", "requirements.conditions.purchases.amountUnit", "requirements.conditions.purchases.condition", "requirements.conditions.purchases.productIdRef", "requirements.conditions.purchases.unit", "requirements.conditions.purchases.value", "requirements.conditions.purchases.volumeUnit", "requirements.conditions.purchases.weightUnit", "requirements.minBasketValue", "requirements.minTripCount", "requirements.offerType", "requirements.products.ean", "requirements.products.familyCode", "requirements.products.gtin", "requirements.products.id", "requirements.products.upc", "requirements.products.upc12", "requirements.redemptionFrequency", "requirements.redemptionLimit", "requirements.redemptionLimitPerTransaction", "requirements.rewards.discounts.amountUnit", "requirements.rewards.discounts.condition", "requirements.rewards.discounts.limitUnit", "requirements.rewards.discounts.limitValue", "requirements.rewards.discounts.productIdRef", "requirements.rewards.discounts.rewardAppliesToPurchaseConditionRef", "requirements.rewards.discounts.type", "requirements.rewards.discounts.unit", "requirements.rewards.discounts.value", "requirements.rewards.discounts.volumeUnit", "requirements.rewards.discounts.weightUnit", "requirements.rewards.rewardConditions", "rewardUnit", "storeCodes", "targetType", "timeZoneOffSetInMilliSeconds", "visible"));*/
		
		Map<Class<?>, JacksonClassAttribute> classLevelJsonAttribute = JacksonClassAttributeCache.generateClassLevelJsonAttribute(
			clazz, properties
			);
		
		for(Entry<Class<?>, JacksonClassAttribute> entry : classLevelJsonAttribute.entrySet()){
			classLevelBeanPropertyFilter.put(entry.getKey(),
					SimpleBeanPropertyFilter.filterOutAllExcept(entry.getValue().attributes.keySet()));
		}
	}
	
	public SimpleBeanPropertyFilter findPropertyFilter(Class<?> clazz){
		return classLevelBeanPropertyFilter.get(clazz);
	}
	
	// TODO: eventually check what is the jgen.context immediate parent
	// for this class, a class can only have unique property names
	// so there is no chance of collision
	// i.e., two properties one ClassA obj1, ClassA obj2, here obj1, obj2
	// are different names, this can be delayed based on the whether
	// there are multiple propertyFilters for the same class to then filter
	// by the size() of the map > 1
	@Override
	public void serializeAsField(Object pojo, JsonGenerator jgen,
			SerializerProvider provider, PropertyWriter writer)
			throws Exception {
		SimpleBeanPropertyFilter propertyFilter = classLevelBeanPropertyFilter.get(pojo.getClass());
		propertyFilter.serializeAsField(pojo, jgen, provider, writer);
	}
	
}
