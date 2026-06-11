package AppSystem;

public class FoodItem {
    String name;
    double price;

    public FoodItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return name + " (RM" + String.format("%.2f", price) + ")";
    }
}
