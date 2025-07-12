package org.engine.config;

import org.junit.Test;

import static org.junit.Assert.*;

public class SMSTaskConfigTest {
    @Test
    public void testValidConfigInput() {
        SMSTaskConfig config = new SMSTaskConfig("10", "1", "60");
        config.validate(); //no Exception

        assertEquals(10, config.getMsisdnCount());
        assertEquals(1, config.getSmsPerSecond());
        assertEquals(60, config.getDurationInSeconds());
        assertEquals(60000, config.getDurationInMillis());
    }

    @Test
    public void testInvalidMsisdnCount() {
        SMSTaskConfig config = new SMSTaskConfig("0", "1", "60");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> config.validate());
        assertEquals("MSISDN count must be positive", exception.getMessage());
    }


    @Test
    public void testInvalidSmsPerSecond() {
        SMSTaskConfig config = new SMSTaskConfig("10", "0", "60");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> config.validate());
        assertEquals("SMS per second must be positive", exception.getMessage());
    }

    @Test
    public void testInvalidDuration() {
        SMSTaskConfig config = new SMSTaskConfig("10", "1", "0");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> config.validate());
        assertEquals("Duration must be positive", exception.getMessage());
    }

    @Test(expected = NumberFormatException.class)
    public void testInvalidNumberFormat() {
        new SMSTaskConfig("abc", "1", "60");
    }


}