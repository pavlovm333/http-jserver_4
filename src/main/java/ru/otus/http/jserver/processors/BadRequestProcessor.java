package ru.otus.http.jserver.processors;

import ru.otus.http.jserver.BadRequestException;
import ru.otus.http.jserver.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BadRequestProcessor implements RequestProcessor{
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        String response = "" +
                "HTTP/1.1 " + ((BadRequestException) request.getErrorCause()).getCode() +
                "\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "\r\n" +
                "<html><body><h1>" + ((BadRequestException) request.getErrorCause()).getDescription() + "</h1></body></html>";
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}

