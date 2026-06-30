package ru.yandex.practicum.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.dictionary.WordleDictionary;
import ru.yandex.practicum.exception.EmptyInputException;
import ru.yandex.practicum.exception.InvalidAlphabetException;
import ru.yandex.practicum.exception.InvalidLengthException;
import ru.yandex.practicum.exception.WordNotFoundInDictionaryException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordleGameTest {

    private WordleDictionary dictionary;

    @BeforeEach
    void setUp() {
        dictionary = new WordleDictionary(List.of(
                "герой",
                "гонец",
                "город",
                "книга",
                "маска",
                "лампа",
                "трава",
                "слово"
        ));
    }

    @Test
    void shouldBuildCorrectPattern() {
        String pattern = WordleGame.buildPattern("гонец", "герой");

        assertEquals("+^-^-", pattern);
    }

    @Test
    void shouldBuildPatternWithRepeatedLettersCorrectly() {
        String pattern = WordleGame.buildPattern("маска", "лампа");

        assertEquals("^+--+", pattern);
    }

    @Test
    void shouldNormalizeGuessBeforeCheck() throws Exception {
        WordleGame game = new WordleGame(dictionary, "герой");

        GuessResult result = game.makeGuess("ГЕРОЙ");

        assertTrue(result.isWin());
        assertEquals("+++++", result.getPattern());
    }

    @Test
    void shouldRejectEmptyInput() {
        WordleGame game = new WordleGame(dictionary, "герой");

        assertThrows(EmptyInputException.class, () -> game.makeGuess("   "));
    }

    @Test
    void shouldRejectWordWithWrongLength() {
        WordleGame game = new WordleGame(dictionary, "герой");

        assertThrows(InvalidLengthException.class, () -> game.makeGuess("кот"));
    }

    @Test
    void shouldRejectWordWithWrongAlphabet() {
        WordleGame game = new WordleGame(dictionary, "герой");

        assertThrows(InvalidAlphabetException.class, () -> game.makeGuess("apple"));
    }

    @Test
    void shouldRejectWordNotFromDictionary() {
        WordleGame game = new WordleGame(dictionary, "герой");

        assertThrows(WordNotFoundInDictionaryException.class, () -> game.makeGuess("абвгд"));
    }

    @Test
    void shouldDecreaseAttemptsOnlyAfterCorrectGuess() throws Exception {
        WordleGame game = new WordleGame(dictionary, "герой");

        assertThrows(InvalidLengthException.class, () -> game.makeGuess("кот"));
        assertEquals(WordleGame.MAX_ATTEMPTS, game.getAttemptsLeft());

        game.makeGuess("гонец");
        assertEquals(WordleGame.MAX_ATTEMPTS - 1, game.getAttemptsLeft());
    }

    @Test
    void shouldReturnHintFromDictionary() {
        WordleGame game = new WordleGame(dictionary, "герой");

        String hint = game.getHint();

        assertTrue(dictionary.contains(hint));
        assertTrue(game.isHintsUsed());
    }

    @Test
    void shouldNotRepeatHints() {
        WordleGame game = new WordleGame(dictionary, "герой");

        String firstHint = game.getHint();
        String secondHint = game.getHint();

        assertNotEquals(firstHint, secondHint);
    }

    @Test
    void shouldFilterHintsByPreviousGuesses() throws Exception {
        WordleGame game = new WordleGame(dictionary, "герой");

        game.makeGuess("гонец");
        String hint = game.getHint();

        assertFalse(hint.equals("гонец"));
        assertEquals("+^-^-", WordleGame.buildPattern("гонец", hint));
    }
}
