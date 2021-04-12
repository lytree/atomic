package top.yang;

import org.apache.commons.beanutils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class BeanUtilBean extends BeanUtilsBean {
    private final Log log = LogFactory.getLog(BeanUtil.class);
// ------------------------------------------------------ Private Class Variables

    /**
     * Contains <code>BeanUtilsBean</code> instances indexed by context classloader.
     */
    private static final ContextClassLoaderLocal<BeanUtilBean>
            BEANS_BY_CLASSLOADER = new ContextClassLoaderLocal<BeanUtilBean>() {
        // Creates the default instance used when the context classloader is unavailable
        @Override
        protected BeanUtilBean initialValue() {
            return new BeanUtilBean();
        }
    };

    /**
     * Gets the instance which provides the functionality for {@link BeanUtils}.
     * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container.
     *
     * @return The (pseudo-singleton) BeanUtils bean instance
     */
    public static BeanUtilBean getInstance() {
        return BEANS_BY_CLASSLOADER.get();
    }

    /**
     * Sets the instance which provides the functionality for {@link BeanUtils}.
     * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container.
     *
     * @param newInstance The (pseudo-singleton) BeanUtils bean instance
     */
    public static void setInstance(BeanUtilBean newInstance) {
        BEANS_BY_CLASSLOADER.set(newInstance);
    }

    public void copyPropertiesIgnoreNull(Object dest, Object orig)
            throws IllegalAccessException, InvocationTargetException {

        // Validate existence of the specified beans
        if (dest == null) {
            throw new IllegalArgumentException
                    ("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }
        if (log.isDebugEnabled()) {
            log.debug("BeanUtils.copyProperties(" + dest + ", " +
                    orig + ")");
        }

        // Copy the properties, converting as necessary
        if (orig instanceof DynaBean) {
            DynaProperty[] origDescriptors =
                    ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                // Need to check isReadable() for WrapDynaBean
                // (see Jira issue# BEANUTILS-61)
                if (getPropertyUtils().isReadable(orig, name) &&
                        getPropertyUtils().isWriteable(dest, name)) {
                    Object value = ((DynaBean) orig).get(name);
                    copyProperty(dest, name, value);
                }
            }
        } else if (orig instanceof Map) {
            @SuppressWarnings("unchecked")
            // Map properties are always of type <String, Object>
            Map<String, Object> propMap = (Map<String, Object>) orig;
            for (Map.Entry<String, Object> entry : propMap.entrySet()) {
                String name = entry.getKey();
                if (getPropertyUtils().isWriteable(dest, name)) {
                    copyProperty(dest, name, entry.getValue());
                }
            }
        } else /* if (orig is a standard JavaBean) */ {
            PropertyDescriptor[] origDescriptors =
                    getPropertyUtils().getPropertyDescriptors(orig);
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if ("class".equals(name)) {
                    continue; // No point in trying to set an object's class
                }
                if (getPropertyUtils().isReadable(orig, name) &&
                        getPropertyUtils().isWriteable(dest, name)) {
                    try {
                        Object value =
                                getPropertyUtils().getSimpleProperty(orig, name);
                        if (value != null) {
                            copyProperty(dest, name, value);
                        }
                    } catch (NoSuchMethodException e) {
                        // Should not happen
                    }
                }
            }
        }

    }

}
