package models;

public class Session {
    int id;
    String cartItemsId;
    String sessionId;
    boolean isAdmin;
    boolean isDeleted;

    public Session(int id, String cartItemsId, String sessionId) {
        this.id = id;
        this.cartItemsId = cartItemsId;
        this.sessionId = sessionId;
    }

    public Session(String cartItemsId, String sessionId) {
        this.cartItemsId = cartItemsId;
        this.sessionId = sessionId;
    }

    public Session(int id, String cartItemsId, String sessionId, boolean isAdmin, boolean isDeleted) {
        this.id = id;
        this.cartItemsId = cartItemsId;
        this.sessionId = sessionId;
        this.isAdmin = isAdmin;
        this.isDeleted = isDeleted;
    }

    public int getId() {
        return id;
    }

    public String getCartItemsId() {
        return cartItemsId;
    }

    public void setCartItemsId(String cartItemsId) {
        this.cartItemsId = cartItemsId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
