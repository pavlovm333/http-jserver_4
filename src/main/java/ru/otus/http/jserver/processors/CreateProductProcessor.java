package ru.otus.http.jserver.processors;

import com.google.gson.Gson;
import ru.otus.http.jserver.HttpRequest;
import ru.otus.http.jserver.application.Product;
import ru.otus.http.jserver.application.ProductsService;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CreateProductProcessor implements RequestProcessor {
    private ProductsService productsService;

    public CreateProductProcessor(ProductsService productsService) {
        this.productsService = productsService;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        Gson gson = new Gson();
        String newProductTitle = gson.fromJson(request.getBody(), String.class);
        Product product = new Product();
        boolean result = productsService.createNewProduct(newProductTitle, product);
        String jsonResult = gson.toJson(product);
        String code = (result) ? "201 Created" : "409 Conflict";
        String response = "" +
                "HTTP/1.1 " + code + "\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                jsonResult;
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}

