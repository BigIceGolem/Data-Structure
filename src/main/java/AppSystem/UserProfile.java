package AppSystem;

import java.util.ArrayList;
import java.util.List;

/*
 * UserProfile.java
 * ----------------
 * Stores a customer's profile information.
 * Kept inside the HashMap in DataRetrievalSystem, keyed by userId,
 * so it can be retrieved in O(1) time.
 *
 * orderHistory tracks all order IDs this customer has ever placed,
 * which lets us quickly show a customer's full order history.
 */
public class UserProfile {
    int          userId;
    String       name;
    String       email;
    List<Integer> orderHistory; // list of order IDs placed by this customer

    public UserProfile(int userId, String name, String email) {
        this.userId       = userId;
        this.name         = name;
        this.email        = email;
        this.orderHistory = new ArrayList<>();
    }

    /*
     * Links a new order ID to this customer's history.
     * Called automatically when an order is confirmed.
     */
    public void addOrderToHistory(int orderId) {
        orderHistory.add(orderId);
    }

    @Override
    public String toString() {
        return "UserProfile{id=" + userId
                + ", name="   + name
                + ", email="  + email
                + ", orders=" + orderHistory + "}";
    }
}
