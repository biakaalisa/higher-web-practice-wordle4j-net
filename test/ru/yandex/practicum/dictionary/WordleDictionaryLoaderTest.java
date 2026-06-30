package ru.yandex.practicum.dictionary;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.DictionaryLoadException;
import ru.yandex.practicum.util.GameLogger;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordleDictionaryLoaderTest {

    @Test
    void shouldLoadDictionaryFromFile() throws Exception {
        Path dictionaryFile = Files.createTempFile("wordle-dictionary", ".txt");
        Files.writeString(dictionaryFile, "ГЕРОЙ\nкот\nЁЖИКИ\n", StandardCharsets.UTF_8);
        WordleDictionaryLoader loader = new WordleDictionaryLoader(testLogger());

        WordleDictionary dictionary = loader.load(dictionaryFile);

        assertEquals(2, dictionary.size());
        assertTrue(dictionary.contains("герой"));
        assertTrue(dictionary.contains("ежики"));
    }

    @Test
    void shouldThrowExceptionForMissingFile() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(testLogger());

        assertThrows(DictionaryLoadException.class, () -> loader.load(Path.of("missing-dictionary-file.txt")));
    }

    @Test
    void shouldThrowExceptionForDictionaryWithoutValidWords() throws Exception {
        Path dictionaryFile = Files.createTempFile("wordle-empty-dictionary", ".txt");
        Files.writeString(dictionaryFile, "кот\napple\n", StandardCharsets.UTF_8);
        WordleDictionaryLoader loader = new WordleDictionaryLoader(testLogger());

        assertThrows(DictionaryLoadException.class, () -> loader.load(dictionaryFile));
    }

    private GameLogger testLogger() {
        return new GameLogger(new PrintWriter(System.out, true));
    }
}
