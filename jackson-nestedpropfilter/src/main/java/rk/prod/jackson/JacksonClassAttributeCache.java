package com.coupons.nextgen.jackson;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache that stores JsonClassAttributes - i.e., all nested attributes
 * (java primitive/wrapper type properties and other class instances)
 * per "Json Root entity" class as well
 * as JacksonClassAttribute per individual class
 * 
 * FYI: A class has a different meaning in the context of Jackson i.e.,
 * a class that has getters and setters and can generate nested attributes
 * for Jackson
 */
public class JacksonClassAttributeCache {
	
	// TODO: pre populate json class attribute map for ONLY root entities
	static final Map<Class<?>, JacksonClassAttribute> rootEntityNestedAttrMap = new ConcurrentHashMap<>();
	
	// only has attributes for the current class but includes all classes that
	// NEED NOT BE root entities
	static final Map<Class<?>, JacksonClassAttribute> nonNestedAttrMap = new ConcurrentHashMap<>();
	
	// all classes (nested) contained within a root class
	static final Map<Class<?>, Set<Class<?>>> nestedClassesMap = new ConcurrentHashMap<>();
	
	public static JacksonClassAttribute generateJsonAttribute(Class<?> clazz) {
		JacksonClassAttribute classAttribute = null;
		if((classAttribute = rootEntityNestedAttrMap.get(clazz)) == null){
			try {
				Set<Class<?>> nestedClasses = new HashSet<>();
				classAttribute = JacksonAttributeBuilder.getBeanUtilsNestedJsonAttribute(
						clazz, nonNestedAttrMap, nestedClasses);
				nestedClassesMap.put(clazz, nestedClasses);
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
			rootEntityNestedAttrMap.put(clazz, classAttribute);
		}
		return classAttribute;
	}
	
	/*
	 * returns a JacksonClassAttribute tree limited to the nested properties provided
	 * 	  a		<< root class
	 *  b	c	<< first level attributes
	 *e  f g  h	<< second level nested attributes
	 *...
	 */
	public static JacksonClassAttribute generateRootEntityNestedJsonAttribute(Class<?> clazz, String ... properties) {
		JacksonClassAttribute srcAttribute = generateJsonAttribute(clazz);
		JacksonClassAttribute destAttribute = new JacksonClassAttribute(clazz);
		for(String prop : properties){
			copyAttribute(srcAttribute, destAttribute, prop.split("\\."));
		}
		return destAttribute;
	}
	
	/*
	 * returns a JacksonClassAttribute tree limited to the nested properties provided
	 * 	  a		<< root class
	 *  b	c	<< first level attributes
	 *e  f g  h	<< second level nested attributes
	 *...
	 */
	public static Map<Class<?>, JacksonClassAttribute> generateClassLevelJsonAttribute(Class<?> clazz, String ... properties) {
		Map<Class<?>, JacksonClassAttribute> customAttributeMap = new HashMap<>();
		JacksonClassAttribute srcAttribute = generateJsonAttribute(clazz);
		for(String prop : properties){
			copyAttribute(srcAttribute, customAttributeMap, prop.split("\\."));
		}
		return customAttributeMap;
	}
	
	private static void copyAttribute(JacksonClassAttribute src,
			Map<Class<?>, JacksonClassAttribute> destClassMap, String[] splitProp) {
		JacksonClassAttribute dest = null;
		if((dest = destClassMap.get(src.getClazz())) == null){
			dest = new JacksonClassAttribute(src.getClazz());
			destClassMap.put(src.getClazz(), dest);
		}
		
		for(int i = 0 ; i < splitProp.length; i++){
			String tmpStr = splitProp[i];
			
			boolean inSrc = src.getAttributes().containsKey(tmpStr);
			if(!inSrc){	// validation error?
				return;
			}
			// else, this is a valid entry in src
			
			// copy the value from src attribute map to dest based on value type
			JacksonClassAttribute srcNestedAttribute = src.attributes.get(tmpStr);
			JacksonClassAttribute destNestedAttribute = dest.attributes.get(tmpStr);
			if(destNestedAttribute == null){
				if(srcNestedAttribute != null){	// save property with ClassAttribute value
					destNestedAttribute = new JacksonClassAttribute(srcNestedAttribute.getClazz());
					dest.attributes.put(tmpStr, destNestedAttribute);
					destClassMap.put(srcNestedAttribute.getClazz(), destNestedAttribute);
				}
				else{	// save property with null value
					dest.attributes.put(tmpStr, destNestedAttribute);	// null
				}
			}
			else{
				// destination already has this field // validation error?
				// validate that they are the same type, but previously if it is a property type
				// above iteration takes care of overwriting
			}
			
			src = srcNestedAttribute;
			dest = destNestedAttribute;
		}
	}

	// i starts at 0
	// use validate flag if required to check if it does exist
	static void copyAttribute(JacksonClassAttribute src, JacksonClassAttribute dest, String[] splitProp){
		// TODO: validate that current root entries: src/dest cannot be null
		for(int i = 0 ; i < splitProp.length; i++){
			String tmpStr = splitProp[i];
			
			boolean inSrc = src.getAttributes().containsKey(tmpStr);
			if(!inSrc){	// validation error?
				return;
			}
			// else, this is a valid entry in src
			
			// copy the value from src attribute map to dest based on value type
			JacksonClassAttribute srcNestedAttribute = src.attributes.get(tmpStr);
			JacksonClassAttribute destNestedAttribute = dest.attributes.get(tmpStr);
			
			if(destNestedAttribute == null){
				if(srcNestedAttribute != null){	// save property with ClassAttribute value
					destNestedAttribute = new JacksonClassAttribute(srcNestedAttribute.getClazz());
					dest.attributes.put(tmpStr, destNestedAttribute);
				}
				else{	// save property with null value
					dest.attributes.put(tmpStr, destNestedAttribute);	// null
				}
			}
			else{
				// destination already has this field // validation error?
				// validate that they are the same type, but previously if it is a property type
				// above iteration takes care of overwriting
			}
			
			src = srcNestedAttribute;
			dest = destNestedAttribute;
		}
	}

}
