package org.engine.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MSISDNParser {

    private static final Logger log = LoggerFactory.getLogger(MSISDNParser.class);
    ArrayList<String> msisdns = new ArrayList<>();

    String path;

    public MSISDNParser(String path) {
        this.path = path;
    }

    public ArrayList<String> parse() {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Trim the line to remove leading and trailing whitespaces
                line = line.trim();

                // Ignore lines starting with '#' or empty lines
                if (!line.startsWith("#") && !line.isEmpty()) {
                    msisdns.add(line);
                }
            }
        } catch (IOException e) {
            log.error("Error reading the file: " + e.getMessage());
        }
        String msisdnLog = String.join(",", msisdns);

        // Output the parsed MSISDNs
        log.info("Parsed MSISDNs: "+msisdnLog);

        return msisdns;
    }



}
