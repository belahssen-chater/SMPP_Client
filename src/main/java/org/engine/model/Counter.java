package org.engine.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Counter {

    private static final Logger log = LoggerFactory.getLogger(Counter.class);
    private int success;
    private int failed;
    private String msisdn;

    public Counter(String msisdn) {

        this.success = 0;
        this.failed = 0;
        this.msisdn = msisdn;
        log.debug("Counter initialized for the MSISDN: {}", msisdn);
    }

    public int getSuccess() {
        return success;
    }

    public int getFailed() {
        return failed;
    }

    public void incrementSuccess() {
        this.success++;
    }

    public void incrementFailed() {
        this.failed++;
    }

    public String getMsisdn() {
        return msisdn;
    }

    @Override
    public String toString() {
        return "Counter{" +
                "success=" + success +
                ", failed=" + failed +
                ", msisdn='" + msisdn + '\'' +
                '}';
    }

}
