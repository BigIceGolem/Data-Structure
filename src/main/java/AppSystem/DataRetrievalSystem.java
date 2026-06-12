package AppSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * DataRetrievalSystem.java
 * ------------------------
 * Provides fast access to user profiles and order records using HashMaps.
 *
 * Why a HashMap?
 *   A HashMap stores data as key-value pairs. Looking up a value by its
 *   key is O(1) — constant time — regardless of how many records exist.
 *   This is much faster than searching a list (O(n)) or a BST (O(log n)).
 *
 *   Example:
 *     userMap.get(3)     → instantly returns Alice's profile
 *     orderMap.get(1001) → instantly returns Order #1001's details
 *
 * Two HashMaps are used:
 *   userMap  : userId (int)  → UserProfile
 *   orderMap : orderId (int) → OrderRecord
 */
public class DataRetrievalSystem {

    // Stores user profiles: the customer's ID is the key, their profile is the value
    // Example: userMap.get(1) gives you Alice's profile immediately
    private final HashMap<Integer, UserProfile> userMap  = new HashMap<>();

    // Stores order records: the order ID is the key, the full order record is the value
    // Example: orderMap.get(1001) gives you Order #1001 immediately
    private final HashMap<Integer, OrderRecord> orderMap = new HashMap<>();

    // ----------------------------------------------------------------
    //  USER PROFILE OPERATIONS
    // ----------------------------------------------------------------

    /*
     * Saves a user profile into the HashMap.
     * put() stores the key-value pair and is O(1).
     */
    public void addUserProfile(UserProfile profile) {
        userMap.put(profile.userId, profile); // key = userId, value = the profile object
        System.out.println("[HashMap] User profile stored: " + profile.name);
    }

    /*
     * Retrieves a user profile using their ID.
     * get() finds the value by key and is O(1) — no looping needed.
     * Returns null if no profile exists for that ID.
     */
    public UserProfile getUserProfile(int userId) {
        UserProfile p = userMap.get(userId);
        if (p == null) System.out.println("[HashMap] User " + userId + " not found.");
        else           System.out.println("[HashMap] Retrieved: " + p);
        return p;
    }

    // ----------------------------------------------------------------
    //  ORDER RECORD OPERATIONS
    // ----------------------------------------------------------------

    /*
     * Creates a permanent OrderRecord from a confirmed Order and saves it to the map.
     * Also links the order to the customer's order history in their user profile.
     */
    public void storeOrder(Order order) {
        // Create the record that will be stored long-term
        OrderRecord rec = new OrderRecord(order.orderId, order.customerId, order.items);
        orderMap.put(order.orderId, rec); // save to the HashMap

        // Find the customer's profile and add this order to their history
        UserProfile p = userMap.get(order.customerId);
        if (p != null) p.addOrderToHistory(order.orderId);

        System.out.println("[HashMap] Order record stored: " + rec);
    }

    /*
     * Retrieves a full order record using the order ID.
     * O(1) lookup — no need to search through a list.
     * Returns null if the order doesn't exist.
     */
    public OrderRecord getOrder(int orderId) {
        OrderRecord rec = orderMap.get(orderId);
        if (rec == null) System.out.println("[HashMap] Order " + orderId + " not found.");
        else             System.out.println("[HashMap] Retrieved: " + rec);
        return rec;
    }

    /*
     * Updates the status field of an existing order.
     * Examples of status values: "Preparing", "Assigned to Hafiz", "Delivered".
     * Since the record is already in the HashMap, we just look it up and change the field.
     * O(1).
     */
    public void updateOrderStatus(int orderId, String status) {
        OrderRecord rec = orderMap.get(orderId);
        if (rec != null) {
            rec.status = status; // update the status directly on the stored object
            System.out.println("[HashMap] Order " + orderId + " status → " + status);
        } else {
            System.out.println("[HashMap] Order " + orderId + " not found.");
        }
    }

    /*
     * Returns all stored order records as a list.
     * Used by FileManager when saving all orders to a CSV file.
     */
    public List<OrderRecord> getAllOrders() {
        return new ArrayList<>(orderMap.values()); // collect all values from the map
    }

    /*
     * Loads a saved OrderRecord back into the map when the app starts.
     * Also re-links the order to the customer's history if their profile is already loaded.
     * This is how we restore previously saved data from the CSV file.
     */
    public void loadOrderRecord(OrderRecord rec) {
        orderMap.put(rec.orderId, rec); // put it back into the map

        // If we already have this customer's profile, add the order to their history
        UserProfile p = userMap.get(rec.customerId);
        if (p != null && !p.orderHistory.contains(rec.orderId))
            p.addOrderToHistory(rec.orderId);
    }
}
