package com.tomato.sprout.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * @Description: 应用启动
 * @author zhaojiulin
 * @Date 2025/10/18 14:19
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TomatoBoot {
    /**
     * 扫描路径
     * @return
     */
    String scanBasePackage() default "";
}
