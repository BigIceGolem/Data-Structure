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

    // A unique ID number for this rider (starts at 201)
    int    id;

    // The rider's name, e.g. "Hafiz"
    String name;

    // How many kilometres the rider is currently from the restaurant
    // A smaller number means the rider is closer and will be picked first
    int    distanceKm;

    // Constructor: creates a new rider with the given details
    public Rider(int id, String name, int distanceKm) {
        this.id          = id;
        this.name        = name;
        this.distanceKm  = distanceKm;
    }

    /*
     * compareTo() tells the PriorityQueue (min-heap) how to rank two riders.
     * We compare by distanceKm — the rider with the SMALLER distance wins.
     *
     * Java's PriorityQueue automatically puts the "smallest" element at the top.
     * Because we compare by distanceKm, the closest rider is always at the top.
     *
     * Integer.compare(a, b) returns:
     *   negative if a < b  → this rider goes before the other (closer)
     *   zero     if a == b → same distance, order doesn't matter
     *   positive if a > b  → the other rider goes first (they're closer)
     */
    @Override
    public int compareTo(Rider other) {
        return Integer.compare(this.distanceKm, other.distanceKm);
    }

    // Controls how a Rider looks when printed, e.g. "Hafiz (ID:201, 3km away)"
    @Override
    public String toString() {
        return name + " (ID:" + id + ", " + distanceKm + "km away)";
    }
}
