package org.engine.service;

import org.engine.utils.MethodsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class MSISDNService {
    private static final Logger log = LoggerFactory.getLogger(MSISDNService.class);

    public ArrayList<String> processMsisdns(ArrayList<String> msisdns, int targetCount) {
        if (msisdns == null || msisdns.isEmpty()) {
            log.error("MSISDN list is empty or null");
            throw new IllegalArgumentException("MSISDN list cannot be empty or null");
        }

        if (msisdns.size() > targetCount) {
            return MethodsUtils.getRandomSubList(msisdns, targetCount);
        }
        return msisdns;
    }
}
