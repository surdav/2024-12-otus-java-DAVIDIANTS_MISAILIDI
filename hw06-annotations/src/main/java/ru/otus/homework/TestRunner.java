package ru.otus.homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.otus.homework.annotations.After;
import ru.otus.homework.annotations.Before;
import ru.otus.homework.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {

    // Logger for logging errors and information
    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    // Private constructor to prevent instantiation
    private TestRunner() {
        // Prevent instantiation
    }

    public static void runTests(Class<?> testClass) {
        try {
            // Lists to store methods annotated with @Before, @Test, and @After
            List<Method> beforeMethods = new ArrayList<>();
            List<Method> testMethods = new ArrayList<>();
            List<Method> afterMethods = new ArrayList<>();

            // Analyze methods in the class to find annotations
            for (Method method : testClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Before.class)) {
                    beforeMethods.add(method);
                } else if (method.isAnnotationPresent(Test.class)) {
                    testMethods.add(method);
                } else if (method.isAnnotationPresent(After.class)) {
                    afterMethods.add(method);
                }
            }

            // Variables to track test results
            int passed = 0;
            int failed = 0;

            // Execute each @Test method
            for (Method testMethod : testMethods) {

                Object testInstance = testClass.getDeclaredConstructor().newInstance();

                if(executeTests(testInstance, beforeMethods, testMethod, afterMethods)) {
                    passed++;
                } else {
                    failed++;
                }
            }

            // Output the summary of test results
            logger.info("\n--- Test Summary ---");
            logger.info("Passed: {}", passed);
            logger.info("Failed: {}", failed);
            logger.info("Total: {}", testMethods.size());

        } catch (Exception e) {
            // Log error details if the test runner encounters an exception
            logger.error("Error while running tests: ", e);
        }
    }

    /**
     * Executes a single test with its associated @Before and @After methods.
     *
     * @param testInstance  the instance of the test class
     * @param beforeMethods the list of @Before methods
     * @param testMethod    the @Test method to execute
     * @param afterMethods  the list of @After methods
     * @return true if the test passed, false otherwise
     */
    private static boolean executeTests(Object testInstance, List<Method> beforeMethods, Method testMethod, List<Method> afterMethods) {
        try {
            // Execute all @Before methods
            for (Method beforeMethod : beforeMethods) {
                beforeMethod.invoke(testInstance);
            }

            // Execute the @Test method
            testMethod.invoke(testInstance);

            // Log success message
            logger.info("Test '{}' passed successfully.", testMethod.getName());
            return true;

        } catch (Exception e) {
            // Log failure message with exception details
            logger.error("Test '{}' failed with exception: ", testMethod.getName(), e.getCause());
            return false;

        } finally {
            // Execute all @After methods
            for (Method afterMethod : afterMethods) {
                try {
                    afterMethod.invoke(testInstance);
                } catch (Exception e) {
                    logger.error("Error in @After method '{}': ", afterMethod.getName(), e.getCause());
                }
            }
        }
    }
}
