package ru.otus.http.jserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpRequest {
    private static final Logger LOGGER = LogManager.getLogger(HttpRequest.class);
    private HttpMethod method;
    private String uri;
    private Map<String, String> parameters;
    private Map<String, String> headers;
    private String body;
    private BufferedReader reader;

    private Exception errorCause;

    public Exception getErrorCause() {
        return errorCause;
    }

    public void setErrorCause(Exception errorCause) {
        this.errorCause = errorCause;
    }

    public String getBody() {
        return body;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRoutingKey() {
        return method + " " + uri; // 'GET /items', 'POST /items'
    }

    public String getUri() {
        return URLDecoder.decode(uri, StandardCharsets.UTF_8);
    }

    public HttpRequest(BufferedReader reader) throws IOException{
        parameters = new HashMap<>();
        headers = new HashMap<>();
        this.reader = reader;
        parse();
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public boolean containsParameter(String key) {
        return parameters.containsKey(key);
    }

    public void parse() throws IOException {
        String paramline;
        int n;
        StringBuilder currentline = new StringBuilder();
        int requestHeaderSize = 0;
        int requestBodySize = 0;

        String lineStart = reader.readLine();
        requestHeaderSize+= lineStart.length();

        int startIndex = lineStart.indexOf(' ');
        int endIndex = lineStart.indexOf(' ', startIndex + 1);
        method = HttpMethod.valueOf(lineStart.substring(0, startIndex));
        uri = lineStart.substring(startIndex + 1, endIndex);
        if (uri.contains("?")) {
            String[] tokens = uri.split("[?]");
            this.uri = tokens[0];
            String[] paramsPairs = tokens[1].split("[&]");
            for (String o : paramsPairs) {
                String[] keyValue = o.split("=");
                this.parameters.put(keyValue[0], keyValue[1]);
            }
        }

        while (reader.ready() && (paramline = reader.readLine()).length() > 0) {
            String[] keyValue = paramline.split(": ", 2);
            headers.put(keyValue[0], keyValue[1]);
            requestHeaderSize+= paramline.length();
            if (requestHeaderSize > Application.limitRequestHeader) {
                errorCause = new BadRequestException(
                        "431 Request Header Fields Too Large",
                        "Request Header Fields Too Large"
                );
                return;
            }
        }

        while (reader.ready()) {
            n = reader.read();
            currentline.append((char) n);
            requestBodySize+= 1;
            if (requestBodySize > Application.limitRequestBody) {
                errorCause = new BadRequestException(
                        "413 Request Entity Too Larg",
                        "Request Entity Too Larg"
                );
                return;
            }
        }
        body = currentline.toString();
    }

    public void info() {
        LOGGER.debug("METHOD: {}", method);
        LOGGER.debug("URI: {}", uri);
        LOGGER.debug("PARAMETRS: {}", parameters);
        LOGGER.debug("HEADERS: {}", headers);
        LOGGER.debug("BODY: {}", body);
    }
}
