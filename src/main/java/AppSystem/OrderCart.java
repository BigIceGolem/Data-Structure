package AppSystem;

import java.util.Stack;

/*
 * OrderCart.java
 * --------------
 * Represents a customer's shopping cart before they confirm their order.
 * Uses a Stack (LIFO — Last In, First Out) to store items.
 *
 * Why a Stack?
 *   Because we need an "undo last item" feature. With a stack,
 *   the most recently added item is always on top, so removing it
 *   (pop) is instant — O(1). This is exactly what LIFO means.
 *
 * How it works:
 *   addItem("Burger")    → stack: [Burger]
 *   addItem("Teh Tarik") → stack: [Burger, Teh Tarik]
 *   undoLastItem()       → removes Teh Tarik → stack: [Burger]
 *   confirmOrder()       → creates an Order from whatever is left
 */
public class OrderCart {

    // the stack holds items the customer has added but not yet confirmed
    private final Stack<String> itemStack = new Stack<>();
    private final int customerId;

    public OrderCart(int customerId) {
        this.customerId = customerId;
    }

    /*
     * Pushes a new item onto the top of the stack.
     * O(1) operation.
     */
    public void addItem(String item) {
        itemStack.push(item);
        System.out.println("  [Cart] Added: " + item);
    }

    /*
     * Removes the most recently added item (top of the stack).
     * This is the undo feature — O(1).
     */
    public void undoLastItem() {
        if (itemStack.isEmpty()) {
            System.out.println("  [Cart] Nothing to undo.");
        } else {
            String removed = itemStack.pop();
            System.out.println("  [Cart Undo] Removed: " + removed);
        }
    }

    /*
     * Locks in the order. Copies all items from the stack into
     * a new Order object, then clears the cart.
     */
    public Order confirmOrder(int orderId) {
        Order order = new Order(orderId, customerId);
        order.items.addAll(itemStack); // copy stack contents into order
        itemStack.clear();             // empty the cart
        System.out.println("  [Cart] Order confirmed: " + order);
        return order;
    }

    /*
     * Shows everything currently in the cart without removing anything.
     */
    public void displayCart() {
        System.out.println("  [Cart] Current items: " + itemStack);
    }
}
