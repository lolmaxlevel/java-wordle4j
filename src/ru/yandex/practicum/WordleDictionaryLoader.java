package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.EmptyDictionaryException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {
    private final Writer log;

    public WordleDictionaryLoader(Writer log) {
        this.log = log;
    }

    public WordleDictionary load(String filename) throws IOException, EmptyDictionaryException {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line);
            }
        }
        log.write("Прочитан словарь: " + filename + ", строк: " + words.size() + System.lineSeparator());
        return new WordleDictionary(words, log);
    }
}
