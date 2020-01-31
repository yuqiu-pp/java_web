package com.smart4j.framework.helper;


import com.smart4j.framework.annotation.Controller;
import com.smart4j.framework.annotation.Service;
import com.smart4j.framework.util.ClassUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * 类操作助手类
 * 获取应用包名下的所有类、所有service类、所有controller类
 */
public final class ClassHelper {

    /**
     * 存放所有加载类
     */
    private static final Set<Class<?>> CLASS_SET;

    static {
        String basePackage = ConfigHelper.getAppBasePackage();
        CLASS_SET = ClassUtil.getClassSet(basePackage);
    }

    public static Set<Class<?>> getClassSet(){
        return CLASS_SET;
    }

    /**
     * 获取所有Service类
     */
    public static Set<Class<?>> getServiceClassSet(){
        Set<Class<?>> set = new HashSet<Class<?>>();
        for (Class<?> cls : CLASS_SET){
            if (cls.isAnnotationPresent(Service.class)){
                set.add(cls);
            }
        }
        return set;
    }

    /**
     * 获取所Controller类
     */
    public static Set<Class<?>> getControllerClassSet(){
        Set<Class<?>> set = new HashSet<Class<?>>();
        for (Class<?> cls : CLASS_SET){
            if (cls.isAnnotationPresent(Controller.class)){
                set.add(cls);
            }
        }
        return set;
    }

    /**
     * 获取应用包下所有Bean类，包括：Service、Controller等
     */
    public static Set<Class<?>> getBeanClassSet(){
        Set<Class<?>> set = new HashSet<Class<?>>();
        set.addAll(getServiceClassSet());
        set.addAll(getControllerClassSet());
        return set;
    }

}
