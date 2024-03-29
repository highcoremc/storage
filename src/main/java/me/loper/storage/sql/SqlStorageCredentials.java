package me.loper.storage.sql;

import java.util.Map;
import java.util.Objects;

public class SqlStorageCredentials {
    private final String address;
    private final String database;
    private final String username;
    private final String password;
    private String poolName = "LoperSQLStorage-hikari";
    private final int maxPoolSize;
    private final int minIdleConnections;
    private final int maxLifetime;
    private final int connectionTimeout;
    private final Map<String, String> properties;

    public SqlStorageCredentials(
            String address, String database, String username, String password,
            int maxPoolSize, int minIdleConnections, int maxLifetime,
            int connectionTimeout, Map<String, String> properties
    ) {
        this.address = address;
        this.database = database;
        this.username = username;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.minIdleConnections = minIdleConnections;
        this.maxLifetime = maxLifetime;
        this.connectionTimeout = connectionTimeout;
        this.properties = properties;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getPoolName() {
        return poolName;
    }

    public String getAddress() {
        return Objects.requireNonNull(this.address, "address");
    }

    public String getDatabase() {
        return Objects.requireNonNull(this.database, "database");
    }

    public String getUsername() {
        return Objects.requireNonNull(this.username, "username");
    }

    public String getPassword() {
        return Objects.requireNonNull(this.password, "password");
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public int getMinIdleConnections() {
        return this.minIdleConnections;
    }

    public int getMaxLifetime() {
        return this.maxLifetime;
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }
}
