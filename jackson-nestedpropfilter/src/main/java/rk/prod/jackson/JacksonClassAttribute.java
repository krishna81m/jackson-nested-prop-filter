package com.coupons.nextgen.jackson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * JacksonClassAttribute represents a Jackson entity class with getters and setters
 * containing all properties that need to be serialized, in the case of
 * nested class attributes, the property itself can be another JacksonClassAttribute
 */
public class JacksonClassAttribute {

	/* current class type
	 */
	final Class<?> clazz;

	/*
	 * -> all primitive/wrapper type properties will have a value of null in map
	 * as they don't have getters and setters are are not nested
	 * -> all object instances which have their own properties with 
	 * getters and setters are nested properties for this entity 
	 * that Jackson needs to serialize and hence will have a value JacksonClassAttribute in map
	 * 
	 * Root entity properties a, b.c, b.c.d, e will be stored in map as
	 * <a, null>, <b, bClassAttribute>, <e, null>
	 * where bClassAttribute will be stored as <c, cClassAttribute> and so on
	 * 
	 * This could have been a tree
	 */
	final Map<String, JacksonClassAttribute> attributes = new HashMap<>();
	
	/* attributes containing same class
	* required for filter to return JacksonClassAttribute based on object class
	* being filtered, if the result has more than 1, then we use the jgen context
	* 
	* final Map<Class<?>, Set<String>> reverseAttributes = new HashMap<>();
	*/
	
	public JacksonClassAttribute(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public Map<String, JacksonClassAttribute> getAttributes() {
		return attributes;
	}

	@Override
	public String toString() {
		return "JacksonClassAttribute [clazz=" + clazz
				+ ", attributes=" + Arrays.toString(attributes.entrySet().toArray()) + "]";
	}

}
