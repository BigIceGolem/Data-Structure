package AppSystem;

/*
 * Customer.java
 * -------------
 * Represents a single customer in the system.
 * This is also a node in the CustomerLinkedList — it holds a
 * reference (next) that points to the next customer in the chain.
 */
public class Customer {
    int      id;       // unique identifier assigned at creation
    String   name;
    String   address;
    Customer next;     // pointer to the next node in the linked list

    public Customer(int id, String name, String address) {
        this.id      = id;
        this.name    = name;
        this.address = address;
        this.next    = null; // no next node yet when first created
    }
}
