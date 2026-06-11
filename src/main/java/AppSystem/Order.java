package AppSystem;

import java.util.ArrayList;
import java.util.List;

/*
 * Order.java
 * ----------
 * Represents a confirmed customer order that has been placed into
 * the order queue. It is created by OrderCart once the customer
 * finishes adding items and hits "confirm".
 *
 * Fields:
 *   orderId    — unique number assigned when the order is created
 *   customerId — links back to the customer who placed the order
 *   items      — list of food item names in this order
 */
public class Order {
    int          orderId;
    int          customerId;
    List<String> items;

    public Order(int orderId, int customerId) {
        this.orderId    = orderId;
        this.customerId = customerId;
        this.items      = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Order#" + orderId
                + " [Customer " + customerId + "]"
                + " Items: "    + items;
    }
}
