package AppSystem;

import java.util.ArrayList;
import java.util.List;

public class OrderRecord {
    int orderId;
    int customerId;
    List<String> items;
    String status;

    public OrderRecord(int orderId, int customerId, List<String> items) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.status = "Pending";
    }

    @Override
    public String toString() {
        return "OrderRecord{id=" + orderId + ", customer=" + customerId
                + ", items=" + items + ", status=" + status + "}";
    }
}
