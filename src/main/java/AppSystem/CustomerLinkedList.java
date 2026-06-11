package AppSystem;

public class CustomerLinkedList {
    private Customer head;

    public void addCustomer(int id, String name, String address) {
        Customer newCustomer = new Customer(id, name, address);
        newCustomer.next = head;
        head = newCustomer;
        System.out.println("[Customer Added] " + name + " (ID: " + id + ")");
    }

    public void deleteCustomer(int id) {
        if (head == null) {
            System.out.println("[Error] Customer list is empty.");
            return;
        }
        if (head.id == id) {
            System.out.println("[Customer Removed] " + head.name);
            head = head.next;
            return;
        }
        Customer curr = head;
        while (curr.next != null && curr.next.id != id) {
            curr = curr.next;
        }
        if (curr.next == null) {
            System.out.println("[Error] Customer ID " + id + " not found.");
        } else {
            System.out.println("[Customer Removed] " + curr.next.name);
            curr.next = curr.next.next;
        }
    }

    public void displayCustomers() {
        if (head == null) {
            System.out.println("[Info] No customers registered.");
            return;
        }
        System.out.println("--- Customer List ---");
        Customer curr = head;
        while (curr != null) {
            System.out.println("  ID: " + curr.id + " | Name: " + curr.name + " | Address: " + curr.address);
            curr = curr.next;
        }
    }

    // returns all customers as a list (used for CSV saving)
    public java.util.List<Customer> toList() {
        java.util.List<Customer> list = new java.util.ArrayList<>();
        Customer curr = head;
        while (curr != null) { list.add(curr); curr = curr.next; }
        return list;
    }

    public Customer findById(int id) {
        Customer curr = head;
        while (curr != null) {
            if (curr.id == id) return curr;
            curr = curr.next;
        }
        return null;
    }
}
