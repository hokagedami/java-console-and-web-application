package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

import static server.helpers.HtmlHelper.getBytesFromInputStream;

public class NotFoundHandler implements HttpHandler {
    String fileName;

    public NotFoundHandler() {
        this.fileName = "static/not-found.html";
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try(var in = getClass().getResourceAsStream("/" + fileName)) {
            if (in == null) {
                System.out.println("404 file not found");
                exchange.sendResponseHeaders(500, 0);
            } else {
                // get response bytes
                var responseBytes = getBytesFromInputStream(in);
                // send response
                exchange.sendResponseHeaders(404, responseBytes.length);
                OutputStream output = exchange.getResponseBody();
                output.write(responseBytes);
                output.close();
            }
        }
    }
}
