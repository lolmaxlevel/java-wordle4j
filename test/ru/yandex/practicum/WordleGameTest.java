package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.InvalidWordException;
import ru.yandex.practicum.exceptions.WordNotFoundInDictionary;

import java.io.Writer;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {
    private Writer log;
    private WordleDictionary dictionary;
    private WordleGame game;

    @BeforeEach
    void prepareGame() throws Exception {
        log = new StringWriter();
        dictionary = new WordleDictionary(
                List.of("герой", "гонец", "горец", "маска", "каска", "карта", "атлас", "салат"), log);
        game = new WordleGame(dictionary, "герой", log);
    }

    @Test
    void wrongLengthDoesNotUseAttempt() {
        assertThrows(InvalidWordException.class, () -> game.makeMove("кот"));
        assertEquals(6, game.getRemainingAttempts());
        assertEquals(dictionary.getWords(), game.getCandidates());
    }

    @Test
    void foreignLettersAreNotAllowed() {
        assertThrows(InvalidWordException.class, () -> game.makeMove("abcde"));
        assertThrows(InvalidWordException.class, () -> game.makeMove("12345"));
        assertThrows(InvalidWordException.class, () -> game.makeMove(""));
        assertThrows(InvalidWordException.class, () -> game.makeMove(null));
        assertEquals(6, game.getRemainingAttempts());
    }

    @Test
    void wordMustBeInDictionary() {
        assertThrows(WordNotFoundInDictionary.class, () -> game.makeMove("океан"));
        assertEquals(6, game.getRemainingAttempts());
        assertEquals(dictionary.getWords(), game.getCandidates());
    }

    @Test
    void guessAnswer() throws Exception {
        assertEquals("+++++", game.makeMove("ГЕРОЙ"));
        assertTrue(game.isWon());
        assertTrue(game.isFinished());
        assertThrows(IllegalStateException.class, () -> game.makeMove("гонец"));
        assertThrows(IllegalStateException.class, () -> game.suggestWord());
    }

    @Test
    void loseAfterSixMoves() throws Exception {
        for (int i = 0; i < 6; i++) {
            game.makeMove("маска");
        }
        assertEquals(0, game.getRemainingAttempts());
        assertTrue(game.isFinished());
        assertFalse(game.isWon());
        assertThrows(IllegalStateException.class, () -> game.makeMove("герой"));
    }

    @Test
    void filterUsingAllMoves() throws Exception {
        game.makeMove("маска");
        assertEquals(List.of("герой", "гонец", "горец"), game.getCandidates());
        game.makeMove("гонец");
        assertEquals(List.of("герой"), game.getCandidates());
        assertEquals("герой", game.suggestWord());
    }

}
