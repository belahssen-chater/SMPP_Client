package org.engine.utils;

import org.engine.model.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MethodsUtils {

    private static final Logger log = LoggerFactory.getLogger(MethodsUtils.class);

    /**
     * Generates a random sublist of the given size from the input list.
     *
     * @param mainList the main list of strings
     * @param num the desired size of the sublist
     * @return a random sublist of size num
     */
    public static ArrayList<String> getRandomSubList(ArrayList<String> mainList, int num) {
        if (num > mainList.size()) {
            log.error("The size of the sublist cannot exceed the size of the main list.");
            throw new IllegalArgumentException("The size of the sublist cannot exceed the size of the main list.");
        }
        if (num < 0) {
            log.error("The size of the sublist cannot be negative.");
            throw new IllegalArgumentException("The size of the sublist cannot be negative.");
        }

        LinkedHashSet<String> uniqueElements = new LinkedHashSet<>(mainList);

        if (num > uniqueElements.size()) {
            log.error("The size of the sublist cannot exceed the number of unique elements in the main list.");
            throw new IllegalArgumentException("The size of the sublist cannot exceed the number of unique elements in the main list.");
        }

        ArrayList<String> uniqueList = new ArrayList<>(uniqueElements);

        Collections.shuffle(uniqueList);

        return new ArrayList<>(uniqueList.subList(0, num));
    }

    /**
     * Génère un fichier résumé enrichi des stats SMS par MSISDN,
     * incluant les SMS attendus, manquants, réussis et échoués.
     *
     * @param counterMap Map MSISDN -> Counter (statistiques)
     * @param filePath Chemin du fichier de sortie
     * @param expectedSmsPerMsisdn Nombre attendu de SMS par MSISDN
     */
    public static void generateSummaryFile(ConcurrentHashMap<String, Counter> counterMap, String filePath, int expectedSmsPerMsisdn) {
        if (counterMap == null || filePath == null) {
            log.error("CounterMap or filePath cannot be null");
            throw new IllegalArgumentException("CounterMap or filePath cannot be null");
        }

        int totalSuccess = 0;
        int totalFailed = 0;
        int totalMissing = 0;
        StringBuilder summaryOutput = new StringBuilder();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Header avec colonnes supplémentaires
            String header1 = String.format("%-15s %-15s %-15s %-15s %-15s%n",
                    "Msisdn", "Success SMS", "Failed SMS", "Expected SMS", "Missing SMS");
            String header2 = String.format("%-15s %-15s %-15s %-15s %-15s%n",
                    "---------------", "---------------", "---------------", "---------------", "---------------");

            writer.write(header1);
            writer.write(header2);
            summaryOutput.append(header1).append(header2);

            for (Map.Entry<String, Counter> entry : counterMap.entrySet()) {
                String msisdn = entry.getKey();
                Counter counter = entry.getValue();
                int success = counter.getSuccess();
                int failed = counter.getFailed();
                int missing = expectedSmsPerMsisdn - success;
                if (missing < 0) missing = 0; // sécurité

                String row = String.format("%-15s %-15d %-15d %-15d %-15d%n",
                        msisdn, success, failed, expectedSmsPerMsisdn, missing);

                writer.write(row);
                summaryOutput.append(row);

                totalSuccess += success;
                totalFailed += failed;
                totalMissing += missing;
            }

            String footer1 = String.format("%-15s %-15s %-15s %-15s %-15s%n",
                    "---------------", "---------------", "---------------", "---------------", "---------------");
            String footer2 = String.format("%-15s %-15d %-15d %-15s %-15d%n",
                    "Total", totalSuccess, totalFailed, "-", totalMissing);
            int totalExpected = expectedSmsPerMsisdn * counterMap.size();
            double successRate = totalExpected > 0 ? (100.0 * totalSuccess) / totalExpected : 0.0;
            String successRateStr = String.format("%nSuccess Rate: %.2f%%%n", successRate);

            writer.write(footer1);
            writer.write(footer2);
            writer.write(successRateStr);
            summaryOutput.append(footer1).append(footer2).append(successRateStr);

            System.out.println("\n=== SMS DELIVERY SUMMARY ===");
            System.out.print(summaryOutput.toString());

            log.info("Summary file generated successfully: {}", filePath);

        } catch (IOException e) {
            log.error("Error writing summary file: " + e.getMessage());
        }
    }

}
