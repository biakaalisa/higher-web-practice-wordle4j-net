package ru.yandex.practicum.game;

public enum LetterState {

    CORRECT('+'),
    PRESENT('^'),
    ABSENT('-');

    private final char symbol;

    LetterState(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }
}
