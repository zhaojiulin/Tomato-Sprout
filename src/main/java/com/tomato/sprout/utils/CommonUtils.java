package com.tomato.sprout.utils;

import com.tomato.sprout.web.model.ReqFile;

import java.sql.Date;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: TODO
 * @date 2025/12/10 17:39
 */
public class CommonUtils {


    /**
     * 是否为基本类型
     *
     * @param targetType
     * @return
     */
    public static boolean isBasic(Class<?> targetType) {
        if (targetType == String.class) {
            return true;
        } else if (targetType == Integer.class || targetType == int.class) {
            return true;
        } else if (targetType == Long.class || targetType == long.class) {
            return true;
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return true;
        } else if (targetType == Double.class || targetType == double.class) {
            return true;
        } else if (targetType == Float.class || targetType == float.class) {
            return true;
        } else if (targetType == ReqFile.class) {
            return true;
        } else return targetType == Date.class;
    }
}
