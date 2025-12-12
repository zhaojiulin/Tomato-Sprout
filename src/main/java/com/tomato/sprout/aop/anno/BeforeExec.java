package com.tomato.sprout.aop.anno;

import com.tomato.sprout.constant.AopMethodMatcherType;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Administrator
 * @version 1.0
 * @description: 前置切面方法
 * @date 2025/12/12 14:56
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeExec {
    /**
     * 需要匹配的字节码类型
     * @return
     */
    Class<?>[] classes() default {};

    /**
     * byteBuddy方法拦截类型
     * @return
     */
    AopMethodMatcherType matcher() default AopMethodMatcherType.OWNER;

    /**
     * AopMethodMatcherType.NAMED时的方法名称
     * @return
     */
    String methodName() default "";
}


