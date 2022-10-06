package top.lytree.model.properties;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import top.lytree.model.enums.ValueEnum;

/**
 * Property enum.
 *
 * @author johnniang
 * @date 3/26/19
 */
public interface PropertyEnum {

    /**
     * Converts value to corresponding enum.
     *
     * @param enumType enum type
     * @param value    database value
     * @return corresponding enum
     */
    static <String, T extends Enum<T> & PropertyEnum> T valueToEnum(Class<T> enumType, String value) {
        if (null == enumType) {
            throw new IllegalArgumentException("enum type must not be null");
        }
        if (null == value) {
            throw new IllegalArgumentException("value must not be null");
        }
        if (!enumType.isEnum()) {
            throw new IllegalArgumentException("type must be an enum type");
        }

        return Stream.of(enumType.getEnumConstants())
                .filter(item ->
                        item.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown database value: " + value));
    }

    /**
     * Converts to value with corresponding type
     *
     * @param value string value must not be blank
     * @param type  property value type must not be null
     * @param <T>   property value type
     * @return property value
     */
    @SuppressWarnings("unchecked")
    static <T> T convertTo(String value, Class<T> type) {
        if (null == type) {
            throw new IllegalArgumentException("Type must not be null");
        }
        if (null == value) {
            throw new IllegalArgumentException("Value must not be null");
        }

        if (type.isAssignableFrom(String.class)) {
            return (T) value;
        }

        if (type.isAssignableFrom(Integer.class)) {
            return (T) Integer.valueOf(value);
        }

        if (type.isAssignableFrom(Long.class)) {
            return (T) Long.valueOf(value);
        }

        if (type.isAssignableFrom(Boolean.class)) {
            return (T) Boolean.valueOf(value);
        }

        if (type.isAssignableFrom(Short.class)) {
            return (T) Short.valueOf(value);
        }

        if (type.isAssignableFrom(Byte.class)) {
            return (T) Byte.valueOf(value);
        }

        if (type.isAssignableFrom(Double.class)) {
            return (T) Double.valueOf(value);
        }

        if (type.isAssignableFrom(Float.class)) {
            return (T) Float.valueOf(value);
        }

        // Should never happen
        throw new UnsupportedOperationException(
                "Unsupported convention for blog property type:" + type.getName() + " provided");
    }


    /**
     * Converts to enum.
     *
     * @param <T>   property value enum type
     * @param value string value must not be null
     * @param type  propertye value enum type must not be null
     * @return property enum value or null
     */
    static <T extends PropertyEnum> T convertToEnum(String value, Class<T> type) {
        if (null == value || "".equals(value)) {
            throw new IllegalArgumentException("Property value must not be blank");
        }
        return Stream.of(type.getEnumConstants()).filter(item ->
                        item.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown database value: " + value));

    }

    /**
     * Check the type is support by the blog property.
     *
     * @param type type to check
     * @return true if supports; false else
     */
    static boolean isSupportedType(Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.isAssignableFrom(String.class)
                || type.isAssignableFrom(Number.class)
                || type.isAssignableFrom(Integer.class)
                || type.isAssignableFrom(Long.class)
                || type.isAssignableFrom(Boolean.class)
                || type.isAssignableFrom(Short.class)
                || type.isAssignableFrom(Byte.class)
                || type.isAssignableFrom(Double.class)
                || type.isAssignableFrom(Float.class)
                || type.isAssignableFrom(Enum.class)
                || type.isAssignableFrom(ValueEnum.class);
    }

    static Map<String, PropertyEnum> getValuePropertyEnumMap() {
        // Get all properties
        List<Class<? extends PropertyEnum>> propertyEnumClasses = new LinkedList<>();

        Map<String, PropertyEnum> result = new HashMap<>();

        propertyEnumClasses.forEach(propertyEnumClass -> {
            PropertyEnum[] propertyEnums = propertyEnumClass.getEnumConstants();

            for (PropertyEnum propertyEnum : propertyEnums) {
                result.put(propertyEnum.getValue(), propertyEnum);
            }
        });

        return result;
    }

    String getValue();

    /**
     * Get property type.
     *
     * @return property type
     */
    Class<?> getType();

    /**
     * Default value.
     *
     * @return default value
     */
    String defaultValue();

    /**
     * Default value with given type.
     *
     * @param propertyType property type must not be null
     * @param <T>          property type
     * @return default value with given type
     */
    default <T> T defaultValue(Class<T> propertyType) {
        // Get default value
        String defaultValue = defaultValue();
        if (defaultValue == null) {
            return null;
        }

        // Convert to the given type
        return PropertyEnum.convertTo(defaultValue, propertyType);
    }

}
