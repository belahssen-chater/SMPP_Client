package org.engine.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMPPConfig {

    private static final Logger log = LoggerFactory.getLogger(SMPPConfig.class);
    private final String host;
    private final String srcAddr;
    private final String systemId;
    private final String systemType;
    private final String password;
    private final int port;

    private static final String DEFAULT_SYSTEM_ID = "bmw.e2e";
    private static final String DEFAULT_SYSTEM_TYPE = "bmw.e2e";
    public static final String DEFAULT_PASSWORD = "51205AVT";
    public static final int DEFAULT_PORT = 9999;

    public SMPPConfig(String host, String srcAddr, String systemId, String systemType, String password, String port) {
        this.host = host;
        this.srcAddr = srcAddr;
        this.systemId = systemId != null ? systemId : DEFAULT_SYSTEM_ID;
        this.systemType = systemType != null ? systemType : DEFAULT_SYSTEM_TYPE;
        this.password = password != null ? password : DEFAULT_PASSWORD;
        this.port = Integer.parseInt(port) > 0 ? Integer.parseInt(port) : DEFAULT_PORT;
    }

    public SMPPConfig(String host, String srcAddr) {
        this(host, srcAddr, null, null,null, "0");
    }

    //verify host&srcAddr
    public void validate() {
        if (host == null || host.trim().isEmpty()) {
            log.error("SMPP GW Host Address cannot be empty");
            throw new IllegalArgumentException("SMPP GW Host Address cannot be empty");
        }
        if (srcAddr == null || srcAddr.trim().isEmpty()) {
            log.error("Source Address cannot be empty");
            throw new IllegalArgumentException("Source Address cannot be empty");
        }
    }


    public String getHost() {
        return host;
    }

    public String getSrcAddr() {
        return srcAddr;
    }

    public String getSystemId() {
        return systemId;
    }

    public String getSystemType() {
        return systemType;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }
}