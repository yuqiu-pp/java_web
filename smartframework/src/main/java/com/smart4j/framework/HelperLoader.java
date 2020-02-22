package com.smart4j.framework;


import com.smart4j.framework.helper.*;
import com.smart4j.framework.util.ClassUtil;

/**
 * 加载需要的Help类
 */
public class HelperLoader {

    public static void init(){
        Class<?>[] classList = {
                ClassHelper.class,
                BeanHelper.class,
                // BeanHelper实例化类时，不包括代理类吗？
                AopHelper.class,
                IocHelper.class,
                ControllerHelper.class
        };

        // 类加载JVM保证线程安全性
        for (Class<?> cls: classList){
            ClassUtil.loadClass(cls.getName());
        }
    }
}
