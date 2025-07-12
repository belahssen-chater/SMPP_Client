package org.engine.utils;

import org.junit.Test;

import static org.junit.Assert.*;
public class FileUtilsTest {

    @Test
    public void testFileExistValidInput() {
        String path = getClass().getResource("/testFile.txt").getPath();
        boolean exists=FileUtils.doesFileExist(path);
        assertTrue(exists);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileExistNullInput(){
        boolean exists=FileUtils.doesFileExist(null);
        assertFalse(exists);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileExistEmptyInput(){
        boolean exists=FileUtils.doesFileExist("");
        assertFalse(exists);
    }

}