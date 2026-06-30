package ru.yandex.practicum.exception;

public class ServerException extends SystemWordleException {

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
