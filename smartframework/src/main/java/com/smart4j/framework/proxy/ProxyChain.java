package com.smart4j.framework.proxy;

import lombok.Getter;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class ProxyChain {

    @Getter
    private final Class<?> targetClass;
    private final Object targetObject;
    @Getter
    private final Method targetMethod;
    private final MethodProxy methodProxy;
    @Getter
    private final Object[] methodParams;

    // 代理链
    private List<Proxy> proxyList = new ArrayList<Proxy>();

    private int proxyIndex = 0;

    public ProxyChain(Class<?> targetClass, Object targetObject, Method targetMethod,
                      MethodProxy methodProxy, Object[] methodParams, List<Proxy> proxyList){
        this.targetClass = targetClass;
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.methodProxy = methodProxy;
        this.methodParams = methodParams;
        this.proxyList = proxyList;
    }

    /**
     * 遍历proxyList，执行每个元素代理的方法
     */
    public Object doProxyChain() throws Throwable{
        Object methodResult;
        // 先调用List中的过滤
        if (proxyIndex < proxyList.size()) {
            methodResult = proxyList.get(proxyIndex++).doProxy(this);
        }
        // 最后调用真正要执行的方法
        else {
            methodResult = methodProxy.invokeSuper(targetObject, methodParams);
        }
        return methodResult;
    }

}
