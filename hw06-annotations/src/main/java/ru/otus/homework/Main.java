package ru.otus.homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    // Logger for logging errors and information
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) {
        // Run the tests and get the results
        var result = TestRunner.runTests(MyTestClass.class);

        // Print the results to the console
        logger.info("--- Test Summary ---");
        logger.info(result.toString());
    }
}
