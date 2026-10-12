package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.InvalidWordException;
import ru.yandex.practicum.exceptions.WordNotFoundInDictionary;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class WordleGame {
    private static final int MAX_ATTEMPTS = 6;

    private final String answer;
    private final WordleDictionary dictionary;
    private final Writer log;
    private final Map<String, String> history = new LinkedHashMap<>();
    private List<String> candidates;
    private int remainingAttempts = MAX_ATTEMPTS;
    private boolean won;

    public WordleGame(WordleDictionary dictionary, Writer log) throws IOException {
        this(dictionary, dictionary.getRandomWord(), log);
    }

    public WordleGame(WordleDictionary dictionary, String answer, Writer log) throws IOException {
        this.answer = WordleDictionary.normalize(answer);
        if (!dictionary.contains(this.answer)) {
            throw new IllegalArgumentException("Ответ должен присутствовать в словаре.");
        }
        this.dictionary = dictionary;
        this.log = log;
        candidates = dictionary.getWords();
        log.write("Новая игра. Ответ: " + this.answer + ", попыток: " + remainingAttempts + System.lineSeparator());
    }

    public String makeMove(String word) throws InvalidWordException, WordNotFoundInDictionary, IOException {
        checkGameIsActive();
        String normalized = WordleDictionary.normalize(word);
        if (!WordleDictionary.isValidWord(normalized)) {
            throw new InvalidWordException("Введите слово из пяти русских букв.");
        }
        if (!dictionary.contains(normalized)) {
            throw new WordNotFoundInDictionary("Слова «" + normalized + "» нет в словаре.");
        }

        String feedback = WordleDictionary.compare(normalized, answer);
        List<String> filtered = new ArrayList<>();
        for (String candidate : candidates) {
            if (WordleDictionary.compare(normalized, candidate).equals(feedback)) {
                filtered.add(candidate);
            }
        }
        if (filtered.isEmpty() || !filtered.contains(answer)) {
            throw new IllegalStateException("При фильтрации потерян правильный ответ.");
        }
        candidates = List.copyOf(filtered);
        history.put(normalized, feedback);
        remainingAttempts--;
        won = normalized.equals(answer);
        log.write("Ход: " + normalized + ", результат: " + feedback
                + ", попыток: " + remainingAttempts + ", вариантов: " + candidates.size() + System.lineSeparator());
        return feedback;
    }

    public String suggestWord() {
        checkGameIsActive();
        List<String> suggestions = new ArrayList<>();
        for (String candidate : candidates) {
            if (!history.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }
        if (suggestions.isEmpty()) {
            throw new IllegalStateException("В незавершённой игре нет вариантов для подсказки.");
        }
        return suggestions.get(ThreadLocalRandom.current().nextInt(suggestions.size()));
    }

    private void checkGameIsActive() {
        if (isFinished()) {
            throw new IllegalStateException("Игра уже завершена.");
        }
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public boolean isWon() {
        return won;
    }

    public boolean isFinished() {
        return won || remainingAttempts == 0;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getCandidates() {
        return candidates;
    }
}
