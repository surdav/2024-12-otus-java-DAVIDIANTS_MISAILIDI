package ru.otus.aop.proxy;

/**
 * Demonstrates the usage of the dynamic proxy with automatic logging.
 */
public class ProxyDemo {

    public static void main(String[] args) {

        TestLoggingInterface testLogging = Ioc.createProxy(new TestLogging(), TestLoggingInterface.class);

        testLogging.calculation(6);
        testLogging.calculation(7, 15);
        testLogging.calculation(3, 5, "Hello");
    }

}
