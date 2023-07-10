package quan.generator.util;

import java.util.*;

/**
 * @author quanchangnai
 */
public class JavaUtils {

    public static void fillGeneratorBasicTypes(Map<String, String> basicTypes) {
        basicTypes.put("byte", "byte");
        basicTypes.put("bool", "boolean");
        basicTypes.put("short", "short");
        basicTypes.put("int", "int");
        basicTypes.put("long", "long");
        basicTypes.put("float", "float");
        basicTypes.put("double", "double");
        basicTypes.put("string", "String");
        basicTypes.put("set", "Set");
        basicTypes.put("list", "List");
        basicTypes.put("map", "Map");
    }

    public static void fillGeneratorClassTypes(Map<String, String> classTypes) {
        classTypes.put("byte", "Byte");
        classTypes.put("bool", "Boolean");
        classTypes.put("short", "Short");
        classTypes.put("int", "Integer");
        classTypes.put("long", "Long");
        classTypes.put("float", "Float");
        classTypes.put("double", "Double");
        classTypes.put("string", "String");
        classTypes.put("set", "HashSet");
        classTypes.put("list", "ArrayList");
        classTypes.put("map", "HashMap");
    }

    public static void fillGeneratorClassNames(Map<String, String> classNames) {
        classNames.put("Boolean", Boolean.class.getName());
        classNames.put("Short", Short.class.getName());
        classNames.put("Integer", Integer.class.getName());
        classNames.put("Long", Long.class.getName());
        classNames.put("Float", Float.class.getName());
        classNames.put("Double", Double.class.getName());
        classNames.put("String", String.class.getName());

        classNames.put("Set", Set.class.getName());
        classNames.put("HashSet", HashSet.class.getName());
        classNames.put("List", List.class.getName());
        classNames.put("ArrayList", ArrayList.class.getName());
        classNames.put("Map", Map.class.getName());
        classNames.put("HashMap", HashMap.class.getName());

        classNames.put("Object", Object.class.getName());
        classNames.put("Class", Class.class.getName());
        classNames.put("Override", Override.class.getName());
        classNames.put("SuppressWarnings", SuppressWarnings.class.getName());

        classNames.put("Objects", Objects.class.getName());
        classNames.put("Arrays", Arrays.class.getName());
        classNames.put("Collection", Collection.class.getName());
        classNames.put("Collections", Collections.class.getName());
    }

}
