package com.tomato.sprout.orm;


import com.tomato.sprout.orm.anno.RepoExec;
import com.tomato.sprout.orm.anno.RepoParam;
import com.tomato.sprout.orm.handle.BasicTypeHandle;
import com.tomato.sprout.orm.parsing.ParamMappingTokenHandle;
import com.tomato.sprout.orm.parsing.SqlParseResult;
import com.tomato.sprout.orm.result.BasicResultTypeHandler;
import com.tomato.sprout.orm.result.ListResultTypeHandle;
import com.tomato.sprout.singleton.DatabaseConnectionPool;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class TomatoMapperProxyFactory {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Set<String> OBJECT_METHODS = Set.of(
            "toString", "hashCode", "equals",
            "clone", "finalize", "getClass",
            "notify", "notifyAll", "wait"
    );

    public <T> T getProxy(Class<T> clazz) {
        // 链接->>数据库连接池
        // 构造预编译
        // 执行sql
        // 根据方法返回类型封装数据

        Object proxyInstance = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{clazz}, (proxy, method, args) -> {
            if(OBJECT_METHODS.contains(method.getName())) {
                return handleObjectMethod(proxy, method, args);
            }
            // sql
            RepoExec annotation = method.getAnnotation(RepoExec.class);
            String sql = annotation.value();
            // sql处理为预编译使用
            ParamMappingTokenHandle paramMappingTokenHandle = new ParamMappingTokenHandle();
            SqlParseResult sqlParseResult = paramMappingTokenHandle.handleToken(sql);
            // 数据库连接池
            DatabaseConnectionPool databaseConnectionPool = DatabaseConnectionPool.getInstance();
            Connection connection = databaseConnectionPool.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlParseResult.getParseSql());
            // 参数映射关系
            HashMap<String, Object> paramMapping = paramValueMapping(method, args);
            // 预编译
            List<Object> params = new ArrayList<>();
            for (int i = 0; i < sqlParseResult.getParamList().size(); i++) {
                Object object = paramMapping.get(sqlParseResult.getParamList().get(i));
                params.add(object);
                BasicTypeHandle<?> basicTypeHandle = new BasicTypeHandle<>(object.getClass());
                basicTypeHandle.setParameter(preparedStatement, i + 1, object);
            }
            System.out.println("SQL："+ sqlParseResult.getParseSql());
            System.out.println("params：" + params);
            // 执行
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            // 返回数据处理
            Object result = getResult(method, resultSet);
            // 归还连接池
            databaseConnectionPool.releaseConnection(connection);
            return result;
        });
        T proxyInstance1 = (T) proxyInstance;
        return proxyInstance1;
    }

    private <T> Object getResult(Method method, ResultSet resultSet) throws SQLException {
        Class<?> methodReturnType = method.getReturnType();
        Object result = null;
        // 基本数据类型
        if (isBasicType(methodReturnType)) {
            BasicResultTypeHandler basicResultTypeHandler = new BasicResultTypeHandler<>(methodReturnType);
            result = basicResultTypeHandler.handle(resultSet);
        } else if (List.class.isAssignableFrom(methodReturnType)) {
            // 集合类型
            Class classType = null;
            Type genericReturnType = method.getGenericReturnType();
            boolean isList = false;
            if (genericReturnType instanceof Class) {
                classType = (Class) genericReturnType;
            } else if (genericReturnType instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
                classType = (Class) actualTypeArguments[0];
                isList = true;
            }
            ListResultTypeHandle listResultTypeHandle = new ListResultTypeHandle<>(classType);
            List<T> handle = listResultTypeHandle.handle(resultSet);
            if (!isList && handle.size() > 0) {
                throw new RuntimeException("Excepted one but found " + handle.size());
            }
            result = isList ? handle : handle.get(0);
        }
        return result;
    }

    private static HashMap<String, Object> paramValueMapping(Method method, Object[] args) {
        HashMap<String, Object> paramMapping = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            paramMapping.put(parameter.getName(), args[i]);
            RepoParam parameterAnnotation = parameter.getAnnotation(RepoParam.class);
            paramMapping.put(parameterAnnotation.value(), args[i]);

        }
        return paramMapping;
    }

    private boolean isBasicType(Class<?> type) {
        return type.isPrimitive() ||
                type == String.class ||
                type == Integer.class ||
                type == Long.class ||
                type == Double.class ||
                type == Float.class ||
                type == Boolean.class ||
                type == BigDecimal.class ||
                type == Date.class ||
                type == Timestamp.class;
    }

    private Object handleObjectMethod(Object proxy, Method method, Object[] args) throws CloneNotSupportedException {
        String methodName = method.getName();

        System.out.println("处理Object方法: " + methodName);

        switch (methodName) {
            case "toString":
                return "Proxy[" + methodName +
                        "@" + System.identityHashCode(proxy) + "]";

            case "hashCode":
                return System.identityHashCode(proxy);

            case "equals":
                if (args != null && args.length == 1) {
                    return proxy == args[0];
                }
                return false;

            case "clone":
                throw new CloneNotSupportedException("代理对象不支持clone");

            case "finalize":
                // 什么都不做，让GC处理
                return null;

            case "getClass":
                return proxy.getClass();

            case "notify":
            case "notifyAll":
            case "wait":
                // 这些方法不应该通过代理调用
                synchronized (proxy) {
                    try {
                        if ("wait".equals(methodName)) {
                            long timeout = args != null && args.length > 0 ?
                                    ((Number) args[0]).longValue() : 0;
                            proxy.wait(timeout);
                        } else if ("notify".equals(methodName)) {
                            proxy.notify();
                        } else {
                            proxy.notifyAll();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
                return null;

            default:
                throw new UnsupportedOperationException("不支持的Object方法: " + methodName);
        }
    }
}
