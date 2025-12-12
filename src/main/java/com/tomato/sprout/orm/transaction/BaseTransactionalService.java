package com.tomato.sprout.orm.transaction;

import com.tomato.sprout.orm.DatabaseConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: 事务管理基类
 * @date 2025/12/11 16:14
 */
public abstract class BaseTransactionalService {
    // 当前线程的事务连接
    private final ThreadLocal<Connection> currentConnection = new ThreadLocal<>();

    // 事务状态
    private final ThreadLocal<Boolean> transactionActive = ThreadLocal.withInitial(() -> false);

    /**
     * 开始事务
     */
    protected void beginTransaction() throws SQLException {
        if (transactionActive.get()) {
            throw new IllegalStateException("Transaction already active");
        }

        Connection connection = DatabaseConnectionPool.getInstance().getConnection();
        connection.setAutoCommit(false);

        currentConnection.set(connection);
        transactionActive.set(true);

        // 绑定到连接持有器，供Mapper使用
        ConnectionHolder.setConnection(connection);
    }

    /**
     * 提交事务
     */
    protected void commit() throws SQLException {
        Connection connection = currentConnection.get();
        if (connection == null || connection.isClosed()) {
            throw new IllegalStateException("No active transaction");
        }

        connection.commit();
    }

    /**
     * 回滚事务
     */
    protected void rollback() throws SQLException {
        Connection connection = currentConnection.get();
        if (connection != null && !connection.isClosed()) {
            connection.rollback();
        }
    }

    /**
     * 结束事务（清理资源）
     */
    protected void endTransaction() throws SQLException {
        Connection connection = currentConnection.get();

        try {
            if (connection != null && !connection.isClosed()) {
                // 恢复自动提交
                connection.setAutoCommit(true);
                // 归还连接
                DatabaseConnectionPool.getInstance().releaseConnection(connection);
            }
        } finally {
            // 清理ThreadLocal
            currentConnection.remove();
            transactionActive.set(false);
            ConnectionHolder.clear();
        }
    }

    /**
     * 执行带事务的操作（模板方法）
     * 这是最常用的方法，子类应该优先使用这个方法
     */
    protected <T> T executeTransactional(TransactionalOperation<T> operation) throws Exception {
        beginTransaction();

        try {
            // 执行业务操作
            T result = operation.execute();

            // 提交事务
            commit();

            return result;

        } catch (Exception e) {
            System.out.println("发生异常时回滚");
            // 发生异常时回滚
            try {
                rollback();
            } catch (SQLException rollbackEx) {
                // 记录日志，但不覆盖原始异常
                e.addSuppressed(rollbackEx);
            }
            throw e;
        } finally {
            // 总是清理资源
            try {
                endTransaction();
            } catch (SQLException endEx) {
                // 记录日志，但不抛出异常
                System.err.println("Error ending transaction: " + endEx.getMessage());
            }
        }
    }

    /**
     * 执行带事务的操作（无返回值）
     */
    protected void executeTransactionalVoid(TransactionalVoidOperation operation) throws Exception {
        executeTransactional(() -> {
            operation.execute();
            return null;
        });
    }

    /**
     * 执行只读操作（不开启事务）
     */
    protected <T> T executeQuery(TransactionalOperation<T> operation) throws Exception {
        Connection connection = null;
        try {
            connection = DatabaseConnectionPool.getInstance().getConnection();
            ConnectionHolder.setConnection(connection);

            return operation.execute();

        } finally {
            if (connection != null) {
                try {
                    DatabaseConnectionPool.getInstance().releaseConnection(connection);
                } catch (Exception ignored) {}
                ConnectionHolder.clear();
            }
        }
    }

    // ==================== 函数式接口定义 ====================

    /**
     * 带返回值的事务操作接口
     */
    @FunctionalInterface
    protected interface TransactionalOperation<T> {
        T execute() throws Exception;
    }

    /**
     * 无返回值的事务操作接口
     */
    @FunctionalInterface
    protected interface TransactionalVoidOperation {
        void execute() throws Exception;
    }

    public static class ConnectionHolder{
        private static final ThreadLocal<Connection> holder = new ThreadLocal<>();

        public static void setConnection(Connection connection) {
            holder.set(connection);
        }

        public static Connection getConnection() {
            return holder.get();
        }

        public static boolean hasConnection() {
            return holder.get() != null;
        }

        public static void clear() {
            holder.remove();
        }
    }
}
