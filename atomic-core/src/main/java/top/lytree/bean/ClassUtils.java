package top.lytree.bean;

import top.lytree.lang.StringUtils;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author pride
 */
public class ClassUtils {

    private static final Comparator<Class<?>> COMPARATOR = (o1, o2) -> Objects.compare(getName(o1), getName(o2), String::compareTo);

    /**
     * The package separator character: {@code '&#x2e;' == {@value}}.
     */
    public static final char PACKAGE_SEPARATOR_CHAR = '.';

    /**
     * The package separator String: {@code "&#x2e;"}.
     */
    public static final String PACKAGE_SEPARATOR = String.valueOf(PACKAGE_SEPARATOR_CHAR);

    /**
     * The inner class separator character: {@code '$' == {@value}}.
     */
    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';

    /**
     * The inner class separator String: {@code "$"}.
     */
    public static final String INNER_CLASS_SEPARATOR = String.valueOf(INNER_CLASS_SEPARATOR_CHAR);

    /**
     * Maps names of primitives to their corresponding primitive {@link Class}es.
     */
    private static final Map<String, Class<?>> namePrimitiveMap = new HashMap<>();

    static {
        namePrimitiveMap.put("boolean", Boolean.TYPE);
        namePrimitiveMap.put("byte", Byte.TYPE);
        namePrimitiveMap.put("char", Character.TYPE);
        namePrimitiveMap.put("short", Short.TYPE);
        namePrimitiveMap.put("int", Integer.TYPE);
        namePrimitiveMap.put("long", Long.TYPE);
        namePrimitiveMap.put("double", Double.TYPE);
        namePrimitiveMap.put("float", Float.TYPE);
        namePrimitiveMap.put("void", Void.TYPE);
    }

    /**
     * Maps primitive {@link Class}es to their corresponding wrapper {@link Class}.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<>();

    static {
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
    }

    /**
     * Maps wrapper {@link Class}es to their corresponding primitive types.
     */
    private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<>();

    static {
        primitiveWrapperMap.forEach((primitiveClass, wrapperClass) -> {
            if (!primitiveClass.equals(wrapperClass)) {
                wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
            }
        });
    }
    /**
     * Maps a primitive class name to its corresponding abbreviation used in array class names.
     */
    private static final Map<String, String> abbreviationMap;

    /**
     * Maps an abbreviation used in array class names to corresponding primitive class name.
     */
    private static final Map<String, String> reverseAbbreviationMap;

    /** Feed abbreviation maps. */
    static {
        final Map<String, String> map = new HashMap<>();
        map.put("int", "I");
        map.put("boolean", "Z");
        map.put("float", "F");
        map.put("long", "J");
        map.put("short", "S");
        map.put("byte", "B");
        map.put("double", "D");
        map.put("char", "C");
        abbreviationMap = Collections.unmodifiableMap(map);
        reverseAbbreviationMap = Collections.unmodifiableMap(map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
    }
    /**
     * 是否为抽象类
     *
     * @param clazz 类
     * @return 是否为抽象类
     */
    public static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * 是否为标准的类<br> 这个类必须：
     *
     * <pre>
     * 1、非接口
     * 2、非抽象类
     * 3、非Enum枚举
     * 4、非数组
     * 5、非注解
     * 6、非原始类型（int, long等）
     * </pre>
     *
     * @param clazz 类
     * @return 是否为标准类
     */
    public static boolean isNormalClass(Class<?> clazz) {
        return null != clazz //
                && !clazz.isInterface() //
                && !isAbstract(clazz) //
                && !clazz.isEnum() //
                && !clazz.isArray() //
                && !clazz.isAnnotation() //
                && !clazz.isSynthetic() //
                && !clazz.isPrimitive();//
    }

    /**
     * 获取指定类型分的默认值<br> 默认值规则为：
     *
     * <pre>
     * 1、如果为原始类型，返回0
     * 2、非原始类型返回{@code null}
     * </pre>
     *
     * @param clazz 类
     * @return 默认值
     */
    public static Object getDefaultValue(Class<?> clazz) {
        // 原始类型
        if (clazz.isPrimitive()) {
            return getPrimitiveDefaultValue(clazz);
        }
        return null;
    }

    /**
     * 获取指定原始类型分的默认值<br> 默认值规则为：
     *
     * <pre>
     * 1、如果为原始类型，返回0
     * 2、非原始类型返回{@code null}
     * </pre>
     *
     * @param clazz 类
     * @return 默认值
     */
    public static Object getPrimitiveDefaultValue(Class<?> clazz) {
        if (long.class == clazz) {
            return 0L;
        } else if (int.class == clazz) {
            return 0;
        } else if (short.class == clazz) {
            return (short) 0;
        } else if (char.class == clazz) {
            return (char) 0;
        } else if (byte.class == clazz) {
            return (byte) 0;
        } else if (double.class == clazz) {
            return 0D;
        } else if (float.class == clazz) {
            return 0f;
        } else if (boolean.class == clazz) {
            return false;
        }
        return null;
    }

    /**
     * 获得默认值列表
     *
     * @param classes 值类型
     * @return 默认值列表
     */
    public static Object[] getDefaultValues(Class<?>... classes) {
        final Object[] values = new Object[classes.length];
        for (int i = 0; i < classes.length; i++) {
            values[i] = getDefaultValue(classes[i]);
        }
        return values;
    }


    /**
     * Returns the (initialized) class represented by {@code className} using the current thread's context class loader.
     * This implementation supports the syntaxes "{@code java.util.Map.Entry[]}", "{@code java.util.Map$Entry[]}",
     * "{@code [Ljava.util.Map.Entry;}", and "{@code [Ljava.util.Map$Entry;}".
     *
     * @param className the class name
     * @return the class represented by {@code className} using the current thread's context class loader
     * @throws NullPointerException   if the className is null
     * @throws ClassNotFoundException if the class is not found
     */


    /**
     * Delegates to {@link Class#getComponentType()} using generics.
     *
     * @param <T> The array class type.
     * @param cls A class or null.
     * @return The array component type or null.
     * @see Class#getComponentType()
     * @since 3.13.0
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getComponentType(final Class<T[]> cls) {
        return cls == null ? null : (Class<T>) cls.getComponentType();
    }

    /**
     * Null-safe version of {@code cls.getName()}
     *
     * @param cls the class for which to get the class name; may be null
     * @return the class name or the empty string in case the argument is {@code null}
     * @see Class#getSimpleName()
     * @since 3.7
     */
    public static String getName(final Class<?> cls) {
        return getName(cls, StringUtils.EMPTY);
    }

    /**
     * Null-safe version of {@code cls.getName()}
     *
     * @param cls         the class for which to get the class name; may be null
     * @param valueIfNull the return value if the argument {@code cls} is {@code null}
     * @return the class name or {@code valueIfNull}
     * @see Class#getName()
     * @since 3.7
     */
    public static String getName(final Class<?> cls, final String valueIfNull) {
        return cls == null ? valueIfNull : cls.getName();
    }

    /**
     * Null-safe version of {@code object.getClass().getName()}
     *
     * @param object the object for which to get the class name; may be null
     * @return the class name or the empty String
     * @see Class#getSimpleName()
     * @since 3.7
     */
    public static String getName(final Object object) {
        return getName(object, StringUtils.EMPTY);
    }

    /**
     * Null-safe version of {@code object.getClass().getSimpleName()}
     *
     * @param object      the object for which to get the class name; may be null
     * @param valueIfNull the value to return if {@code object} is {@code null}
     * @return the class name or {@code valueIfNull}
     * @see Class#getName()
     * @since 3.0
     */
    public static String getName(final Object object, final String valueIfNull) {
        return object == null ? valueIfNull : object.getClass().getName();
    }

    /**
     * Gets a {@link List} of all interfaces implemented by the given class and its superclasses.
     *
     * <p>
     * The order is determined by looking through each interface in turn as declared in the source file and following its
     * hierarchy up. Then each superclass is considered in the same way. Later duplicates are ignored, so the order is
     * maintained.
     * </p>
     *
     * @param cls the class to look up, may be {@code null}
     * @return the {@link List} of interfaces in order, {@code null} if null input
     */
    public static List<Class<?>> getAllInterfaces(final Class<?> cls) {
        if (cls == null) {
            return null;
        }

        final LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<>();
        getAllInterfaces(cls, interfacesFound);

        return new ArrayList<>(interfacesFound);
    }

    /**
     * Gets the interfaces for the specified class.
     *
     * @param cls             the class to look up, may be {@code null}
     * @param interfacesFound the {@link Set} of interfaces for the class
     */
    private static void getAllInterfaces(Class<?> cls, final HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            final Class<?>[] interfaces = cls.getInterfaces();

            for (final Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }

            cls = cls.getSuperclass();
        }
    }
    /**
     * Gets the class name minus the package name from a {@link Class}.
     *
     * <p>
     * This method simply gets the name using {@code Class.getName()} and then calls {@link #getShortClassName(Class)}. See
     * relevant notes there.
     * </p>
     *
     * @param cls the class to get the short name for.
     * @return the class name without the package name or an empty string. If the class is an inner class then the returned
     *         value will contain the outer class or classes separated with {@code .} (dot) character.
     */
    public static String getShortClassName(final Class<?> cls) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return getShortClassName(cls.getName());
    }

    /**
     * Gets the class name of the {@code object} without the package name or names.
     *
     * <p>
     * The method looks up the class of the object and then converts the name of the class invoking
     * {@link #getShortClassName(Class)} (see relevant notes there).
     * </p>
     *
     * @param object the class to get the short name for, may be {@code null}
     * @param valueIfNull the value to return if the object is {@code null}
     * @return the class name of the object without the package name, or {@code valueIfNull} if the argument {@code object}
     *         is {@code null}
     */
    public static String getShortClassName(final Object object, final String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getShortClassName(object.getClass());
    }

    /**
     * Gets the class name minus the package name from a String.
     *
     * <p>
     * The string passed in is assumed to be a class name - it is not checked. The string has to be formatted the way as the
     * JDK method {@code Class.getName()} returns it, and not the usual way as we write it, for example in import
     * statements, or as it is formatted by {@code Class.getCanonicalName()}.
     * </p>
     *
     * <p>
     * The difference is is significant only in case of classes that are inner classes of some other classes. In this case
     * the separator between the outer and inner class (possibly on multiple hierarchy level) has to be {@code $} (dollar
     * sign) and not {@code .} (dot), as it is returned by {@code Class.getName()}
     * </p>
     *
     * <p>
     * Note that this method is called from the {@link #getShortClassName(Class)} method using the string returned by
     * {@code Class.getName()}.
     * </p>
     *
     * <p>
     * Note that this method differs from {@link #getSimpleName(Class)} in that this will return, for example
     * {@code "Map.Entry"} whilst the {@code java.lang.Class} variant will simply return {@code "Entry"}. In this example
     * the argument {@code className} is the string {@code java.util.Map$Entry} (note the {@code $} sign.
     * </p>
     *
     * @param className the className to get the short name for. It has to be formatted as returned by
     *        {@code Class.getName()} and not {@code Class.getCanonicalName()}
     * @return the class name of the class without the package name or an empty string. If the class is an inner class then
     *         value contains the outer class or classes and the separator is replaced to be {@code .} (dot) character.
     */
    public static String getShortClassName(String className) {
        if (StringUtils.isEmpty(className)) {
            return StringUtils.EMPTY;
        }

        final StringBuilder arrayPrefix = new StringBuilder();

        // Handle array encoding
        if (className.startsWith("[")) {
            while (className.charAt(0) == '[') {
                className = className.substring(1);
                arrayPrefix.append("[]");
            }
            // Strip Object type encoding
            if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
                className = className.substring(1, className.length() - 1);
            }

            if (reverseAbbreviationMap.containsKey(className)) {
                className = reverseAbbreviationMap.get(className);
            }
        }

        final int lastDotIdx = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
        final int innerIdx = className.indexOf(INNER_CLASS_SEPARATOR_CHAR, lastDotIdx == -1 ? 0 : lastDotIdx + 1);
        String out = className.substring(lastDotIdx + 1);
        if (innerIdx != -1) {
            out = out.replace(INNER_CLASS_SEPARATOR_CHAR, PACKAGE_SEPARATOR_CHAR);
        }
        return out + arrayPrefix;
    }

    /**
     * Returns whether the given {@code type} is a primitive or primitive wrapper ({@link Boolean}, {@link Byte},
     * {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Double}, {@link Float}).
     *
     * @param type The class to query or null.
     * @return true if the given {@code type} is a primitive or primitive wrapper ({@link Boolean}, {@link Byte},
     * {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Double}, {@link Float}).
     * @since 3.1
     */
    public static boolean isPrimitiveOrWrapper(final Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.isPrimitive() || isPrimitiveWrapper(type);
    }

    /**
     * Returns whether the given {@code type} is a primitive wrapper ({@link Boolean}, {@link Byte}, {@link Character},
     * {@link Short}, {@link Integer}, {@link Long}, {@link Double}, {@link Float}).
     *
     * @param type The class to query or null.
     * @return true if the given {@code type} is a primitive wrapper ({@link Boolean}, {@link Byte}, {@link Character},
     * {@link Short}, {@link Integer}, {@link Long}, {@link Double}, {@link Float}).
     * @since 3.1
     */
    public static boolean isPrimitiveWrapper(final Class<?> type) {
        return wrapperPrimitiveMap.containsKey(type);
    }

    /**
     * Converts the specified array of primitive Class objects to an array of its corresponding wrapper Class objects.
     *
     * @param classes the class array to convert, may be null or empty
     * @return an array which contains for each given class, the wrapper class or the original class if class is not a
     * primitive. {@code null} if null input. Empty array if an empty array passed in.
     * @since 2.1
     */
    public static Class<?>[] primitivesToWrappers(final Class<?>... classes) {
        if (classes == null) {
            return null;
        }

        if (classes.length == 0) {
            return classes;
        }

        final Class<?>[] convertedClasses = new Class[classes.length];
        Arrays.setAll(convertedClasses, i -> primitiveToWrapper(classes[i]));
        return convertedClasses;
    }

    /**
     * Converts the specified primitive Class object to its corresponding wrapper Class object.
     *
     * <p>
     * NOTE: From v2.2, this method handles {@code Void.TYPE}, returning {@code Void.TYPE}.
     * </p>
     *
     * @param cls the class to convert, may be null
     * @return the wrapper class for {@code cls} or {@code cls} if {@code cls} is not a primitive. {@code null} if null
     * input.
     * @since 2.1
     */
    public static Class<?> primitiveToWrapper(final Class<?> cls) {
        Class<?> convertedClass = cls;
        if (cls != null && cls.isPrimitive()) {
            convertedClass = primitiveWrapperMap.get(cls);
        }
        return convertedClass;
    }

    /**
     * Converts the specified array of wrapper Class objects to an array of its corresponding primitive Class objects.
     *
     * <p>
     * This method invokes {@code wrapperToPrimitive()} for each element of the passed in array.
     * </p>
     *
     * @param classes the class array to convert, may be null or empty
     * @return an array which contains for each given class, the primitive class or <b>null</b> if the original class is not
     * a wrapper class. {@code null} if null input. Empty array if an empty array passed in.
     * @see #wrapperToPrimitive(Class)
     * @since 2.4
     */
    public static Class<?>[] wrappersToPrimitives(final Class<?>... classes) {
        if (classes == null) {
            return null;
        }

        if (classes.length == 0) {
            return classes;
        }

        final Class<?>[] convertedClasses = new Class[classes.length];
        Arrays.setAll(convertedClasses, i -> wrapperToPrimitive(classes[i]));
        return convertedClasses;
    }

    /**
     * Converts the specified wrapper class to its corresponding primitive class.
     *
     * <p>
     * This method is the counter part of {@code primitiveToWrapper()}. If the passed in class is a wrapper class for a
     * primitive type, this primitive type will be returned (e.g. {@code Integer.TYPE} for {@code Integer.class}). For other
     * classes, or if the parameter is <b>null</b>, the return value is <b>null</b>.
     * </p>
     *
     * @param cls the class to convert, may be <b>null</b>
     * @return the corresponding primitive type if {@code cls} is a wrapper class, <b>null</b> otherwise
     * @see #primitiveToWrapper(Class)
     * @since 2.4
     */
    public static Class<?> wrapperToPrimitive(final Class<?> cls) {
        return wrapperPrimitiveMap.get(cls);
    }

}
