package top.yang.configuration;

import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import top.yang.model.enums.ValueEnum;


public class AttributeConverterInterceptor {

    private AttributeConverterInterceptor() {
    }

    @RuntimeType
    public static <T extends Enum<T> & ValueEnum<V>, V> V convertToDatabaseColumn(T attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @RuntimeType
    public static <T extends Enum<T> & ValueEnum<V>, V> T convertToEntityAttribute(V dbData,
            @FieldValue("enumType") Class<T> enumType) {
        return dbData == null ? null : ValueEnum.valueToEnum(enumType, dbData);
    }
}
