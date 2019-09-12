package quan.database;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.conf.ConnectionUrlParser;
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
        basicDataSource.setUrl(config.connectionString);
        basicDataSource.setInitialSize(config.poolInitialSize);
        basicDataSource.setMaxTotal(config.poolMaxTotal);
        basicDataSource.setMinIdle(config.poolMinIdle);
        basicDataSource.setMaxIdle(config.poolMaxIdle);
        basicDataSource.setMaxWaitMillis(config.poolMaxWaitMillis);
        basicDataSource.setPoolPreparedStatements(true);

        List<String> initSqlList = new ArrayList<>();
        initSqlList.add(String.format("CREATE DATABASE IF NOT EXISTS `%s` CHARACTER SET 'utf8mb4'", config.database));
        initSqlList.add(String.format("USE `%s`", config.database));
        basicDataSource.setConnectionInitSqls(initSqlList);
        basicDataSource.setDefaultAutoCommit(true);

        dataSource = basicDataSource;

    }

    @Override
    public Config getConfig() {
        return (Config) super.getConfig();
    }

    @Override
    protected void registerTable0(Table table) {
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            String sql = String.format("CREATE TABLE IF NOT EXISTS `%s`( _key VARCHAR(%d) PRIMARY KEY,_data text ) ENGINE = INNODB DEFAULT charset = utf8mb4", table.getName(), getConfig().tableKeyLength);
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
    protected <K, V extends Data<K>> V get(Table<K, V> table, K key) {
        checkClosed();
        String sql = String.format("SELECT _data FROM `%s` WHERE _key = ?", table.getName());
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, key.toString());
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            String _data = resultSet.getString(1);
            V data = table.getDataFactory().apply(key);
            data.decode(new JSONObject(JSON.parseObject(_data)));
            return data;
        } catch (Exception e) {
            throw new DbException(e);
        }
    }

    @Override
    protected <K, V extends Data<K>> void put(V data) {
        checkClosed();
        String sql = String.format("INSERT INTO `%s`(_key, _data) values(?, ?) ON DUPLICATE KEY UPDATE _data = VALUES(_data)", data.getTable().getName());
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, data.getKey().toString());
            statement.setString(2, data.encode().toJSONString());
            statement.execute();
        } catch (Exception e) {
            throw new DbException(e);
        }
    }

    @Override
    protected <K, V extends Data<K>> void delete(Table<K, V> table, K key) {
        checkClosed();
        String sql = String.format("DELETE FROM `%s` WHERE _key = ?", table.getName());
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, key.toString());
            statement.execute();
        } catch (Exception e) {
            throw new DbException(e);
        }
    }

    @Override
    protected <K, V extends Data<K>> void bulkWrite(Table<K, V> table, Set<V> puts, Set<K> deletes) {
        checkClosed();

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                //插入或更新
                if (!puts.isEmpty()) {
                    String sql = String.format("INSERT INTO `%s`(_key, _data) values(?, ?) ON DUPLICATE KEY UPDATE _data = values(_data)" , table.getName());
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
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
                    String sql = String.format("DELETE FROM `%s` WHERE _key = ?" , table.getName());
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
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

        /**
         * 连接字符串,例如 jdbc:mysql://localhost:3306/test?user=root&password=123456&useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
         */
        private String connectionString;

        /**
         * MySql数据库名
         */
        private String database;

        private int poolInitialSize = 10;

        private int poolMaxTotal = 50;

        private int poolMinIdle = 10;

        private int poolMaxIdle = 30;

        private long poolMaxWaitMillis = 10000;

        /**
         * MySql表主键最大长度
         */
        private int tableKeyLength = 40;


        public String getConnectionString() {
            return connectionString;
        }

        public Config setConnectionString(String connectionString) {
            database = ConnectionUrlParser.parseConnectionString(connectionString).getPath();
            if (database == null) {
                throw new IllegalArgumentException("必须指定连接数据库");
            }
            this.connectionString = connectionString.replace("/" + database + "?", "?");
            return this;
        }

        public int getPoolInitialSize() {
            return poolInitialSize;
        }

        public Config setPoolInitialSize(int poolInitialSize) {
            this.poolInitialSize = poolInitialSize;
            return this;
        }

        public int getPoolMaxTotal() {
            return poolMaxTotal;
        }

        public Config setPoolMaxTotal(int poolMaxTotal) {
            this.poolMaxTotal = poolMaxTotal;
            return this;
        }

        public int getPoolMinIdle() {
            return poolMinIdle;
        }

        public Config setPoolMinIdle(int poolMinIdle) {
            this.poolMinIdle = poolMinIdle;
            return this;
        }

        public int getPoolMaxIdle() {
            return poolMaxIdle;
        }

        public Config setPoolMaxIdle(int poolMaxIdle) {
            this.poolMaxIdle = poolMaxIdle;
            return this;
        }

        public long getPoolMaxWaitMillis() {
            return poolMaxWaitMillis;
        }

        public Config setPoolMaxWaitMillis(long poolMaxWaitMillis) {
            this.poolMaxWaitMillis = poolMaxWaitMillis;
            return this;
        }


        public int getTableKeyLength() {
            return tableKeyLength;
        }

        public Config setTableKeyLength(int tableKeyLength) {
            this.tableKeyLength = tableKeyLength;
            return this;
        }
    }


}
