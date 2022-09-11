package top.yang.configuration;


import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.util.ClassUtils;
import top.yang.model.enums.ValueEnum;
import top.yang.model.properties.PropertyEnum;


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
        AttributeConverterAutoGenerator generator = new AttributeConverterAutoGenerator(factory.getBeanClassLoader());

        findValueEnumClasses()
                .stream()
                .map(generator::generate)
                .map(Class::getName)
                .forEach(pui::addManagedClassName);
    }

    private Set<Class<?>> findValueEnumClasses() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        // include ValueEnum class
        scanner.addIncludeFilter(new AssignableTypeFilter(ValueEnum.class));
        // exclude PropertyEnum class
        scanner.addExcludeFilter(new AssignableTypeFilter(PropertyEnum.class));

        return scanner.findCandidateComponents(PACKAGE_TO_SCAN)
                .stream()
                .filter(bd -> bd.getBeanClassName() != null)
                .map(bd -> ClassUtils.resolveClassName(bd.getBeanClassName(), null))
                .collect(Collectors.toSet());
    }
}
