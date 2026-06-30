package ru.yandex.practicum.util;

import ru.yandex.practicum.dictionary.WordleDictionary;
import ru.yandex.practicum.exception.EmptyInputException;
import ru.yandex.practicum.exception.InvalidAlphabetException;
import ru.yandex.practicum.exception.InvalidLengthException;
import ru.yandex.practicum.exception.WordNotFoundInDictionaryException;

public class InputValidator {

    private final WordleDictionary dictionary;

    public InputValidator(WordleDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public String validateGuess(String input)
            throws EmptyInputException,
            InvalidLengthException,
            InvalidAlphabetException,
            WordNotFoundInDictionaryException {
        String normalized = WordNormalizer.normalize(input);

        if (normalized.isEmpty() || normalized.isBlank()) {
            throw new EmptyInputException();
        }

        if (normalized.length() != WordleDictionary.WORD_LENGTH) {
            throw new InvalidLengthException(WordleDictionary.WORD_LENGTH);
        }

        if (!WordNormalizer.hasOnlyRussianLetters(normalized)) {
            throw new InvalidAlphabetException();
        }

        if (!dictionary.contains(normalized)) {
            throw new WordNotFoundInDictionaryException(normalized);
        }

        return normalized;
    }
}
