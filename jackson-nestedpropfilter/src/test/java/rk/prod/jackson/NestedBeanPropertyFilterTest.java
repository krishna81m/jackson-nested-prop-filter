package rk.prod.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.junit.Assert;
import org.junit.Test;
import rk.prod.jackson.datatype.Pojo;
import rk.prod.jackson.datatype.Pojo2;
import rk.prod.jackson.datatype.Pojo3;

/**
 * Created by igreenfi on 11/13/2016.
 */
public class NestedBeanPropertyFilterTest {

    Pojo pojo = new Pojo("a", 2, new Pojo2("c.a", 4, new Pojo3("c.c.a", 7)));

    private SimpleFilterProvider p = new NestedPropertyFilterProvider().addFilter("nestedPropertyFilter", NestedBeanPropertyFilter.filterOutAllExcept(Pojo.class, "a", "c.c", "c.a"));

    private SimpleFilterProvider p1 = new NestedPropertyFilterProvider().addFilter("nestedPropertyFilter", NestedBeanPropertyFilter.filterOutAllExcept(Pojo.class, "a", "c.c.*", "c.a"));

    private SimpleFilterProvider p2 = new NestedPropertyFilterProvider().addFilter("nestedPropertyFilter", NestedBeanPropertyFilter.filterOutAllExcept(Pojo.class, "a", "c.*", "c.c.a"));

    @Test
    public void serializeTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setFilterProvider(p);

        String valueAsString = objectMapper.writeValueAsString(pojo);

        System.out.println(valueAsString);

        Assert.assertEquals("{\"a\":\"a\",\"c\":{\"a\":\"c.a\",\"c\":{}}}", valueAsString);
    }

    @Test
    public void serializeWithAstrixTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setFilterProvider(p1);

        String valueAsString = objectMapper.writeValueAsString(pojo);

        System.out.println(valueAsString);

        Assert.assertEquals("{\"a\":\"a\",\"c\":{\"a\":\"c.a\",\"c\":{\"a\":\"c.c.a\",\"b\":7}}}", valueAsString);
    }

    @Test
    public void serializeWithAstrix2Test() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setFilterProvider(p2);

        String valueAsString = objectMapper.writeValueAsString(pojo);

        System.out.println(valueAsString);

        Assert.assertEquals("{\"a\":\"a\",\"c\":{\"a\":\"c.a\",\"b\":4,\"c\":{\"a\":\"c.c.a\"}}}", valueAsString);
    }

}
