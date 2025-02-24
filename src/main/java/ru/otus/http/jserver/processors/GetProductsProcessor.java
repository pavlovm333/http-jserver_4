package ru.otus.http.jserver.processors;

import com.google.gson.Gson;
import ru.otus.http.jserver.HttpRequest;
import ru.otus.http.jserver.application.Product;
import ru.otus.http.jserver.application.ProductsService;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetProductsProcessor implements RequestProcessor {
    private ProductsService productsService;

    public GetProductsProcessor(ProductsService productsService) {
        this.productsService = productsService;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        String jsonResult = null;
        Gson gson = new Gson();
        if (request.containsParameter("id")) {
            Long id = Long.parseLong(request.getParameter("id"));
            Product product = productsService.getProductById(id);
            jsonResult = gson.toJson(product);
        } else {
            List<Product> products = productsService.getAllProducts();
            jsonResult = gson.toJson(products);
        }
        String response = "" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                jsonResult;
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
