package com.smart4j.framework.helper;


import com.smart4j.framework.annotation.Action;
import com.smart4j.framework.bean.Handler;
import com.smart4j.framework.bean.Request;
import com.smart4j.framework.util.ArrayUtil;
import com.smart4j.framework.util.CollectionUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 有Controller注解的类中
 * 有Action注解的方法，需要建立method和path的映射关系
 *
 * 遍历Bean容器中所有类  还是 遍历所有Controller类 ??
 * 每个类的成员方法中有Action注解的，建立映射Map<Field,String>
 */
public class ControllerHelper {

    private static final Map<Request, Handler> ACTION_MAP = new HashMap<>();

    static {
        // 获取所有Controller类
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();

        if (!CollectionUtil.isEmpty(controllerClassSet)){
            // 遍历类
            for (Class<?> ctrlClass: controllerClassSet){
                Method[] ctrlMethods = ctrlClass.getDeclaredMethods();
                if (!ArrayUtil.isEmpty(ctrlMethods)){
                    // 遍历类的成员方法
                    for (Method ctrlMethod: ctrlMethods){
                        // 有Action注解的
                        if (ctrlMethod.isAnnotationPresent(Action.class)){
                            // 获取Action注解内容。 getDeclaredAnnotation参数指定注解类型，因为可能有多个注解
                            Action action = ctrlMethod.getDeclaredAnnotation(Action.class);
                            String mapping = action.value();
                            // 验证URL映射规则
                            if (mapping.matches("\\w+:/\\w*")){
                                String[] array = mapping.split(":");
                                if (!ArrayUtil.isEmpty(array) && array.length==2){
                                    // 获取请求方法与请求路径
                                    String requestMethod = array[0];
                                    String requestPath = array[1];
                                    Request request = new Request(requestMethod, requestPath);
                                    Handler handler = new Handler(ctrlClass, ctrlMethod);
                                    // 建立映射Map<Field,String>
                                    ACTION_MAP.put(request, handler);
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    public static Handler getHandler(String requestMethod, String requestPath){
        Request request = new Request(requestMethod, requestPath);
        return ACTION_MAP.get(request);
    }
}
