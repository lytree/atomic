package top.yang.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.yang.utils.SnowFlake;

/**
 * @author PrideYang
 */
@Configuration
public class SnowFlakeBeanConfiguration {

  @Value("${top.yang.datacenterId:1}")
  private Integer datacenterId;
  @Value("${top.yang.machineId:1}")
  private Integer machineId;


  @Bean
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
