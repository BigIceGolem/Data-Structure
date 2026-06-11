package AppSystem;

import java.util.ArrayList;
import java.util.List;

/*
 * CustomerLinkedList.java
 * -----------------------
 * A singly linked list that stores Customer objects.
 *
 * Why a linked list?
 *   - Adding a new customer is O(1) — we just put it at the front.
 *   - Removing a customer only requires relinking two pointers,
 *     no shifting of elements like an array would need.
 *   - Memory grows and shrinks as customers are added/removed.
 *
 * Structure:  head -> [C1] -> [C2] -> [C3] -> null
 */
public class CustomerLinkedList {

    // head points to the first customer in the list (or null if empty)
    private Customer head;

    /*
     * Adds a new customer at the front of the list.
     * This is O(1) because we don't need to walk the whole list.
     */
    public void addCustomer(int id, String name, String address) {
        Customer newCustomer = new Customer(id, name, address);
        newCustomer.next = head; // new node points to the old head
        head = newCustomer;      // head now points to the new node
        System.out.println("[Customer Added] " + name + " (ID: " + id + ")");
    }

    /*
     * Removes the customer with the given ID.
     * We walk the list until we find the node just before the target,
     * then bypass it by relinking the pointers.
     */
    public void deleteCustomer(int id) {
        if (head == null) {
            System.out.println("[Error] Customer list is empty.");
            return;
        }
        // special case: the target is the very first node
        if (head.id == id) {
            System.out.println("[Customer Removed] " + head.name);
            head = head.next; // move head forward, old head is garbage collected
            return;
        }
        // walk until curr.next is the node we want to delete
        Customer curr = head;
        while (curr.next != null && curr.next.id != id) {
            curr = curr.next;
        }
        if (curr.next == null) {
            System.out.println("[Error] Customer ID " + id + " not found.");
        } else {
            System.out.println("[Customer Removed] " + curr.next.name);
            curr.next = curr.next.next; // skip over the deleted node
        }
    }

    /*
     * Prints every customer by walking from head to null.
     */
    public void displayCustomers() {
        if (head == null) {
            System.out.println("[Info] No customers registered.");
            return;
        }
        System.out.println("--- Customer List ---");
        Customer curr = head;
        while (curr != null) {
            System.out.println("  ID: " + curr.id
                    + " | Name: "    + curr.name
                    + " | Address: " + curr.address);
            curr = curr.next;
        }
    }

    /*
     * Searches for a customer by ID. Returns null if not found.
     * Used internally when linking customers to orders.
     */
    public Customer findById(int id) {
        Customer curr = head;
        while (curr != null) {
            if (curr.id == id) return curr;
            curr = curr.next;
        }
        return null;
    }

    /*
     * Converts the linked list into a plain ArrayList.
     * Used by FileManager when saving to CSV.
     */
    public List<Customer> toList() {
        List<Customer> list = new ArrayList<>();
        Customer curr = head;
        while (curr != null) {
            list.add(curr);
            curr = curr.next;
        }
        return list;
    }
}
