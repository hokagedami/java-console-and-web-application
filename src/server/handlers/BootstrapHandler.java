package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class is responsible for serving static resources specifically the Bootstrap files
 */
public class BootstrapHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        InputStream inputStream = getClass().getResourceAsStream("/static" + path);
        if (inputStream != null) {
            byte[] data = inputStream.readAllBytes();
            exchange.sendResponseHeaders(200, data.length);
            OutputStream os = exchange.getResponseBody();
            os.write(data);
            os.close();
        } else {
            // File not found
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        }
    }
}
