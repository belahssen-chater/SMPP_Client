package org.engine.service;

import org.engine.model.Counter;
import org.jsmpp.bean.*;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.SubmitSmResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SmsSubmitter {

    private static final Logger log = LoggerFactory.getLogger(SmsSubmitter.class);
    private static final AtomicInteger taskCounter = new AtomicInteger(0);

    // Runtime configuration
    private static int TOTAL_SMS_PER_MSISDN = 120; // Will be set dynamically
    private static int TOTAL_TASKS = 3;

    /**
     * Set runtime parameters (called by runner)
     */
    public static void setRuntimeParameters(int smsPerSecond, int durationSeconds, int numberOfTasks) {
        TOTAL_SMS_PER_MSISDN = smsPerSecond * durationSeconds;
        TOTAL_TASKS = numberOfTasks;
        log.info("Runtime parameters set: {} SMS per MSISDN, {} total tasks", TOTAL_SMS_PER_MSISDN, TOTAL_TASKS);
    }

    /**
     * Unified SMS Task submission with automatic perfect distribution
     */
    public static void submitSmsTask(String taskName, SMPPSession session, String srcAddr,
                                     List<String> msisdns, String message, int smsPerSocket,
                                     ConcurrentHashMap<String, Counter> counterMap) {
        try {
            int taskNum = extractTaskNumber(taskName);

            // Handle distribution with remainder
            int baseSmsPerMsisdn = TOTAL_SMS_PER_MSISDN / TOTAL_TASKS;
            int remainder = TOTAL_SMS_PER_MSISDN % TOTAL_TASKS;

            int smsPerMsisdnThisTask = baseSmsPerMsisdn + (taskNum < remainder ? 1 : 0);

            log.info("Task {}: Sending {} SMS per MSISDN (total={} / tasks={} + remainder adjustment)",
                    taskName, smsPerMsisdnThisTask, TOTAL_SMS_PER_MSISDN, TOTAL_TASKS);

            int totalSent = 0;

            for (String msisdn : msisdns) {
                for (int i = 0; i < smsPerMsisdnThisTask; i++) {
                    boolean success = submitSingleSms(session, srcAddr, msisdn, message);

                    // Update success/fail counter
                    Counter counter = counterMap.get(msisdn);
                    if (counter != null) {
                        if (success) {
                            counter.incrementSuccess();
                        } else {
                            counter.incrementFailed();
                        }
                    }

                    totalSent++;

                    // Optional throttle delay (adjust if needed)
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.warn("Task {} interrupted", taskName);
                        return;
                    }
                }
                log.debug("Task {}: Sent {} SMS to {}", taskName, smsPerMsisdnThisTask, msisdn);
            }

            log.info("Task {} completed: Total SMS sent = {}", taskName, totalSent);

        } catch (Exception e) {
            log.error("Task {} encountered error: {}", taskName, e.getMessage(), e);
        }
    }

    /**
     * Submit a single SMS using SMPP
     */
    public static boolean submitSingleSms(SMPPSession session, String srcAddr,
                                          String destinationAddr, String message) {
        try {
            log.debug("Sending SMS to {} from {}", destinationAddr, srcAddr);

            SubmitSmResult result = session.submitShortMessage(
                    "", // serviceType
                    TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, srcAddr,
                    TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, destinationAddr,
                    new ESMClass(), (byte) 0, (byte) 1, null, null,
                    new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
                    (byte) 0, new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false),
                    (byte) 0, message.getBytes()
            );

            log.info("SMS submitted successfully (message_id={})", result.getMessageId());
            return true;

        } catch (Exception e) {
            log.error("Failed to submit SMS to {}: {}", destinationAddr, e.getMessage());
            return false;
        }
    }

    /**
     * Extract integer from task name (ex: task-0 → 0)
     */
    private static int extractTaskNumber(String taskName) {
        try {
            return Integer.parseInt(taskName.replace("task-", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}
