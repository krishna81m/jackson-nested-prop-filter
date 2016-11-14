package rk.prod.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.junit.Test;
import rk.prod.jackson.datatype.Pojo;
import rk.prod.jackson.datatype.Pojo2;

/**
 * Created by igreenfi on 11/13/2016.
 */
public class NestedBeanPropertyFilterTest {
    private SimpleFilterProvider p = new NestedPropertyFilterProvider().addFilter("nestedPropertyFilter", new NestedBeanPropertyFilter(Pojo.class, "a", "c.b"));



    @Test
    public void serializeTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();


        Pojo pojo = new Pojo("a", 2, new Pojo2("c.a", 4));

        objectMapper.setFilterProvider(p);

        String valueAsString = objectMapper.writeValueAsString(pojo);

        System.out.println(valueAsString);

    }

}
