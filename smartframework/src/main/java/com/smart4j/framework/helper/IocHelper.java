package com.smart4j.framework.helper;

import com.smart4j.framework.annotation.Inject;
import com.smart4j.framework.util.ArrayUtil;
import com.smart4j.framework.util.CollectionUtil;
import com.smart4j.framework.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 依赖注入
 * Service实例化，不是由开发者通过new方式来实例化，而是框架通过自身来实例化，
 * 这样的过程称为Ioc（Inverseion of Control，控制反转）
 * 也将控制反转称为DI（Dependency Injection，依赖注入）
 *
 * 为类中的 类成员变量赋值，值从Bean容器Map<Class<?>,Object>中取
 */
public final class IocHelper {

    static {
        // 获取所有Bean对象
        Map<Class<?>,Object> beanMap = BeanHelper.getBeanMap();

        if (!CollectionUtil.isEmpty(beanMap)){
            // 遍历每一个对象
            for (Map.Entry<Class<?>,Object> beanEntry: beanMap.entrySet()){
                // 获取bean类与bean实例
                Class<?> beanClass = beanEntry.getKey();
                Object beanInstance = beanEntry.getValue();

                // 遍历成员变量是否有inject注解
                Field[] beanFields = beanClass.getDeclaredFields();
                if (!ArrayUtil.isEmpty(beanFields)){
                    for (Field beanField: beanFields){
                        // 如果有注解，为成员变量赋值，值从Bean容器中取
                        if (beanField.isAnnotationPresent(Inject.class)){
                            Class<?> beanFieldClass = beanField.getType();
                            Object beanFieldInstance = BeanHelper.getBeanObject(beanFieldClass);

                            if (beanFieldInstance != null){
                                ReflectionUtil.setField(beanInstance, beanField, beanFieldInstance);
                            }
                        }
                    }

                }
            }
        }
    }


}
