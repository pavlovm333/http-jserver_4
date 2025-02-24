package ru.otus.http.jserver.processors;

import com.google.gson.Gson;
import ru.otus.http.jserver.BadRequestException;
import ru.otus.http.jserver.HttpRequest;
import ru.otus.http.jserver.application.ErrorDto;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Default400Processor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        ErrorDto errorDto = new ErrorDto(
                ((BadRequestException)request.getErrorCause()).getCode(),
                ((BadRequestException) request.getErrorCause()).getDescription()
        );
        Gson gson = new Gson();
        String jsonError = gson.toJson(errorDto);
        String response = "" +
                "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                jsonError;
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
