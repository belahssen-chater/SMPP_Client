package org.engine.service;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class MSISDNServiceTest {
    @Test
    public void testProcessMsisdnsValidList() {
        MSISDNService service = new MSISDNService();
        ArrayList<String> msisdns = new ArrayList<>(Arrays.asList("12345", "67890", "54321", "09876"));

        ArrayList<String> result = service.processMsisdns(msisdns, 3);
        assertEquals(3, result.size());
        assertTrue(msisdns.containsAll(result));
    }

    @Test
    public void testProcessMsisdnsSmallerList() {
        MSISDNService service = new MSISDNService();
        ArrayList<String> msisdns = new ArrayList<>(Arrays.asList("12345", "67890"));

        ArrayList<String> result = service.processMsisdns(msisdns, 3);
        assertEquals(msisdns.size(), result.size());
        assertTrue(msisdns.containsAll(result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessMsisdnsEmptyList() {
        MSISDNService service = new MSISDNService();
        ArrayList<String> msisdns = new ArrayList<>();

        service.processMsisdns(msisdns, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessMsisdnsNullList() {
        MSISDNService service = new MSISDNService();

        service.processMsisdns(null, 3);
    }

}