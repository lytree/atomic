package top.yang.net.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import top.yang.net.config.OkHttpClientConfiguration;


@ConfigurationProperties(
        prefix = "retrofit"
)
public class RetrofitProperties {

    @NestedConfigurationProperty
    private OkHttpClientConfiguration okHttpClientAutoConfiguration;

    public OkHttpClientConfiguration getOkHttpClientAutoConfiguration() {
        return okHttpClientAutoConfiguration;
    }

    public void setOkHttpClientAutoConfiguration(OkHttpClientConfiguration okHttpClientAutoConfiguration) {
        this.okHttpClientAutoConfiguration = okHttpClientAutoConfiguration;
    }
}
