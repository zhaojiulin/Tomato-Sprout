package com.tomato.sprout.orm.result;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class BasicResultTypeHandler<T> extends ResultTypeHandle<T> implements ResultHandle<T> {
    private final Class<T> type;
    private final String columnName;

    public BasicResultTypeHandler(Class<T> type) {
        this(type, null);
    }

    public BasicResultTypeHandler(Class<T> type, String columnName) {
        this.type = type;
        this.columnName = columnName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T handle(ResultSet rs) throws SQLException {
        if(null == rs) {
            return null;
        }
        if (!rs.next()) {
            return null;
        }
        // 确定列名：优先使用指定的列名，否则使用第一列
        String actualColumnName = columnName;
        if (actualColumnName == null) {
            ResultSetMetaData metaData = rs.getMetaData();
            actualColumnName = metaData.getColumnLabel(1); // 使用别名
        }

        return getTypeValue(type, rs, actualColumnName);
    }

    @Override
    public Class<T> getResultType() {
        return type;
    }
}
