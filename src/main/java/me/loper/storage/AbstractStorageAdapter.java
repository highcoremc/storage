package me.loper.storage;

import me.loper.scheduler.SchedulerAdapter;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public abstract class AbstractStorageAdapter {

    private final SchedulerAdapter scheduler;

    public AbstractStorageAdapter(SchedulerAdapter scheduler) {
        this.scheduler = scheduler;
    }

    protected <T> CompletableFuture<T> makeFuture(Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, this.scheduler.async());
    }

    protected CompletableFuture<Void> makeFuture(Throwing.Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, this.scheduler.async());
    }

    public abstract Storage getImplementation();

    public String getName() {
        return this.getImplementation().getImplementationName();
    }

    public void init() {
        try {
            this.getImplementation().init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            this.getImplementation().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
