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

    // The stack that holds the items the customer has added so far
    // The top of the stack is always the most recently added item
    private final Stack<String> itemStack = new Stack<>();

    // Remembers which customer this cart belongs to
    private final int customerId;

    // Constructor: creates a new empty cart for a specific customer
    public OrderCart(int customerId) {
        this.customerId = customerId;
    }

    /*
     * Adds a food item to the top of the stack.
     * push() is O(1) — it just puts the item on top.
     */
    public void addItem(String item) {
        itemStack.push(item); // place item on top of the stack
        System.out.println("  [Cart] Added: " + item);
    }

    /*
     * Removes the most recently added item (the one on top of the stack).
     * This is the "undo" feature — O(1).
     * If the cart is empty, we just show a message and do nothing.
     */
    public void undoLastItem() {
        if (itemStack.isEmpty()) {
            System.out.println("  [Cart] Nothing to undo.");
        } else {
            String removed = itemStack.pop(); // remove and return the top item
            System.out.println("  [Cart Undo] Removed: " + removed);
        }
    }

    /*
     * Finalises the order.
     * Copies all items from the cart into a new Order object,
     * then empties the cart so it's ready for the next customer.
     */
    public Order confirmOrder(int orderId) {
        Order order = new Order(orderId, customerId);
        order.items.addAll(itemStack); // copy every item from the stack into the order
        itemStack.clear();             // empty the cart — it's been confirmed
        System.out.println("  [Cart] Order confirmed: " + order);
        return order;
    }

    /*
     * Shows all items currently in the cart without removing anything.
     * Useful so the customer can review before confirming.
     */
    public void displayCart() {
        System.out.println("  [Cart] Current items: " + itemStack);
    }
}
