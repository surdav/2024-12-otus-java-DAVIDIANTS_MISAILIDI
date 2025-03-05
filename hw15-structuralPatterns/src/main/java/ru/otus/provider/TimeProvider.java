package ru.otus.provider;

import java.time.LocalDateTime;

public interface TimeProvider {

    LocalDateTime getCurrentTime();
}
