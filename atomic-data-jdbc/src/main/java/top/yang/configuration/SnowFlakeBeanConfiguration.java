package top.yang.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.event.BeforeSaveCallback;
import top.yang.domain.pojo.BaseEntity;
import top.yang.utils.SnowFlake;
import top.yang.utils.StringUtils;

/**
 * @author PrideYang
 */
@Configuration
public class SnowFlakeBeanConfiguration {

    @Value("${top.yang.datacenterId:1}")
    private Integer datacenterId;
    @Value("${top.yang.machineId:1}")
    private Integer machineId;


    @Bean("snowFlake")
    public SnowFlake snowFlake() {
        return new SnowFlake(datacenterId, machineId);
    }


    public Integer getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(Integer datacenterId) {
        this.datacenterId = datacenterId;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }
}
