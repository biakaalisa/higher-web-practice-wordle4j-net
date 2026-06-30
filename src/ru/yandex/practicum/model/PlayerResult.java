package ru.yandex.practicum.model;

public class PlayerResult {

    private String nickname;
    private int attempts;
    private boolean hintsUsed;

    public PlayerResult() {
    }

    public PlayerResult(String nickname, int attempts, boolean hintsUsed) {
        this.nickname = nickname;
        this.attempts = attempts;
        this.hintsUsed = hintsUsed;
    }

    public String getNickname() {
        return nickname;
    }

    public int getAttempts() {
        return attempts;
    }

    public boolean isHintsUsed() {
        return hintsUsed;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public void setHintsUsed(boolean hintsUsed) {
        this.hintsUsed = hintsUsed;
    }
}