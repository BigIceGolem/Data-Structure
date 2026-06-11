package AppSystem;

import java.util.LinkedList;
import java.util.Queue;

/*
 * OrderQueue.java
 * ---------------
 * Manages the queue of confirmed orders waiting to be prepared.
 * Uses a Queue (FIFO — First In, First Out) so that orders are
 * always processed in the exact order they were placed.
 *
 * Why a Queue?
 *   It's fair — the first customer to order gets served first.
 *   This mirrors how a real restaurant ticket system works.
 *
 * How it works:
 *   enqueue(order1) → queue: [order1]
 *   enqueue(order2) → queue: [order1, order2]
 *   processNext()   → returns order1 → queue: [order2]
 */
public class OrderQueue {

    // LinkedList is used as the underlying structure for the queue
    private final Queue<Order> queue = new LinkedList<>();

    /*
     * Adds a new order to the back of the queue — O(1).
     */
    public void enqueue(Order order) {
        queue.add(order);
        System.out.println("[Order Queue] Enqueued: " + order);
    }

    /*
     * Removes and returns the order at the front of the queue — O(1).
     * This is the FIFO behaviour: whoever ordered first gets processed first.
     */
    public Order processNextOrder() {
        if (queue.isEmpty()) {
            System.out.println("[Order Queue] No orders to process.");
            return null;
        }
        Order o = queue.poll(); // removes from the front
        System.out.println("[Order Queue] Processing: " + o);
        return o;
    }

    /*
     * Shows all pending orders without removing any of them.
     */
    public void displayQueue() {
        if (queue.isEmpty()) {
            System.out.println("[Order Queue] No pending orders.");
        } else {
            System.out.println("[Order Queue] Pending orders: " + queue);
        }
    }
}
