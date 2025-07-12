package org.engine.config;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ThreadPoolConfig {
    public static final Logger log= LoggerFactory.getLogger(ThreadPoolConfig.class);
    private final ExecutorService executorService;

    public ThreadPoolConfig() {
        this.executorService = Executors.newFixedThreadPool(Integer.MAX_VALUE);
        log.info("Thread pool initialized");
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void shutdown() {
        executorService.shutdown();
        log.info("Thread pool has been shutdown");
    }

}
