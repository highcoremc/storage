package me.loper.storage.nosql.redis;

import java.util.Objects;

public class RedisStorageCredentials {

    private final String address;
    private final String password;
    private int database = 0;

    private final int maxPoolSize;
    private final long connectionTimeout;

    public RedisStorageCredentials(
            String address,
            String password,
            int database,
            int maxPoolSize,
            long connectionTimeout
    ) {
        this.address = address;
        this.database = database;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.connectionTimeout = connectionTimeout;
    }

    public RedisStorageCredentials(String address, String password, int maxPoolSize, int connectionTimeout) {
        this.address = address;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.connectionTimeout = connectionTimeout;
    }

    public String getAddress() {
        return Objects.requireNonNull(this.address, "address");
    }

    public String getPassword() {
        return Objects.requireNonNull(this.password, "password");
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public long getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public int getDatabase() {
        return this.database;
    }
}
