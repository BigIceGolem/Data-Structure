package AppSystem;

/*
 * Restaurant.java
 * ---------------
 * Represents a single restaurant in the system.
 * Like Customer, this doubles as a linked list node — the 'next'
 * field lets RestaurantLinkedList chain them together.
 */
public class Restaurant {
    int        id;      // unique identifier
    String     name;
    String     cuisine; // e.g. "Malaysian", "Italian"
    Restaurant next;    // pointer to the next node in the linked list

    public Restaurant(int id, String name, String cuisine) {
        this.id      = id;
        this.name    = name;
        this.cuisine = cuisine;
        this.next    = null;
    }
}
