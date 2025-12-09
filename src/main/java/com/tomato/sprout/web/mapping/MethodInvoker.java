package com.tomato.sprout.web.mapping;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tomato.sprout.web.model.ReqFile;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: 映射的方法调用
 * @date 2025/10/17 23:08
 */
public class MethodInvoker {

    /**
     * @description: 请求映射调用方法
     * @author zhaojiulin
     * @param: handlerMethod:方法信息 params请求中的参数和值的键值对
     * @return: 执行返回信息
     * @Date: 2025/10/18 13:03
     */
    public Object invokeHandler(HandlerMethod handlerMethod, HashMap<String, Object> params, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
        Object controller = handlerMethod.getController();
        Method method = handlerMethod.getMethod();
        method.setAccessible(true);
        Object[] args = prepareMethodArguments(params, handlerMethod.getParameters(), response);
        return method.invoke(controller, args);
    }

    /**
     * 解析方法与入参
     *
     * @param params
     * @param parameters
     * @return
     */
    private Object[] prepareMethodArguments(HashMap<String, Object> params, LinkedHashMap<String, Class<?>> parameters, HttpServletResponse response) {
        Object[] args = new Object[parameters.size()];
        int i = 0;
        for (Map.Entry<String, Class<?>> p : parameters.entrySet()) {
            if(isBasic(p.getValue())) {
                args[i++] = convertValue(params.get(p.getKey()), p.getValue());
                continue;
            }
            // 额外response参数赋值
            if(HttpServletResponse.class.isAssignableFrom(p.getValue())) {
                args[i++] = response;
                continue;
            }
            Object newInstance = null;
            try {
                Gson gson = new Gson();
                try {
                    // json格式
                    newInstance = gson.fromJson(params.get(p.getKey()).toString(), p.getValue());
                } catch (Exception e) {
                    newInstance = p.getValue().getDeclaredConstructor().newInstance();
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        Field declaredField = newInstance.getClass().getDeclaredField(entry.getKey());
                        declaredField.setAccessible(true);
                        declaredField.set(newInstance, convertValue(params.get(entry.getKey()), declaredField.getType()));
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            args[i++] = newInstance;

        }
        return args;
    }

    /**
     * 字段值类型转换
     *
     * @param value
     * @param targetType
     * @return
     */
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if (targetType.isInstance(value)) {
            return value;
        }

        if (!targetType.isPrimitive()) {
            return new Gson().fromJson(value.toString(), targetType);
        }

        return convertBasicValue(value, targetType);
    }

    private boolean isBasic(Class<?> targetType) {
        if (targetType == String.class) {
            return true;
        } else if (targetType == Integer.class || targetType == int.class) {
            return true;
        } else if (targetType == Long.class || targetType == long.class) {
            return true;
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return true;
        } else if (targetType == Double.class || targetType == double.class) {
            return true;
        } else if (targetType == Float.class || targetType == float.class) {
            return true;
        } else if (targetType == ReqFile.class) {
            return true;
        } else return targetType == Date.class;
    }

    private Object convertBasicValue(Object value, Class<?> targetType) {
        if (targetType == String.class) {
            return value.toString();
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(value.toString());
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(value.toString());
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(value.toString());
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(value.toString());
        } else if (targetType == Float.class || targetType == float.class) {
            return Float.parseFloat(value.toString());
        } else if (targetType == Date.class) {
            return LocalDateTime.parse(value.toString());
        }
        return value;
    }
}
