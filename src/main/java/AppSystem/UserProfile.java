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

    // The unique ID of this customer — used as the key in the HashMap
    int          userId;

    // The customer's name
    String       name;

    // The customer's email address
    String       email;

    // A list of all order IDs placed by this customer, e.g. [1001, 1005, 1009]
    // Grows each time the customer places a new order
    List<Integer> orderHistory;

    // Constructor: creates a new profile for the given customer
    // Order history starts empty — orders are added as the customer places them
    public UserProfile(int userId, String name, String email) {
        this.userId       = userId;
        this.name         = name;
        this.email        = email;
        this.orderHistory = new ArrayList<>(); // no orders yet
    }

    /*
     * Records a new order ID in this customer's history.
     * Called automatically when the customer confirms a new order.
     */
    public void addOrderToHistory(int orderId) {
        orderHistory.add(orderId);
    }

    // Controls how a UserProfile looks when printed
    // Example: "UserProfile{id=1, name=Alice, email=alice@email.com, orders=[1001, 1005]}"
    @Override
    public String toString() {
        return "UserProfile{id=" + userId
                + ", name="   + name
                + ", email="  + email
                + ", orders=" + orderHistory + "}";
    }
}
