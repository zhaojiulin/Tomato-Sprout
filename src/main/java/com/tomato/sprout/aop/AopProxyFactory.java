package com.tomato.sprout.aop;

import com.tomato.sprout.aop.interfaces.AopExecAdvice;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: TODO
 * @date 2025/12/12 15:15
 */

public class AopProxyFactory {

    public static class Interceptor {
        private final List<AopExecAdvice> adviceList;

        public Interceptor(List<AopExecAdvice> adviceList) {
            this.adviceList = adviceList;
        }

        @RuntimeType
        public Object intercept(
                @Origin Method method,
                @This Object proxy,
                @AllArguments Object[] args,
                @SuperCall Callable<?> callable) throws Exception {

            // 执行前置通知
            for (AopExecAdvice advice : adviceList) {
                advice.execBefore(proxy, method, args);
            }
            // 执行原始方法
            Object call = callable.call();
            // 执行后置通知
            for (AopExecAdvice advice : adviceList) {
                advice.execAfter(proxy, method, args);
            }
            return call;
        }
    }


    public <T> Object getProxy(Class<?> targetClass, List<AopExecAdvice> advice) {
        try {
            return new ByteBuddy()
                    .subclass(targetClass)
                    .method(ElementMatchers.isAccessibleTo(targetClass)) // 拦截所属当前实例的方法---todo指定名称或匹配或注解
                    .intercept(MethodDelegation.to(new Interceptor(advice)))
                    .make()
                    .load(targetClass.getClassLoader())
                    .getLoaded()
                    .newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
