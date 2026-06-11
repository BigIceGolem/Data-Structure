package AppSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/*
 * DeliveryAssignment.java
 * -----------------------
 * Manages available riders using a Min-Heap (Priority Queue).
 *
 * Why a Min-Heap?
 *   We always want to assign the nearest rider automatically.
 *   In a min-heap, the smallest element (shortest distance) is always
 *   at the top, so finding the best rider is O(1) and removing it
 *   is O(log n) — much better than scanning a list which is O(n).
 *
 *   Comparison:
 *     Linear search (array/list) : O(n) — checks every rider
 *     Min-Heap (priority queue)  : O(log n) — heap adjusts itself
 *
 * Java's PriorityQueue is a min-heap by default.
 * It uses the compareTo() method in Rider to decide the order.
 */
public class DeliveryAssignment {

    // the heap keeps the nearest rider at the top automatically
    private final PriorityQueue<Rider> minHeap = new PriorityQueue<>();

    /*
     * Adds a rider to the heap. The heap re-arranges itself to keep
     * the nearest rider at the top — O(log n).
     */
    public void addRider(Rider r) {
        minHeap.offer(r);
        System.out.println("[Riders] Added: " + r);
    }

    /*
     * Removes and returns the nearest rider (top of the heap).
     * The heap automatically promotes the next-nearest rider to the top.
     * O(log n).
     */
    public Rider assignBestRider() {
        if (minHeap.isEmpty()) {
            System.out.println("[Riders] No riders available.");
            return null;
        }
        Rider best = minHeap.poll(); // always the nearest rider
        System.out.println("[Riders] Assigned best rider: " + best);
        return best;
    }

    /*
     * Shows all riders currently in the heap.
     * Note: the printed order is internal heap order, not sorted —
     * only the first element is guaranteed to be the minimum.
     */
    public void displayRiders() {
        if (minHeap.isEmpty()) {
            System.out.println("[Riders] No riders available.");
        } else {
            System.out.println("[Riders] Available riders: " + minHeap);
        }
    }

    /*
     * Returns all riders as a list for CSV saving.
     */
    public List<Rider> toList() {
        return new ArrayList<>(minHeap);
    }
}
