package ru.yandex.practicum.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.model.PlayerResult;
import ru.yandex.practicum.model.PlayerStatistic;
import ru.yandex.practicum.util.GameLogger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WordleClient {

    private static final String DEFAULT_SERVER_URL = "http://localhost:8080";
    private static final String JSON_CONTENT_TYPE = "application/json;charset=utf-8";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String RESULTS_PATH = "/results";
    private static final String TOP_PATH = "/top";
    private static final int SUCCESS_MIN_CODE = 200;
    private static final int SUCCESS_MAX_CODE = 299;

    private final HttpClient httpClient;
    private final Gson gson;
    private final String serverUrl;
    private final GameLogger logger;

    public WordleClient(GameLogger logger) {
        this(DEFAULT_SERVER_URL, logger);
    }

    public WordleClient(String serverUrl, GameLogger logger) {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.serverUrl = serverUrl;
        this.logger = logger;
    }

    public boolean sendResult(PlayerResult result) {
        String json = gson.toJson(result);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + RESULTS_PATH))
                .header(CONTENT_TYPE, JSON_CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            boolean success = isSuccess(response.statusCode());
            if (!success) {
                logger.warn("Сервер вернул код " + response.statusCode());
            }
            return success;
        } catch (IOException exception) {
            logger.error("Не удалось отправить результат на сервер", exception);
            return false;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            logger.error("Отправка результата прервана", exception);
            return false;
        }
    }

    public List<PlayerStatistic> getTop() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + TOP_PATH))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (!isSuccess(response.statusCode())) {
                logger.warn("Сервер вернул код " + response.statusCode());
                return new ArrayList<>();
            }

            Type type = new TypeToken<List<PlayerStatistic>>() {
            }.getType();
            List<PlayerStatistic> top = gson.fromJson(response.body(), type);
            if (top == null) {
                return new ArrayList<>();
            }
            return top;
        } catch (IOException exception) {
            logger.error("Не удалось получить рейтинг с сервера", exception);
            return new ArrayList<>();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            logger.error("Получение рейтинга прервано", exception);
            return new ArrayList<>();
        }
    }

    private boolean isSuccess(int statusCode) {
        return statusCode >= SUCCESS_MIN_CODE && statusCode <= SUCCESS_MAX_CODE;
    }
}
