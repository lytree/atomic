package top.yang.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.util.ParsingUtils;
import org.springframework.lang.NonNullApi;
import org.springframework.util.Assert;

/**
 * @author PrideYang
 */
@Configuration
public class NamingStrategyConfiguration {

    /**
     * Naming strategy for naming entity columns
     *
     * @return PhysicalNamingStrategy
     * @see <a href="https://stackoverflow.com/questions/53334685/how-to-tweak-namingstrategy-for-spring-data-jdbc/53335830#53335830">How to implement {@link NamingStrategy}</a>
     */
    @Bean
    public NamingStrategy namingStrategy() {
        return new NamingStrategy() {
            @Override
            public String getColumnName(RelationalPersistentProperty property) {
                Assert.notNull(property, "Property must not be null.");
                return ParsingUtils.reconcatenateCamelCase(property.getName(), "_");
            }
        };
    }
}
