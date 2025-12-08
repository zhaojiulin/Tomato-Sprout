package com.tomato.sprout;

import com.tomato.sprout.web.serve.EmbeddedServer;
import com.tomato.sprout.web.serve.EmbeddedServerFactory;

/**
 * @author zhaojiulin
 * @version 1.0
 * @Description: 容器启动
 * @Date 2025/10/18 14:25
 */
public class TomatoBootApplication {

    public static void start(Class<?> primarySource, String[] args) {
        run(primarySource, args);
    }

    public static TomatoApplicationContext startContext(Class<?> primarySource, String[] args) {
        return run(primarySource, args);
    }

    private static TomatoApplicationContext run(Class<?> primarySource, String[] args) {
        System.out.println("___________                    __                     _________                           __   \n" +
                "\\__    ___/___   _____ _____ _/  |_  ____            /   _____/____________  ____  __ ___/  |_ \n" +
                "  |    | /  _ \\ /     \\\\__  \\\\   __\\/  _ \\   ______  \\_____  \\\\____ \\_  __ \\/  _ \\|  |  \\   __\\\n" +
                "  |    |(  <_> )  Y Y  \\/ __ \\|  | (  <_> ) /_____/  /        \\  |_> >  | \\(  <_> )  |  /|  |  \n" +
                "  |____| \\____/|__|_|  (____  /__|  \\____/          /_______  /   __/|__|   \\____/|____/ |__|  \n" +
                "                     \\/     \\/                              \\/|__|                             ");
        // 创建上下文
        TomatoApplicationContext context = new TomatoApplicationContext();
        // 扫描并注册bean
        context.scanBeanDefinition(primarySource);
        // 初始化bean
        context.refreshBean();
        // 启动内嵌服务器
        startTomcatServer(context);
        return context;
    }

    /**
     * 启动服务器 暂时tomcat
     */
    private static void startTomcatServer(TomatoApplicationContext context) {
        EmbeddedServer embeddedServer = EmbeddedServerFactory.handleServe(context);
        embeddedServer.start();
    }

}
