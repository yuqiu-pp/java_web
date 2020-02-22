package com.smart4j.framework.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;


/**
 * ProxyManager 创建代理对象实例
 *
 * 谁来调用ProxyManager？
 * 当然是切面类了，因为在切面类中，需要在 目标方法 被调用前后进行增强
 * 写一个抽象类，让它提供一个模板方法
 */
public class ProxyManager {

    public static <T> T createProxy(final Class<?> targetClass, final List<Proxy> proxyList){
        return (T) Enhancer.create(targetClass, new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                return new ProxyChain(targetClass, o, method, methodProxy,
                        objects, proxyList).doProxyChain();
            }
        });
    }

    // class CglibProxy implements MethodInterceptor{
    //
    //     @Override
    //     public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            // 因为invokeSuper调用在ProxyChain的doProxyChain()中，所以这里要调用它，
            // return methodProxy.invokeSuper(o, objects);

            // proxyList这里获取不到，只能是靠调用发传入(上面的createProxy实现就是这样) 或者 ProxyChain中设为static
            // ProxyChain proxyChain = new ProxyChain(o.getClass(), o, method, methodProxy, objects, proxyList);
        // }
    // }
}
