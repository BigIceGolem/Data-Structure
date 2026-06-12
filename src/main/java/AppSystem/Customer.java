package AppSystem;

/*
 * Customer.java
 * -------------
 * Represents a single customer in the system.
 * This is also a node in the CustomerLinkedList — it holds a
 * reference (next) that points to the next customer in the chain.
 */
public class Customer {

    // A unique number given to each customer so we can tell them apart
    int      id;

    // The customer's full name
    String   name;

    // The customer's delivery address
    String   address;

    // A pointer to the next customer in the linked list
    // When this is null, it means this customer is the last one in the list
    Customer next;

    // Constructor: creates a new customer with the given details
    public Customer(int id, String name, String address) {
        this.id      = id;
        this.name    = name;
        this.address = address;
        this.next    = null; // not linked to any other customer yet
    }
}
