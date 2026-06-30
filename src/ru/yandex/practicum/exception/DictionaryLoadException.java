package ru.yandex.practicum.exception;

public class DictionaryLoadException extends SystemWordleException {

    public DictionaryLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DictionaryLoadException(String message) {
        super(message);
    }
}
