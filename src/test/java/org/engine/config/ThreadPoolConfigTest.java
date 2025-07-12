package org.engine.config;

import org.junit.Test;

import java.util.concurrent.ExecutorService;

import static org.junit.Assert.*;

public class ThreadPoolConfigTest {

    @Test
    public void testThreadPoolInitialization() {
        ThreadPoolConfig config = new ThreadPoolConfig();
        ExecutorService executorService = config.getExecutorService();

        assertNotNull("ExecutorService should not be null", executorService);
        assertFalse("ExecutorService should not be shutdown initially", executorService.isShutdown());
    }

    @Test
    public void testThreadPoolShutdown() {
        ThreadPoolConfig config = new ThreadPoolConfig();
        ExecutorService executorService = config.getExecutorService();

        config.shutdown();
        assertTrue("ExecutorService should be shutdown after calling shutdown", executorService.isShutdown());
    }

}