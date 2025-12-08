package com.tomato.sprout.orm.result;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultHandle<T> {
    /**
     * 处理查询结果
     * @param rs 结果集
     * @return 处理后的对象
     * @throws SQLException
     */
    T handle(ResultSet rs) throws SQLException;

    /**
     * 获取结果类型
     * @return 结果类型Class
     */
    Class<T> getResultType();
}
