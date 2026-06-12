package AppSystem;

/*
 * Restaurant.java
 * ---------------
 * Represents a single restaurant in the system.
 * Like Customer, this doubles as a linked list node — the 'next'
 * field lets RestaurantLinkedList chain them together.
 */
public class Restaurant {

    // A unique number for this restaurant (starts at 101)
    int        id;

    // The restaurant's display name, e.g. "Burger King"
    String     name;

    // The type of food served, e.g. "Malaysian", "Italian"
    String     cuisine;

    // Pointer to the next restaurant in the linked list
    // null means this is the last restaurant in the chain
    Restaurant next;

    // Constructor: creates a new restaurant with the given details
    public Restaurant(int id, String name, String cuisine) {
        this.id      = id;
        this.name    = name;
        this.cuisine = cuisine;
        this.next    = null; // not linked to any other restaurant yet
    }
}
