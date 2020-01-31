package com.smart4j.framework.bean;


import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 封装请求
 * 包括两部分：请求方法和路径
 * 例如 @Action("get:/customer")
 */
@Data
public class Request {

    // 请求方法
    private String requestMethod;

    // 请求路径
    private String requestPath;

    public Request(String requestMethod, String requestPath){
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
    }

    @Override
    public int hashCode(){
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj){
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
