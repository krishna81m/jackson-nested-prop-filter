package rk.prod.jackson;

import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * NestedPropertyFilterProvider provides a SimpleBeanPropertyFilter based on 
 * the class type using NestedBeanPropertyFilter
 * A NestedPropertyFilterProvider should be added with a specific filter ID
 * that is also defined for all classes annotated with JsonFilter
 */
public class NestedPropertyFilterProvider extends SimpleFilterProvider {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SimpleFilterProvider addFilter(String id, PropertyFilter filter) {
		if (!(filter instanceof NestedBeanPropertyFilter)){
			throw new ClassCastException("Expected NestedBeanPropertyFilter class");
		}
        _filtersById.put(id, filter);
        return this;
    }

    /**
     * Overloaded variant just to resolve "ties" when using {@link SimpleBeanPropertyFilter}.
     */
    public SimpleFilterProvider addFilter(String id, SimpleBeanPropertyFilter filter) {
    	if (!(filter instanceof NestedBeanPropertyFilter)){
			throw new ClassCastException("Expected NestedBeanPropertyFilter class");
		}
        _filtersById.put(id, filter);
        return this;
    }
    
    public PropertyFilter findPropertyFilter(Object filterId, Object valueToFilter) {
		PropertyFilter f = _filtersById.get(filterId);
		
		// get filter for class
		f = ((NestedBeanPropertyFilter)f).findPropertyFilter(valueToFilter.getClass());
		
        if (f == null) {
            f = _defaultFilter;
            if (f == null && _cfgFailOnUnknownId) {
                throw new IllegalArgumentException("No filter configured with id '"+filterId+"' (type "
                        +filterId.getClass().getName()+")");
            }
        }
        return f;
	};

}
