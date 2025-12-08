package com.tomato.sprout.orm.parsing;

import java.util.List;

public class SqlParseResult {
    private String parseSql;

    private List<String> paramList;

    public SqlParseResult(String parseSql, List<String> paramList) {
        this.parseSql = parseSql;
        this.paramList = paramList;
    }

    public String getParseSql() {
        return parseSql;
    }

    public void setParseSql(String parseSql) {
        this.parseSql = parseSql;
    }

    public List<String> getParamList() {
        return paramList;
    }

    public void setParamList(List<String> paramList) {
        this.paramList = paramList;
    }
}
