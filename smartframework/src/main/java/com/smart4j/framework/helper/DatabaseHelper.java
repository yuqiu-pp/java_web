package com.smart4j.framework.helper;

import com.smart4j.framework.ConfigConstant;
import org.apache.commons.collections4.QueueUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DatabaseHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);
    // 保存线程的连接
    private static final ThreadLocal<Connection> CONNECTION_HOLD;
    // 连接池
    private static final BasicDataSource DATA_SOURCE;
    // 实体查询
    private static final QueryRunner QUEUE_RUNNER;


    static {
        CONNECTION_HOLD = new ThreadLocal<>();
        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setUrl(ConfigHelper.getJdbcUrl());
        DATA_SOURCE.setUsername(ConfigHelper.getJdbcUsername());
        DATA_SOURCE.setPassword(ConfigHelper.getJdbcPassword());
        QUEUE_RUNNER = new QueryRunner();
    }

    private BasicDataSource getDataSource(){
        return DATA_SOURCE;
    }

    /**
     * 获取数据库连接
     */
    private static Connection getConnection(){
        Thread thread = Thread.currentThread();
        Connection conn = CONNECTION_HOLD.get();
        if (conn == null){
            try {
                conn = DATA_SOURCE.getConnection();
            } catch (SQLException e) {
                LOGGER.error("get connection failure");
            }finally {
                CONNECTION_HOLD.set(conn);
            }
        }

        return conn;
    }

    /**
     * 释放数据库连接
     * 由于使用连接池，所以不需要真正关闭conn
     * 连接池的conn.close()应该只是返回连接给连接池
     *
     */


    /**
     * 开启事务
     * 需要将Connection放入ThreadLocal，提交事物时从ThreadLocal中移除
     * 这样整个事务内数据库连接都从ThreadLocal中获取，保证使用同一个连接
     */
    public static void beginTranscation(){
        Connection conn = getConnection();
        if (conn != null){
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                LOGGER.error("begin transaction failure", e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLD.set(conn);
            }
        }
    }

    /**
     * 提交事物
     */
    public static void commitTransaction(){
        Connection conn = getConnection();
        if (conn != null){
            try {
                conn.commit();
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure", e);
                throw new RuntimeException();
            }finally {
                CONNECTION_HOLD.remove();
            }
        }
    }

    /**
     * 回滚事务
     */
    public static void rollbackTransaction(){
        Connection conn = getConnection();
        if (conn != null){
            try {
                conn.rollback();
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("rollback failure", e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLD.remove();
            }
        }
    }


    /**
     * 查询实体列表
     */
    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object...pararms){
        List<T> entityList;
        Connection conn = getConnection();
        try {
            entityList = QUEUE_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), pararms);
            conn.close();

        } catch (Exception e) {
            LOGGER.error("query entity failure", e);
            throw new RuntimeException(e);
        } finally {
        }
        return entityList;

    }

}
