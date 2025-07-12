package org.engine.runner;

import org.engine.config.SMSTaskConfig;
import org.engine.config.SMPPConfig;
import org.engine.config.ThreadPoolConfig;
import org.engine.model.Counter;
import org.engine.service.MSISDNService;
import org.engine.service.SmsSubmitter;
import org.engine.utils.MethodsUtils;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.TypeOfNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class LoadSMSEngineRunner {

    private static final Logger log = LoggerFactory.getLogger(LoadSMSEngineRunner.class);

    public static void main(String[] args) {
        try {
            if (args.length < 7) {
                System.err.println("Usage: java -jar smpp-sms-simulator.jar <host> <srcAddr> <msisdnCount> <smsPerSecond> <msisdnFilePath> <durationInSec> <smsPerSocket> [systemId=...] [systemType=...] [password=...] [port=...] [message=...]");
                return;
            }

            // Arguments obligatoires
            String host = args[0];
            String srcAddr = args[1];
            String msisdnCountStr = args[2];
            String smsPerSecondStr = args[3];
            String msisdnFilePath = args[4];
            String durationStr = args[5];
            String smsPerSocketStr = args[6];

            // Paramètres optionnels
            String systemId = null;
            String systemType = null;
            String password = null;
            String port = null;
            String message = "Hello World €$£";

            for (int i = 7; i < args.length; i++) {
                if (args[i].startsWith("systemId=")) {
                    systemId = args[i].substring("systemId=".length());
                } else if (args[i].startsWith("systemType=")) {
                    systemType = args[i].substring("systemType=".length());
                } else if (args[i].startsWith("password=")) {
                    password = args[i].substring("password=".length());
                } else if (args[i].startsWith("port=")) {
                    port = args[i].substring("port=".length());
                } else if (args[i].startsWith("message=")) {
                    message = args[i].substring("message=".length());
                }
            }

            // Validation et préparation des configs
            SMPPConfig smppConfig = new SMPPConfig(host, srcAddr, systemId, systemType, password, port);
            smppConfig.validate();

            SMSTaskConfig taskConfig = new SMSTaskConfig(msisdnCountStr, smsPerSecondStr, durationStr);
            taskConfig.validate();

            int totalSmsCount = taskConfig.getSmsPerSecond() * (int) taskConfig.getDurationInSeconds() * taskConfig.getMsisdnCount();
            int smsPerSocket = Integer.parseInt(smsPerSocketStr);
            int totalSockets = (int) Math.ceil((double) totalSmsCount / smsPerSocket);

            log.info("Total SMS to send: {}, SMS per Socket: {}, Threads needed: {}", totalSmsCount, smsPerSocket, totalSockets);

            // Chargement MSISDN depuis fichier
            List<String> loadedMsisdns = readMsisdnsFromFile(msisdnFilePath);
            MSISDNService msisdnService = new MSISDNService();
            ArrayList<String> selectedMsisdns = msisdnService.processMsisdns(new ArrayList<>(loadedMsisdns), taskConfig.getMsisdnCount());

            // Initialisation des compteurs par MSISDN
            ConcurrentHashMap<String, Counter> counterMap = new ConcurrentHashMap<>();
            for (String msisdn : selectedMsisdns) {
                counterMap.put(msisdn, new Counter(msisdn));
            }

            // Configuration pool de threads
            ThreadPoolConfig poolConfig = new ThreadPoolConfig();
            ExecutorService executor = poolConfig.getExecutorService();
            CountDownLatch latch = new CountDownLatch(totalSockets);

            // Définir paramètres runtime pour SmsSubmitter (distribution parfaite)
            SmsSubmitter.setRuntimeParameters(
                    taskConfig.getSmsPerSecond(),
                    (int) taskConfig.getDurationInSeconds(),
                    totalSockets
            );

            // Calcul du nombre SMS attendu par MSISDN (utile pour le résumé)
            int expectedSmsPerMsisdn = taskConfig.getSmsPerSecond() * (int) taskConfig.getDurationInSeconds();

            // Lancement des tâches SMPP dans le pool
            for (int i = 0; i < totalSockets; i++) {
                final int taskIndex = i;
                String finalMessage = message;
                executor.submit(() -> {
                    SMPPSession session = new SMPPSession();
                    try {
                        session.connectAndBind(
                                smppConfig.getHost(),
                                smppConfig.getPort(),
                                new BindParameter(
                                        BindType.BIND_TRX,
                                        smppConfig.getSystemId(),
                                        smppConfig.getPassword(),
                                        smppConfig.getSystemType(),
                                        TypeOfNumber.UNKNOWN,
                                        NumberingPlanIndicator.UNKNOWN,
                                        null
                                )
                        );

                        SmsSubmitter.submitSmsTask(
                                "task-" + taskIndex,
                                session,
                                smppConfig.getSrcAddr(),
                                selectedMsisdns,
                                finalMessage,
                                smsPerSocket,
                                counterMap
                        );

                    } catch (Exception e) {
                        log.error("Error in task-{}: {}", taskIndex, e.getMessage(), e);
                    } finally {
                        try {
                            session.unbindAndClose();
                        } catch (Exception e) {
                            log.warn("Failed to close session for task-{}", taskIndex);
                        }
                        latch.countDown();
                    }
                });
            }

            latch.await(); // Attente de la fin de toutes les tâches
            poolConfig.shutdown();

            // Génération du rapport final (avec expectedSmsPerMsisdn)
            MethodsUtils.generateSummaryFile(counterMap, "sms_summary.txt", expectedSmsPerMsisdn);

        } catch (Exception e) {
            log.error("Fatal error in LoadSMSEngineRunner: {}", e.getMessage(), e);
        }
    }

    private static List<String> readMsisdnsFromFile(String filePath) {
        List<String> msisdns = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String msisdn = line.trim();
                if (!msisdn.isEmpty()) {
                    msisdns.add(msisdn);
                }
            }
        } catch (Exception e) {
            log.error("Error reading MSISDNs from file '{}': {}", filePath, e.getMessage());
            throw new RuntimeException("Failed to read MSISDNs", e);
        }

        log.info("Loaded {} MSISDNs from file: {}", msisdns.size(), filePath);
        return msisdns;
    }
}
