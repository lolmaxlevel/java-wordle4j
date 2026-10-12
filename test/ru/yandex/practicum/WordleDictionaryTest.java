package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Writer;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryTest {
    private static Writer log;

    @BeforeAll
    static void prepareLog() {
        log = new StringWriter();
    }

    @Test
    void normalizeWord() {
        assertEquals("ежики", WordleDictionary.normalize("  ЁЖИКИ "));
    }

    @Test
    void removeWrongWordsAndDuplicates() throws Exception {
        WordleDictionary dictionary = new WordleDictionary(
                List.of(" ГЕРОЙ ", "герой", "кот", "abcde", "12345", "ЁЖИКИ"), log);
        assertEquals(List.of("герой", "ежики"), dictionary.getWords());
        assertTrue(dictionary.contains("ГЕРОЙ"));
        assertFalse(dictionary.contains("кот"));
    }

    @Test
    void compareWords() {
        assertEquals("+^-^-", WordleDictionary.compare("гонец", "герой"));
        assertEquals("+++++", WordleDictionary.compare("герой", "герой"));
        assertEquals("-----", WordleDictionary.compare("маска", "герой"));
    }

    @Test
    void compareRepeatedLetters() {
        assertEquals("^^++^", WordleDictionary.compare("атлас", "салат"));
        assertEquals("-++++", WordleDictionary.compare("каска", "маска"));
        assertEquals("++--+", WordleDictionary.compare("каска", "карта"));
    }
}
