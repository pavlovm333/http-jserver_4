package ru.otus.http.jserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private static final Logger LOGGER = LogManager.getLogger(HttpServer.class);
    private int port;
    private Dispatcher dispatcher;

    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher();
    }

    public void start() {
        ExecutorService serv = Executors.newFixedThreadPool(Application.threadsNumber);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Сервер запущен на порту: {}", port);
            while (true) {
                Socket socket = serverSocket.accept();
                serv.execute(() -> {
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                        LOGGER.info("Подключился новый клиент в потоке {}", Thread.currentThread().getName());
                        HttpRequest request = new HttpRequest(bufferedReader);
                        request.info();
                        dispatcher.execute(request, socket.getOutputStream());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            serv.shutdown();
        }
    }
}

