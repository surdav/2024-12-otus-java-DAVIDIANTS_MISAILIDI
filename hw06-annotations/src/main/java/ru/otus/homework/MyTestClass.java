package ru.otus.homework;

import ru.otus.homework.annotations.After;
import ru.otus.homework.annotations.Before;
import ru.otus.homework.annotations.Test;

public class MyTestClass {

    @Before
    public void setUp() {
        System.out.println("Before: Setting up the test");
    }

    @Test
    public void test1() {
        System.out.println("Test1: Running test 1");
    }

    @Test
    public void test2() {
        System.out.println("Test2: Running test 2");
        throw new RuntimeException("Test2: Exception in test 2");
    }

    @After
    public void tearDown() {
        System.out.println("After: Cleaning up after the test");
    }
}
