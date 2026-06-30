package ru.yandex.practicum.util;

import ru.yandex.practicum.exception.SystemWordleException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class GameLogger implements AutoCloseable {

    private final PrintWriter writer;

    public GameLogger(PrintWriter writer) {
        this.writer = writer;
    }

    public static PrintWriter createWriter(Path path) throws SystemWordleException {
        try {
            return new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8));
        } catch (IOException exception) {
            throw new SystemWordleException("Не удалось создать лог-файл", exception);
        }
    }

    public void info(String message) {
        write("INFO", message, null);
    }

    public void warn(String message) {
        write("WARN", message, null);
    }

    public void error(String message, Throwable throwable) {
        write("ERROR", message, throwable);
    }

    @Override
    public void close() {
        writer.close();
    }

    private void write(String level, String message, Throwable throwable) {
        writer.println(LocalDateTime.now() + " " + level + " " + message);
        if (throwable != null) {
            throwable.printStackTrace(writer);
        }
        writer.flush();
    }
}
