package ru.otus.http.jserver;

import ru.otus.http.jserver.application.ProductsService;
import ru.otus.http.jserver.processors.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dispatcher {
    private Map<String, RequestProcessor> router;
    private Default400Processor default400Processor;
    private Default404Processor default404Processor;
    private Default500Processor default500Processor;
    private GetFileProcessor getFileProcessor;

    public Dispatcher() {
        ProductsService productsService = new ProductsService();
        this.router = new HashMap<>();
        this.router.put("GET /calc", new CalculatorProcessor());
        this.router.put("GET /welcome", new WelcomeProcessor());
        this.router.put("GET /products", new GetProductsProcessor(productsService));
        this.router.put("POST /products", new CreateProductProcessor(productsService));
        this.default404Processor = new Default404Processor();
        this.default400Processor = new Default400Processor();
        this.default500Processor = new Default500Processor();
        this.router.put("PUT /products", new UpdateProductProcessor(productsService));
        this.router.put("DELETE /products", new DeleteProductProcessor(productsService));
        this.getFileProcessor = new GetFileProcessor();
        this.badRequestProcessor = new BadRequestProcessor();

    }

    private BadRequestProcessor badRequestProcessor;

    public void execute(HttpRequest request, OutputStream output) throws IOException {
        if (request.getErrorCause() != null) {
            badRequestProcessor.execute(request, output);
            return;
        }
        try {
            if (!router.containsKey(request.getRoutingKey())) {

                String regex = "^GET /\\[(.+)\\]$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(request.getRoutingKey());
                if (matcher.matches()) {
                    getFileProcessor.execute(request, output);
                    return;
                }

                if ((request.getMethod() == HttpMethod.POST) && (router.containsKey("GET " + request.getUri()))) {
                    throw new BadRequestExceptionEx("405 Method Not Allowed", "Method Not Allowed");
                }

                default404Processor.execute(request, output);
                return;
            }
            router.get(request.getRoutingKey()).execute(request, output);
        } catch (BadRequestExceptionEx e) {
            request.setErrorCause(e);
            badRequestProcessor.execute(request, output);
        } catch (BadRequestException e) {
            e.printStackTrace();
            request.setErrorCause(e);
            default400Processor.execute(request, output);
        } catch (Exception e) {
            e.printStackTrace();
            default500Processor.execute(request, output);
        }
    }
}
