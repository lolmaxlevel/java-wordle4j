package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {
    private final Writer log = new StringWriter();

    @Test
    void loadRussianWords() throws Exception {
        Path file = Files.createTempFile("wordle-test", ".txt");
        try {
            try (Writer writer = new FileWriter(file.toFile(), StandardCharsets.UTF_8)) {
                writer.write("ГЕРОЙ\nёжики\nкот\nгерой\n");
            }
            WordleDictionary dictionary = new WordleDictionaryLoader(log).load(file.toString());
            assertEquals(List.of("герой", "ежики"), dictionary.getWords());
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void loadMissingFile() throws Exception {
        Path folder = Files.createTempDirectory("wordle-missing");
        try {
            assertThrows(IOException.class,
                    () -> new WordleDictionaryLoader(log).load(folder.resolve("missing.txt").toString()));
        } finally {
            Files.deleteIfExists(folder);
        }
    }
}
