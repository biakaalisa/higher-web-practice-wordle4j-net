package ru.yandex.practicum.server;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.model.PlayerResult;
import ru.yandex.practicum.model.PlayerStatistic;
import ru.yandex.practicum.util.GameLogger;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class StatisticsStorageTest {

    @Test
    void shouldAddResultAndBuildTop() throws Exception {
        StatisticsStorage storage = createStorage();

        storage.addResult(new PlayerResult("Ivan", 4, false));
        storage.addResult(new PlayerResult("Ivan", 3, true));
        storage.addResult(new PlayerResult("Anna", 2, false));

        List<PlayerStatistic> top = storage.getTop();

        assertEquals(2, top.size());
        assertEquals("Ivan", top.get(0).getNickname());
        assertEquals(2, top.get(0).getWins());
        assertEquals(3, top.get(0).getBestAttempts());
        assertEquals("Anna", top.get(1).getNickname());
    }

    @Test
    void shouldLimitTopToTenPlayers() throws Exception {
        StatisticsStorage storage = createStorage();

        for (int i = 0; i < 12; i++) {
            storage.addResult(new PlayerResult("player" + i, 6, false));
        }

        assertEquals(10, storage.getTop().size());
    }

    @Test
    void shouldReturnCopyOfResults() throws Exception {
        StatisticsStorage storage = createStorage();
        storage.addResult(new PlayerResult("Ivan", 4, false));

        List<PlayerResult> results = storage.getResults();
        results.clear();

        assertFalse(storage.getResults().isEmpty());
    }

    private StatisticsStorage createStorage() throws Exception {
        Path file = Files.createTempFile("wordle-statistics", ".json");
        return new StatisticsStorage(new WordleServerStatisticLoader(file, testLogger()));
    }

    private GameLogger testLogger() {
        return new GameLogger(new PrintWriter(System.out, true));
    }
}
