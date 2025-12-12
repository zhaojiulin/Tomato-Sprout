package com.tomato.sprout.aop.anno;

import com.tomato.sprout.constant.AopMethodMatcherType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Administrator
 * @version 1.0
 * @description: 后置执行
 * @date 2025/12/12 17:21
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterExec {
    Class[] value() default {};

    /**
     * byteBuddy方法拦截类型
     * @return
     */
    AopMethodMatcherType matcher() default AopMethodMatcherType.OWNER;
}
