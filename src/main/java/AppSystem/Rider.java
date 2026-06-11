package AppSystem;

/*
 * Rider.java
 * ----------
 * Represents a delivery rider available to take an order.
 *
 * Implements Comparable<Rider> so that Java's PriorityQueue
 * (the min-heap in DeliveryAssignment) knows how to rank riders.
 * The comparison is based on distanceKm — the rider closest to
 * the restaurant has the highest priority (smallest distance = top).
 */
public class Rider implements Comparable<Rider> {

    int    id;
    String name;
    int    distanceKm; // how far the rider is from the restaurant right now

    public Rider(int id, String name, int distanceKm) {
        this.id          = id;
        this.name        = name;
        this.distanceKm  = distanceKm;
    }

    /*
     * Defines the ordering rule for the min-heap.
     * Returning a negative number means "this rider goes before other".
     * We want the smallest distance at the top, so we compare distanceKm.
     */
    @Override
    public int compareTo(Rider other) {
        return Integer.compare(this.distanceKm, other.distanceKm);
    }

    @Override
    public String toString() {
        return name + " (ID:" + id + ", " + distanceKm + "km away)";
    }
}
