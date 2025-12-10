package com.tomato.sprout.web.serve;

import com.tomato.sprout.TomatoApplicationContext;
import com.tomato.sprout.anno.Component;
import com.tomato.sprout.interfaces.ApplicationContextAware;
import com.tomato.sprout.unique.ConfigurationManager;
import com.tomato.sprout.web.servlet.DispatcherServlet;
import com.tomato.sprout.web.servlet.TomatoServletInitializer;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

/**
 * @author zhaojiulin
 * @version 1.0
 * @Description: 内嵌tomcat
 * @Date 2025/10/18 13:00
 */
@Component
public class TomcatEmbeddedServer implements EmbeddedServer, ApplicationContextAware {
    private int port = 8088;

    public TomcatEmbeddedServer() {
    }

    @Override
    public void setApplicationContext(TomatoApplicationContext applicationContext) {
        String servePort = ConfigurationManager.getInstance().getProperty("serve.port");
        if (servePort != null) {
            port = Integer.parseInt(servePort);
        }
    }

    @Override
    public void start() {
        try {
            Tomcat tomcat = new Tomcat();
            // 设置端口（避免冲突可以使用8081）
            tomcat.setPort(port);

            // 设置基础目录
            tomcat.setBaseDir(createTempDir().getAbsolutePath());

            // 必须调用getConnector()来初始化连接器
            Connector connector = tomcat.getConnector();
            connector.setProperty("maxThreads", "200");
            connector.setProperty("connectionTimeout", "30000");

            // 3. 创建上下文并添加映射Servlet
            Context ctx = tomcat.addContext("", null);
            // 映射所有请求到Servlet
            ctx.addServletContainerInitializer(new TomatoServletInitializer(), new HashSet<>());

            tomcat.start();
            System.out.println("TomatoBoot application started on port: " + this.port);

            tomcat.getServer().await();

        } catch (Exception e) {
            throw new RuntimeException("Failed to start embedded Tomcat", e);
        }
    }

    private static File createTempDir() {
        try {
            File tempDir = File.createTempFile("tomcat.", ".dir");
            tempDir.delete();
            tempDir.mkdir();
            tempDir.deleteOnExit();
            return tempDir;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
