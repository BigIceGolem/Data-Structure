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

    // A unique number to identify this order (starts at 1001)
    int          orderId;

    // The ID of the customer who placed this order
    // We use this to look up their profile and order history
    int          customerId;

    // A list of food item names that were ordered, e.g. ["Whopper Meal", "Onion Rings"]
    List<String> items;

    // Constructor: creates a new empty order for a given customer
    // Items are added later using order.items.add(...)
    public Order(int orderId, int customerId) {
        this.orderId    = orderId;
        this.customerId = customerId;
        this.items      = new ArrayList<>(); // start with an empty list of items
    }

    // Controls how an Order looks when printed
    // Example output: "Order#1001 [Customer 1] Items: [Whopper Meal, Onion Rings]"
    @Override
    public String toString() {
        return "Order#" + orderId
                + " [Customer " + customerId + "]"
                + " Items: "    + items;
    }
}
