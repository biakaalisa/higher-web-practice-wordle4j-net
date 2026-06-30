package ru.yandex.practicum.exception;

public class EmptyInputException extends InvalidWordException {

    public EmptyInputException() {
        super("Пустой ввод не является словом");
    }
}
