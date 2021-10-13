package test.database.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MyConnectionPool {
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Mimimimaks";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/new_schema";
    private static final Integer POOL_SIZE = 5;
    private static final Integer DEFAULT_POOL_SIZE = 10;
    private static BlockingQueue<Connection> pool;
    private static List<Connection> sourceConnection;


    static {
        initConnectionPool();
    }

    private MyConnectionPool() {
        throw new IllegalStateException("Utility Class!");
    }

    private static void initConnectionPool() {
/*       var poolSize = Prop.get(POOL_SIZE_KEY)
        if size lies in prop we should make the null check
        var size = POOL_SIZE == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);*/

        pool = new ArrayBlockingQueue<>(POOL_SIZE);
        sourceConnection = new ArrayList<>(POOL_SIZE);
//        заполняем пул до сайза то есть инициализируем соединения
        for (int i = 0; i < POOL_SIZE; i++) {
            var dbConnection = getDbConnection();
            var proxyConnection = (Connection)
                    Proxy.newProxyInstance(ConnectionManager.class.getClassLoader(), new Class[]{Connection.class},
                            (proxy, method, args) -> method.getName().equals("close")
                                    ? pool.add((Connection) proxy)
                                    : method.invoke(dbConnection, args));
            pool.add(proxyConnection);
            sourceConnection.add(dbConnection);
        }
    }

    public static Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection getDbConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * При вызове любого метода со взятием соединения, оборачиваем его в try и в finally закрываем closePool
     */
    public static void closePool() {
        for (Connection sourceConnection : sourceConnection) {
            try {
                sourceConnection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

