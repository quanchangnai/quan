package quan.database;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.jdbc.Driver;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by quanchangnai on 2019/7/8.
 */
public class MySqlDB extends Database {

    private DataSource dataSource;

    public MySqlDB(Config config) {
        super(config);
    }

    @Override
    protected void open0() {
        MySqlDB.Config config = getConfig();
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(Driver.class.getName());
        basicDataSource.setUsername(config.username);
        basicDataSource.setPassword(config.password);
        basicDataSource.setUrl(config.url);
        basicDataSource.setInitialSize(config.initialSize);
        basicDataSource.setMaxTotal(config.maxTotal);
        basicDataSource.setMaxIdle(config.maxIdle);
        basicDataSource.setMaxWaitMillis(config.maxWaitMillis);
        basicDataSource.setMinIdle(config.minIdle);
        basicDataSource.setPoolPreparedStatements(true);

        List<String> initSqlList = new ArrayList<>();
        initSqlList.add(String.format("CREATE DATABASE IF NOT EXISTS `%s` CHARACTER SET 'utf8mb4'", config.databaseName));
        initSqlList.add(String.format("USE `%s`", config.databaseName));
        basicDataSource.setConnectionInitSqls(initSqlList);
        basicDataSource.setDefaultAutoCommit(true);

        dataSource = basicDataSource;

    }

    @Override
    public Config getConfig() {
        return (Config) super.getConfig();
    }

    @Override
    protected void registerCache0(Cache cache) {
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            String sql = String.format("CREATE TABLE IF NOT EXISTS `%s`( _key VARCHAR(%d) PRIMARY KEY,_data text ) ENGINE = INNODB DEFAULT charset = utf8mb4", cache.getName(), getConfig().keyLength);
            statement.execute(sql);
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }


    @Override
    protected void close0() {
        try {
            ((BasicDataSource) dataSource).close();
            dataSource = null;
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    @Override
    protected <K, V extends Data<K>> V get(Cache<K, V> cache, K key) {
        checkClosed();
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT _data FROM `" + cache.getName() + "` WHERE _key=?")) {
            statement.setString(1, key.toString());
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            String _data = resultSet.getString(1);
            V data = cache.getDataFactory().apply(key);
            data.decode(new JSONObject(JSON.parseObject(_data)));
            return data;
        } catch (Exception e) {
            throw new DbException(e);
        }
    }

    @Override
    protected <K, V extends Data<K>> void put(V data) {
        checkClosed();
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + data.getCache().getName() + "`(_key, _data) values(?, ?) ON DUPLICATE KEY UPDATE _data=values(_data)")) {
            statement.setString(1, data.getKey().toString());
            statement.setString(2, data.encode().toJSONString());
            statement.execute();
        } catch (Exception e) {
            throw new DbException(e);
        }
    }

    @Override
    protected <K, V extends Data<K>> void delete(Cache<K, V> cache, K key) {
        checkClosed();
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM `" + cache.getName() + "` WHERE _key=?")) {
            statement.setString(1, key.toString());
            statement.execute();
        } catch (Exception e) {
            throw new DbException(e);
        }
    }

    @Override
    protected <K, V extends Data<K>> void bulkWrite(Cache<K, V> cache, Set<V> puts, Set<K> deletes) {
        checkClosed();

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                //插入或更新
                if (!puts.isEmpty()) {
                    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + cache.getName() + "`(_key, _data) values(?, ?) ON DUPLICATE KEY UPDATE _data=values(_data)")) {
                        for (V putData : puts) {
                            statement.setString(1, putData.getKey().toString());
                            statement.setString(2, putData.encode().toJSONString());
                            statement.addBatch();
                        }
                        statement.executeBatch();
                    }
                }

                //删除
                if (!deletes.isEmpty()) {
                    try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `" + cache.getName() + "` WHERE _key=?")) {
                        for (K deleteKey : deletes) {
                            statement.setString(1, deleteKey.toString());
                            statement.addBatch();
                        }
                        statement.executeBatch();
                    }
                }

                //提交
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (Exception e) {
            throw new DbException(e);
        }


    }

    public static class Config extends Database.Config {

        private String username = "root";

        private String password = "123456";

        private String databaseName = "test";

        private String url = "jdbc:mysql://localhost:3306?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai";

        private int initialSize = 10;

        private int maxTotal = 50;

        private int maxIdle = 60;

        private long maxWaitMillis = 10000;

        private int minIdle = 30;

        /**
         * MySql表主键最大长度
         */
        private int keyLength = 40;


        public String getUsername() {
            return username;
        }

        public Config setUsername(String username) {
            this.username = username;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public Config setPassword(String password) {
            this.password = password;
            return this;
        }

        public String getDatabaseName() {
            return databaseName;
        }

        public Config setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public Config setUrl(String url) {
            this.url = url;
            return this;
        }

        public int getInitialSize() {
            return initialSize;
        }

        public Config setInitialSize(int initialSize) {
            this.initialSize = initialSize;
            return this;
        }

        public int getMaxTotal() {
            return maxTotal;
        }

        public Config setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
            return this;
        }

        public int getMaxIdle() {
            return maxIdle;
        }

        public Config setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
            return this;
        }

        public long getMaxWaitMillis() {
            return maxWaitMillis;
        }

        public Config setMaxWaitMillis(long maxWaitMillis) {
            this.maxWaitMillis = maxWaitMillis;
            return this;
        }

        public int getMinIdle() {
            return minIdle;
        }

        public Config setMinIdle(int minIdle) {
            this.minIdle = minIdle;
            return this;
        }

        public int getKeyLength() {
            return keyLength;
        }

        public Config setKeyLength(int keyLength) {
            this.keyLength = keyLength;
            return this;
        }
    }


}
