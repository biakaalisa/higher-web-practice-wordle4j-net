package ru.yandex.practicum.exception;

public class InvalidAlphabetException extends InvalidWordException {

    public InvalidAlphabetException() {
        super("Слово должно состоять только из русских букв");
    }
}
