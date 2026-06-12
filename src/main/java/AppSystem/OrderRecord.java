package AppSystem;

import java.util.ArrayList;
import java.util.List;

/*
 * OrderRecord.java
 * ----------------
 * A permanent record of a placed order, stored in the HashMap
 * inside DataRetrievalSystem.
 *
 * Unlike Order.java (which is a temporary object used in the queue),
 * OrderRecord is the long-term stored version that tracks status
 * changes (Pending → Preparing → Assigned to Rider → Delivered).
 */
public class OrderRecord {

    // The unique ID of this order
    int          orderId;

    // The ID of the customer who placed the order
    int          customerId;

    // The list of food item names included in this order
    List<String> items;

    // Tracks where the order currently is in the process
    // Starts as "Pending" and gets updated as the order progresses
    String       status;

    // Constructor: creates a new permanent order record from an existing order
    public OrderRecord(int orderId, int customerId, List<String> items) {
        this.orderId    = orderId;
        this.customerId = customerId;
        // We copy the items list so changes to the original Order don't affect this record
        this.items      = new ArrayList<>(items);
        // Every order starts in the "Pending" state
        this.status     = "Pending";
    }

    // Controls how an OrderRecord looks when printed
    // Example: "OrderRecord{id=1001, customer=1, items=[Whopper Meal], status=Pending}"
    @Override
    public String toString() {
        return "OrderRecord{id=" + orderId
                + ", customer=" + customerId
                + ", items="    + items
                + ", status="   + status + "}";
    }
}
