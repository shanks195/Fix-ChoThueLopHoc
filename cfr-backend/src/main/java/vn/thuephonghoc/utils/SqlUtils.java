package vn.thuephonghoc.utils;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SqlUtils {

    public static final String COLON = ":";

    private static volatile Map<String, DruidDataSource> map = new HashMap<>();

    private static String getKey(String jdbcUrl, String username, String password) {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(username)) {
            sb.append(username);
        }
        if (!StringUtils.isEmpty(password)) {
            sb.append(COLON).append(password);
        }
        sb.append(COLON).append(jdbcUrl.trim());

        return SecureUtil.md5(sb.toString());
    }

    /**
     * Get data source
     *
     * @param jdbcUrl
     * @param userName
     * @param password
     * @return
     */
    private static DataSource getDataSource(String jdbcUrl, String userName, String password) {
        String key = getKey(jdbcUrl, userName, password);
        if (!map.containsKey(key) || null == map.get(key)) {
            DruidDataSource druidDataSource = new DruidDataSource();

            String className;
            try {
                className = DriverManager.getDriver(jdbcUrl.trim()).getClass().getName();
            } catch (SQLException e) {
                throw new RuntimeException("Get class name error: =" + jdbcUrl);
            }
            if (StringUtils.isEmpty(className)) {
                DataTypeEnum dataTypeEnum = DataTypeEnum.urlOf(jdbcUrl);
                if (null == dataTypeEnum ) {
                    throw new RuntimeException("Not supported data type: jdbcUrl=" + jdbcUrl);
                }
                druidDataSource.setDriverClassName(dataTypeEnum.getDriver());
            } else {
                druidDataSource.setDriverClassName(className);
            }


            druidDataSource.setUrl(jdbcUrl);
            druidDataSource.setUsername(userName);
            druidDataSource.setPassword(password);
         // Configure the time to wait for the timeout to get the connection
            druidDataSource.setMaxWait(3000);
            // Configure initial size, minimum, maximum
            druidDataSource.setInitialSize(1);
            druidDataSource.setMinIdle(1);
            druidDataSource.setMaxActive(1);

            // Configure how long the interval is to detect idle connections that need to be closed, the unit is milliseconds
            druidDataSource.setTimeBetweenEvictionRunsMillis(50000);
            // Configure how long to wait once the retry fails multiple times before continuing to retry the connection, in milliseconds
            druidDataSource.setTimeBetweenConnectErrorMillis(18000);
            // Configure the minimum survival time of a connection in the pool, in milliseconds
            druidDataSource.setMinEvictableIdleTimeMillis(300000);
            // This feature solves the problem of MySQL server closing the connection in 8 hours
            druidDataSource.setMaxEvictableIdleTimeMillis(25200000);

            try {
                druidDataSource.init();
            } catch (SQLException e) {
                log.error("Exception during pool initialization", e);
                throw new RuntimeException(e.getMessage());
            }
            map.put(key, druidDataSource);
        }
        return map.get(key);
    }

    private static Connection getConnection(String jdbcUrl, String userName, String password) {
        DataSource dataSource = getDataSource(jdbcUrl, userName, password);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (Exception ignored) {}
        try {
            int timeOut = 5;
            if (null == connection || connection.isClosed() || !connection.isValid(timeOut)) {
                log.info("connection is closed or invalid, retry get connection!");
                connection = dataSource.getConnection();
            }
        } catch (Exception e) {
            log.error("create connection error, jdbcUrl: {}", jdbcUrl);
            throw new RuntimeException("create connection error, jdbcUrl: "+ jdbcUrl);
        }
        return connection;
    }

    private static void releaseConnection(Connection connection) {
        if (null != connection) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("connection close error:" + e.getMessage());
            }
        }
    }


    public static void closeResult(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean testConnection(String jdbcUrl, String userName, String password) {
        Connection connection = null;
        try {
            connection = getConnection(jdbcUrl, userName, password);
            if (null != connection) {
                return true;
            }
        }catch (Exception e){
            log.info("Get connection failed:" + e.getMessage());
        }finally {
            releaseConnection(connection);
        }
        return false;
    }

    public static String executeFile(String jdbcUrl, String userName, String password, File sqlFile) {
        Connection connection = getConnection(jdbcUrl, userName, password);
        try {
            batchExecute(connection, readSqlList(sqlFile));
        } catch (Exception e) {
            log.error("An exception occurred during the execution of the sql script: {}",e.getMessage());
            return e.getMessage();
        }finally {
            releaseConnection(connection);
        }
        return "success";
    }


    /**
     * Execute sql in batch
     * @param connection
     * @param sqlList
     * @return
     */
    public static void batchExecute(Connection connection, List<String> sqlList) throws SQLException {
        Statement st = connection.createStatement();
        for (String sql: sqlList) {
            if (sql.endsWith(";")) {
                sql = sql.substring(0, sql.length()-1);
            }
            st.addBatch(sql);
        }
        st.executeBatch();
    }

    /**
     * Read the sql statement in the file into the list in units of;
     * @param sqlFile
     * @return
     * @throws Exception
     */
    private static List<String> readSqlList(File sqlFile) throws Exception {
        List<String> sqlList = Lists.newArrayList();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(sqlFile), StandardCharsets.UTF_8))) {
            String tmp;
            while ((tmp = reader.readLine()) != null) {
                log.info("line:{}", tmp);
                if (tmp.endsWith(";")) {
                    sb.append(tmp);
                    sqlList.add(sb.toString());
                    sb.delete(0, sb.length());
                } else {
                    sb.append(tmp);
                }
            }
            if (!"".endsWith(sb.toString().trim())) {
                sqlList.add(sb.toString());
            }
        }

        return sqlList;
    }

}
