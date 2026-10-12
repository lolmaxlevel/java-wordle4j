package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.EmptyDictionaryException;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class WordleDictionary {
    private final List<String> words;
    private final Set<String> wordSet;

    public WordleDictionary(List<String> sourceWords, Writer log) throws EmptyDictionaryException, IOException {
        List<String> filteredWords = new ArrayList<>();
        wordSet = new HashSet<>();
        for (String word : sourceWords) {
            String normalized = normalize(word);
            if (isValidWord(normalized) && wordSet.add(normalized)) {
                filteredWords.add(normalized);
            }
        }
        if (filteredWords.isEmpty()) {
            throw new EmptyDictionaryException("В словаре нет подходящих пятибуквенных слов.");
        }
        words = List.copyOf(filteredWords);
        log.write("Словарь подготовлен. Количество слов: " + words.size() + System.lineSeparator());
    }

    public static String normalize(String word) {
        if (word == null) {
            return "";
        }
        return word.strip().toLowerCase(Locale.ROOT).replace('ё', 'е');
    }

    public static boolean isValidWord(String word) {
        return word != null && word.matches("[а-я]{5}");
    }

    public boolean contains(String word) {
        return wordSet.contains(normalize(word));
    }

    public List<String> getWords() {
        return words;
    }

    public String getRandomWord() {
        return words.get(ThreadLocalRandom.current().nextInt(words.size()));
    }

    public static String compare(String guess, String answer) {
        if (!isValidWord(guess) || !isValidWord(answer)) {
            throw new IllegalArgumentException("Для сравнения нужны нормализованные слова из пяти русских букв.");
        }
        char[] result = {'-', '-', '-', '-', '-'};
        int[] remainingLetters = new int[32];

        // Сначала отмечаем точные совпадения, чтобы не использовать их буквы второй раз.
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                result[i] = '+';
            } else {
                remainingLetters[answer.charAt(i) - 'а']++;
            }
        }
        for (int i = 0; i < 5; i++) {
            int letter = guess.charAt(i) - 'а';
            if (result[i] != '+' && remainingLetters[letter] > 0) {
                result[i] = '^';
                remainingLetters[letter]--;
            }
        }
        StringBuilder feedback = new StringBuilder();
        for (char mark : result) {
            feedback.append(mark);
        }
        return feedback.toString();
    }
}
