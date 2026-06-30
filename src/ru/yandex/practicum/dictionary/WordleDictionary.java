package ru.yandex.practicum.dictionary;

import ru.yandex.practicum.util.WordNormalizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WordleDictionary {

    public static final int WORD_LENGTH = 5;

    private final List<String> words;
    private final Set<String> wordSet;
    private final Random random;

    public WordleDictionary(List<String> words) {
        LinkedHashSet<String> normalizedWords = new LinkedHashSet<>();

        for (String word : words) {
            String normalized = WordNormalizer.normalize(word);
            if (isValidDictionaryWord(normalized)) {
                normalizedWords.add(normalized);
            }
        }

        this.words = new ArrayList<>(normalizedWords);
        this.wordSet = new LinkedHashSet<>(normalizedWords);
        this.random = new Random();
    }

    public boolean contains(String word) {
        return wordSet.contains(WordNormalizer.normalize(word));
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст");
        }

        return words.get(random.nextInt(words.size()));
    }

    public List<String> getWords() {
        return Collections.unmodifiableList(words);
    }

    public int size() {
        return words.size();
    }

    public boolean isEmpty() {
        return words.isEmpty();
    }

    public static boolean isValidDictionaryWord(String word) {
        return word != null
                && word.length() == WORD_LENGTH
                && WordNormalizer.hasOnlyRussianLetters(word);
    }
}
