package ru.otus.http.jserver.processors;

import com.google.gson.Gson;
import ru.otus.http.jserver.HttpRequest;
import ru.otus.http.jserver.application.Product;
import ru.otus.http.jserver.application.ProductsService;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class UpdateProductProcessor implements RequestProcessor {
    private ProductsService productsService;

    public UpdateProductProcessor(ProductsService productsService) {
        this.productsService = productsService;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        Gson gson = new Gson();
        Product newProduct = gson.fromJson(request.getBody(), Product.class);
        productsService.updateByNewProduct(newProduct);
        String response = "" +
                "HTTP/1.1 204 Ok\r\n" +
                 "Content-Type: text/html\r\n" +
                 "\r\n";
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }

}






