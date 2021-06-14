/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package top.yang.system;


import top.yang.exception.ConfigException;

import java.io.IOException;
import java.util.Properties;

/**
 * 配置文件读取工具类
 */
public final class ConfigUtil {

    private ConfigUtil() {
    }

    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("top.yang.config.properties"));
//            logger.warn("当前环境配置为：{}", getProperty("env"));
        } catch (IOException e) {
            throw new ConfigException("9998", "获取环境配置变量初始化异常");
        }
    }

    private static String getProperty(String keyName) {
        return properties.getProperty(keyName, "").trim();
    }

}
