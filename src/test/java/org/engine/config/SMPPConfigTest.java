package org.engine.config;


import org.junit.Test;

import static org.junit.Assert.*;

public class SMPPConfigTest {

    @Test
    public void testValideConfig() {
        SMPPConfig config= new SMPPConfig("localhost","12345");

        config.validate();

        assertEquals("localhost",config.getHost());
        assertEquals("12345",config.getSrcAddr());
        assertEquals("bmw.e2e",config.getSystemId());
        assertEquals("51205AVT",config.getPassword());
        assertEquals(9999,config.getPort());
    }

    @Test
    public void testEmptyHostConfig(){
        SMPPConfig config= new SMPPConfig("","12345");//"" or null
        Exception exception = assertThrows(IllegalArgumentException.class, config::validate);
        assertEquals("SMPP GW Host Address cannot be empty",exception.getMessage());


    }


    @Test
    public void testEmptySrcAddrConfig(){
        SMPPConfig config= new SMPPConfig("local",null);//"" or null
        Exception exception = assertThrows(IllegalArgumentException.class, config::validate);
        assertEquals("Source Address cannot be empty",exception.getMessage());
    }

    @Test
    public void testAllParamsConfig(){
        SMPPConfig config= new SMPPConfig("10.235.70.186","39140","bmw.prod","BBMMWW","Yq305512","9999");
        assertEquals(9999, config.getPort());
        System.out.println("host: "+config.getHost());
        System.out.println("systemId: "+config.getSystemId());
        System.out.println("systemType: "+config.getSystemType());
        System.out.println("password: "+config.getPassword());
        System.out.println("source Address: "+config.getSrcAddr());


    }

}