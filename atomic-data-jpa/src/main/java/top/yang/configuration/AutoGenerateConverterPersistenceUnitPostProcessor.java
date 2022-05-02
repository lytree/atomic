package top.yang.configuration;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;
import static net.bytebuddy.description.type.TypeDescription.Generic.Builder.parameterizedType;
import static net.bytebuddy.implementation.FieldAccessor.ofField;
import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.isDefaultConstructor;
import static net.bytebuddy.matcher.ElementMatchers.named;

import java.lang.reflect.Modifier;
import java.util.Set;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.util.ClassUtils;
import top.yang.model.enums.support.ValueEnum;
import top.yang.model.properties.support.PropertyEnum;


class AttributeConverterAutoGenerator {

    /**
     * Auto generation suffix.
     */
    public static final String AUTO_GENERATION_SUFFIX = "$AttributeConverterGeneratedByByteBuddy";

    private final ClassLoader classLoader;

    public AttributeConverterAutoGenerator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public <T> Class<?> generate(Class<T> clazz) {
        try {
            return new ByteBuddy()
                    .with(new NamingStrategy.AbstractBase() {
                        @Override
                        protected String name(TypeDescription superClass) {
                            return clazz.getName() + AUTO_GENERATION_SUFFIX;
                        }
                    })
                    .subclass(
                            parameterizedType(AttributeConverter.class, clazz, Integer.class).build())
                    .annotateType(ofType(Converter.class).define("autoApply", true).build())
                    .constructor(isDefaultConstructor())
                    .intercept(MethodCall.invoke(Object.class.getDeclaredConstructor())
                            .andThen(ofField("enumType").setsValue(clazz)))
                    .defineField("enumType", Class.class, Modifier.PRIVATE | Modifier.FINAL)
                    .method(named("convertToDatabaseColumn"))
                    .intercept(to(AttributeConverterInterceptor.class))
                    .method(named("convertToEntityAttribute"))
                    .intercept(to(AttributeConverterInterceptor.class))
                    .make()
                    .load(this.classLoader, ClassLoadingStrategy.Default.INJECTION.allowExistingTypes())
                    .getLoaded();
        } catch (NoSuchMethodException e) {
            // should never happen
            throw new RuntimeException("Failed to get declared constructor.", e);
        }
    }

    public static boolean isGeneratedByByteBuddy(String className) {
        return StringUtils.endsWith(className, AUTO_GENERATION_SUFFIX);
    }

}

class AttributeConverterInterceptor {

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

/**
 * Attribute converter persistence unit post processor.
 *
 * @author johnniang
 */
public class AutoGenerateConverterPersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {

    private final String PACKAGE_TO_SCAN;

    private final ConfigurableListableBeanFactory factory;

    public AutoGenerateConverterPersistenceUnitPostProcessor(
            ConfigurableListableBeanFactory factory, String PACKAGE_TO_SCAN) {
        this.factory = factory;
        this.PACKAGE_TO_SCAN = PACKAGE_TO_SCAN;
    }

    @Override
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        var generator = new AttributeConverterAutoGenerator(factory.getBeanClassLoader());

        findValueEnumClasses()
                .stream()
                .map(generator::generate)
                .map(Class::getName)
                .forEach(pui::addManagedClassName);
    }

    private Set<Class<?>> findValueEnumClasses() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        // include ValueEnum class
        scanner.addIncludeFilter(new AssignableTypeFilter(ValueEnum.class));
        // exclude PropertyEnum class
        scanner.addExcludeFilter(new AssignableTypeFilter(PropertyEnum.class));

        return scanner.findCandidateComponents(PACKAGE_TO_SCAN)
                .stream()
                .filter(bd -> bd.getBeanClassName() != null)
                .map(bd -> ClassUtils.resolveClassName(bd.getBeanClassName(), null))
                .collect(toUnmodifiableSet());
    }
}
