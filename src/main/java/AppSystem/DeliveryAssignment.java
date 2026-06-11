package AppSystem;

import java.util.PriorityQueue;

public class DeliveryAssignment {
    private final PriorityQueue<Rider> minHeap = new PriorityQueue<>();

    public void addRider(Rider r) {
        minHeap.offer(r);
        System.out.println("[Riders] Added: " + r);
    }

    public Rider assignBestRider() {
        if (minHeap.isEmpty()) {
            System.out.println("[Riders] No riders available.");
            return null;
        }
        Rider best = minHeap.poll();
        System.out.println("[Riders] Assigned best rider: " + best);
        return best;
    }

    public void displayRiders() {
        System.out.println("[Riders] Available (heap order): " + minHeap);
    }

    // returns all riders currently in the heap (used for CSV saving)
    public java.util.List<Rider> toList() {
        return new java.util.ArrayList<>(minHeap);
    }
}
