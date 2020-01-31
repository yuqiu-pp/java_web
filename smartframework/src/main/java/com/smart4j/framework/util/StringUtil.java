package com.smart4j.framework.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtil.class);


    public static boolean isEmpty(String str){
        return StringUtils.isEmpty(str);
    }

    public static String[] splitString(String str, String spl){
        return StringUtils.split(str, spl);
    }
}
