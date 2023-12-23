package server.helpers;

import com.sun.net.httpserver.HttpExchange;
import models.User;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Base64;

/**
 * This class contains helper methods for the handlers.
 * The methods are static, so they can be called without instantiating the class.
 * The methods are public, so they can be called from other classes.
 * The methods are: sendResponse, VerifyUserIsAdmin, IsLogin
 */
public class HandlerHelpers {
    private static final User admin = new User("admin", "admin");

    /**
     * This method sends a response to the client by writing an output stream the response string.
     * @param exchange HttpExchange object
     * @param response String containing the response to send to the client
     * @throws IOException Exception thrown if there is an error sending the response
     */
    public static void SendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        OutputStream output = exchange.getResponseBody();
        output.write(response.getBytes());
        output.close();
    }

    /**
     * This method verifies that the user is an admin.
     * @param exchange HttpExchange object
     * @return boolean true if user is admin, false if not
     */
    public static boolean VerifyUserIsAdmin(HttpExchange exchange) {
        // get cookie
        String cookie = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookie != null) {
            String[] split = cookie.split(";");
            var tokenCookie = Arrays.stream(split).filter(s -> s.contains("token")).findFirst().orElse(null);
            if(tokenCookie == null) {
                return false;
            }
            String token = tokenCookie.split("=")[1];
            var decodedTokenBase64 = Base64.getDecoder().decode(token);
            String decodedToken = new String(decodedTokenBase64);
            String[] split2 = decodedToken.split(":");
            String username = split2[0];
            String password = split2[1];
            return username.equals(admin.username)
                    && password.equals(admin.password);
        }
        return false;
    }

    /**
     * This method generates a session id using java.util.UUID.randomUUID().
     * @return String containing the session id
     */
    public static String GenerateSessionId() {
        return java.util.UUID.randomUUID().toString();
    }

}
