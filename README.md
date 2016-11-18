# jackson-nested-prop-filter
jackson-nested-prop-filter

Let's you filter Jackson nested class properties without any (hardly noticeable) performance overhead. Your typical Spring 4.2+ controller can be modified as follows:



```
FilterProvider customJsonFilterProvider;

@PostConstruct
public void setup(){
   customJsonFilterProvider = new NestedPropertyFilterProvider()
			.addFilter("nestedPropertyFilter", 
				NestedBeanPropertyFilter.filterOutAllExcept(YourVO.class, "prop1", "prop1.prop2", "prop1.prop2.prop3")); 
...


@RequestMapping(method = RequestMethod.POST, value = "/springjsonfilter")
	public @ResponseBody MappingJacksonValue jsonFilter() {
		YourVO responseVO = helper.yourVOs();
		MappingJacksonValue jacksonValue = new MappingJacksonValue(responseVO);
		jacksonValue.setFilters(customJsonFilterProvider);
		return jacksonValue;
	}
```

of course, all entities that are being filtered should have the @JsonFilter("nestedPropertyFilter") with the correct filter name so object mapper correctly picks the right filter with the name: "nestedPropertyFilter"

```
public class CustomObjectMapper extends ObjectMapper{

	private static final long serialVersionUID = 1L;

	public CustomObjectMapper() {
		super();
		this.registerModule(new AfterburnerModule());
		this.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
		this.addMixIn(Object.class, NestedBeanPropertyFilter.class);
	}

}
```


you can use * as part of the path it mean all the attributes of the field.
