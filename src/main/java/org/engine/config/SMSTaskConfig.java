package org.engine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMSTaskConfig {

    private static final Logger log = LoggerFactory.getLogger(SMSTaskConfig.class);

    private final int msisdnCount;
    private final int smsPerSecond;
    private final long durationInSeconds;

    public SMSTaskConfig(String msisdnCount, String smsPerSecond, String durationInSeconds) {
        this.msisdnCount = Integer.parseInt(msisdnCount);
        this.smsPerSecond = Integer.parseInt(smsPerSecond);
        this.durationInSeconds = Long.parseLong(durationInSeconds);
    }

    public void validate() {
        if (msisdnCount <= 0) {
            log.error("MSISDN count must be positive");
            throw new IllegalArgumentException("MSISDN count must be positive");
        }
        if (smsPerSecond <= 0) {
            log.error("SMS per second must be positive");
            throw new IllegalArgumentException("SMS per second must be positive");
        }
        if (durationInSeconds <= 0) {
            log.error("Duration must be positive");
            throw new IllegalArgumentException("Duration must be positive");
        }
    }


    public int getMsisdnCount() {
        return msisdnCount;
    }

    public int getSmsPerSecond() {
        return smsPerSecond;
    }

    public long getDurationInSeconds() {
        return durationInSeconds;
    }

    public long getDurationInMillis(){
        return durationInSeconds*1000;
    }
}
