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

    // head points to the first customer in the list
    // If head is null, the list is empty
    private Customer head;

    /*
     * Adds a new customer at the FRONT of the list.
     * This is O(1) — we don't need to walk through the whole list to add someone.
     *
     * Before: head -> [C1] -> [C2]
     * After:  head -> [newCustomer] -> [C1] -> [C2]
     */
    public void addCustomer(int id, String name, String address) {
        Customer newCustomer = new Customer(id, name, address);
        newCustomer.next = head; // the new customer points to whoever used to be first
        head = newCustomer;      // the new customer is now the first in the list
        System.out.println("[Customer Added] " + name + " (ID: " + id + ")");
    }

    /*
     * Removes the customer with the matching ID from the list.
     * We walk the list until we find the node just before the one we want to delete,
     * then we "skip over" the target by relinking the pointer around it.
     */
    public void deleteCustomer(int id) {
        // Can't delete from an empty list
        if (head == null) {
            System.out.println("[Error] Customer list is empty.");
            return;
        }

        // Special case: the customer we want to delete is the very first one
        if (head.id == id) {
            System.out.println("[Customer Removed] " + head.name);
            head = head.next; // move head forward — the old first customer is now gone
            return;
        }

        // Walk through the list, stopping at the node just BEFORE the one we want
        Customer curr = head;
        while (curr.next != null && curr.next.id != id) {
            curr = curr.next;
        }

        // If curr.next is null, we walked to the end without finding the ID
        if (curr.next == null) {
            System.out.println("[Error] Customer ID " + id + " not found.");
        } else {
            // Skip over the node we want to delete by pointing past it
            System.out.println("[Customer Removed] " + curr.next.name);
            curr.next = curr.next.next;
        }
    }

    /*
     * Prints every customer in the list.
     * We start at head and follow the 'next' pointers until we reach null (end of list).
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
            curr = curr.next; // move to the next customer
        }
    }

    /*
     * Searches for a customer by their ID number.
     * Returns the Customer object if found, or null if not.
     * Used internally when we need to link a customer to their order.
     */
    public Customer findById(int id) {
        Customer curr = head;
        while (curr != null) {
            if (curr.id == id) return curr; // found the customer — return them
            curr = curr.next;               // not this one — keep looking
        }
        return null; // got to the end without finding the ID
    }

    /*
     * Converts the linked list into a normal ArrayList.
     * Used by FileManager when it needs to save all customers to a CSV file.
     */
    public List<Customer> toList() {
        List<Customer> list = new ArrayList<>();
        Customer curr = head;
        while (curr != null) {
            list.add(curr);    // add this customer to the regular list
            curr = curr.next;  // move to the next customer
        }
        return list;
    }
}
