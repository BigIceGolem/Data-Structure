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

    private Restaurant head; // first restaurant in the chain

    /*
     * Inserts a new restaurant at the front of the list — O(1).
     */
    public void addRestaurant(int id, String name, String cuisine) {
        Restaurant r = new Restaurant(id, name, cuisine);
        r.next = head; // new node points to old head
        head   = r;    // head now points to new node
        System.out.println("[Restaurant Added] " + name
                + " (ID: " + id + ", Cuisine: " + cuisine + ")");
    }

    /*
     * Removes the restaurant with the given ID.
     * Walks the list, finds the node before the target,
     * and re-links to skip over it.
     */
    public void deleteRestaurant(int id) {
        if (head == null) {
            System.out.println("[Error] Restaurant list is empty.");
            return;
        }
        if (head.id == id) {
            System.out.println("[Restaurant Removed] " + head.name);
            head = head.next;
            return;
        }
        Restaurant curr = head;
        while (curr.next != null && curr.next.id != id) {
            curr = curr.next;
        }
        if (curr.next == null) {
            System.out.println("[Error] Restaurant ID " + id + " not found.");
        } else {
            System.out.println("[Restaurant Removed] " + curr.next.name);
            curr.next = curr.next.next; // unlink the target node
        }
    }

    /*
     * Walks the full list and prints each restaurant.
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
            curr = curr.next;
        }
    }

    /*
     * Converts the linked list to an ArrayList for CSV saving.
     */
    public List<Restaurant> toList() {
        List<Restaurant> list = new ArrayList<>();
        Restaurant curr = head;
        while (curr != null) {
            list.add(curr);
            curr = curr.next;
        }
        return list;
    }
}
