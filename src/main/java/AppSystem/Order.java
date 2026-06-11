package AppSystem;

import java.util.ArrayList;
import java.util.List;

public class Order {
    int orderId;
    int customerId;
    List<String> items;

    public Order(int orderId, int customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Order#" + orderId + " [Customer " + customerId + "] Items: " + items;
    }
}
