package AppSystem;

public class Customer {
    int id;
    String name;
    String address;
    Customer next;

    public Customer(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.next = null;
    }
}
