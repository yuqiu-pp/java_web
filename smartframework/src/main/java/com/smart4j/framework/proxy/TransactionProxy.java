package com.smart4j.framework.proxy;

import com.smart4j.framework.annotation.Transaction;
import com.smart4j.framework.helper.DatabaseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class TransactionProxy implements Proxy{

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionProxy.class);


    // 保证同一线程中事务控制相关逻辑只会执行一次  ？？？
    private static final ThreadLocal<Boolean> FLAG_HOLDER;

    static {
        FLAG_HOLDER = ThreadLocal.withInitial(() -> false);
    }


    @Override
    public Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result;
        boolean flag = FLAG_HOLDER.get();
        Method method = proxyChain.getTargetMethod();
        if (!flag && method.isAnnotationPresent(Transaction.class)){
            FLAG_HOLDER.set(true);
            try {
                DatabaseHelper.beginTranscation();
                LOGGER.debug("begin transaction");
                result = proxyChain.doProxyChain();
                DatabaseHelper.commitTransaction();
                LOGGER.debug("commit transaction");
            } catch (Exception e) {
                DatabaseHelper.rollbackTransaction();
                LOGGER.debug("rollback transaction");
                // 为啥不是 throw RuntimeException
                throw e;
            } finally {
                FLAG_HOLDER.remove();
            }
        }else {
            result = proxyChain.doProxyChain();
        }


        return result;
    }
}
