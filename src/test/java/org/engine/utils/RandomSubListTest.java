package org.engine.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

//import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class RandomSubListTest {

    @Test
    public void testGetRandomSubListValidInput() {
        ArrayList<String> mainList = new ArrayList<>(Arrays.asList("apple", "banana", "orange", "kiwi", "grape"));
        Integer num = 3;

        ArrayList<String> result = MethodsUtils.getRandomSubList(mainList, num);

        // Test that the size of the result matches the requested number
        assertEquals("The size of the returned list should match the requested size.",num.intValue(), result.size());
        //assertEquals("The size of the returned list should match the requested size.",4, result.size());

        // Test that all elements in the result are unique
        assertEquals("The returned list should contain only unique elements.", new HashSet<>(result).size(), result.size());

        // Test that all elements in the result are from the main list
        assertTrue("All elements in the result should be from the main list.", mainList.containsAll(result));
    }

    @Test
    public void testGetRandomSubListWithDuplicatesInInput() {
        ArrayList<String> mainList = new ArrayList<>(Arrays.asList("apple", "banana", "apple", "orange", "banana", "kiwi"));
        int num = 2;

        ArrayList<String> result = MethodsUtils.getRandomSubList(mainList, num);

        // Test that the size of the result matches the requested number
        assertEquals("The size of the returned list should match the requested size.", num, result.size());

        // Test that all elements in the result are unique
        assertEquals("The returned list should contain only unique elements.", new HashSet<>(result).size(), result.size());

        // Test that all elements in the result are from the main list
        assertTrue("All elements in the result should be from the main list.", mainList.containsAll(result));
        System.out.println("Result List: "+result);
    }

    @Test
    public void testGetRandomSubListExceedingUniqueElements() {
        ArrayList<String> mainList = new ArrayList<>(Arrays.asList("apple", "banana", "apple", "orange"));
        int num = 5;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> MethodsUtils.getRandomSubList(mainList, num));
        assertEquals("The size of the sublist cannot exceed the size of the main list.", exception.getMessage());
    }

    @Test
    public void testGetRandomSubListNegativeNum() {
        ArrayList<String> mainList = new ArrayList<>(Arrays.asList("apple", "banana", "orange"));
        int num = -1;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> MethodsUtils.getRandomSubList(mainList, num));
        assertEquals("The size of the sublist cannot be negative.", exception.getMessage());
    }

    @Test
    public void testGetRandomSubListZeroNum() {
        ArrayList<String> mainList = new ArrayList<>(Arrays.asList("apple", "banana", "orange"));
        int num = 0;

        ArrayList<String> result = MethodsUtils.getRandomSubList(mainList, num);

        // Test that the result is an empty list
        assertTrue("The returned list should be empty when num is 0.",result.isEmpty());
    }
}
