package com.tech.labs.DateTimeProvider;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public interface Clock {
    LocalDateTime currentTime();
    void addAction(Consumer<LocalDateTime> action);
}
