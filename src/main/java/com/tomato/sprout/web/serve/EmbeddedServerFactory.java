package com.tomato.sprout.web.serve;

import com.tomato.sprout.TomatoApplicationContext;
import com.tomato.sprout.unique.ConfigurationManager;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: 内嵌服务器工厂类
 * @date 2025/10/22 17:24
 */
public class EmbeddedServerFactory {

    public static EmbeddedServer handleServe(TomatoApplicationContext context) {
        String serveType = ConfigurationManager.getInstance().getProperty("serve.type");
        return switch (serveType) {
            default -> ((TomcatEmbeddedServer)context.getBean("tomcatEmbeddedServer"));
        };

    }
}
