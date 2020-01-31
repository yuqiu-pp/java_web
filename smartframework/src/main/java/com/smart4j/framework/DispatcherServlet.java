package com.smart4j.framework;

import com.smart4j.framework.bean.*;
import com.smart4j.framework.helper.BeanHelper;
import com.smart4j.framework.helper.ConfigHelper;
import com.smart4j.framework.helper.ControllerHelper;
import com.smart4j.framework.util.*;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


/**
 * 请求转发器
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        // 初始化相关Help类
        HelperLoader.init();

        // 获取ServletContext对象，用于注册Servlet
        ServletContext servletContext = servletConfig.getServletContext();

        // 注册处理JSP的servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath() + "*");

        // 注册处理静态资源的默认Servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath() + "*");

    }



    /**
     * 通过反射执行方法需要：Bean实例(用类名从Bean容器中查)、成员函数(Action)、参数(req中提取)
     * @param req  例如 /customer
     * @param resp
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取请求方法与路径
        String reqMethod = req.getMethod().toLowerCase();
        String reqPath = req.getPathInfo();

        // 获取Action, 从request,handler映射表中
        Handler handler = ControllerHelper.getHandler(reqMethod, reqPath);

        if (handler != null){
            // 获取Bean实例
            Class<?> ctrlClass = handler.getCtrlClass();
            Object ctrlBeanInstance = BeanHelper.getBeanObject(ctrlClass);
            // 获取参数
            Map<String,Object> paramMap = new HashMap<>();
            Enumeration<String> paramNames = req.getParameterNames();
            while (paramNames.hasMoreElements()){
                String paramName = paramNames.nextElement();
                String paramvalue = req.getParameter(paramName);
                paramMap.put(paramName, paramvalue);
            }
            // http请求body
            String body = CodecUtil.decodedUrl(StreamUtil.getString(req.getInputStream()));
            if (!StringUtil.isEmpty(body)){
                String[] params = StringUtil.splitString(body, "&");
                if (!ArrayUtil.isEmpty(params)){
                    for (String param: params){
                        String[] array = StringUtil.splitString(param, "=");
                        if (!ArrayUtil.isEmpty(array) && array.length==2){
                            String paramName = array[0];
                            String paramValue = array[1];
                            paramMap.put(paramName, paramValue);
                        }
                    }
                }

            }
            Param param = new Param(paramMap);

            // Action方法调用
            Method actionMethod = handler.getActionMethod();
            Object rst = ReflectionUtil.invokeMethod(ctrlBeanInstance, actionMethod, param);

            // 处理返回值
            if (rst instanceof View){
                // 返回JSP页面
                View view = (View) rst;
                String path = view.getPath();
                if (!StringUtil.isEmpty(path)){
                    // 重定向
                    if (path.startsWith("/")){
                        resp.sendRedirect(req.getContextPath() + path);
                    }else {
                        Map<String,Object> model = view.getModel();
                        for (Map.Entry<String, Object> entry: model.entrySet()){
                            req.setAttribute(entry.getKey(), entry.getValue());
                        }
                        req.getRequestDispatcher(ConfigHelper.getAppJspPath()+path).forward(req,resp);
                    }
                }
            }else if (rst instanceof Data){
                // 返回JSON数据
                Data data = (Data) rst;
                Object model = data.getModel();
                if (model != null){
                    // POJO转JSON
                    String json = JsonUtil.toJson(model);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    PrintWriter writer = resp.getWriter();
                    writer.write(json);
                    writer.flush();
                    writer.close();
                }
            }

        }






    }
}
