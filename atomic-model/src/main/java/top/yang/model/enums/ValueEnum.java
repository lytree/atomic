package top.yang.model.enums;

import java.util.stream.Stream;

public interface ValueEnum<T> {


    /**
     * Gets enum value.
     *
     * @return enum value
     */
    T getValue();

    /**
     * Converts value to corresponding enum.
     *
     * @param enumType enum type
     * @param value    database value
     * @param <V>      value generic
     * @param <E>      enum generic
     * @return corresponding enum
     */
    static <V, E extends Enum<E> & ValueEnum<V>> E valueToEnum(Class<E> enumType, V value) {
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
                .filter(item -> item.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown database value: " + value));
    }
}
