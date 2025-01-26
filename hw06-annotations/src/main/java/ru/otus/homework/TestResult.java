package ru.otus.homework;

public record TestResult(int passed, int failed, int total) {

    @Override
    public String toString() {
        return String.format("Passed: %d%nFailed: %d%nTotal: %d", passed, failed, total);
    }
}
