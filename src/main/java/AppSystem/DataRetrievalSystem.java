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
 *     userMap.get(3)    → instantly returns Alice's profile
 *     orderMap.get(1001) → instantly returns Order #1001's details
 *
 * Two HashMaps are used:
 *   userMap  : userId (int)  → UserProfile
 *   orderMap : orderId (int) → OrderRecord
 */
public class DataRetrievalSystem {

    // key = customer ID, value = their profile
    private final HashMap<Integer, UserProfile> userMap  = new HashMap<>();

    // key = order ID, value = the order's full record
    private final HashMap<Integer, OrderRecord> orderMap = new HashMap<>();

    // ----------------------------------------------------------------
    //  USER PROFILE OPERATIONS
    // ----------------------------------------------------------------

    /*
     * Stores a user profile in the HashMap.
     * put() is O(1).
     */
    public void addUserProfile(UserProfile profile) {
        userMap.put(profile.userId, profile);
        System.out.println("[HashMap] User profile stored: " + profile.name);
    }

    /*
     * Retrieves a user profile by ID.
     * get() is O(1) — no looping needed.
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
     * Creates and stores an OrderRecord when an order is confirmed.
     * Also links the order ID to the customer's order history.
     */
    public void storeOrder(Order order) {
        OrderRecord rec = new OrderRecord(order.orderId, order.customerId, order.items);
        orderMap.put(order.orderId, rec);

        // add this order to the customer's history in the userMap
        UserProfile p = userMap.get(order.customerId);
        if (p != null) p.addOrderToHistory(order.orderId);

        System.out.println("[HashMap] Order record stored: " + rec);
    }

    /*
     * Retrieves an order record by order ID — O(1).
     */
    public OrderRecord getOrder(int orderId) {
        OrderRecord rec = orderMap.get(orderId);
        if (rec == null) System.out.println("[HashMap] Order " + orderId + " not found.");
        else             System.out.println("[HashMap] Retrieved: " + rec);
        return rec;
    }

    /*
     * Updates the status field of an existing order (e.g. "Preparing",
     * "Assigned to Hafiz", "Delivered"). The record is already in the
     * map so we just update the field directly — O(1).
     */
    public void updateOrderStatus(int orderId, String status) {
        OrderRecord rec = orderMap.get(orderId);
        if (rec != null) {
            rec.status = status;
            System.out.println("[HashMap] Order " + orderId + " status → " + status);
        } else {
            System.out.println("[HashMap] Order " + orderId + " not found.");
        }
    }

    /*
     * Returns all order records as a list for CSV saving.
     */
    public List<OrderRecord> getAllOrders() {
        return new ArrayList<>(orderMap.values());
    }

    /*
     * Directly loads a saved OrderRecord back into the map on startup.
     * Also re-links the order to the customer's history if their profile
     * is already loaded.
     */
    public void loadOrderRecord(OrderRecord rec) {
        orderMap.put(rec.orderId, rec);
        UserProfile p = userMap.get(rec.customerId);
        if (p != null && !p.orderHistory.contains(rec.orderId))
            p.addOrderToHistory(rec.orderId);
    }
}
