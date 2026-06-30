package ru.yandex.practicum.util;

import java.util.Locale;

public final class WordNormalizer {

    private WordNormalizer() {
    }

    public static String normalize(String word) {
        if (word == null) {
            return "";
        }

        return word.trim()
                .toLowerCase(Locale.ROOT)
                .replace('ё', 'е');
    }

    public static boolean hasOnlyRussianLetters(String word) {
        if (word == null || word.isBlank()) {
            return false;
        }

        for (int i = 0; i < word.length(); i++) {
            char symbol = word.charAt(i);
            if (symbol < 'а' || symbol > 'я') {
                return false;
            }
        }

        return true;
    }
}
