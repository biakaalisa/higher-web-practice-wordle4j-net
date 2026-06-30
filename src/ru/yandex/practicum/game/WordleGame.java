package ru.yandex.practicum.game;

import ru.yandex.practicum.dictionary.WordleDictionary;
import ru.yandex.practicum.exception.EmptyInputException;
import ru.yandex.practicum.exception.InvalidAlphabetException;
import ru.yandex.practicum.exception.InvalidLengthException;
import ru.yandex.practicum.exception.InvalidWordException;
import ru.yandex.practicum.exception.WordNotFoundInDictionaryException;
import ru.yandex.practicum.util.InputValidator;
import ru.yandex.practicum.util.WordNormalizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordleGame {

    public static final int MAX_ATTEMPTS = 6;

    private final WordleDictionary dictionary;
    private final InputValidator inputValidator;
    private final String answer;
    private final List<String> previousGuesses;
    private final List<String> previousPatterns;
    private final List<String> previousHints;
    private int attemptsLeft;
    private boolean finished;
    private boolean won;
    private boolean hintsUsed;

    public WordleGame(WordleDictionary dictionary) {
        this(dictionary, dictionary.getRandomWord());
    }

    public WordleGame(WordleDictionary dictionary, String answer) {
        this.dictionary = dictionary;
        this.inputValidator = new InputValidator(dictionary);
        this.answer = WordNormalizer.normalize(answer);
        this.previousGuesses = new ArrayList<>();
        this.previousPatterns = new ArrayList<>();
        this.previousHints = new ArrayList<>();
        this.attemptsLeft = MAX_ATTEMPTS;
    }

    public GuessResult makeGuess(String input)
            throws InvalidWordException,
            EmptyInputException,
            InvalidLengthException,
            InvalidAlphabetException,
            WordNotFoundInDictionaryException {
        if (finished) {
            throw new InvalidWordException("Игра уже завершена");
        }

        String guess = validateGuess(input);
        String pattern = buildPattern(guess, answer);
        attemptsLeft--;
        previousGuesses.add(guess);
        previousPatterns.add(pattern);
        won = guess.equals(answer);

        if (won || attemptsLeft == 0) {
            finished = true;
        }

        return new GuessResult(guess, pattern, won, attemptsLeft);
    }

    public String getHint() {
        hintsUsed = true;

        for (String candidate : dictionary.getWords()) {
            if (isUnusedCandidate(candidate) && matchesHistory(candidate)) {
                previousHints.add(candidate);
                return candidate;
            }
        }

        return "Подсказок больше нет";
    }

    public String getAnswer() {
        return answer;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isWon() {
        return won;
    }

    public boolean isHintsUsed() {
        return hintsUsed;
    }

    public List<String> getPreviousGuesses() {
        return Collections.unmodifiableList(previousGuesses);
    }

    public List<String> getPreviousPatterns() {
        return Collections.unmodifiableList(previousPatterns);
    }

    public List<String> getPreviousHints() {
        return Collections.unmodifiableList(previousHints);
    }

    public static String buildPattern(String guess, String answer) {
        String normalizedGuess = WordNormalizer.normalize(guess);
        String normalizedAnswer = WordNormalizer.normalize(answer);
        char[] result = new char[WordleDictionary.WORD_LENGTH];
        boolean[] usedAnswerLetters = new boolean[WordleDictionary.WORD_LENGTH];

        for (int i = 0; i < WordleDictionary.WORD_LENGTH; i++) {
            if (normalizedGuess.charAt(i) == normalizedAnswer.charAt(i)) {
                result[i] = LetterState.CORRECT.getSymbol();
                usedAnswerLetters[i] = true;
            } else {
                result[i] = LetterState.ABSENT.getSymbol();
            }
        }

        for (int i = 0; i < WordleDictionary.WORD_LENGTH; i++) {
            if (result[i] == LetterState.CORRECT.getSymbol()) {
                continue;
            }

            char guessLetter = normalizedGuess.charAt(i);
            for (int j = 0; j < WordleDictionary.WORD_LENGTH; j++) {
                if (!usedAnswerLetters[j] && guessLetter == normalizedAnswer.charAt(j)) {
                    result[i] = LetterState.PRESENT.getSymbol();
                    usedAnswerLetters[j] = true;
                    break;
                }
            }
        }

        return new String(result);
    }

    private String validateGuess(String input)
            throws EmptyInputException,
            InvalidLengthException,
            InvalidAlphabetException,
            WordNotFoundInDictionaryException {
        return inputValidator.validateGuess(input);
    }

    private boolean isUnusedCandidate(String candidate) {
        return !previousGuesses.contains(candidate) && !previousHints.contains(candidate);
    }

    private boolean matchesHistory(String candidate) {
        for (int i = 0; i < previousGuesses.size(); i++) {
            String guess = previousGuesses.get(i);
            String pattern = previousPatterns.get(i);

            if (!buildPattern(guess, candidate).equals(pattern)) {
                return false;
            }
        }

        return true;
    }
}
