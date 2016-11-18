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

    public PropertyFilter findPropertyFilter(Object filterId, Object valueToFilter) {
		PropertyFilter filter = _filtersById.get(filterId);

        if (filter instanceof NestedBeanPropertyFilter) {

            // get filter for class
            filter = ((NestedBeanPropertyFilter) filter).findPropertyFilter(valueToFilter.getClass());

            if (filter == null) {
                filter = _defaultFilter;
                if (filter == null && _cfgFailOnUnknownId) {
                    throw new IllegalArgumentException("No filter configured with id '" + filterId + "' (type "
                            + filterId.getClass().getName() + ")");
                }
            }
            return filter;
        } else {
            return super.findPropertyFilter(filterId, valueToFilter);
        }

	}

}
