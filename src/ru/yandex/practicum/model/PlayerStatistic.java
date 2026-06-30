package ru.yandex.practicum.model;

public class PlayerStatistic {

    private String nickname;
    private int wins;
    private int bestAttempts;
    private boolean hintsUsed;

    public PlayerStatistic() {
    }

    public PlayerStatistic(String nickname, int wins, int bestAttempts, boolean hintsUsed) {
        this.nickname = nickname;
        this.wins = wins;
        this.bestAttempts = bestAttempts;
        this.hintsUsed = hintsUsed;
    }

    public String getNickname() {
        return nickname;
    }

    public int getWins() {
        return wins;
    }

    public int getBestAttempts() {
        return bestAttempts;
    }

    public boolean isHintsUsed() {
        return hintsUsed;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setBestAttempts(int bestAttempts) {
        this.bestAttempts = bestAttempts;
    }

    public void setHintsUsed(boolean hintsUsed) {
        this.hintsUsed = hintsUsed;
    }

    @Override
    public String toString() {
        return nickname
                + " — побед: "
                + wins
                + ", лучшая игра: "
                + bestAttempts
                + ", подсказки: "
                + (hintsUsed ? "да" : "нет");
    }
}
