package com.smart4j.framework.helper;


import com.smart4j.framework.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * getBeanClassSet中的Bean类需要实例化
 * 循环调用ReflectionUtil类的newInstance方法，根据类来实例化对象
 * 最后将创建的对象存放到一个静态的Map<Class<?>,Object>中，key：类名，value：Bean对象
 *
 * 相当于容器，因为在Map中存放了Bean类与实例的映射
 */
public final class BeanHelper {
    public static final Map<Class<?>,Object> BEAN_MAP = new HashMap<>();

    static {
        // 获取包下所有类
        Set<Class<?>> beanClassSet = ClassHelper.getBeanClassSet();
        // 循环实例化
        for (Class<?> beanClass: beanClassSet){
            Object obj = ReflectionUtil.newInstance(beanClass);
            BEAN_MAP.put(beanClass, obj);
        }
    }

    /**
     * 获取Bean映射
     */
    public static Map<Class<?>,Object> getBeanMap(){
        return BEAN_MAP;
    }

    /**
     *   获取Bean对象
     */
    public static <T>T getBeanObject(Class<?> cls){
        if (!BEAN_MAP.containsKey(cls)){
            throw new RuntimeException("Can not get bean by class:" + cls);
        }
        return (T) BEAN_MAP.get(cls);
    }

}
