package ru.otus.http.jserver;

public class BadRequestException extends RuntimeException {
    private String code;
    private String description;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BadRequestException(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
