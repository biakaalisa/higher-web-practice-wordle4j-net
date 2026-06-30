package ru.yandex.practicum.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordNormalizerTest {

    @Test
    void shouldNormalizeWord() {
        assertEquals("елка", WordNormalizer.normalize(" ЁЛКА "));
    }

    @Test
    void shouldReturnEmptyStringForNull() {
        assertEquals("", WordNormalizer.normalize(null));
    }

    @Test
    void shouldReturnTrueForRussianLetters() {
        assertTrue(WordNormalizer.hasOnlyRussianLetters("герой"));
    }

    @Test
    void shouldReturnFalseForForeignLetters() {
        assertFalse(WordNormalizer.hasOnlyRussianLetters("apple"));
    }
}
