package com.tomato.sprout.orm.handle;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface TypeHandle<T> {
    void setParameter(PreparedStatement ps, int index, T parameter) throws SQLException;
}
