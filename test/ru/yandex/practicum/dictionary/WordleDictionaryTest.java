package ru.yandex.practicum.dictionary;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordleDictionaryTest {

    @Test
    void shouldNormalizeWordsAndRemoveDuplicates() {
        WordleDictionary dictionary = new WordleDictionary(List.of("ГЕРОЙ", "герой", "ЁЖИКИ", "кот"));

        assertEquals(2, dictionary.size());
        assertTrue(dictionary.contains("герой"));
        assertTrue(dictionary.contains("ЕЖИКИ"));
        assertFalse(dictionary.contains("кот"));
    }

    @Test
    void shouldReturnUnmodifiableWords() {
        WordleDictionary dictionary = new WordleDictionary(List.of("герой"));

        assertThrows(UnsupportedOperationException.class, () -> dictionary.getWords().add("маска"));
    }

    @Test
    void shouldThrowExceptionForRandomWordFromEmptyDictionary() {
        WordleDictionary dictionary = new WordleDictionary(List.of("кот"));

        assertThrows(IllegalStateException.class, dictionary::getRandomWord);
    }

    @Test
    void shouldValidateDictionaryWord() {
        assertTrue(WordleDictionary.isValidDictionaryWord("герой"));
        assertFalse(WordleDictionary.isValidDictionaryWord("кот"));
        assertFalse(WordleDictionary.isValidDictionaryWord("apple"));
        assertFalse(WordleDictionary.isValidDictionaryWord(null));
    }
}
