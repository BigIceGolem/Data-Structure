package AppSystem;

import java.util.ArrayList;
import java.util.List;

/*
 * RestaurantLinkedList.java
 * -------------------------
 * A singly linked list that stores Restaurant objects.
 * Works exactly the same way as CustomerLinkedList.
 *
 * Why a linked list?
 *   Same reasons as CustomerLinkedList: O(1) insertion at the head,
 *   no wasted array slots, and easy pointer-based deletion.
 *
 * Structure:  head -> [R1] -> [R2] -> [R3] -> null
 */
public class RestaurantLinkedList {

    // head points to the first restaurant in the chain
    // null means the list is empty
    private Restaurant head;

    /*
     * Inserts a new restaurant at the FRONT of the list.
     * This is O(1) — no need to walk to the end.
     *
     * Before: head -> [R1] -> [R2]
     * After:  head -> [newRest] -> [R1] -> [R2]
     */
    public void addRestaurant(int id, String name, String cuisine) {
        Restaurant r = new Restaurant(id, name, cuisine);
        r.next = head; // the new restaurant points to whoever used to be first
        head   = r;    // the new restaurant is now first in the list
        System.out.println("[Restaurant Added] " + name
                + " (ID: " + id + ", Cuisine: " + cuisine + ")");
    }

    /*
     * Removes the restaurant with the matching ID.
     * Walks the list to find the node just before the target,
     * then relinks the pointer to skip over it.
     */
    public void deleteRestaurant(int id) {
        // Can't delete from an empty list
        if (head == null) {
            System.out.println("[Error] Restaurant list is empty.");
            return;
        }

        // Special case: the restaurant to delete is the very first one
        if (head.id == id) {
            System.out.println("[Restaurant Removed] " + head.name);
            head = head.next; // move head forward — old first restaurant is gone
            return;
        }

        // Walk until we find the node just before the one we want to remove
        Restaurant curr = head;
        while (curr.next != null && curr.next.id != id) {
            curr = curr.next;
        }

        if (curr.next == null) {
            // Reached the end without finding the ID
            System.out.println("[Error] Restaurant ID " + id + " not found.");
        } else {
            // Skip over the restaurant we want to delete
            System.out.println("[Restaurant Removed] " + curr.next.name);
            curr.next = curr.next.next;
        }
    }

    /*
     * Prints all restaurants by walking through the list from head to null.
     */
    public void displayRestaurants() {
        if (head == null) {
            System.out.println("[Info] No restaurants registered.");
            return;
        }
        System.out.println("--- Restaurant List ---");
        Restaurant curr = head;
        while (curr != null) {
            System.out.println("  ID: " + curr.id
                    + " | Name: "    + curr.name
                    + " | Cuisine: " + curr.cuisine);
            curr = curr.next; // move to the next restaurant
        }
    }

    /*
     * Converts the linked list into a regular ArrayList.
     * Used by FileManager when saving restaurant data to a CSV file.
     */
    public List<Restaurant> toList() {
        List<Restaurant> list = new ArrayList<>();
        Restaurant curr = head;
        while (curr != null) {
            list.add(curr);   // add this restaurant to the regular list
            curr = curr.next; // move to the next one
        }
        return list;
    }
}
