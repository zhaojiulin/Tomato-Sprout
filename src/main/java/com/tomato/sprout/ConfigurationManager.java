package com.tomato.sprout;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: 全局唯一配置文件
 * @date 2025/10/18 18:14
 */
public class ConfigurationManager {
    private static volatile ConfigurationManager instance;
    private static Properties properties;

    private ConfigurationManager() {
        // 唯一初始化
        loadConfiguration();
    }

    private void loadConfiguration() {
        properties = new Properties();
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (resourceAsStream != null) {
                // 加载
                properties.load(resourceAsStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取实例
     *
     * @return
     */
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                // 双重检查
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    public String getProperty(String key) {
        String property = properties.getProperty(key);
        return property == null ? "" : property;
    }
}
