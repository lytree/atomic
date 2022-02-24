package top.yang.net.autoconfigure;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AssignableTypeFilter;
import top.yang.net.spring.RetrofitClientFactoryBean;
import top.yang.net.spring.RetrofitFactoryBean;

public class ClassPathRetrofitClientScanner extends ClassPathBeanDefinitionScanner {

    private ResourceLoader resourceLoader;
    private Class<? extends Annotation> annotationClass;

    private Class<?> markerInterface;


    public ClassPathRetrofitClientScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }


    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void setMarkerInterface(Class<?> markerInterface) {
        this.markerInterface = markerInterface;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void registerFilters() {
        if (annotationClass != null) {
            addIncludeFilter(new AssignableTypeFilter(this.annotationClass) {
                @Override
                protected boolean matchClassName(String className) {
                    return false;
                }
            });
        }
    }

    @NotNull
    @Override
    protected Set<BeanDefinitionHolder> doScan(@NotNull String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        if (beanDefinitionHolders.isEmpty()) {
            logger.warn("No RetrofitClient was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitionHolders);
        }
        return beanDefinitionHolders;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            definition = (GenericBeanDefinition) beanDefinitionHolder.getBeanDefinition();
            if (logger.isDebugEnabled()) {
                logger.debug("Creating RetrofitClientBean with name '" + beanDefinitionHolder.getBeanName()
                        + "' and '" + definition.getBeanClassName() + "' Interface");
            }
            String beanClassName = definition.getBeanClassName();
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
            // beanClass全部设置为RetrofitFactoryBean
            definition.setBeanClass(RetrofitClientFactoryBean.class);
        }
    }
}
