package ru.yandex.practicum.exceptions;

public class WordNotFoundInDictionary extends Exception {
    public WordNotFoundInDictionary(String message) {
        super(message);
    }
}
