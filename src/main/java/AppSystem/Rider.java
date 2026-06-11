package AppSystem;

public class Rider implements Comparable<Rider> {
    int id;
    String name;
    int distanceKm;

    public Rider(int id, String name, int distanceKm) {
        this.id = id;
        this.name = name;
        this.distanceKm = distanceKm;
    }

    @Override
    public int compareTo(Rider other) {
        return Integer.compare(this.distanceKm, other.distanceKm);
    }

    @Override
    public String toString() {
        return name + " (ID:" + id + ", " + distanceKm + "km away)";
    }
}
