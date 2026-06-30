package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.exception.ServerException;
import ru.yandex.practicum.model.PlayerResult;
import ru.yandex.practicum.util.GameLogger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class WordleServer {

    public static final int DEFAULT_PORT = 8080;

    private static final String SERVER_LOG_FILE = "wordle-server.log";
    private static final String STATISTICS_FILE = "wordle-statistics.json";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String JSON_CONTENT_TYPE = "application/json;charset=utf-8";
    private static final String POST_METHOD = "POST";
    private static final String GET_METHOD = "GET";
    private static final String RESULTS_PATH = "/results";
    private static final String TOP_PATH = "/top";
    private static final int MIN_ATTEMPTS = 1;
    private static final int MAX_ATTEMPTS = 6;

    private final GameLogger logger;
    private final StatisticsStorage statisticsStorage;
    private final Gson gson;
    private final int port;
    private HttpServer server;

    public WordleServer(GameLogger logger, StatisticsStorage statisticsStorage) {
        this(logger, statisticsStorage, DEFAULT_PORT);
    }

    public WordleServer(GameLogger logger, StatisticsStorage statisticsStorage, int port) {
        this.logger = logger;
        this.statisticsStorage = statisticsStorage;
        this.gson = new Gson();
        this.port = port;
    }

    public void start() throws ServerException {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext(RESULTS_PATH, new ResultsHandler());
            server.createContext(TOP_PATH, new TopHandler());
            server.start();
            logger.info("Сервер запущен на порту " + port);
        } catch (IOException exception) {
            logger.error("Ошибка запуска сервера", exception);
            throw new ServerException("Не удалось запустить сервер", exception);
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            logger.info("Сервер остановлен");
        }
    }

    public static void main(String[] args) {
        try {
            PrintWriter logWriter = GameLogger.createWriter(Path.of(SERVER_LOG_FILE));
            GameLogger logger = new GameLogger(logWriter);
            WordleServerStatisticLoader loader = new WordleServerStatisticLoader(
                    Path.of(STATISTICS_FILE),
                    logger
            );
            StatisticsStorage storage = new StatisticsStorage(loader);
            WordleServer wordleServer = new WordleServer(logger, storage);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                wordleServer.stop();
                logger.close();
            }));
            wordleServer.start();
        } catch (Exception exception) {
            writeEmergencyLog(exception);
        }
    }

    private void sendResponse(HttpExchange exchange, HttpStatusCode statusCode, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add(CONTENT_TYPE, JSON_CONTENT_TYPE);
        exchange.sendResponseHeaders(statusCode.getCode(), response.length);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }

    private void sendServerError(HttpExchange exchange, Exception exception) throws IOException {
        logger.error("Ошибка обработки запроса", exception);
        sendResponse(exchange, HttpStatusCode.INTERNAL_SERVER_ERROR, "{\"error\":\"server error\"}");
    }

    private boolean isValidResult(PlayerResult result) {
        return result != null
                && result.getNickname() != null
                && !result.getNickname().isBlank()
                && result.getAttempts() >= MIN_ATTEMPTS
                && result.getAttempts() <= MAX_ATTEMPTS;
    }

    private static void writeEmergencyLog(Exception exception) {
        try (PrintWriter logWriter = GameLogger.createWriter(Path.of(SERVER_LOG_FILE))) {
            new GameLogger(logWriter).error("Критическая ошибка сервера", exception);
        } catch (Exception ignored) {
            System.err.println("Не удалось записать ошибку сервера в лог-файл");
        }
    }

    private class ResultsHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!POST_METHOD.equals(exchange.getRequestMethod())) {
                    sendResponse(exchange, HttpStatusCode.METHOD_NOT_ALLOWED, "{\"error\":\"method not allowed\"}");
                    return;
                }

                try (InputStreamReader reader = new InputStreamReader(
                        exchange.getRequestBody(),
                        StandardCharsets.UTF_8
                )) {
                    PlayerResult result = gson.fromJson(reader, PlayerResult.class);
                    if (!isValidResult(result)) {
                        sendResponse(exchange, HttpStatusCode.BAD_REQUEST, "{\"error\":\"invalid result\"}");
                        return;
                    }
                    statisticsStorage.addResult(result);
                    sendResponse(exchange, HttpStatusCode.CREATED, gson.toJson(result));
                } catch (JsonSyntaxException exception) {
                    logger.warn("Получен некорректный JSON результата");
                    sendResponse(exchange, HttpStatusCode.BAD_REQUEST, "{\"error\":\"invalid json\"}");
                }
            } catch (Exception exception) {
                sendServerError(exchange, exception);
            }
        }
    }

    private class TopHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!GET_METHOD.equals(exchange.getRequestMethod())) {
                    sendResponse(exchange, HttpStatusCode.METHOD_NOT_ALLOWED, "{\"error\":\"method not allowed\"}");
                    return;
                }

                sendResponse(exchange, HttpStatusCode.OK, gson.toJson(statisticsStorage.getTop()));
            } catch (Exception exception) {
                sendServerError(exchange, exception);
            }
        }
    }
}
