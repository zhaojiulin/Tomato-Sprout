package com.tomato.sprout.web.mapping;

import com.google.gson.Gson;
import com.tomato.sprout.constant.RequestMethod;
import com.tomato.sprout.utils.CommonUtils;
import com.tomato.sprout.web.anno.RequestBody;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

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
        Object[] args = prepareMethodArguments(params, handlerMethod, response);
        return method.invoke(controller, args);
    }

    /**
     * 解析方法与入参
     *
     * @param params
     * @param handlerMethod
     * @return
     */
    private Object[] prepareMethodArguments(HashMap<String, Object> params, HandlerMethod handlerMethod, HttpServletResponse response) {
        LinkedHashMap<String, Parameter> parameters = handlerMethod.getParameters();
        Object[] args = new Object[parameters.size()];
        int i = 0;
        for (Map.Entry<String, Parameter> p : parameters.entrySet()) {
            String paramKey = p.getKey();      // parameters中的键（是arg0、arg1）
            Parameter pType = p.getValue();    // parameters中的Parameter对象

            Object argVal = params.get(paramKey);
            // 真实形参名
            String argName = pType.getName();
            if (Objects.isNull(argVal)) {
                argVal = params.get(argName);
            }
            // 特殊参数
            if (HttpServletResponse.class.isAssignableFrom(pType.getType())) {
                args[i++] = response;
                continue;
            }
            // 基础参数
            if (CommonUtils.isBasic(pType.getType())) {
                if(argVal instanceof HashMap<?,?>) {
                    Map<?, ?> sourceMap = (Map<?, ?>) argVal;
                    argVal = sourceMap.get(argName);
                }
                args[i++] = convertValue(argVal, pType.getType());
                continue;
            }
            // 复杂对象
            args[i++] = resolveComplexParameter(handlerMethod.getHttpMethods()[0], p, argVal == null ? params : argVal);

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
        return convertBasicValue(value, targetType);
    }

    /**
     * 基本类型转换
     *
     * @param value
     * @param targetType
     * @return
     */
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

    /**
     * 是否为json
     *
     * @param o
     * @return
     */
    public boolean isValidJsonGson(Object o) {
        if (null == o) {
            return false;
        }
        String jsonStr = o.toString();
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return false;
        }

        String trimmed = jsonStr.trim();
        if (!(trimmed.startsWith("{") || trimmed.startsWith("["))) {
            return false;
        }

        try {
            Gson gson = new Gson();
            gson.toJson(jsonStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isCastJson(Object o, Class<?> targetType) {
        try {
            Gson gson = new Gson();
            gson.fromJson(o.toString(), targetType);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 对象转换和对象创建
     *
     * @param requestMethod
     * @param p
     * @param object
     * @return
     */
    private Object resolveComplexParameter(RequestMethod requestMethod,
                                           Map.Entry<String, Parameter> p,
                                           Object object) {
        Object newInstance = null;
        Parameter parameter = p.getValue();
        Class<?> paramType = parameter.getType();
        try {
            if (requestMethod == RequestMethod.POST) {
                boolean hasRequestBody = parameter.isAnnotationPresent(RequestBody.class);
                newInstance = createAndBindObject(paramType, object, hasRequestBody);
            } else {
                newInstance = createAndBindObject(paramType, object, true);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return newInstance;
    }

    private Object createAndBindObject(Class<?> paramType, Object object, boolean fields)
            throws Exception {
        if (isValidJsonGson(object) && isCastJson(object, paramType)) {
            return new Gson().fromJson(object.toString(), paramType);
        }
        Object instance = paramType.getDeclaredConstructor().newInstance();
        if (!fields) {
            return instance;
        }
        Map<String, Object> objectMap = objectToMap(object);
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            try {
                Field field = paramType.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object convertedValue = convertValue(fieldValue, field.getType());
                field.set(instance, convertedValue);
            } catch (NoSuchFieldException e) {
                // 字段不存在，跳过
            }
        }

        return instance;
    }

    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        Map<String, Object> map = new HashMap<>();
        if (obj instanceof Map) {
            Map<?, ?> sourceMap = (Map<?, ?>) obj;
            for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();

                // 如果 key 是字符串，直接放入
                if (key instanceof String) {
                    map.put((String) key, value);
                }
                // 如果 key 不是字符串，转换为字符串
                else if (key != null) {
                    map.put(key.toString(), value);
                }
            }
        }
        return map;
    }

}
