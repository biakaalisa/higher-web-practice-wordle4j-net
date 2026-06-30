package ru.yandex.practicum.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.dictionary.WordleDictionary;
import ru.yandex.practicum.exception.EmptyInputException;
import ru.yandex.practicum.exception.InvalidAlphabetException;
import ru.yandex.practicum.exception.InvalidLengthException;
import ru.yandex.practicum.exception.WordNotFoundInDictionaryException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InputValidatorTest {

    private InputValidator validator;

    @BeforeEach
    void setUp() {
        WordleDictionary dictionary = new WordleDictionary(List.of("елка", "герой"));
        validator = new InputValidator(dictionary);
    }

    @Test
    void shouldNormalizeValidInput() throws Exception {
        String result = validator.validateGuess(" ГЕРОЙ ");

        assertEquals("герой", result);
    }

    @Test
    void shouldThrowEmptyInputException() {
        assertThrows(EmptyInputException.class, () -> validator.validateGuess("   "));
    }

    @Test
    void shouldThrowInvalidLengthException() {
        assertThrows(InvalidLengthException.class, () -> validator.validateGuess("кот"));
    }

    @Test
    void shouldThrowInvalidAlphabetException() {
        assertThrows(InvalidAlphabetException.class, () -> validator.validateGuess("apple"));
    }

    @Test
    void shouldThrowWordNotFoundException() {
        assertThrows(WordNotFoundInDictionaryException.class, () -> validator.validateGuess("маска"));
    }
}
