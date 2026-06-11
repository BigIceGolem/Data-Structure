package AppSystem;

public class Restaurant {
    int id;
    String name;
    String cuisine;
    Restaurant next;

    public Restaurant(int id, String name, String cuisine) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.next = null;
    }
}
