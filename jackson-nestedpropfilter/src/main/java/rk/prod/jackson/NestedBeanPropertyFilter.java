package rk.prod.jackson;

import java.util.HashMap;
import java.util.List;
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

	private Map<Class<?>, SimpleBeanPropertyFilter> classLevelBeanPropertyFilter = new HashMap<Class<?>, SimpleBeanPropertyFilter>();

	/**
	 * Construct filter based on many nested properties as follows
	 * prop1, classAObj.class1Obj.prop2, classAObj.class2Obj, prop3 
	 * @param properties
	 */
	public NestedBeanPropertyFilter(Class<?> clazz, final String ... properties){
	
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
