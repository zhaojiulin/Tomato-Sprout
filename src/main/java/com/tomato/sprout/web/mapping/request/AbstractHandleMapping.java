package com.tomato.sprout.web.mapping.request;

import com.tomato.sprout.web.mapping.HandleMethodMappingHolder;
import com.tomato.sprout.web.mapping.HandlerMethod;
import com.tomato.sprout.web.mapping.MethodInvoker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: 请求方式处理抽象类
 * @date 2025/10/21 23:10
 */
public abstract class AbstractHandleMapping implements HandleMapping {
    private static final Logger log = Logger.getLogger(AbstractHandleMapping.class.getName());

    public final void doHandleMapping(HttpServletRequest req, HttpServletResponse resp) {
        String reqMethod = req.getMethod();
        String requestURI = req.getRequestURI();
        HandlerMethod handlerMethod = HandleMethodMappingHolder.getInstance().getHandlerMethod(reqMethod.toUpperCase(Locale.ROOT), requestURI);
        if (Objects.isNull(handlerMethod)) {
            sendJsonResponse(resp, 500, "{\"error\": \"接口未找到: " + reqMethod + " " + requestURI + "\"}");
            return;
        }
        // 入参解析
        HashMap<String, Object> hashMap = doParam(req);
        // 方法调用
        try {
            Object result = doMethod(handlerMethod, hashMap, resp);
            sendJsonResponse(resp, 200, Objects.nonNull(result) ? result.toString() : "");
        } catch (Exception e ) {
            e.printStackTrace();
            sendJsonResponse(resp, 500, "{\"error\": \"Invocation ERROR: " + handlerMethod.getHttpMethods()[0] + " " + handlerMethod.getUrl() + "\"}");
        }
    }

    private Object doMethod(HandlerMethod handlerMethod, HashMap<String, Object> paramMap, HttpServletResponse resp) throws InvocationTargetException, IllegalAccessException {
        MethodInvoker methodInvoker = new MethodInvoker();
        return methodInvoker.invokeHandler(handlerMethod, paramMap, resp);
    }

    private void sendJsonResponse(HttpServletResponse resp, int status, String json) {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=utf-8");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = null;
        try {
            out = resp.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!json.isEmpty()) {
            out.print(json);
        }
        out.flush();
    }
}
