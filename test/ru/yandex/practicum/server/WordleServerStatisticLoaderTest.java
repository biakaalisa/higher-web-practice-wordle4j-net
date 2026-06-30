package ru.yandex.practicum.server;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.ServerException;
import ru.yandex.practicum.model.PlayerResult;
import ru.yandex.practicum.util.GameLogger;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordleServerStatisticLoaderTest {

    @Test
    void shouldReturnEmptyListWhenFileDoesNotExist() throws Exception {
        Path file = Files.createTempDirectory("wordle-stats").resolve("missing.json");
        WordleServerStatisticLoader loader = new WordleServerStatisticLoader(file, testLogger());

        assertTrue(loader.load().isEmpty());
    }

    @Test
    void shouldSaveAndLoadResults() throws Exception {
        Path file = Files.createTempFile("wordle-stats", ".json");
        WordleServerStatisticLoader loader = new WordleServerStatisticLoader(file, testLogger());
        List<PlayerResult> results = List.of(new PlayerResult("Ivan", 4, true));

        loader.save(results);
        List<PlayerResult> loaded = loader.load();

        assertEquals(1, loaded.size());
        assertEquals("Ivan", loaded.get(0).getNickname());
        assertEquals(4, loaded.get(0).getAttempts());
    }

    @Test
    void shouldThrowExceptionForInvalidJson() throws Exception {
        Path file = Files.createTempFile("wordle-broken-stats", ".json");
        Files.writeString(file, "not json", StandardCharsets.UTF_8);
        WordleServerStatisticLoader loader = new WordleServerStatisticLoader(file, testLogger());

        assertThrows(ServerException.class, loader::load);
    }

    private GameLogger testLogger() {
        return new GameLogger(new PrintWriter(System.out, true));
    }
}
