/**
 * Copyright 2015-现在 广州市领课网络科技有限公司
 */
package top.yang.system;

import java.io.IOException;
import java.util.Properties;

/**
 * 配置文件读取工具类
 */
public final class SystemUtil {


    private SystemUtil() {
    }

    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("system.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getProperty(String keyName) {
        return properties.getProperty(keyName, "").trim();
    }

}
