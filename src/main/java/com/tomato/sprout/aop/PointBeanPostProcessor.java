package com.tomato.sprout.aop;

import com.tomato.sprout.anno.Component;
import com.tomato.sprout.interfaces.BeanPostProcessor;

/****
 * @Description: AOP后置实现
 * 待实现
 * @author zhaojiulin
 * @Date 2025/10/18 12:15
 * @version 1.0
 */
@Component
public class PointBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("AOP before Initialization:"+beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("AOP after Initialization:"+beanName);
        return bean;
    }
}
