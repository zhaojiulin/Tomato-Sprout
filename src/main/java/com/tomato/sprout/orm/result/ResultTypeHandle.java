package com.tomato.sprout.orm.result;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ResultTypeHandle<T> {

    @SuppressWarnings("unchecked")
    protected T getTypeValue(Class<T> type, ResultSet rs, String columnName) throws SQLException {
        if (rs.wasNull()) {
            return getNullValue(type);
        }
        Object value = rs.getObject(columnName);
        // 处理数据库NULL值
        if (type == Integer.class || type == int.class) {
            return (T) Integer.valueOf(rs.getInt(columnName));
        } else if (type == Long.class || type == long.class) {
            return (T) Long.valueOf(rs.getLong(columnName));
        } else if (type == Double.class || type == double.class) {
            return (T) Double.valueOf(rs.getDouble(columnName));
        } else if (type == Float.class || type == float.class) {
            return (T) Float.valueOf(rs.getFloat(columnName));
        } else if (type == Boolean.class || type == boolean.class) {
            return (T) Boolean.valueOf(rs.getBoolean(columnName));
        } else if (type == String.class) {
            return (T) rs.getString(columnName);
        } else if (type == BigDecimal.class) {
            return (T) rs.getBigDecimal(columnName);
        } else if (type == Date.class) {
            return (T) rs.getDate(columnName);
        } else if (type == Timestamp.class) {
            return (T) rs.getTimestamp(columnName);
        } else {
            // 其他类型尝试直接获取
            return (T) value;
        }
    }

    private T getNullValue(Class<T> type) {
        // 对于基本类型，返回默认值；对于包装类型返回null
        if (type == int.class) return (T) Integer.valueOf(0);
        if (type == long.class) return (T) Long.valueOf(0L);
        if (type == double.class) return (T) Double.valueOf(0.0);
        if (type == float.class) return (T) Float.valueOf(0.0f);
        if (type == boolean.class) return (T) Boolean.FALSE;
        return null;
    }
}
