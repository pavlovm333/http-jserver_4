package ru.otus.http.jserver.processors;


import ru.otus.http.jserver.HttpRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class GetFileProcessor implements RequestProcessor {

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
         String uri = request.getUri();
         try (FileInputStream fileInputStream = new FileInputStream("./src/main/resources/static/" + uri.substring(2,uri.length() - 1))) {
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length;
            output.write(("" +
                    "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/octet-stream\r\n" +
                    "Content-Disposition: inline; filename=\"" +
                    uri.substring(2,uri.length() - 1) +
                    "\"\r\n" +
                    "\r\n").getBytes(StandardCharsets.UTF_8));
            while ((length = fileInputStream.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
        }
    }




}
