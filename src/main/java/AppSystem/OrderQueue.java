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

    // The queue that holds all confirmed orders waiting to be processed
    // LinkedList is used here because it efficiently supports adding at the
    // back and removing from the front — exactly what a queue needs
    private final Queue<Order> queue = new LinkedList<>();

    /*
     * Adds a confirmed order to the BACK of the queue.
     * This is O(1) — adding to the end of a LinkedList is instant.
     */
    public void enqueue(Order order) {
        queue.add(order); // place the new order at the back of the line
        System.out.println("[Order Queue] Enqueued: " + order);
    }

    /*
     * Takes the FIRST order out of the queue and returns it for processing.
     * This is FIFO — whoever ordered first gets processed first.
     * poll() is O(1) — removing from the front of a LinkedList is instant.
     * Returns null if the queue is empty.
     */
    public Order processNextOrder() {
        if (queue.isEmpty()) {
            System.out.println("[Order Queue] No orders to process.");
            return null;
        }
        Order o = queue.poll(); // remove and return the order at the front
        System.out.println("[Order Queue] Processing: " + o);
        return o;
    }

    /*
     * Prints all orders currently waiting in the queue.
     * Does NOT remove any orders — just shows what's pending.
     */
    public void displayQueue() {
        if (queue.isEmpty()) {
            System.out.println("[Order Queue] No pending orders.");
        } else {
            System.out.println("[Order Queue] Pending orders: " + queue);
        }
    }
}
