package AppSystem;

public class RestaurantLinkedList {
    private Restaurant head;

    public void addRestaurant(int id, String name, String cuisine) {
        Restaurant r = new Restaurant(id, name, cuisine);
        r.next = head;
        head = r;
        System.out.println("[Restaurant Added] " + name + " (ID: " + id + ", Cuisine: " + cuisine + ")");
    }

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
            curr.next = curr.next.next;
        }
    }

    public void displayRestaurants() {
        if (head == null) {
            System.out.println("[Info] No restaurants registered.");
            return;
        }
        System.out.println("--- Restaurant List ---");
        Restaurant curr = head;
        while (curr != null) {
            System.out.println("  ID: " + curr.id + " | Name: " + curr.name + " | Cuisine: " + curr.cuisine);
            curr = curr.next;
        }
    }

    // returns all restaurants as a list (used for CSV saving)
    public java.util.List<Restaurant> toList() {
        java.util.List<Restaurant> list = new java.util.ArrayList<>();
        Restaurant curr = head;
        while (curr != null) { list.add(curr); curr = curr.next; }
        return list;
    }
}
