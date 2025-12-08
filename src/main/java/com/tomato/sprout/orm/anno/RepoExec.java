package com.tomato.sprout.orm.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * @Description: sql执行注解
 * @author zhaojiulin
 * @Date 2025/10/31 17:06
 * @version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RepoExec {
    String value() default "";
}
