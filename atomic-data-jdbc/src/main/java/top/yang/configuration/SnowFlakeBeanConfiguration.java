package top.yang.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.yang.utils.idgen.contract.IIdGenerator;
import top.yang.utils.idgen.contract.IdGeneratorOptions;
import top.yang.utils.idgen.idgen.DefaultIdGenerator;

/**
 * @author PrideYang
 */
@Configuration
public class SnowFlakeBeanConfiguration {

    @Value("${top.yang.workId:1}")
    private Short workId;


    @Bean("snowFlake")
    public IIdGenerator snowFlake() {
        return new DefaultIdGenerator(new IdGeneratorOptions(workId));
    }

}
