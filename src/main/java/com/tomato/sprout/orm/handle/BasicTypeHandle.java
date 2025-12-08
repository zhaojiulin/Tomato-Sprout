package com.tomato.sprout.orm.handle;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class BasicTypeHandle<T> implements TypeHandle<T> {
    private final Class<T> type;

    public BasicTypeHandle(Class<T> type) {
        this.type = type;
    }

    @Override
    public void setParameter(PreparedStatement ps, int index, Object parameter) throws SQLException {
        if (type == Integer.class || type == int.class) {
            ps.setInt(index, ((Number) parameter).intValue());
        } else if (type == Long.class || type == long.class) {
            ps.setLong(index, ((Number) parameter).longValue());
        } else if (type == Double.class || type == double.class) {
            ps.setDouble(index, ((Number) parameter).doubleValue());
        } else if (type == Float.class || type == float.class) {
            ps.setFloat(index, ((Number) parameter).floatValue());
        } else if (type == Boolean.class || type == boolean.class) {
            ps.setDouble(index, ((Number) parameter).doubleValue());
        } else if (type == String.class) {
            ps.setString(index, parameter.toString());
        } else if (type == BigDecimal.class) {
            ps.setBigDecimal(index, new BigDecimal(String.valueOf(parameter)));
        } else if (type == Date.class) {
            ps.setDate(index, (Date) parameter);
        } else if (type == Timestamp.class) {
            ps.setTimestamp(index, (Timestamp) parameter);
        } else {
            // 其他类型尝试
            ps.setObject(index, parameter);
        }
    }
}
