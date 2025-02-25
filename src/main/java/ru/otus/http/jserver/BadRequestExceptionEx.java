package ru.otus.http.jserver;

public class BadRequestExceptionEx extends BadRequestException{
    public BadRequestExceptionEx(String code, String description) {
       super(code, description);
    }
}
