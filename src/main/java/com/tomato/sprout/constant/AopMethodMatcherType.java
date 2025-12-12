package com.tomato.sprout.constant;

/**
 * @author Administrator
 * @version 1.0
 * @description: 方法匹配类型
 * ANY-执行方法中的所有方法
 * OWNER-属于指定字节码中的方法
 * NAMED-指定方法名
 * @date 2025/12/12 18:49
 */
public enum AopMethodMatcherType {
    ANY,
    OWNER,
    NAMED,
}
