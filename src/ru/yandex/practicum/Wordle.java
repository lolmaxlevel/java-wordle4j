package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.InvalidWordException;
import ru.yandex.practicum.exceptions.WordNotFoundInDictionary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class Wordle {
    public static void main(String[] args) {
        try {
            try (BufferedWriter log = openLog("wordle.log");
                 BufferedReader input = new BufferedReader(
                         new InputStreamReader(System.in, StandardCharsets.UTF_8));
                 BufferedWriter output = new BufferedWriter(
                         new OutputStreamWriter(System.out, StandardCharsets.UTF_8))) {
                WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
                WordleDictionary dictionary = loader.load("words_ru.txt");
                WordleGame game = new WordleGame(dictionary, log);
                play(game, input, output);
                log.write("Игра завершена. Победа: " + game.isWon()
                        + ", оставшихся попыток: " + game.getRemainingAttempts() + System.lineSeparator());
            }
        } catch (Exception error) {
            writeError(error);
            System.exit(1);
        }
    }

    static void play(WordleGame game, BufferedReader input, Writer output) throws IOException {
        output.write("Wordle: отгадайте слово из пяти букв за шесть попыток." + System.lineSeparator());
        output.write("+ — буква на месте, ^ — буква на другом месте, - — буквы нет." + System.lineSeparator());
        output.write("Нажмите Enter в пустой строке, чтобы компьютер сделал ход." + System.lineSeparator());

        while (!game.isFinished()) {
            output.write("Осталось попыток: " + game.getRemainingAttempts() + System.lineSeparator());
            output.write("> ");
            output.flush();
            String word = input.readLine();
            if (word == null) {
                output.write(System.lineSeparator());
                output.write("Игра прервана: ввод закончился." + System.lineSeparator());
                break;
            }
            word = WordleDictionary.normalize(word);
            if (word.isEmpty()) {
                word = game.suggestWord();
            }
            try {
                String feedback = game.makeMove(word);
                output.write(word + System.lineSeparator());
                output.write(feedback + System.lineSeparator());
            } catch (InvalidWordException | WordNotFoundInDictionary error) {
                output.write(error.getMessage() + System.lineSeparator());
            }
        }

        if (game.isWon()) {
            output.write("Победа! Вы отгадали слово." + System.lineSeparator());
        } else if (game.isFinished()) {
            output.write("Попытки закончились. Вы проиграли." + System.lineSeparator());
        }
        output.write("Загаданное слово: " + game.getAnswer() + System.lineSeparator());
        output.flush();
    }

    private static BufferedWriter openLog(String filename) throws IOException {
        return new BufferedWriter(new FileWriter(filename, StandardCharsets.UTF_8, true));
    }

    private static void writeError(Exception error) {
        try (BufferedWriter log = openLog("wordle.log")) {
            writeException(log, error);
        } catch (Exception ignored) {
            // Если лог недоступен, завершаемся без стектрейса в консоли игрока.
        }
    }

    private static void writeException(Writer log, Exception error) throws IOException {
        log.write(error.toString() + System.lineSeparator());
        for (StackTraceElement element : error.getStackTrace()) {
            log.write("\tat " + element + System.lineSeparator());
        }
    }
}
