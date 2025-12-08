package com.tomato.sprout.web.mapping;

import com.tomato.sprout.constant.RequestMethod;
import com.tomato.sprout.web.anno.RequestBody;
import com.tomato.sprout.web.anno.RequestParam;
import com.tomato.sprout.web.anno.WebController;
import com.tomato.sprout.web.anno.WebRequestMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: 请求映射单例
 * @date 2025/10/21 23:38
 */
public class HandleMethodMappingHolder {
    private static volatile HandleMethodMappingHolder instance;
    /**
     * web请求与控制器映射信息
     */
    private final ConcurrentHashMap<String, HandlerMethod> handleMapping = new ConcurrentHashMap<>();

    private HandleMethodMappingHolder() {

    }

    /**
     * 单例
     * @return
     */
    public static HandleMethodMappingHolder getInstance() {
        // 使用CAS思想，确保只设置一次
        if (instance == null) {
            synchronized (HandleMethodMappingHolder.class) {
                if (instance == null) {
                    instance = new HandleMethodMappingHolder();
                }
            }
        }
        return instance;
    }

    /**
     * 注册请求地址与方法映射
     *
     * @param controllerClass
     * @param newInstance
     */
    public void processController(Class<?> controllerClass, Object newInstance) {
        if (!controllerClass.isAnnotationPresent(WebController.class)) {
            return;
        }
        WebController webControllerAnnotation =
                controllerClass.getAnnotation(WebController.class);
        String basePath = webControllerAnnotation.value();
        WebRequestMapping parentRequest = controllerClass.getAnnotation(WebRequestMapping.class);
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(WebRequestMapping.class)) {
                WebRequestMapping mappingAnnotation =
                        method.getAnnotation(WebRequestMapping.class);
                String path = basePath + mappingAnnotation.value();
                RequestMethod requestMethod = mappingAnnotation.method();
                if(Objects.nonNull(parentRequest)) {
                    path = parentRequest.value() + path;
                }
                String key = requestMethod.name() + ":" + path;
                // 重复路径检查
                if(handleMapping.containsKey(key)) {
                    throw new RuntimeException("web url mapping repeat");
                }
                Parameter[] parameters = method.getParameters();
                LinkedHashMap<String, Class<?>> params = new LinkedHashMap<>();
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    // 单个形参名称
                    if (parameter.isAnnotationPresent(RequestParam.class)) {
                        RequestParam annotation = parameter.getAnnotation(RequestParam.class);
                        params.put(annotation.value(), parameter.getType());
                    }
                    // 对象形参
                    if(parameter.isAnnotationPresent(RequestBody.class)) {
                        params.put(parameter.getName(), parameter.getType());
                    }
                    // 无注解的形参
                    if(!parameter.isAnnotationPresent(RequestParam.class) && !parameter.isAnnotationPresent(RequestBody.class)) {
                        params.put(parameter.getName(), parameter.getType());
                    }
                }
                HandlerMethod handlerMethod = new HandlerMethod(newInstance, method, path, new RequestMethod[]{requestMethod}, params);
                handleMapping.put(key, handlerMethod);
                System.out.println("Mapped: " + key + " -> " + method.getName());
            }
        }
    }


    /**
     * 获取请求映射信息
     *
     * @param method
     * @param uri
     * @return
     */
    public HandlerMethod getHandlerMethod(String method, String uri) {
        return instance.handleMapping.get(String.format("%s:%s", method, uri));
    }

}
