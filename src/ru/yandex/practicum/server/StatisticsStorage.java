package ru.yandex.practicum.server;

import ru.yandex.practicum.exception.ServerException;
import ru.yandex.practicum.model.PlayerResult;
import ru.yandex.practicum.model.PlayerStatistic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatisticsStorage {

    private static final int TOP_LIMIT = 10;

    private final WordleServerStatisticLoader loader;
    private final List<PlayerResult> results;

    public StatisticsStorage(WordleServerStatisticLoader loader) throws ServerException {
        this.loader = loader;
        this.results = new ArrayList<>(loader.load());
    }

    public synchronized void addResult(PlayerResult result) throws ServerException {
        results.add(result);
        loader.save(results);
    }

    public synchronized List<PlayerStatistic> getTop() {
        Map<String, PlayerStatisticAccumulator> statistics = new LinkedHashMap<>();

        for (PlayerResult result : results) {
            String nickname = result.getNickname();
            if (!statistics.containsKey(nickname)) {
                statistics.put(nickname, new PlayerStatisticAccumulator(nickname));
            }
            statistics.get(nickname).add(result);
        }

        List<PlayerStatistic> top = new ArrayList<>();
        for (PlayerStatisticAccumulator accumulator : statistics.values()) {
            top.add(accumulator.toStatistic());
        }

        top.sort((first, second) -> {
            int winsComparison = Integer.compare(second.getWins(), first.getWins());
            if (winsComparison != 0) {
                return winsComparison;
            }
            return Integer.compare(first.getBestAttempts(), second.getBestAttempts());
        });

        if (top.size() > TOP_LIMIT) {
            return new ArrayList<>(top.subList(0, TOP_LIMIT));
        }

        return top;
    }

    public synchronized List<PlayerResult> getResults() {
        return new ArrayList<>(results);
    }

    private static class PlayerStatisticAccumulator {

        private final String nickname;
        private int wins;
        private int bestAttempts;
        private boolean hintsUsed;

        private PlayerStatisticAccumulator(String nickname) {
            this.nickname = nickname;
            this.bestAttempts = Integer.MAX_VALUE;
        }

        private void add(PlayerResult result) {
            wins++;
            if (result.getAttempts() < bestAttempts) {
                bestAttempts = result.getAttempts();
            }
            if (result.isHintsUsed()) {
                hintsUsed = true;
            }
        }

        private PlayerStatistic toStatistic() {
            return new PlayerStatistic(nickname, wins, bestAttempts, hintsUsed);
        }
    }
}
