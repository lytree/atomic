package top.yang.reflect;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author PrideYang
 */
public class MethodUtils extends org.apache.commons.lang3.reflect.MethodUtils {

    /**
     * Only log warning about accessibility work around once.
     * <p>
     * Note that this is broken when this class is deployed via a shared classloader in a container, as the warning message will be emitted only once, not once per webapp. However
     * making the warning appear once per webapp means having a map keyed by context classloader which introduces nasty memory-leak problems. As this warning is really optional we
     * can ignore this problem; only one of the webapps will get the warning in its logs but that should be good enough.
     */
    private static boolean loggedAccessibleWarning;

    /**
     * Indicates whether methods should be cached for improved performance.
     * <p>
     * Note that when this class is deployed via a shared classloader in a container, this will affect all webapps. However making this configurable per webapp would mean having a
     * map keyed by context classloader which may introduce memory-leak problems.
     */
    private static boolean CACHE_METHODS = true;

    /**
     * Stores a cache of MethodDescriptor -> Method in a WeakHashMap.
     * <p>
     * The keys into this map only ever exist as temporary variables within methods of this class, and are never exposed to users of this class. This means that the WeakHashMap is
     * used only as a mechanism for limiting the size of the cache, ie a way to tell the garbage collector that the contents of the cache can be completely garbage-collected
     * whenever it needs the memory. Whether this is a good approach to this problem is doubtful; something like the commons-collections LRUMap may be more appropriate (though of
     * course selecting an appropriate size is an issue).
     * <p>
     * This static variable is safe even when this code is deployed via a shared classloader because it is keyed via a MethodDescriptor object which has a Class as one of its
     * members and that member is used in the MethodDescriptor.equals method. So two components that load the same class via different classloaders will generate non-equal
     * MethodDescriptor objects and hence end up with different entries in the map.
     */
    private static final Map<MethodDescriptor, Reference<Method>> cache = Collections
            .synchronizedMap(new WeakHashMap<MethodDescriptor, Reference<Method>>());

    /**
     * Try to make the method accessible
     *
     * @param method The source arguments
     */
    private static void setMethodAccessible(final Method method) {
        try {
            //
            // XXX Default access superclass workaround
            //
            // When a public class has a default access superclass
            // with public methods, these methods are accessible.
            // Calling them from compiled code works fine.
            //
            // Unfortunately, using reflection to invoke these methods
            // seems to (wrongly) to prevent access even when the method
            // modifier is public.
            //
            // The following workaround solves the problem but will only
            // work from sufficiently privileges code.
            //
            // Better workarounds would be gratefully accepted.
            //
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }

        } catch (final SecurityException se) {
            // log but continue just in case the method.invoke works anyway
            final Logger log = LoggerFactory.getLogger(MethodUtils.class);
            if (!loggedAccessibleWarning) {
                boolean vulnerableJVM = false;
                try {
                    final String specVersion = System.getProperty("java.specification.version");
                    if (specVersion.charAt(0) == '1' &&
                            (specVersion.charAt(2) == '0' ||
                                    specVersion.charAt(2) == '1' ||
                                    specVersion.charAt(2) == '2' ||
                                    specVersion.charAt(2) == '3')) {

                        vulnerableJVM = true;
                    }
                } catch (final SecurityException e) {
                    // don't know - so display warning
                    vulnerableJVM = true;
                }
                if (vulnerableJVM) {
                    log.warn(
                            "Current Security Manager restricts use of workarounds for reflection bugs "
                                    + " in pre-1.4 JVMs.");
                }
                loggedAccessibleWarning = true;
            }
            log.debug("Cannot setAccessible on method. Therefore cannot use jvm access bug workaround.", se);
        }
    }

    /**
     * Returns the sum of the object transformation cost for each class in the source argument list.
     *
     * @param srcArgs  The source arguments
     * @param destArgs The destination arguments
     * @return The total transformation cost
     */
    private static float getTotalTransformationCost(final Class<?>[] srcArgs, final Class<?>[] destArgs) {

        float totalCost = 0.0f;
        for (int i = 0; i < srcArgs.length; i++) {
            Class<?> srcClass, destClass;
            srcClass = srcArgs[i];
            destClass = destArgs[i];
            totalCost += getObjectTransformationCost(srcClass, destClass);
        }

        return totalCost;
    }

    /**
     * Gets the number of steps required needed to turn the source class into the destination class. This represents the number of steps in the object hierarchy graph.
     *
     * @param srcClass  The source class
     * @param destClass The destination class
     * @return The cost of transforming an object
     */
    private static float getObjectTransformationCost(Class<?> srcClass, final Class<?> destClass) {
        float cost = 0.0f;
        while (srcClass != null && !destClass.equals(srcClass)) {
            if (destClass.isPrimitive()) {
                final Class<?> destClassWrapperClazz = getPrimitiveWrapper(destClass);
                if (destClassWrapperClazz != null && destClassWrapperClazz.equals(srcClass)) {
                    cost += 0.25f;
                    break;
                }
            }
            if (destClass.isInterface() && isAssignmentCompatible(destClass, srcClass)) {
                // slight penalty for interface match.
                // we still want an exact match to override an interface match, but
                // an interface match should override anything where we have to get a
                // superclass.
                cost += 0.25f;
                break;
            }
            cost++;
            srcClass = srcClass.getSuperclass();
        }

        /*
         * If the destination class is null, we've traveled all the way up to
         * an Object match. We'll penalize this by adding 1.5 to the cost.
         */
        if (srcClass == null) {
            cost += 1.5f;
        }

        return cost;
    }

    /**
     * <p>Determine whether a type can be used as a parameter in a method invocation.
     * This method handles primitive conversions correctly.</p>
     *
     * <p>In order words, it will match a {@code Boolean</code> to a <code>boolean},
     * a {@code Long</code> to a <code>long}, a {@code Float</code> to a <code>float}, a {@code Integer</code> to a <code>int}, and a {@code Double</code> to a <code>double}. Now
     * logic widening matches are allowed. For example, a {@code Long</code> will not match a <code>int}.
     *
     * @param parameterType    the type of parameter accepted by the method
     * @param parameterization the type of parameter being tested
     * @return true if the assignment is compatible.
     */
    public static final boolean isAssignmentCompatible(final Class<?> parameterType, final Class<?> parameterization) {
        // try plain assignment
        if (parameterType.isAssignableFrom(parameterization)) {
            return true;
        }

        if (parameterType.isPrimitive()) {
            // this method does *not* do widening - you must specify exactly
            // is this the right behavior?
            final Class<?> parameterWrapperClazz = getPrimitiveWrapper(parameterType);
            if (parameterWrapperClazz != null) {
                return parameterWrapperClazz.equals(parameterization);
            }
        }

        return false;
    }

    /**
     * Gets the wrapper object class for the given primitive type class. For example, passing {@code boolean.class</code> returns <code>Boolean.class}
     *
     * @param primitiveType the primitive type class for which a match is to be found
     * @return the wrapper type associated with the given primitive or null if no match is found
     */
    public static Class<?> getPrimitiveWrapper(final Class<?> primitiveType) {
        // does anyone know a better strategy than comparing names?
        if (boolean.class.equals(primitiveType)) {
            return Boolean.class;
        }
        if (float.class.equals(primitiveType)) {
            return Float.class;
        }
        if (long.class.equals(primitiveType)) {
            return Long.class;
        }
        if (int.class.equals(primitiveType)) {
            return Integer.class;
        }
        if (short.class.equals(primitiveType)) {
            return Short.class;
        }
        if (byte.class.equals(primitiveType)) {
            return Byte.class;
        }
        if (double.class.equals(primitiveType)) {
            return Double.class;
        }
        if (char.class.equals(primitiveType)) {
            return Character.class;
        }
        return null;
    }

    /**
     * Gets the class for the primitive type corresponding to the primitive wrapper class given. For example, an instance of {@code Boolean.class</code> returns a
     * <code>boolean.class}.
     *
     * @param wrapperType the
     * @return the primitive type class corresponding to the given wrapper class, null if no match is found
     */
    public static Class<?> getPrimitiveType(final Class<?> wrapperType) {
        // does anyone know a better strategy than comparing names?
        if (Boolean.class.equals(wrapperType)) {
            return boolean.class;
        }
        if (Float.class.equals(wrapperType)) {
            return float.class;
        }
        if (Long.class.equals(wrapperType)) {
            return long.class;
        }
        if (Integer.class.equals(wrapperType)) {
            return int.class;
        }
        if (Short.class.equals(wrapperType)) {
            return short.class;
        }
        if (Byte.class.equals(wrapperType)) {
            return byte.class;
        }
        if (Double.class.equals(wrapperType)) {
            return double.class;
        }
        if (Character.class.equals(wrapperType)) {
            return char.class;
        }
        final Logger log = LoggerFactory.getLogger(MethodUtils.class);
        if (log.isDebugEnabled()) {
            log.debug("Not a known primitive wrapper class: " + wrapperType);
        }
        return null;
    }

    /**
     * Find a non primitive representation for given primitive class.
     *
     * @param clazz the class to find a representation for, not null
     * @return the original class if it not a primitive. Otherwise the wrapper class. Not null
     */
    public static Class<?> toNonPrimitiveClass(final Class<?> clazz) {
        if (clazz.isPrimitive()) {
            final Class<?> primitiveClazz = MethodUtils.getPrimitiveWrapper(clazz);
            // the above method returns
            if (primitiveClazz != null) {
                return primitiveClazz;
            }
        }
        return clazz;
    }

    /**
     * Gets the method from the cache, if present.
     *
     * @param md The method descriptor
     * @return The cached method
     */
    private static Method getCachedMethod(final MethodDescriptor md) {
        if (CACHE_METHODS) {
            final Reference<Method> methodRef = cache.get(md);
            if (methodRef != null) {
                return methodRef.get();
            }
        }
        return null;
    }

    /**
     * Add a method to the cache.
     *
     * @param md     The method descriptor
     * @param method The method to cache
     */
    private static void cacheMethod(final MethodDescriptor md, final Method method) {
        if (CACHE_METHODS && method != null) {
            cache.put(md, new WeakReference<>(method));
        }
    }

    /**
     * Represents the key to looking up a Method by reflection.
     */
    private static class MethodDescriptor {

        private final Class<?> cls;
        private final String methodName;
        private final Class<?>[] paramTypes;
        private final boolean exact;
        private final int hashCode;

        /**
         * The sole constructor.
         *
         * @param cls        the class to reflect, must not be null
         * @param methodName the method name to obtain
         * @param paramTypes the array of classes representing the parameter types
         * @param exact      whether the match has to be exact.
         */
        public MethodDescriptor(final Class<?> cls, final String methodName, Class<?>[] paramTypes,
                final boolean exact) {
            if (cls == null) {
                throw new IllegalArgumentException("Class cannot be null");
            }
            if (methodName == null) {
                throw new IllegalArgumentException("Method Name cannot be null");
            }
            if (paramTypes == null) {
                paramTypes = BeanUtils.EMPTY_CLASS_ARRAY;
            }

            this.cls = cls;
            this.methodName = methodName;
            this.paramTypes = paramTypes;
            this.exact = exact;

            this.hashCode = methodName.length();
        }

        /**
         * Checks for equality.
         *
         * @param obj object to be tested for equality
         * @return true, if the object describes the same Method.
         */
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof MethodDescriptor)) {
                return false;
            }
            final MethodDescriptor md = (MethodDescriptor) obj;

            return exact == md.exact &&
                    methodName.equals(md.methodName) &&
                    cls.equals(md.cls) &&
                    java.util.Arrays.equals(paramTypes, md.paramTypes);
        }

        /**
         * Returns the string length of method name. I.e. if the hashcodes are different, the objects are different. If the hashcodes are the same, need to use the equals method to
         * determine equality.
         *
         * @return the string length of method name.
         */
        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}
