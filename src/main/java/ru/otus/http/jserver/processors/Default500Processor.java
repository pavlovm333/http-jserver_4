package ru.otus.http.jserver.processors;

import ru.otus.http.jserver.HttpRequest;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Default500Processor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        String response = "" +
                "HTTP/1.1 500 Internal Server Error\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body><h1>Internal Server Error: something wrong...</h1></body></html>";
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
