package com.smart4j.framework.bean;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * 封装Action信息
 * 包括两部分：类和方法名
 */
@Data
public class Handler {

    private Class<?> ctrlClass;
    private Method actionMethod;

    public Handler(Class<?> ctrlClass, Method actionMethod){
        this.ctrlClass = ctrlClass;
        this.actionMethod = actionMethod;
    }

}
