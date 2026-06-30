package ru.yandex.practicum.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerResultTest {

    @Test
    void shouldStorePlayerResultFields() {
        PlayerResult result = new PlayerResult("Ivan", 4, true);

        assertEquals("Ivan", result.getNickname());
        assertEquals(4, result.getAttempts());
        assertTrue(result.isHintsUsed());
    }

    @Test
    void shouldAllowSettersForJson() {
        PlayerResult result = new PlayerResult();

        result.setNickname("Anna");
        result.setAttempts(2);
        result.setHintsUsed(false);

        assertEquals("Anna", result.getNickname());
        assertEquals(2, result.getAttempts());
        assertFalse(result.isHintsUsed());
    }
}
