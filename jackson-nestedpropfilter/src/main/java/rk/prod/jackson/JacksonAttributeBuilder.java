package com.coupons.nextgen.jackson;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;

public class JacksonAttributeBuilder {

	public static JacksonClassAttribute getBeanUtilsNestedJsonAttribute(Class<?> clazz,  
			Map<Class<?>, JacksonClassAttribute> nonNestedAttributeMap, 
			Set<Class<?>> classes) throws Exception {
	    JacksonClassAttribute jsonAttribute = new JacksonClassAttribute(clazz);
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(clazz);
		for(PropertyDescriptor descr : descriptors){
			// if you want values, use: descr.getValue(attributeName)
			if(descr.getPropertyType().getName().equals("java.lang.Class")){
				continue;
			}
			// a primitive, a CharSequence(String), Number, Date, URI, URL, Locale, Class, or corresponding array
			// or add more like UUID or other types
			if(!BeanUtils.isSimpleProperty(descr.getPropertyType())){
				Field collectionfield = clazz.getDeclaredField(descr.getName());
		        if(collectionfield.getGenericType() instanceof ParameterizedType){
					ParameterizedType integerListType = (ParameterizedType) collectionfield.getGenericType();
			        Class<?> actualClazz = (Class<?>) integerListType.getActualTypeArguments()[0];			        
			        
			        JacksonClassAttribute attribute = getBeanUtilsNestedJsonAttribute(actualClazz, nonNestedAttributeMap, classes);
			        jsonAttribute.getAttributes().put(descr.getName(), attribute);
			        classes.add(actualClazz);
		        }
		        else{	// or a complex custom type to get nested fields
		        	JacksonClassAttribute attribute = getBeanUtilsNestedJsonAttribute(descr.getPropertyType(), nonNestedAttributeMap, classes);
		        	jsonAttribute.getAttributes().put(descr.getName(), attribute);
		        	classes.add(descr.getPropertyType());
		        }
			}
			else{
				jsonAttribute.getAttributes().put(descr.getDisplayName(), null);
			}
		}
	
		nonNestedAttributeMap.put(clazz, jsonAttribute);
		return jsonAttribute;
	}

}
