package com.smart4j.framework.helper;

import com.smart4j.framework.annotation.Aspect;
import com.smart4j.framework.annotation.Service;
import com.smart4j.framework.proxy.AspectProxy;
import com.smart4j.framework.proxy.Proxy;
import com.smart4j.framework.proxy.ProxyManager;
import com.smart4j.framework.proxy.TransactionProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 方法拦截助手
 *
 * 目标：1.目标类和代理列表 映射关系
 *      2.代理实例 存入Bean容器 <目标类Class, 代理对象实例>
 *
 * 代理对象实例：由ProxyManager.createProxy(targetClass, proxyList);来创建；
 *
 * 代理对象列表proxyList怎么来？
 *
 * 已知：可以根据@Aspect注解，获取所有切面类(即要增强的类，实现了增强处理接口)
 * 是切面类的另一个条件是：必须要扩展AspectProxy抽象类(因为抽象类中的doProxy真正调用增加接口和原方法)
 * 所以，再根据父类必须是AspectProxy的条件进行筛选，得到切面类
 */
public class AopHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AopHelper.class);

    static {
        try {
            // 代理类(切面类) 和 目标类集合(一个代理类可以对应多个目标类) 的映射表
            Map<Class<?>, Set<Class<?>>> proxyMap = createProxyMap();
            // 根据proxyMap => 目标类 和 代理对象列表 的映射关系
            Map<Class<?>, List<Proxy>> targetMap = createTargetMap(proxyMap);
            // 遍历targetMap，创建代理对象实例，
            for (Map.Entry<Class<?>, List<Proxy>> targetEntry : targetMap.entrySet()){
                Class<?> targetClass = targetEntry.getKey();
                List<Proxy> proxyList = targetEntry.getValue();
                // 创建代理对象
                Object proxy = ProxyManager.createProxy(targetClass, proxyList);
                // 目标类的Class和代理对象 加入 Bean容器
                BeanHelper.setBean(targetClass, proxy);
            }
        } catch (Exception e) {
            LOGGER.error("aop failure", e);
        }
    }

    /**
     * <Class<ControllerAspect>, Set<Class<Controller>>
     * 创建代理类ControllerAspect的实例
     * 存入Map<Class<Controller>>, ControllerAspect的实例的List>
     */
    private static Map<Class<?>, List<Proxy>> createTargetMap(Map<Class<?>, Set<Class<?>>>proxyMap) throws Exception{
        Map<Class<?>, List<Proxy>> targetMap = new HashMap<>();

        for (Map.Entry<Class<?>, Set<Class<?>>> proxyEntry : proxyMap.entrySet()){
            Class<?> proxyClass = proxyEntry.getKey();
            Set<Class<?>> targetClassSet = proxyEntry.getValue();
            for (Class<?> targetClass : targetClassSet){
                // 创建代理对象实例，并加入代理链
                Proxy proxy = (Proxy) proxyClass.newInstance();
                if (targetMap.containsKey(targetClass)){
                    targetMap.get(targetClass).add(proxy);
                }else {
                    List<Proxy> proxyList = new ArrayList<>();
                    proxyList.add(proxy);
                    targetMap.put(targetClass, proxyList);
                }
            }
        }
        return targetMap;
    }


    /**
     * 筛选 1.继承AspectProxy；2.有@Aspect注解的类集合
     *
     * 例如：
     * 1 @Aspect(Controller.class)
     * 1 public class ControllerAspect extends AspectProxy
     * 得到的集合：<Class<ControllerAspect>, Set<Class<Controller>>  ??
     */
    private static Map<Class<?>, Set<Class<?>>> createProxyMap() throws Exception{
        Map<Class<?>, Set<Class<?>>> proxyMap = new HashMap<>();
        addAspectProxy(proxyMap);
        addTransactionProxy(proxyMap);
        return proxyMap;
    }

    /**
     * 筛选 1.继承AspectProxy；2.有@Aspect注解的类集合
     *
     * 例如：
     * 1 @Aspect(Controller.class)
     * 1 public class ControllerAspect extends AspectProxy
     * 得到的集合：<Class<ControllerAspect>, Set<Class<Controller>>  ??
     */
    private static void addAspectProxy(Map<Class<?>, Set<Class<?>>> proxyMap) throws Exception {
        // AspectProxy的所有具体类，即web应用中继承了AspectProxy的类
        Set<Class<?>> aspectProxyClassSet = ClassHelper.getClassSetBySuper(AspectProxy.class);
        // 筛选用@Aspect注解的类
        for (Class<?> aspectProxyClass : aspectProxyClassSet){
            if (aspectProxyClass.isAnnotationPresent(Aspect.class)){
                // ???  Todo
                // 取到@x.x.x.x.Aspect(value=class x.x.Controller.class)
                // value通过Aspect注解的value()接口得到
                Aspect aspect = aspectProxyClass.getAnnotation(Aspect.class);
                Set<Class<?>> targetClassSet = createTargetClassSet(aspect);
                proxyMap.put(aspectProxyClass, targetClassSet);

            }
        }
    }

    /**
     * 集合：<TransactionProxy.class, Set<被代理的类></>></>
     */
    private static void addTransactionProxy(Map<Class<?>, Set<Class<?>>> proxyMap){
        Set<Class<?>> serviceClassSet = ClassHelper.getClassSetByAnnotation(Service.class);
        proxyMap.put(TransactionProxy.class, serviceClassSet);
    }

    // Todo
    private static Set<Class<?>> createTargetClassSet(Aspect aspect) throws Exception{
        Set<Class<?>> targetClassSet = new HashSet<>();
        // 取出注解中的value
        Class<? extends Annotation> annotationValue = aspect.value();
        if (annotationValue!=null && !annotationValue.equals(Aspect.class)){
            targetClassSet.addAll(ClassHelper.getClassSetByAnnotation(annotationValue));
        }
        return targetClassSet;
    }
}
