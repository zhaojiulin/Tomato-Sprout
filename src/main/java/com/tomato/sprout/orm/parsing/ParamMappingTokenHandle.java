package com.tomato.sprout.orm.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamMappingTokenHandle {

    private final Pattern pattern = Pattern.compile("#\\{([^}]+)}");
    public SqlParseResult handleToken(String var1) {
        List<String> list = new ArrayList<>();
        StringBuilder preparedSql = new StringBuilder();
        // 正则匹配#{1111}
        Matcher matcher = pattern.matcher(var1);
        while (matcher.find()) {
            list.add(matcher.group(1));
            matcher.appendReplacement(preparedSql, "?");
        }
        matcher.appendTail(preparedSql);
        return new SqlParseResult(preparedSql.toString(), list);

    }
}
