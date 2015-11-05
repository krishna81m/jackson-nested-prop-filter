# jackson-nested-prop-filter
jackson-nested-prop-filter

Let's you filter Jackson nested class properties without any (hardly noticeable) performance overhead. Your typical Spring 4.2+ controller can be modified as follows:

```
FilterProvider customJsonFilterProvider;

@PostConstruct
public void setup(){
   customJsonFilterProvider = new NestedPropertyFilterProvider()
			.addFilter("antPathFilter", 
				new NestedBeanPropertyFilter(YourVO.class, "prop1", "prop1.prop2", "prop1.prop2.prop3"); 
...


@RequestMapping(method = RequestMethod.POST, value = "/springjsonfilter")
	public @ResponseBody MappingJacksonValue jsonFilter() {
		YourVO responseVO = helper.yourVOs();
		MappingJacksonValue jacksonValue = new MappingJacksonValue(responseVO);
		jacksonValue.setFilters(customJsonFilterProvider);
		return jacksonValue;
	}
```
