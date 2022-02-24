package top.yang.net.spring;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import retrofit2.CallAdapter;
import retrofit2.CallAdapter.Factory;
import retrofit2.Converter;
import retrofit2.Retrofit;
import top.yang.net.annotation.RetrofitClient;

public class RetrofitClientFactoryBean<T> implements FactoryBean<T>, EnvironmentAware, InitializingBean {

    private static final String SUFFIX = "/";
    private OkHttpClient okHttpClient;
    private Environment environment;
    private Class<T> mapperInterface;

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
        return null;
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

    @Override
    public void afterPropertiesSet() throws Exception {

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
        Class<? extends CallAdapter.Factory>[] callAdapterFactoryClasses = retrofitClient.callAdapterFactories();
        Class<? extends CallAdapter.Factory>[] globalCallAdapterFactoryClasses = retrofitConfigBean.getGlobalCallAdapterFactoryClasses();
        List<Factory> callAdapterFactories = getCallAdapterFactories(callAdapterFactoryClasses, globalCallAdapterFactoryClasses);
        if (!CollectionUtils.isEmpty(callAdapterFactories)) {
            callAdapterFactories.forEach(retrofitBuilder::addCallAdapterFactory);
        }
        // 添加Converter.Factory
        Class<? extends Converter.Factory>[] converterFactoryClasses = retrofitClient.converterFactories();
        Class<? extends Converter.Factory>[] globalConverterFactoryClasses = retrofitConfigBean.getGlobalConverterFactoryClasses();

        List<Converter.Factory> converterFactories = getConverterFactories(converterFactoryClasses, globalConverterFactoryClasses);
        if (!CollectionUtils.isEmpty(converterFactories)) {
            converterFactories.forEach(retrofitBuilder::addConverterFactory);
        }
        return retrofitBuilder.build();
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
}
