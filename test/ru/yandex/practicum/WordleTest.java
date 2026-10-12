package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.Writer;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    private WordleGame game;
    private StringWriter text;
    private Writer output;

    @BeforeEach
    void prepareGame() throws Exception {
        Writer log = new StringWriter();
        WordleDictionary dictionary = new WordleDictionary(List.of("герой", "маска"), log);
        game = new WordleGame(dictionary, "герой", log);
        text = new StringWriter();
        output = text;
    }

    @Test
    void playerWins() throws Exception {
        try (BufferedReader input = new BufferedReader(new StringReader("ГЕРОЙ\n"))) {
            Wordle.play(game, input, output);
        }
        assertTrue(game.isWon());
        assertTrue(text.toString().contains("+++++"));
        assertTrue(text.toString().contains("Победа"));
        assertTrue(text.toString().contains("Загаданное слово: герой"));
    }

    @Test
    void emptyLineMakesComputerMove() throws Exception {
        Writer log = new StringWriter();
        WordleDictionary dictionary = new WordleDictionary(List.of("герой"), log);
        game = new WordleGame(dictionary, log);
        try (BufferedReader input = new BufferedReader(new StringReader("\n"))) {
            Wordle.play(game, input, output);
        }
        assertTrue(game.isWon());
        assertEquals(5, game.getRemainingAttempts());
        assertTrue(text.toString().contains("герой"));
        assertTrue(text.toString().contains("+++++"));
    }

    @Test
    void endOfInputStopsGame() throws Exception {
        try (BufferedReader input = new BufferedReader(new StringReader(""))) {
            Wordle.play(game, input, output);
        }
        assertEquals(6, game.getRemainingAttempts());
        assertFalse(game.isWon());
        assertTrue(text.toString().contains("Загаданное слово: герой"));
    }

}
