package top.yang.net.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import retrofit2.CallAdapter;
import retrofit2.CallAdapter.Factory;
import retrofit2.Converter;
import retrofit2.Retrofit;
import top.yang.net.annotation.RetrofitClient;
import top.yang.net.autoconfigure.RetrofitProperties;

public class RetrofitClientFactoryBean<T> implements FactoryBean<T>, EnvironmentAware, ApplicationContextAware {

    private static final String SUFFIX = "/";
    private Environment environment;
    private Class<T> mapperInterface;
    private RetrofitProperties retrofitProperties;
    private ApplicationContext applicationContext;

    public RetrofitClientFactoryBean() {

    }


    public RetrofitClientFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public T getObject() throws Exception {
        return getRetrofit(mapperInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return this.mapperInterface;
    }

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }


    /**
     * 获取Retrofit实例，一个retrofitClient接口对应一个Retrofit实例 Obtain a Retrofit instance, a retrofitClient interface corresponds to a Retrofit instance
     *
     * @param retrofitClientInterfaceClass retrofitClientInterfaceClass
     * @return Retrofit instance
     */
    private synchronized Retrofit getRetrofit(Class<?> retrofitClientInterfaceClass) {
        RetrofitClient retrofitClient = retrofitClientInterfaceClass.getAnnotation(RetrofitClient.class);
        String baseUrl = retrofitClient.baseUrl();

        baseUrl = convertBaseUrl(retrofitClient, baseUrl, environment);

        OkHttpClient client = new OkHttpClient();
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .validateEagerly(retrofitClient.validateEagerly())
                .client(client);

        // 添加CallAdapter.Factory
        Class<? extends CallAdapter.Factory>[] callAdapterFactoryClasses = retrofitClient.callAdapter();
        Class<? extends CallAdapter.Factory>[] globalCallAdapterFactoryClasses = retrofitProperties.getGlobalCallAdapter();

        List<Factory> callAdapterFactories = getCallAdapterFactories(callAdapterFactoryClasses, globalCallAdapterFactoryClasses);
        if (!CollectionUtils.isEmpty(callAdapterFactories)) {
            callAdapterFactories.forEach(retrofitBuilder::addCallAdapterFactory);
        }
        // 添加Converter.Factory
        Class<? extends Converter.Factory>[] converterFactoryClasses = retrofitClient.converter();
        Class<? extends Converter.Factory>[] globalConverterFactoryClasses = retrofitProperties.getGlobalConverter();

        List<Converter.Factory> converterFactories = getConverterFactories(converterFactoryClasses, globalConverterFactoryClasses);
        if (!CollectionUtils.isEmpty(converterFactories)) {
            converterFactories.forEach(retrofitBuilder::addConverterFactory);
        }
        return retrofitBuilder.build();
    }

    private List<CallAdapter.Factory> getCallAdapterFactories(Class<? extends CallAdapter.Factory>[] callAdapterFactoryClasses,
            Class<? extends CallAdapter.Factory>[] globalCallAdapterFactoryClasses) throws IllegalAccessException, InstantiationException {
        List<Class<? extends CallAdapter.Factory>> combineCallAdapterFactoryClasses = new ArrayList<>();

        if (callAdapterFactoryClasses != null && callAdapterFactoryClasses.length != 0) {
            combineCallAdapterFactoryClasses.addAll(Arrays.asList(callAdapterFactoryClasses));
        }

        if (globalCallAdapterFactoryClasses != null && globalCallAdapterFactoryClasses.length != 0) {
            combineCallAdapterFactoryClasses.addAll(Arrays.asList(globalCallAdapterFactoryClasses));
        }

        if (combineCallAdapterFactoryClasses.isEmpty()) {
            return Collections.emptyList();
        }

        List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>();

        for (Class<? extends CallAdapter.Factory> callAdapterFactoryClass : combineCallAdapterFactoryClasses) {
            CallAdapter.Factory callAdapterFactory = applicationContext.getBean(callAdapterFactoryClass);

            if (callAdapterFactory == null) {
                callAdapterFactory = callAdapterFactoryClass.newInstance();
            }

        }
        return callAdapterFactories;
    }

    private List<Converter.Factory> getConverterFactories(Class<? extends Converter.Factory>[] converterFactoryClasses,
            Class<? extends Converter.Factory>[] globalConverterFactoryClasses) throws IllegalAccessException, InstantiationException {
        List<Class<? extends Converter.Factory>> combineConverterFactoryClasses = new ArrayList<>();

        if (converterFactoryClasses != null && converterFactoryClasses.length != 0) {
            combineConverterFactoryClasses.addAll(Arrays.asList(converterFactoryClasses));
        }

        if (globalConverterFactoryClasses != null && globalConverterFactoryClasses.length != 0) {
            combineConverterFactoryClasses.addAll(Arrays.asList(globalConverterFactoryClasses));
        }

        if (combineConverterFactoryClasses.isEmpty()) {
            return Collections.emptyList();
        }

        List<Converter.Factory> converterFactories = new ArrayList<>();

        for (Class<? extends Converter.Factory> converterFactoryClass : combineConverterFactoryClasses) {
            Converter.Factory converterFactory = CONVERTER_FACTORIES_CACHE.get(converterFactoryClass);
            if (converterFactory == null) {
                converterFactory = ApplicationContextUtils.getBean(applicationContext, converterFactoryClass);
                if (converterFactory == null) {
                    converterFactory = converterFactoryClass.newInstance();
                }
                CONVERTER_FACTORIES_CACHE.put(converterFactoryClass, converterFactory);
            }
            converterFactories.add(converterFactory);
        }
        return converterFactories;
    }

    private static String convertBaseUrl(RetrofitClient retrofitClient, String baseUrl, Environment environment) {
        if (StringUtils.hasText(baseUrl)) {
            baseUrl = environment.resolveRequiredPlaceholders(baseUrl);
            // 解析baseUrl占位符
            if (!baseUrl.endsWith(SUFFIX)) {
                baseUrl += SUFFIX;
            }
        }
        return baseUrl;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.retrofitProperties = applicationContext.getBean(RetrofitProperties.class);
    }
}
