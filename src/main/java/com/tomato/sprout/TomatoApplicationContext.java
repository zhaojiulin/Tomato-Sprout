package com.tomato.sprout;

import com.tomato.sprout.anno.Autowired;
import com.tomato.sprout.anno.Component;
import com.tomato.sprout.anno.Scope;
import com.tomato.sprout.anno.TomatoBoot;
import com.tomato.sprout.aop.AopProxyFactory;
import com.tomato.sprout.aop.anno.AfterExec;
import com.tomato.sprout.aop.anno.AopAdvice;
import com.tomato.sprout.aop.anno.BeforeExec;
import com.tomato.sprout.aop.interfaces.AopExecAdvice;
import com.tomato.sprout.constant.BeanScopeType;
import com.tomato.sprout.core.BeanDefinition;
import com.tomato.sprout.core.CircularDependencyCheck;
import com.tomato.sprout.core.ClassPathScanner;
import com.tomato.sprout.interfaces.ApplicationContextAware;
import com.tomato.sprout.interfaces.BeanNameAware;
import com.tomato.sprout.interfaces.BeanPostProcessor;
import com.tomato.sprout.interfaces.InitializingBean;
import com.tomato.sprout.orm.MapperProxyFactory;
import com.tomato.sprout.orm.anno.RepoMapper;
import com.tomato.sprout.web.anno.WebController;
import com.tomato.sprout.web.mapping.HandleMethodMappingHolder;
import com.tomato.sprout.web.serve.TomcatEmbeddedServer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class TomatoApplicationContext {
    /**
     * 单例池
     */
    private final ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    /**
     * BeanDefinition定义
     */
    private final ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    /**
     * 实例化BeanPostProcessor
     */
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    private final ConcurrentHashMap<Class<?>, List<AopExecAdvice>> beanToAdvice = new ConcurrentHashMap<>();
    /**
     * 循环依赖检查类-禁止依赖循环
     */
    private final CircularDependencyCheck circularDependencyCheck = new CircularDependencyCheck();
    /**
     * mapper代理类创建工厂
     */
    private final MapperProxyFactory mapperProxyFactory = new MapperProxyFactory();
    private final AopProxyFactory aopProxyFactory = new AopProxyFactory();

    /**
     * 扫描bean
     *
     * @param primarySource
     */
    public void scanBeanDefinition(Class<?> primarySource) {
        TomatoBoot componentScan = primarySource.getDeclaredAnnotation(TomatoBoot.class);
        // 扫描用户路径
        ClassPathScanner classPathScanner = new ClassPathScanner();
        String applicationPath = componentScan.scanBasePackage().isEmpty() ? primarySource.getPackage().getName() : componentScan.scanBasePackage();
        String[] packages = new String[]{applicationPath};
        Set<Class<?>> classSet = new HashSet<>();
        frameworkClass(classSet);
        for (String path : packages) {
            classSet.addAll(classPathScanner.scan(path));
        }
        for (Class<?> clazz : classSet) {
            // 创建beanDefinition
            registerBeanDefinition(clazz);
        }
        registerInternalPostProcessors();
        registerInternalPostAdvice();
    }

    /**
     * 框架class
     *
     * @param classSet
     */
    private void frameworkClass(Set<Class<?>> classSet) {
        classSet.add(TomcatEmbeddedServer.class);
    }

    /**
     * 获取BeanDefinition
     *
     * @param clazz
     */
    private void registerBeanDefinition(Class<?> clazz) {
        if (clazz.isAnnotation()) {
            return;
        }
        // 是否有标记这个类是bean
        if (!clazz.isAnnotationPresent(Component.class) && !clazz.isAnnotationPresent(WebController.class) && !clazz.isAnnotationPresent(RepoMapper.class) && !clazz.isAnnotationPresent(com.tomato.sprout.aop.anno.AopAdvice.class)) {
            return;
        }
        String beanName = getClassBeanName(clazz);
        // 解析类，判断当前bean是单例bean，还是原型bean;创建bean信息BeanDefinition
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setClazz(clazz);
        beanDefinition.setMapperInterface(clazz.isInterface() && clazz.isAnnotationPresent(RepoMapper.class));
        beanDefinition.setNeedProxy(beanDefinition.isMapperInterface());
        if (clazz.isAnnotationPresent(Scope.class)) {
            Scope scopeAnno = clazz.getDeclaredAnnotation(Scope.class);
            beanDefinition.setScope(scopeAnno.value());
        } else {
            beanDefinition.setScope(BeanScopeType.SINGLETON);
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    /**
     * 注册内置BeanPostProcessor
     *
     * @param
     */
    public void registerInternalPostProcessors() {
        List<BeanDefinition> processorBeanDefinition = beanDefinitionMap.values().stream()
                .filter(beanDefinition -> BeanPostProcessor.class.isAssignableFrom(beanDefinition.getClazz()))
                .toList();
        for (BeanDefinition beanDefinition : processorBeanDefinition) {
            Object bean = getBean(getClassBeanName(beanDefinition.getClazz()));
            beanPostProcessors.add((BeanPostProcessor) bean);
        }
    }

    public void registerInternalPostAdvice() {
        List<BeanDefinition> processorBeanDefinition = beanDefinitionMap.values().stream()
                .filter(beanDefinition -> AopExecAdvice.class.isAssignableFrom(beanDefinition.getClazz()))
                .toList();
        for (BeanDefinition beanDefinition : processorBeanDefinition) {
            getBean(getClassBeanName(beanDefinition.getClazz()));
        }
    }

    /**
     * 根据bean实例化class、属性注入、依赖注入、web映射
     */
    public void refreshBean() {
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            Object bean = getBean(getClassBeanName(beanDefinition.getClazz()));
            singletonObjects.put(beanName, bean);
            HandleMethodMappingHolder.getInstance().processController(beanDefinition.getClazz(), bean);
        });
    }

    /**
     * 获取完整的bean
     *
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            // 单例bean从单例池获取
            if (beanDefinition.getScope().equals(BeanScopeType.SINGLETON)) {
                Object o = singletonObjects.get(beanName);
                return Objects.isNull(o) ? createBean(beanName, beanDefinition) : o;
            } else {
                // 原型模式
                return createBean(beanName, beanDefinition);
            }
        } else {
            throw new NullPointerException();
        }
    }

    /**
     * 循环依赖检查
     * bean实例化
     * 循环依赖结束
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    @SuppressWarnings("unchecked")
    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        circularDependencyCheck.startCreation(beanName);
        Object bean = doCreateBean(beanName, beanDefinition);
        circularDependencyCheck.endCreation(beanName);
        return bean;
    }

    /**
     * 创建bean
     * 实例化
     * 属性填充/依赖注入
     * 执行Aware实现类方法
     * 执行Bean初始化之前自定义处理
     * 初始化Bean
     * 执行Bean初始化之后自定义处理
     */
    private Object doCreateBean(String beanName, BeanDefinition beanDefinition) {
        Class<?> clazz = beanDefinition.getClazz();
        try {
            Object instance;
            if (beanDefinition.isMapperInterface() && beanDefinition.isNeedProxy()) {
                instance = mapperProxyFactory.getProxy(clazz);
            } else {
                instance = clazz.getDeclaredConstructor().newInstance();
                if (beanToAdvice.get(clazz) != null) {
                    instance = aopProxyFactory.getProxy(clazz, beanToAdvice.get(clazz));
                }
            }
            // 依赖注入
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    Class<?> fieldType = declaredField.getType();
                    Object bean;
                    if (fieldType.isInterface() && fieldType.isAnnotationPresent(RepoMapper.class)) {
                        bean = getBean(declaredField.getName());
                        if (Objects.isNull(bean)) {
                            bean = mapperProxyFactory.getProxy(clazz);
                        }
                        if (beanToAdvice.get(bean.getClass()) != null) {
                            bean = aopProxyFactory.getProxy(bean.getClass(), beanToAdvice.get(clazz));
                        }
                    } else {
                        bean = getBean(declaredField.getName());
                    }

                    Autowired autowiredAnno = declaredField.getDeclaredAnnotation(Autowired.class);
                    if (bean == null && autowiredAnno.required()) {
                        throw new NullPointerException();
                    }
                    declaredField.setAccessible(true);
                    declaredField.set(instance, bean);
                }
            }
            // 执行实现Aware接口和BeanPostProcessor接口方法
            instance = doAwareAndPost(beanName, instance, clazz);
            if (clazz.isAnnotationPresent(AopAdvice.class)) {
                Method[] declaredMethods = clazz.getDeclaredMethods();
                for (Method declaredMethod : declaredMethods) {
                    if (declaredMethod.isAnnotationPresent(BeforeExec.class)) {
                        BeforeExec annotation = declaredMethod.getAnnotation(BeforeExec.class);
                        Class<?>[] value = annotation.classes();
                        for (Class<?> aClass : value) {
                            List<AopExecAdvice> aopExecAdvices = beanToAdvice.get(aClass);
                            if (null == aopExecAdvices || aopExecAdvices.isEmpty()) {
                                aopExecAdvices = new ArrayList<>();
                            }
                            boolean anyMatch = aopExecAdvices.stream().anyMatch(item -> item.getClass().equals(clazz));
                            if (!anyMatch) {
                                aopExecAdvices.add((AopExecAdvice) instance);
                            }
                            beanToAdvice.put(aClass, aopExecAdvices);
                        }

                    }
                    if (declaredMethod.isAnnotationPresent(AfterExec.class)) {
                        AfterExec annotation = declaredMethod.getAnnotation(AfterExec.class);
                        Class<?>[] value = annotation.value();
                        for (Class<?> aClass : value) {
                            List<AopExecAdvice> aopExecAdvices = beanToAdvice.get(aClass);
                            if (null == aopExecAdvices || aopExecAdvices.isEmpty()) {
                                aopExecAdvices = new ArrayList<>();
                            }
                            boolean anyMatch = aopExecAdvices.stream().anyMatch(item -> item.getClass().equals(clazz));
                            if (!anyMatch) {
                                aopExecAdvices.add((AopExecAdvice) instance);
                            }
                            beanToAdvice.put(aClass, aopExecAdvices);
                        }

                    }
                }
            }
            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Aware接口和BeanPostProcessor接口子类实现
     *
     * @param beanName
     * @param instance
     * @param clazz
     * @return
     */
    private Object doAwareAndPost(String beanName, Object instance, Class<?> clazz) {
        if (instance instanceof BeanNameAware) {
            ((BeanNameAware) instance).setBeanName(beanName);
        }
        if (instance instanceof ApplicationContextAware) {
            ((ApplicationContextAware) instance).setApplicationContext(this);
        }
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            // BeanPostProcessor 扩展机制 前置
            instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);

        }
        // 自定义初始化
        if (instance instanceof InitializingBean) {
            ((InitializingBean) instance).afterPropertiesSet();
        }
        // BeanPostProcessor 扩展机制 后置
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            // BeanPostProcessor 扩展机制 前置
            instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);

        }
        return instance;
    }

    private String getClassBeanName(Class<?> clazz) {
        String beanName = "";
        if (clazz.isAnnotationPresent(Component.class)) {
            Component componentAnno = clazz.getDeclaredAnnotation(Component.class);
            // beanName获取
            beanName = componentAnno.value();
        }
        if (beanName.isEmpty()) {
            beanName = clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1);
        }
        return beanName;
    }
}
