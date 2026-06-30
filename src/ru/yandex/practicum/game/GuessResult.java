package ru.yandex.practicum.game;

public class GuessResult {

    private final String guess;
    private final String pattern;
    private final boolean win;
    private final int attemptsLeft;

    public GuessResult(String guess, String pattern, boolean win, int attemptsLeft) {
        this.guess = guess;
        this.pattern = pattern;
        this.win = win;
        this.attemptsLeft = attemptsLeft;
    }

    public String getGuess() {
        return guess;
    }

    public String getPattern() {
        return pattern;
    }

    public boolean isWin() {
        return win;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }
}
