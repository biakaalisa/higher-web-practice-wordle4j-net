package ru.yandex.practicum.exception;

public class InvalidLengthException extends InvalidWordException {

    public InvalidLengthException(int expectedLength) {
        super("Слово должно состоять из " + expectedLength + " букв");
    }
}
