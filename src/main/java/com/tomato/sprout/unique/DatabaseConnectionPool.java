package com.tomato.sprout.unique;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: TODO
 * @date 2025/10/18 18:26
 */
public class DatabaseConnectionPool {
    private static volatile DatabaseConnectionPool instance;
    private final List<Connection> connectionPool;
    private final List<Connection> usedConnections;
    private final int MAX_POOL_SIZE;
    private final String url;
    private final String user;
    private final String password;
    private final Integer maxWaitTime;

    private DatabaseConnectionPool() {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        MAX_POOL_SIZE = Integer.parseInt(configurationManager.getProperty("database.pool.size"));
        url = String.valueOf(configurationManager.getProperty("database.url"));
        user = String.valueOf(configurationManager.getProperty("database.user"));
        password = String.valueOf(configurationManager.getProperty("database.password"));
        maxWaitTime = Integer.valueOf(configurationManager.getProperty("database.maxWaitTime"));
        connectionPool = new ArrayList<>(MAX_POOL_SIZE);
        usedConnections = new ArrayList<>(MAX_POOL_SIZE);
        initPool();
    }

    /**
     * @description: 创建单例实例
     * // synchronized 双重检查 防止上一个获取锁的已经创建过
     * @author zhaojiulin
     * @param: null
     * @return: 连接池唯一实例
     * @Date: 2025/10/18 19:24
     */
    public static DatabaseConnectionPool getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionPool.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionPool();
                }
            }
        }
        return instance;
    }

    /**
     * @description: 初始化连接池
     * @author zhaojiulin
     * @param: null
     * @return: 无
     * @Date: 2025/10/18 19:23
     */
    private void initPool() {
        System.out.printf("初始化数据库连接池开始：%s%n", url);
        for (int i = 0; i < MAX_POOL_SIZE; i++) {
            try {
                connectionPool.add(DriverManager.getConnection(url, user, password));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.printf("初始化数据库连接池完成，连接池数：%s，最大连接池数量：%s%n", connectionPool.size(), MAX_POOL_SIZE);
    }

    /**
     * @description: 获取数据库连接
     * @author zhaojiulin
     * @param: null
     * @return: Connection 数据库连接
     * @Date: 2025/10/18 19:23
     */
    public synchronized Connection getConnection() {
        try {
            if (connectionPool.isEmpty()) {
                throw new RuntimeException("Pool is empty");
            }
            // 取出一个连接
            Connection connection = connectionPool.remove(connectionPool.size() - 1);
            // 连接失效 重新创建并放入连接池
            if (null == connection || connection.isClosed() || !connection.isValid(maxWaitTime)) {
                connection = DriverManager.getConnection(url, user, password);
                connectionPool.add(connection);
            }
            // 放入使用连接池
            usedConnections.add(connection);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @description: 归还连接
     * @author
     * @param: null
     * @return:
     * @Date: 2025/10/18 19:39
     */
    public synchronized void releaseConnection(Connection connection) {
        usedConnections.remove(connection);
        connectionPool.add(connection);
    }

    /**
     * @description: 关闭所有连接
     * @author zhaojiulin
     * @param: null
     * @return: void
     * @Date: 2025/10/18 19:38
     */
    public synchronized void shutdown() {
        for (Connection connection : connectionPool) {
            try {
                connection.close();
            } catch (SQLException e) {
                // 记录日志
            }
        }
        for (Connection connection : usedConnections) {
            try {
                connection.close();
            } catch (SQLException e) {
                // 记录日志
            }
        }
    }
}
