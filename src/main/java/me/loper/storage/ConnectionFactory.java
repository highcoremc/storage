package me.loper.storage;

import java.util.function.Function;

public interface ConnectionFactory<E extends AutoCloseable> {
    String getImplementationName();

    void init();

    void shutdown() throws Exception;

    E getConnection() throws Exception;

    Function<String, String> getStatementProcessor();
}