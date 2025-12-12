package com.banana.content;

import com.banana.UserService;
import com.tomato.sprout.aop.anno.AfterExec;
import com.tomato.sprout.aop.anno.AopAdvice;
import com.tomato.sprout.aop.anno.BeforeExec;
import com.tomato.sprout.aop.interfaces.AopExecAdvice;

import java.lang.reflect.Method;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: 指定类方法执行前后通知测试类
 * @date 2025/12/12 15:12
 */
@AopAdvice
public class AopExecAdviceService implements AopExecAdvice {

    @BeforeExec(classes = {UserService.class})
    @Override
    public void execBefore(Object target, Method method, Object[] args) {
        System.out.println("custom Before Advice");
    }

    @AfterExec(value = {UserService.class})
    @Override
    public void execAfter(Object target, Method method, Object[] args) {
        System.out.println("custom After Advice");
    }
}
