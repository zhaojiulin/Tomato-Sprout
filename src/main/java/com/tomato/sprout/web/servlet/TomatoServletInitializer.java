package com.tomato.sprout.web.servlet;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;

import java.util.Set;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: 文件上传配置
 * @date 2025/12/9 14:08
 */
public class TomatoServletInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> set, ServletContext ctx) throws ServletException {
        // 1. 创建Servlet实例
        Servlet dispatcherServlet = new DispatcherServlet();

        // 2. 注册Servlet并获取ServletRegistration.Dynamic对象
        ServletRegistration.Dynamic registration = ctx.addServlet(
                "dispatcherServlet", // Servlet名称
                dispatcherServlet
        );

        // 3. 设置URL映射
        registration.addMapping("/*"); // 或你的特定路径，如 "/api/*"`
        registration.setLoadOnStartup(1);

        // 4. 【关键】从注解中读取配置，并显式设置MultipartConfig
        MultipartConfig annotation = DispatcherServlet.class.getAnnotation(MultipartConfig.class);
        if (annotation != null) {
            registration.setMultipartConfig(new MultipartConfigElement(
                    annotation.location(),
                    annotation.maxFileSize(),
                    annotation.maxRequestSize(),
                    (int) annotation.fileSizeThreshold()
            ));
        } else {
            // 如果没有注解，也可以直接创建配置
            registration.setMultipartConfig(new MultipartConfigElement(
                    "", // 临时目录
                    20 * 1024 * 1024, // maxFileSize
                    50 * 1024 * 1024, // maxRequestSize
                    1024 * 1024 // fileSizeThreshold
            ));
        }

        // 5. 设置加载顺序（可选）
        registration.setLoadOnStartup(1);

        System.out.println("DispatcherServlet已注册，并配置了Multipart支持");
    }
}
