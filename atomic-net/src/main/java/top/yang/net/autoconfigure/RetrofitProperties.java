package top.yang.net.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Converter.Factory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import top.yang.net.config.OkHttpClientConfiguration;


@ConfigurationProperties(
        prefix = "retrofit"
)
public class RetrofitProperties {

    @NestedConfigurationProperty
    private OkHttpClientConfiguration okHttpClientAutoConfiguration;


    private Class<? extends Converter.Factory>[] globalConverter = (Class<? extends Converter.Factory>[]) new Class[]{JacksonConverterFactory.class};


    private Class<? extends CallAdapter.Factory>[] globalCallAdapter;


    public Class<? extends Factory>[] getGlobalConverter() {
        return globalConverter;
    }

    public void setGlobalConverter(Class<? extends Factory>[] globalConverter) {
        this.globalConverter = globalConverter;
    }

    public Class<? extends CallAdapter.Factory>[] getGlobalCallAdapter() {
        return globalCallAdapter;
    }

    public void setGlobalCallAdapter(Class<? extends CallAdapter.Factory>[] globalCallAdapter) {
        this.globalCallAdapter = globalCallAdapter;
    }

    public OkHttpClientConfiguration getOkHttpClientAutoConfiguration() {
        return okHttpClientAutoConfiguration;
    }

    public void setOkHttpClientAutoConfiguration(OkHttpClientConfiguration okHttpClientAutoConfiguration) {
        this.okHttpClientAutoConfiguration = okHttpClientAutoConfiguration;
    }
}
