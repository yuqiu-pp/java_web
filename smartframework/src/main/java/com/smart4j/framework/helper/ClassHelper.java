package com.smart4j.framework.helper;


import com.smart4j.framework.annotation.Controller;
import com.smart4j.framework.annotation.Service;
import com.smart4j.framework.util.ClassUtil;

import java.lang.annotation.Annotation;
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

    /**
     * 获取应用包名下某父类(或接口)的所有子类
     *
     * 应用场景：扩展AspectProxy抽象类的所有具体类
     */
     public static Set<Class<?>> getClassSetBySuper(Class<?> superClass){
         Set<Class<?>> classSet = new HashSet<>();

         for (Class<?> cls : CLASS_SET){
             // A.isAssignableFrom(B)  A所代表的类 是否为B所代表的类，或者B所代表的类的super类
             // 后一个条件排除掉相同类，所以这里只取了super类
             if (superClass.isAssignableFrom(cls) && !superClass.equals(cls)){
                 classSet.add(cls);
             }
         }
         return classSet;
     }

    /**
     * 获取应用包名下带有某注解的所有类
     * 例如：@Aspect(Controller.class)中的Annotation
     * 应用场景：获取带有Aspect注解的所有类
     */
    public static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> annotationClass){
        Set<Class<?>> classSet = new HashSet<>();

        for (Class<?> cls : CLASS_SET){
            if (cls.isAnnotationPresent(annotationClass)){
                classSet.add(cls);
            }
        }
        return classSet;
    }
}
