package com.banana;

import com.tomato.sprout.anno.Component;
import com.tomato.sprout.interfaces.BeanPostProcessor;

import java.util.Locale;

@Component
public class ProcessorTest implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("自定义ProcessorTest初始化前");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("自定义ProcessorTest初始化后");
        return bean;
    }
}
