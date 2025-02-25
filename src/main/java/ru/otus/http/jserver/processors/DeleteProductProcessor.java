package ru.otus.http.jserver.processors;

import com.google.gson.Gson;
import ru.otus.http.jserver.HttpRequest;
import ru.otus.http.jserver.application.ProductsService;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DeleteProductProcessor implements RequestProcessor {
    private ProductsService productsService;

    public DeleteProductProcessor(ProductsService productsService) {
        this.productsService = productsService;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        Gson gson = new Gson();
        boolean found;
        if (request.containsParameter("id")) {
            Long id = Long.parseLong(request.getParameter("id"));
            productsService.deleteProductById(id);
        } else {
            productsService.deleteAllProducts();
        }
        String response = "" +
                "HTTP/1.1 201 Ok\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n";
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
