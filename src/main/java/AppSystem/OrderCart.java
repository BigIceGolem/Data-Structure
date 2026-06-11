package AppSystem;

import java.util.Stack;

public class OrderCart {
    private final Stack<String> itemStack = new Stack<>();
    private final int customerId;

    public OrderCart(int customerId) {
        this.customerId = customerId;
    }

    public void addItem(String item) {
        itemStack.push(item);
        System.out.println("  [Cart] Added: " + item);
    }

    public void undoLastItem() {
        if (itemStack.isEmpty()) {
            System.out.println("  [Cart] Nothing to undo.");
        } else {
            String removed = itemStack.pop();
            System.out.println("  [Cart Undo] Removed: " + removed);
        }
    }

    public Order confirmOrder(int orderId) {
        Order order = new Order(orderId, customerId);
        order.items.addAll(itemStack);
        itemStack.clear();
        System.out.println("  [Cart] Order confirmed: " + order);
        return order;
    }

    public void displayCart() {
        System.out.println("  [Cart] Current items: " + itemStack);
    }
}
