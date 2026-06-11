package AppSystem;

import java.util.HashMap;

public class DataRetrievalSystem {
    private final HashMap<Integer, UserProfile>  userMap  = new HashMap<>();
    private final HashMap<Integer, OrderRecord>  orderMap = new HashMap<>();

    public void addUserProfile(UserProfile profile) {
        userMap.put(profile.userId, profile);
        System.out.println("[HashMap] User profile stored: " + profile.name);
    }

    public UserProfile getUserProfile(int userId) {
        UserProfile p = userMap.get(userId);
        if (p == null) System.out.println("[HashMap] User " + userId + " not found.");
        else           System.out.println("[HashMap] Retrieved: " + p);
        return p;
    }

    public void storeOrder(Order order) {
        OrderRecord rec = new OrderRecord(order.orderId, order.customerId, order.items);
        orderMap.put(order.orderId, rec);

        UserProfile p = userMap.get(order.customerId);
        if (p != null) p.addOrderToHistory(order.orderId);

        System.out.println("[HashMap] Order record stored: " + rec);
    }

    public OrderRecord getOrder(int orderId) {
        OrderRecord rec = orderMap.get(orderId);
        if (rec == null) System.out.println("[HashMap] Order " + orderId + " not found.");
        else             System.out.println("[HashMap] Retrieved: " + rec);
        return rec;
    }

    public void updateOrderStatus(int orderId, String status) {
        OrderRecord rec = orderMap.get(orderId);
        if (rec != null) {
            rec.status = status;
            System.out.println("[HashMap] Order " + orderId + " status updated to: " + status);
        }
    }

    // returns all order records (used for CSV saving)
    public java.util.List<OrderRecord> getAllOrders() {
        return new java.util.ArrayList<>(orderMap.values());
    }

    // reload an order record directly (used during CSV loading)
    public void loadOrderRecord(OrderRecord rec) {
        orderMap.put(rec.orderId, rec);
        UserProfile p = userMap.get(rec.customerId);
        if (p != null && !p.orderHistory.contains(rec.orderId))
            p.addOrderToHistory(rec.orderId);
    }
}
