package ru.otus.http.jserver.processors;

import ru.otus.http.jserver.HttpRequest;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class Default404Processor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        String response = "" +
                "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "\r\n" +
                "<html><body><img alt=Page Not Found src= data:image/jpg;base64,";
        try (FileInputStream fileInputStream = new FileInputStream("./src/main/resources/page_not_found.jpg")) {
            byte[] buffer = fileInputStream.readAllBytes();
            output.write(response.getBytes(StandardCharsets.UTF_8));
            output.write(Base64.getEncoder().encode(buffer));
            output.write((" /></body></html>").getBytes(StandardCharsets.UTF_8));
        }
    }
}

