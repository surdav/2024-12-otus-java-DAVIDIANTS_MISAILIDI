package ru.otus.aop.proxy;

/**
 * Interface defining calculation methods.
 */
public interface TestLoggingInterface {

    void calculation(int param);
    void calculation(int param1, int param2);
    void calculation(int param1, int param2, String param3);

}
