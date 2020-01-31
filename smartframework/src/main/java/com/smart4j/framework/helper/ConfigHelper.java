package com.smart4j.framework.helper;


import com.smart4j.framework.ConfigConstant;
import com.smart4j.framework.util.PropsUtil;

import java.util.Properties;

import static com.smart4j.framework.ConfigConstant.*;

public class ConfigHelper {

    private static final Properties CONFIG_PROPS = PropsUtil.loadPropsUtf8(ConfigConstant.CONFIG_FILE);


    public static String getJdbcDriver(){
        return PropsUtil.getString(CONFIG_PROPS, JDBC_DRIVER);
    }


    public static String getJdbcUrl(){
        return PropsUtil.getString(CONFIG_PROPS, JDBC_URL);
    }


    /**
     * 获取 JDBC 用户名
     */
    public static String getJdbcUsername() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_USERNAME);
    }

    /**
     * 获取 JDBC 密码
     */
    public static String getJdbcPassword() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_PASSWORD);
    }

    /**
     * 获取应用基础包名
     */
    public static String getAppBasePackage() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_BASE_PACKAGE);
    }

    /**
     * 获取应用 JSP 路径
     */
    public static String getAppJspPath() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_JSP_PATH, "/WEB-INF/view/");
    }

    /**
     * 获取应用静态资源路径
     */
    public static String getAppAssetPath() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_ASSET_PATH, "/asset/");
    }
}
