package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.exception.ServerException;
import ru.yandex.practicum.model.PlayerResult;
import ru.yandex.practicum.util.GameLogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WordleServerStatisticLoader {

    private static final Gson GSON = new Gson();

    private final Path statisticFile;
    private final GameLogger logger;

    public WordleServerStatisticLoader(Path statisticFile, GameLogger logger) {
        this.statisticFile = statisticFile;
        this.logger = logger;
    }

    public List<PlayerResult> load() throws ServerException {
        if (!Files.exists(statisticFile)) {
            logger.info("Файл статистики не найден. Будет создан новый список: " + statisticFile);
            return new ArrayList<>();
        }

        try (BufferedReader reader = Files.newBufferedReader(statisticFile, StandardCharsets.UTF_8)) {
            Type resultListType = new TypeToken<List<PlayerResult>>() {
            }.getType();
            List<PlayerResult> results = GSON.fromJson(reader, resultListType);
            if (results == null) {
                logger.warn("Файл статистики пуст: " + statisticFile);
                return new ArrayList<>();
            }
            logger.info("Статистика загружена. Записей: " + results.size());
            return results;
        } catch (IOException exception) {
            logger.error("Ошибка чтения статистики: " + statisticFile, exception);
            throw new ServerException("Не удалось загрузить статистику", exception);
        } catch (JsonSyntaxException exception) {
            logger.error("Некорректный формат файла статистики: " + statisticFile, exception);
            throw new ServerException("Не удалось прочитать статистику", exception);
        }
    }

    public void save(List<PlayerResult> results) throws ServerException {
        try (BufferedWriter writer = Files.newBufferedWriter(statisticFile, StandardCharsets.UTF_8)) {
            GSON.toJson(results, writer);
            logger.info("Статистика сохранена. Записей: " + results.size());
        } catch (IOException exception) {
            logger.error("Ошибка сохранения статистики: " + statisticFile, exception);
            throw new ServerException("Не удалось сохранить статистику", exception);
        }
    }
}
