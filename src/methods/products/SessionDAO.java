package methods.products;

import database.Migration;
import models.Session;

import java.sql.SQLException;

public class SessionDAO {
    private final Migration migration;

    public SessionDAO(Migration migration) {
        this.migration = migration;
    }

    public Session createSession(String sessionId) {
        var con = migration.getDbConnection();
        try {
            var sql = "INSERT INTO sessions (cart_items_ids, session_id, is_admin, is_deleted) VALUES (?, ?, ?, ?)";
            var preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, "");
            preparedStatement.setString(2, sessionId);
            preparedStatement.setBoolean(3, false);
            preparedStatement.setBoolean(4, false);
            preparedStatement.executeUpdate();
            return getSessionBySessionId(sessionId);
        } catch (SQLException e) {
            System.out.printf("Error creating session %s%n", e.getMessage());
            return null;
        }
    }
    public Session getSessionByID(int id) {
        try {
            var connection = migration.getDbConnection();
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery("SELECT * FROM sessions WHERE id = " + id);
            if (resultSet.next()) {
                var sessionId = resultSet.getString("session_id");
                var cartItemsId = resultSet.getString("cart_items_ids");
                var isAdmin = resultSet.getBoolean("is_admin");
                var isDeleted = resultSet.getBoolean("is_deleted");
                connection.close();
                return new Session(id, cartItemsId, sessionId, isAdmin, isDeleted);
            }
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Session getSessionBySessionId(String sessionId) {
        try {
            var connection = migration.getDbConnection();
            var sql = "SELECT * FROM sessions WHERE session_id = ?";
            var preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, sessionId);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                var id = resultSet.getInt("id");
                var cartItemsId = resultSet.getString("cart_items_ids");
                var isAdmin = resultSet.getBoolean("is_admin");
                var isDeleted = resultSet.getBoolean("is_deleted");
                connection.close();
                return new Session(id, cartItemsId, sessionId, isAdmin, isDeleted);
            }
            connection.close();
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String getCartItemsIdBySessionId(String sessionId) {
        try {
            var connection = migration.getDbConnection();
            var sql = "SELECT cart_items_ids FROM sessions WHERE session_id = ?";
            var preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, sessionId);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                var cartItemsId = resultSet.getString("cart_items_ids");
                connection.close();
                return cartItemsId;
            }
            connection.close();
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String addItemCart(String sessionId, String cartItemsId, int quantity) {
        var con = migration.getDbConnection();
        try {
            var session = getSessionBySessionId(sessionId);
            if (session == null) {
                return null;
            }
            var cartItems = session.getCartItemsId();
            cartItems += "," + cartItemsId + ":" + quantity;
            var sql = "UPDATE sessions SET cart_items_ids = ? WHERE session_id = ?";
            var preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, cartItems);
            preparedStatement.setString(2, sessionId);
            preparedStatement.executeUpdate();
            con.close();
            return cartItems;
        } catch (SQLException e) {
            System.out.printf("Error adding product cart %s%n", e.getMessage());
            return null;
        }
    }

    public void updateSessionCartItem(String sessionId, String cartItemsIds) {
        var con = migration.getDbConnection();
        try {
            var sql = "UPDATE sessions SET cart_items_ids = ? WHERE session_id = ?";
            var preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, cartItemsIds);
            preparedStatement.setString(2, sessionId);
            preparedStatement.executeUpdate();
            con.close();
            getSessionBySessionId(sessionId);
        } catch (SQLException e) {
            System.out.printf("Error updating session cart %s%n", e.getMessage());
        }
    }

}
