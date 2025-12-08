package com.tomato.sprout.constant;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2025/12/8 15:51
 */
public enum HttpContentType {
    FORM_DATA("multipart/form-data"),
    FORM("application/x-www-form-urlencoded"),
    JSON("application/json");

    private String value;
    HttpContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
