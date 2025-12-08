package com.tomato.sprout.web.mapping.request;

import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: GET请求
 * // 单个字段
 * // 单个对象
 * // 单个字段 单个对象
 * @date 2025/10/21 23:09
 */
public class GetMappingHandle extends AbstractHandleMapping{
    private final Logger log = Logger.getLogger(this.getClass().getName());

    @Override
    public HashMap<String, Object> doParam(HttpServletRequest req) {
        HashMap<String, Object> paramMap = new HashMap<>();
        Enumeration<String> parameterNames = req.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            log.info("parameterName: " + parameterName);
            paramMap.put(parameterName, req.getParameter(parameterName));
        }
        try {
            BufferedReader reader = req.getReader();
            StringBuilder jsonBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            if(!jsonBuilder.isEmpty()) {
                String jsonBody = jsonBuilder.toString();
                System.out.println("jsonBody: " + jsonBody);
                paramMap.put("arg0", jsonBody);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return paramMap;
    }

}
