package top.yang.net.autoconfigure;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;
import top.yang.net.annotation.RetrofitClient;

@EnableConfigurationProperties(RetrofitProperties.class)
public class RetrofitAutoConfiguration implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RetrofitAutoConfiguration.class);
    private final RetrofitProperties retrofitProperties;

    public RetrofitAutoConfiguration(RetrofitProperties retrofitProperties) {
        this.retrofitProperties = retrofitProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public static class AutoConfigurationRetrofitClientScannerRegistrar implements ApplicationContextAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {

        private ApplicationContext applicationContext;
        private ResourceLoader resourceLoader;

        @Override
        public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

        @Override
        public void registerBeanDefinitions(@NotNull AnnotationMetadata importingClassMetadata, @NotNull BeanDefinitionRegistry registry) {
            if (!AutoConfigurationPackages.has(this.applicationContext)) {
//                ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);
                return;
            }

            List<String> packages = AutoConfigurationPackages.get(this.applicationContext);
            ClassPathRetrofitClientScanner scanner = new ClassPathRetrofitClientScanner(registry);
            if (resourceLoader != null) {
                scanner.setResourceLoader(resourceLoader);
            }
            scanner.setAnnotationClass(RetrofitClient.class);
            scanner.registerFilters();
            scanner.doScan(StringUtils.toStringArray(packages));
        }


        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }
    }
}
