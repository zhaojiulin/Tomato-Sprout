package com.tomato.sprout.core;

import com.tomato.sprout.constant.BeanScopeType;

/**
 * @author zhaojiulin
 * @version 1.0
 * @Description: bean的初始化信息
 * class 类的字节码
 * scope bean的作用域
 * @Date 2025/10/18 12:33
 */
public class BeanDefinition {
    /**
     * bean类型
     */
    private Class<?> clazz;
    /**
     * 作用域
     */
    private BeanScopeType scope;


    private boolean isMapperInterface;
    private boolean needProxy; // 是否需要创建代理

    public BeanDefinition() {
    }

    public BeanDefinition(Class<?> clazz, BeanScopeType scope) {
        this.clazz = clazz;
        this.scope = scope;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public BeanScopeType getScope() {
        return scope;
    }

    public void setScope(BeanScopeType scope) {
        this.scope = scope;
    }

    public boolean isMapperInterface() {
        return isMapperInterface;
    }

    public void setMapperInterface(boolean mapperInterface) {
        isMapperInterface = mapperInterface;
    }

    public boolean isNeedProxy() {
        return needProxy;
    }

    public void setNeedProxy(boolean needProxy) {
        this.needProxy = needProxy;
    }
}
