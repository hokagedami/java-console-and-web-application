package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static server.helpers.HandlerHelpers.SendResponse;

/**
 * This class is responsible for handling requests for static resources
 */
public class StaticResourceHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String fileName = exchange.getRequestURI().getPath().substring(1);
        String response = new String(Files.readAllBytes(Paths.get("public/" + fileName)));
        SendResponse(exchange, response);
    }
}
