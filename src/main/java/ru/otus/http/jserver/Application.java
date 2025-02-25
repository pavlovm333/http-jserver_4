package ru.otus.http.jserver;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;
import org.ini4j.Wini;
import java.io.*;


public class Application {
    static final Logger LOGGER = LogManager.getLogger(Application.class);
    static int port = 8189;
    static int threadsNumber = Runtime.getRuntime().availableProcessors();
    static int limitRequestHeader = 4096;
    static int limitRequestBody = 8192;
    public static int limitResponceBody = 8192;


    static {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("./src/main/resources/application.ini"))) {
            Wini ini = new Wini(bufferedReader);
            Ini.Section section = ini.get("server");
            if (section.containsKey("port")) {
                port = ini.get("server", "port", int.class);
            }
            if (section.containsKey("threadsNumber")) {
                threadsNumber = ini.get("server", "threadsNumber", int.class);
            }
            if (section.containsKey("limitRequestHeader")) {
                limitRequestHeader = ini.get("server", "limitRequestHeader", int.class);
            }
            if (section.containsKey("limitRequestBody")) {
                limitRequestBody = ini.get("server", "limitRequestBody", int.class);
            }
            if (section.containsKey("limitResponceBody")) {
                limitResponceBody = ini.get("server", "limitResponceBody", int.class);
            }
        } catch(Exception e){
            LOGGER.error(e);
        }
    }

    public static void main(String[] args) {
        new HttpServer(port).start();
    }
}
