package ru.yandex.practicum.dictionary;

import ru.yandex.practicum.exception.DictionaryLoadException;
import ru.yandex.practicum.util.GameLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {

    private final GameLogger logger;

    public WordleDictionaryLoader(GameLogger logger) {
        this.logger = logger;
    }

    public WordleDictionary load(Path path) throws DictionaryLoadException {
        logger.info("Начало загрузки словаря: " + path);
        List<String> words = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    words.add(line);
                }
            }
        } catch (IOException exception) {
            logger.error("Ошибка чтения словаря: " + path, exception);
            throw new DictionaryLoadException("Не удалось загрузить словарь", exception);
        }

        WordleDictionary dictionary = new WordleDictionary(words);
        if (dictionary.isEmpty()) {
            DictionaryLoadException exception = new DictionaryLoadException("В словаре нет подходящих слов");
            logger.error("Словарь загружен, но подходящих слов нет: " + path, exception);
            throw exception;
        }

        logger.info("Словарь успешно загружен. Подходящих слов: " + dictionary.size());
        return dictionary;
    }
}
