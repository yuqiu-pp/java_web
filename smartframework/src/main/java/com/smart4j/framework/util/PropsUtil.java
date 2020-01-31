package com.smart4j.framework.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;


/**
 * 属性文件工具类
 */
public final class PropsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);

    /**
     * 加载属性文件
     * @param fileName
     * @return
     */
    public static Properties loadProps(String fileName){
        Properties props = null;
        InputStream is = null;

        try {
            is=Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (null == is){
                throw new FileNotFoundException(fileName + "file is not found");
            }
            props = new Properties();
            props.load(is);
        }catch (IOException e){
            LOGGER.error("load properties file failure", e);
        }finally {
            if (null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    // e.printStackTrace();
                    LOGGER.error("close input stream failure", e);
                }
            }
        }

        return props;
    }

    /**
     * 加载属性文件，支持中文
     */
    public static Properties loadPropsUtf8(String fileName){
        Properties props = null;
        InputStreamReader isr = null;

        try {
            if (fileName != null){
                isr = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName),
                        "utf-8");
                if (isr == null){
                    throw new FileNotFoundException(fileName + "can not find");
                }
                props = new Properties();
                props.load(isr);
            }
        }catch (Exception e){
            LOGGER.error("load properties fail");
        }finally {
            if (isr != null){
                try {
                    isr.close();
                } catch (IOException e) {
                    // e.printStackTrace();
                    LOGGER.error("close input stream failure");
                }
            }
        }


        return props;
    }

    /**
     * 获取字符型属性，默认值为空字符串
     * @param props
     * @param key
     * @return
     */
    public static String getString(Properties props, String key){
        return getString(props, key, "");
    }

    public static String getString(Properties props, String key, String defaultValue){
        String value = defaultValue;
        if (props.containsKey(key)){
            value = props.getProperty(key);
        }
        return value;
    }

    /**
     * 获取数字型属性，默认值为0
     */
    public static int getInt(Properties props, String key){
        return getInt(props, key, 0);
    }

    public static int getInt(Properties props, String key, int defaultValue){
        int value = 0;
        if (props.containsKey(key)){
            // ?? 取出的key值可能为空
            value = Integer.parseInt(props.getProperty(key));
        }
        return value;
    }

    /**
     * 获取布尔型属性，默认false
     */
    public static boolean getBoolean(Properties props, String key){
        return getBoolean(props, key, false);
    }

    public static boolean getBoolean(Properties props, String key, boolean defaultValue){
        boolean value = defaultValue;
        if (props.containsKey(key)){
            value = Boolean.parseBoolean(props.getProperty(key));
        }
        return value;
    }

    /**
     * 获取中文属性，默认是空字符串
     */
    public static String getUtf8(Properties props, String key){
        return getUtf8(props, key, "");
    }

    public static String getUtf8(Properties props, String key, String defaultValue){
        String value = defaultValue;
        if (props.containsKey(key)){
            // 不同的机器读取properties的编码方式可能不同,这种不推荐
            // try {
                // value = new String(props.getProperty(key).getBytes("ISO-8859-1"), "utf8");
            // } catch (UnsupportedEncodingException e) {
            //     e.printStackTrace();
            // }

            // 使用InputStreamReader
            value = props.getProperty(key);
        }
        return value;
    }

}
