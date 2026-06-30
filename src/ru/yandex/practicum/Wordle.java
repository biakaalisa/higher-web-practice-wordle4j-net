package ru.yandex.practicum;

import ru.yandex.practicum.client.WordleClient;
import ru.yandex.practicum.dictionary.WordleDictionary;
import ru.yandex.practicum.dictionary.WordleDictionaryLoader;
import ru.yandex.practicum.exception.GameException;
import ru.yandex.practicum.exception.SystemWordleException;
import ru.yandex.practicum.game.GuessResult;
import ru.yandex.practicum.game.WordleGame;
import ru.yandex.practicum.model.PlayerResult;
import ru.yandex.practicum.model.PlayerStatistic;
import ru.yandex.practicum.util.GameLogger;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Wordle {

    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "wordle.log";

    public static void main(String[] args) {
        try (PrintWriter logWriter = GameLogger.createWriter(Path.of(LOG_FILE));
             Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            GameLogger logger = new GameLogger(logWriter);
            WordleDictionaryLoader loader = new WordleDictionaryLoader(logger);
            WordleDictionary dictionary = loader.load(Path.of(DICTIONARY_FILE));
            WordleGame game = new WordleGame(dictionary);
            WordleClient client = new WordleClient(logger);

            logger.info("Игра запущена");
            runGame(scanner, game, client, logger);
            logger.info("Игра завершена");
        } catch (SystemWordleException exception) {
            writeEmergencyLog(exception);
        } catch (Exception exception) {
            writeEmergencyLog(exception);
        }
    }

    private static void runGame(Scanner scanner, WordleGame game, WordleClient client, GameLogger logger) {
        System.out.println("Введите слово из пяти букв. Пустая строка покажет подсказку.");

        while (!game.isFinished()) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input.isBlank()) {
                String hint = game.getHint();
                logger.info("Игрок запросил подсказку");
                System.out.println(hint);
                continue;
            }

            try {
                GuessResult result = game.makeGuess(input);
                logger.info("Засчитан ход. Осталось попыток: " + result.getAttemptsLeft());
                System.out.println(result.getPattern());
                System.out.println("Осталось попыток: " + result.getAttemptsLeft());
            } catch (GameException exception) {
                logger.warn("Некорректный ход: " + exception.getMessage());
                System.out.println(exception.getMessage());
            }
        }

        if (game.isWon()) {
            System.out.println("Вы победили. Слово: " + game.getAnswer());
            sendResultAndPrintTop(scanner, game, client, logger);
        } else {
            System.out.println("Попытки закончились. Слово: " + game.getAnswer());
        }
    }

    private static void sendResultAndPrintTop(
            Scanner scanner,
            WordleGame game,
            WordleClient client,
            GameLogger logger
    ) {
        System.out.print("Введите никнейм для рейтинга: ");
        String nickname = scanner.nextLine().trim();

        if (nickname.isBlank()) {
            logger.warn("Игрок не ввёл никнейм для статистики");
            System.out.println("Никнейм не указан. Результат не отправлен.");
            return;
        }

        int attemptsUsed = WordleGame.MAX_ATTEMPTS - game.getAttemptsLeft();
        PlayerResult result = new PlayerResult(nickname, attemptsUsed, game.isHintsUsed());
        boolean sent = client.sendResult(result);

        if (!sent) {
            System.out.println("Не удалось отправить результат на сервер.");
            return;
        }

        System.out.println("Результат отправлен.");
        printTop(client.getTop());
    }

    private static void printTop(List<PlayerStatistic> top) {
        if (top.isEmpty()) {
            System.out.println("Рейтинг пока пуст.");
            return;
        }

        System.out.println("Топ игроков:");
        for (int i = 0; i < top.size(); i++) {
            System.out.println((i + 1) + ". " + top.get(i));
        }
    }

    private static void writeEmergencyLog(Exception exception) {
        try (PrintWriter logWriter = GameLogger.createWriter(Path.of(LOG_FILE))) {
            GameLogger logger = new GameLogger(logWriter);
            logger.error("Критическая ошибка", exception);
        } catch (Exception ignored) {
            System.err.println("Не удалось записать критическую ошибку в лог-файл");
        }
    }
}
