package ru.yandex.practicum.exception;

public class WordNotFoundInDictionaryException extends GameException {

    public WordNotFoundInDictionaryException(String word) {
        super("Слова нет в словаре: " + word);
    }
}
