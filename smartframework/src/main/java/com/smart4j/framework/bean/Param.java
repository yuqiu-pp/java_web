package com.smart4j.framework.bean;

import com.smart4j.framework.util.CastUtil;

import java.util.Map;

public class Param {

    private Map<String, Object> paramMap;

    public Param(Map<String, Object> paramMap){
        this.paramMap = paramMap;
    }

    /**
     * 根据参数名获取long型值
     */
    // public long getLong(String name){
    //     return CastUtil.castLong(name);
    // }

    public Map<String, Object> getParamMap(){
        return paramMap;
    }
}
