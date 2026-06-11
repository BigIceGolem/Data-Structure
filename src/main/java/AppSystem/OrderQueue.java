package AppSystem;

import java.util.LinkedList;
import java.util.Queue;

public class OrderQueue {
    private final Queue<Order> queue = new LinkedList<>();

    public void enqueue(Order order) {
        queue.add(order);
        System.out.println("[Order Queue] Enqueued: " + order);
    }

    public Order processNextOrder() {
        if (queue.isEmpty()) {
            System.out.println("[Order Queue] No orders to process.");
            return null;
        }
        Order o = queue.poll();
        System.out.println("[Order Queue] Processing: " + o);
        return o;
    }

    public void displayQueue() {
        System.out.println("[Order Queue] Pending orders: " + queue);
    }
}
