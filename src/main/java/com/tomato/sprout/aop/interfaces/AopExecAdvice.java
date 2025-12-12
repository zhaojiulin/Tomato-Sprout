package com.tomato.sprout.aop.interfaces;

import java.lang.reflect.Method;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: 前后通知接口
 *
 * @date 2025/12/12 14:59
 */
public interface AopExecAdvice {
    void execBefore(Object target, Method method, Object[] args);
    void execAfter(Object target, Method method, Object[] args);
}
