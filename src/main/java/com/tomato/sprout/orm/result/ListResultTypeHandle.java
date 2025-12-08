package com.tomato.sprout.orm.result;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListResultTypeHandle<T> extends ResultTypeHandle<T> implements ResultHandle<List<T>> {
    private final Class<T> type;

    public ListResultTypeHandle(Class<T> type) {
        this.type = type;
    }

    @Override
    public List<T> handle(ResultSet rs) throws SQLException {
        ArrayList<T> objects = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        List<String> columnNames = new ArrayList<>();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            columnNames.add(metaData.getColumnName(i + 1));
        }
        HashMap<String, Method> setMethodMapping = new HashMap<>();
        for (Method declaredMethod : type.getDeclaredMethods()) {
            if (declaredMethod.getName().startsWith("set")) {
                // 对象set
                String propertyName = declaredMethod.getName().substring(3);
                propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
                setMethodMapping.put(propertyName, declaredMethod);
            }
        }
        while (rs.next()) {
            Object newInstance = null;
            try {
                newInstance = type.getDeclaredConstructor().newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < columnNames.size(); i++) {
                Method setMethod = setMethodMapping.get(columnNames.get(i));
                Class<?>[] parameterTypes = setMethod.getParameterTypes();
                try {
                    // 对象成员变量赋值
                    setMethod.invoke(newInstance, getTypeValue((Class<T>) parameterTypes[0], rs, columnNames.get(i)));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            objects.add((T) newInstance);
        }
        return objects;
    }

    @Override
    public Class<List<T>> getResultType() {

        return null;
    }
}
