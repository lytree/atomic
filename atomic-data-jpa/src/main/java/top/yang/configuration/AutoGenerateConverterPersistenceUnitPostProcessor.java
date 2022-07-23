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
