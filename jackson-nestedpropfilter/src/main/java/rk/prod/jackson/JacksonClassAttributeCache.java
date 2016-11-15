package rk.prod.jackson;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache that stores JsonClassAttributes - i.e., all nested attributes
 * (java primitive/wrapper type properties and other class instances)
 * per "Json Root entity" class as well
 * as JacksonClassAttribute per individual class
 * <p>
 * FYI: A class has a different meaning in the context of Jackson i.e.,
 * a class that has getters and setters and can generate nested attributes
 * for Jackson
 */
public class JacksonClassAttributeCache {

    private static final Splitter dotSplitter = Splitter.on('.')
            .trimResults()
            .omitEmptyStrings();

    private static final ArrayList<String> PROP_ASTRIX = Lists.newArrayList("*");

    private static final Map<KeyHolder, Map<Class<?>, JacksonClassAttribute>> cacheGlobal = new ConcurrentHashMap<>();

    // TODO: pre populate json class attribute map for ONLY root entities
    private static final Map<Class<?>, JacksonClassAttribute> rootEntityNestedAttrMap = new ConcurrentHashMap<>();

    // only has attributes for the current class but includes all classes that
    // NEED NOT BE root entities
    private static final Map<Class<?>, JacksonClassAttribute> nonNestedAttrMap = new ConcurrentHashMap<>();

    // all classes (nested) contained within a root class
    private static final Map<Class<?>, Set<Class<?>>> nestedClassesMap = new ConcurrentHashMap<>();

    private static JacksonClassAttribute generateJsonAttribute(Class<?> clazz) {
        JacksonClassAttribute classAttribute = rootEntityNestedAttrMap.get(clazz);
        if (classAttribute == null) {
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
    public static JacksonClassAttribute generateRootEntityNestedJsonAttribute(Class<?> clazz, String... properties) {
        JacksonClassAttribute srcAttribute = generateJsonAttribute(clazz);
        JacksonClassAttribute destAttribute = new JacksonClassAttribute(clazz);
        for (String prop : properties) {
            copyAttribute(srcAttribute, destAttribute, dotSplitter.splitToList(prop), null);
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
    public static Map<Class<?>, JacksonClassAttribute> generateClassLevelJsonAttribute(Class<?> clazz, String... properties) {
        KeyHolder key = new KeyHolder(clazz, properties);
        Map<Class<?>, JacksonClassAttribute> result = cacheGlobal.get(key);
        if (result == null) {
            result = new HashMap<>();
            JacksonClassAttribute srcAttribute = generateJsonAttribute(clazz);
            for (String prop : properties) {
                copyAttribute(srcAttribute, result, dotSplitter.splitToList(prop));
            }
            cacheGlobal.put(key, result);
        }
        return result;
    }

    private static void copyAttribute(JacksonClassAttribute src, Map<Class<?>, JacksonClassAttribute> destClassMap, List<String> splitProp) {
        JacksonClassAttribute dest = null;
        if ((dest = destClassMap.get(src.getClazz())) == null) {
            dest = new JacksonClassAttribute(src.getClazz());
            destClassMap.put(src.getClazz(), dest);
        }

        copyAttribute(src, dest, splitProp, destClassMap);
    }

    // i starts at 0
    // use validate flag if required to check if it does exist
    private static void copyAttribute(JacksonClassAttribute src, JacksonClassAttribute dest, List<String> splitProp, Map<Class<?>, JacksonClassAttribute> destClassMap) {
        // TODO: validate that current root entries: src/dest cannot be null
        for (String prop : splitProp) {

            if (prop.equals("*")) {
                //if someone configure on primitive value * skip it.
                if (src == null) {
                    break;
                }

                Map<String, JacksonClassAttribute> srcAttributes = src.getAttributes();
                for (Map.Entry<String, JacksonClassAttribute> attributeEntry : srcAttributes.entrySet()) {
                    String key = attributeEntry.getKey();
                    JacksonClassAttribute destNestedAttribute = dest.getAttributes().get(key);
                    if (destNestedAttribute == null) {
                        JacksonClassAttribute srcJacksonClassAttribute = attributeEntry.getValue();
                        if (srcJacksonClassAttribute != null) {    // save property with ClassAttribute value
                            destNestedAttribute = new JacksonClassAttribute(srcJacksonClassAttribute.getClazz());
                            dest.getAttributes().put(key, destNestedAttribute);
                            if (destClassMap != null) {
                                destClassMap.put(attributeEntry.getValue().getClazz(), destNestedAttribute);
                            }
                            copyAttribute(srcJacksonClassAttribute, destNestedAttribute, PROP_ASTRIX, destClassMap);
                        } else {    // save property with null value
                            dest.getAttributes().put(key, null);    // null
                        }
                    } else {

                    }
                }
                break;
            } else {
                boolean inSrc = src.getAttributes().containsKey(prop);
                if (!inSrc) {    // validation error?
                    return;
                }
                // else, this is a valid entry in src

                // copy the value from src attribute map to dest based on value type
                JacksonClassAttribute srcNestedAttribute = src.getAttributes().get(prop);
                JacksonClassAttribute destNestedAttribute = dest.getAttributes().get(prop);
                if (destNestedAttribute == null) {
                    if (srcNestedAttribute != null) {    // save property with ClassAttribute value
                        destNestedAttribute = new JacksonClassAttribute(srcNestedAttribute.getClazz());
                        dest.getAttributes().put(prop, destNestedAttribute);
                        if (destClassMap != null) {
                            destClassMap.put(srcNestedAttribute.getClazz(), destNestedAttribute);
                        }
                    } else {    // save property with null value
                        dest.getAttributes().put(prop, null);    // null
                    }
                } else {
                    // destination already has this field // validation error?
                    // validate that they are the same type, but previously if it is a property type
                    // above iteration takes care of overwriting
                }

                src = srcNestedAttribute;
                dest = destNestedAttribute;
            }
        }
    }


    static class KeyHolder {

        private final Class<?> clazz;

        private final String[] properties;

        public KeyHolder(Class<?> clazz, String[] properties) {
            this.clazz = clazz;
            this.properties = properties;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            KeyHolder keyHolder = (KeyHolder) o;

            return new EqualsBuilder()
                    .append(clazz, keyHolder.clazz)
                    .append(properties, keyHolder.properties)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(clazz)
                    .append(properties)
                    .toHashCode();
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("KeyHolder{");
            sb.append("clazz=").append(clazz);
            sb.append(", properties=").append(properties == null ? "null" : Arrays.asList(properties).toString());
            sb.append('}');
            return sb.toString();
        }
    }

}
