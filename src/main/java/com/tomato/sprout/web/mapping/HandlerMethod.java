package com.tomato.sprout.web.mapping;

import com.tomato.sprout.constant.RequestMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
/**
 * @Description: 控制器-方法信息
 * @author zhaojiulin
 * @Date 2025/10/18 13:03
 * @version 1.0
 */
public class HandlerMethod {
    private Object controller;     // 控制器实例
    private Method method;         // 处理方法
    private String url;            // 请求URL
    private RequestMethod[] httpMethods; // HTTP方法
    private boolean responseBody;  // 是否返回JSON

    // 参数信息：参数名 -> 参数类型
    private LinkedHashMap<String, Parameter> parameters;
    // 参数信息：形参真实名或者requestParam名 -> 参数类型
    private LinkedHashMap<String, Parameter> nameParameters;

    public HandlerMethod(Object controller, Method method, String url,
                         RequestMethod[] httpMethods, LinkedHashMap<String, Parameter> parameters,  LinkedHashMap<String, Parameter> nameParameters) {
        this.controller = controller;
        this.method = method;
        this.url = url;
        this.httpMethods = httpMethods;
        this.parameters = parameters;
        this.nameParameters = nameParameters;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RequestMethod[] getHttpMethods() {
        return httpMethods;
    }

    public void setHttpMethods(RequestMethod[] httpMethods) {
        this.httpMethods = httpMethods;
    }

    public boolean isResponseBody() {
        return responseBody;
    }

    public void setResponseBody(boolean responseBody) {
        this.responseBody = responseBody;
    }

    public LinkedHashMap<String, Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(LinkedHashMap<String, Parameter> parameters) {
        this.parameters = parameters;
    }

    public LinkedHashMap<String, Parameter> getNameParameters() {
        return nameParameters;
    }

    public void setNameParameters(LinkedHashMap<String, Parameter> nameParameters) {
        this.nameParameters = nameParameters;
    }
}
