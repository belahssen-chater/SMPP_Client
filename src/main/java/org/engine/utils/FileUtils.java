package org.engine.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileUtils {
    public static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static boolean doesFileExist(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            log.error("File path cannot be null or empty");
            throw new IllegalArgumentException("File path cannot be null or empty");
        }


        File file = new File(filePath);
        boolean exists=file.isFile();

        if(!exists){
            log.error("File does not exist or is not a file: " + filePath);
            throw new IllegalArgumentException("File does not exist or is not a file");
        }
        return exists;
    }
}
