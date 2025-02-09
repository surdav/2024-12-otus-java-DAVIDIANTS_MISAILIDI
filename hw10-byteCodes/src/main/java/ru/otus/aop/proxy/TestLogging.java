package ru.otus.aop.proxy;

/**
 * Implementation of TestLoggingInterface with methods annotated for logging.
 */
public class TestLogging implements TestLoggingInterface {

    @Override
    @Log
    public void calculation(int param) {
        // No logging here, proxy will handle it
    }

    @Override
    @Log
    public void calculation(int param1, int param2) {
        // No logging here, proxy will handle it
    }

    @Override
    @Log
    public void calculation(int param1, int param2, String param3) {
        // No logging here, proxy will handle it
    }
}
