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

    // The min-heap that holds all available riders
    // The rider with the SMALLEST distanceKm is always at the top
    private final PriorityQueue<Rider> minHeap = new PriorityQueue<>();

    /*
     * Adds a rider to the heap.
     * The heap automatically rearranges itself so the nearest rider stays on top.
     * Adding is O(log n) because the heap may need to "bubble up" the new rider.
     */
    public void addRider(Rider r) {
        minHeap.offer(r); // add the rider to the heap
        System.out.println("[Riders] Added: " + r);
    }

    /*
     * Removes and returns the nearest rider (the one at the top of the heap).
     * After removal, the heap automatically promotes the next-nearest rider to the top.
     * This is O(log n).
     *
     * Returns null if there are no riders available.
     */
    public Rider assignBestRider() {
        if (minHeap.isEmpty()) {
            System.out.println("[Riders] No riders available.");
            return null;
        }
        // poll() removes and returns the top item — always the nearest rider
        Rider best = minHeap.poll();
        System.out.println("[Riders] Assigned best rider: " + best);
        return best;
    }

    /*
     * Shows all riders currently in the heap.
     * Note: the printed order reflects the heap's internal structure, not a sorted list.
     * Only the very first item printed is guaranteed to be the nearest rider.
     */
    public void displayRiders() {
        if (minHeap.isEmpty()) {
            System.out.println("[Riders] No riders available.");
        } else {
            System.out.println("[Riders] Available riders: " + minHeap);
        }
    }

    /*
     * Converts the heap into a plain ArrayList.
     * Used by FileManager when saving rider data to a CSV file.
     */
    public List<Rider> toList() {
        return new ArrayList<>(minHeap);
    }
}
