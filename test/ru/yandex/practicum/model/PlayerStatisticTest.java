package ru.yandex.practicum.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerStatisticTest {

    @Test
    void shouldStorePlayerStatisticFields() {
        PlayerStatistic statistic = new PlayerStatistic("Ivan", 3, 2, true);

        assertEquals("Ivan", statistic.getNickname());
        assertEquals(3, statistic.getWins());
        assertEquals(2, statistic.getBestAttempts());
        assertTrue(statistic.isHintsUsed());
    }

    @Test
    void shouldAllowSettersForJson() {
        PlayerStatistic statistic = new PlayerStatistic();

        statistic.setNickname("Anna");
        statistic.setWins(1);
        statistic.setBestAttempts(5);
        statistic.setHintsUsed(false);

        assertEquals("Anna", statistic.getNickname());
        assertEquals(1, statistic.getWins());
        assertEquals(5, statistic.getBestAttempts());
        assertFalse(statistic.isHintsUsed());
    }
}
