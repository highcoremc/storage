package me.loper.storage.sql.connection.factory;

import com.zaxxer.hikari.HikariConfig;
import me.loper.storage.sql.SqlStorageCredentials;

import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

public class MySqlConnectionFactory extends HikariConnectionFactory {

    public MySqlConnectionFactory(SqlStorageCredentials configuration) {
        super(configuration);
    }

    @Override
    public String getImplementationName() {
        return "MySQL";
    }

    @Override
    protected String getDriverClass() {
        return "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
    }

    @Override
    protected void appendProperties(HikariConfig config, Map<String, String> properties) {
        // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        properties.putIfAbsent("cachePrepStmts", "true");
        properties.putIfAbsent("prepStmtCacheSize", "250");
        properties.putIfAbsent("prepStmtCacheSqlLimit", "2048");
        properties.putIfAbsent("useServerPrepStmts", "true");
        properties.putIfAbsent("useLocalSessionState", "true");
        properties.putIfAbsent("rewriteBatchedStatements", "true");
        properties.putIfAbsent("cacheResultSetMetadata", "true");
        properties.putIfAbsent("cacheServerConfiguration", "true");
        properties.putIfAbsent("elideSetAutoCommits", "true");
        properties.putIfAbsent("maintainTimeStats", "false");
        properties.putIfAbsent("alwaysSendSetIsolation", "false");
        properties.putIfAbsent("cacheCallableStmts", "true");

        // append configurable properties
        super.appendProperties(config, properties);
    }

    @Override
    protected Logger getLogger() {
        return null;
    }

    @Override
    public Function<String, String> getStatementProcessor() {
        return s -> s.replace("'", "`"); // use backticks for quotes
    }

}
