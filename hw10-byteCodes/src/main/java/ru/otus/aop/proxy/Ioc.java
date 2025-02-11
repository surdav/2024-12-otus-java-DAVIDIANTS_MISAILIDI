package ru.otus.aop.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for creating proxy instances with automatic logging.
 * This class is package-private as it is intended for internal use only.
 */
class Ioc {

    private static final Logger logger = LoggerFactory.getLogger(Ioc.class);

    // Private constructor to prevent instantiation.
    private Ioc() {}

    /**
     * Creates a proxy instance for the given target object implementing the specified interface.
     *
     * @param target         the original target object
     * @param interfaceClass the interface that the proxy should implement
     * @param <T>            the type of the interface
     * @return a proxy instance that logs method parameters for methods annotated with @Log
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target, Class<T> interfaceClass) {

        InvocationHandler handler = new LoggingInvocationHandler(target);

        // Create proxy instance with the provided interface.
        return (T) Proxy.newProxyInstance(
                Ioc.class.getClassLoader(),
                new Class<?>[]{interfaceClass},
                handler
        );
    }

    public static class LoggingInvocationHandler implements InvocationHandler {

        // Indicates the original target object whose methods will be invoked through the proxy.
        private final Object target;

        // Cache to store whether a method is annotated with @Log.
        // ConcurrentHashMap is used to ensure thread-safe caching of reflection results in a concurrent environment without explicit synchronization, thereby improving performance.
        private final Map<Method, Boolean> methodAnnotationCache = new ConcurrentHashMap<>();

        // Stores the current target object in the target field.
        LoggingInvocationHandler(Object target) {
            this.target = target;
        }

        // Intercepts method calls on the proxy and adds logging. This method invokes the original method via reflection.
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            // Determine if the corresponding target method is annotated with @Log.
            // Since the method comes from the interface, we retrieve its implementation.
            // computeIfAbsent checks if the key (method) exists in methodAnnotationCache.
            // If it exists, it returns the cached value.
            boolean shouldLog = methodAnnotationCache.computeIfAbsent(method, m -> {
                try {
                    Method targetMethod = target.getClass().getMethod(m.getName(), m.getParameterTypes());

                    // If not, it computes the value (method.isAnnotationPresent(Log.class)) and stores it in the cache.
                    return targetMethod.isAnnotationPresent(Log.class);

                } catch (NoSuchMethodException e) {
                    return false;
                }
                    });

            // If the method is annotated with @Log, it generates a string containing information about the method and its parameters.
            if (shouldLog) {
                StringBuilder logMessage = new StringBuilder("executed method: ")
                        .append(method.getName())
                        .append(", params: ");

                if (args != null && args.length > 0) {
                    for (Object arg : args) {
                        logMessage.append(arg).append(", ");
                    }

                    // Remove the last comma and space.
                    logMessage.setLength(logMessage.length() - 2);
                } else {
                    logMessage.append("none");
                }

                // If INFO logging is disabled, we avoid unnecessary string building and method calls.
                if (logger.isInfoEnabled()) {
                    logger.info(logMessage.toString());
                }
            }

            // Invokes the original method on the target object with the provided arguments args.
            return method.invoke(target, args);
        }
    }

}