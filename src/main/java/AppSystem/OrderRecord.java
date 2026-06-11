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
    int          orderId;
    int          customerId;
    List<String> items;
    String       status; // tracks the current stage of the order

    public OrderRecord(int orderId, int customerId, List<String> items) {
        this.orderId    = orderId;
        this.customerId = customerId;
        this.items      = new ArrayList<>(items); // copy so changes don't affect the original
        this.status     = "Pending";              // every order starts as Pending
    }

    @Override
    public String toString() {
        return "OrderRecord{id=" + orderId
                + ", customer=" + customerId
                + ", items="    + items
                + ", status="   + status + "}";
    }
}
