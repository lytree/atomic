package top.lytree.configuration;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodCall;
import top.lytree.lang.StringUtils;

import java.lang.reflect.Modifier;

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;
import static net.bytebuddy.description.type.TypeDescription.Generic.Builder.parameterizedType;
import static net.bytebuddy.implementation.FieldAccessor.ofField;
import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.isDefaultConstructor;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class AttributeConverterAutoGenerator {

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
