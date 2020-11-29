package me.loper.storage.sql.connection.factory;

import com.google.common.collect.ImmutableList;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.loper.storage.ConnectionFactory;
import me.loper.storage.sql.SqlStorageCredentials;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public abstract class HikariConnectionFactory implements ConnectionFactory<Connection> {

    protected final SqlStorageCredentials configuration;
    private HikariDataSource hikari;

    public HikariConnectionFactory(SqlStorageCredentials configuration) {
        this.configuration = configuration;
    }

    protected String getDriverClass() {
        return null;
    }

    protected void appendProperties(HikariConfig config, Map<String, String> properties) {
        for (Map.Entry<String, String> property : properties.entrySet()) {
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }
    }

    protected void appendConfigurationInfo(HikariConfig config) {
        String address = this.configuration.getAddress();
        String[] addressSplit = address.split(":");
        address = addressSplit[0];
        String port = addressSplit.length > 1 ? addressSplit[1] : "3306";

        config.setDataSourceClassName(getDriverClass());
        config.addDataSourceProperty("serverName", address);
        config.addDataSourceProperty("port", port);
        config.addDataSourceProperty("databaseName", this.configuration.getDatabase());
        config.setUsername(this.configuration.getUsername());
        config.setPassword(this.configuration.getPassword());
    }

    @Override
    public void init() {
        HikariConfig config;
        try {
            config = new HikariConfig();
        } catch (LinkageError e) {
            // dumb plugins seem to keep doing stupid stuff with shading of SLF4J and Log4J.
            // detect this and print a more useful error message.
            handleLinkageError(e);
            throw e;
        }

        config.setPoolName("pdsync-hikari");

        appendConfigurationInfo(config);

        Map<String, String> properties = new HashMap<>(this.configuration.getProperties());

        // https://github.com/brettwooldridge/HikariCP/wiki/Rapid-Recovery
        properties.putIfAbsent("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));

        appendProperties(config, properties);

        config.setMaximumPoolSize(this.configuration.getMaxPoolSize());
        config.setMinimumIdle(this.configuration.getMinIdleConnections());
        config.setMaxLifetime(this.configuration.getMaxLifetime());
        config.setConnectionTimeout(this.configuration.getConnectionTimeout());

        // don't perform any initial connection validation - we subsequently call #getConnection
        // to setup the schema anyways
        config.setInitializationFailTimeout(-1);

        this.hikari = new HikariDataSource(config);
    }

    @Override
    public void shutdown() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.hikari == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }
        Connection connection = this.hikari.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }
        return connection;
    }

    private void handleLinkageError(LinkageError linkageError) {
        List<String> noteworthyClasses = ImmutableList.of(
                "org.slf4j.LoggerFactory",
                "org.slf4j.ILoggerFactory",
                "org.apache.logging.slf4j.Log4jLoggerFactory",
                "org.apache.logging.log4j.spi.LoggerContext",
                "org.apache.logging.log4j.spi.AbstractLoggerAdapter",
                "org.slf4j.impl.StaticLoggerBinder"
        );

        if (null == this.getLogger()) {
            return;
        }

        this.getLogger().warning("A " + linkageError.getClass().getSimpleName() + " has occurred whilst initialising Hikari. This is likely due to classloading conflicts between other plugins.");
        this.getLogger().warning("Please check for other plugins below (and try loading org.nocraft.loperd.datasync.common without them installed) before reporting the issue.");

        for (String className : noteworthyClasses) {
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (Exception ex) {
                continue;
            }

            ClassLoader loader = clazz.getClassLoader();
            String loaderName = loader.toString();

            this.getLogger().warning("Class " + className + " has been loaded by: " + loaderName);
        }
    }

    protected abstract Logger getLogger();
}
