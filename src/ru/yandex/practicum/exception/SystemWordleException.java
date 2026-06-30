package ru.yandex.practicum.exception;

public class SystemWordleException extends WordleException {

    public SystemWordleException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemWordleException(String message) {
        super(message);
    }
}
