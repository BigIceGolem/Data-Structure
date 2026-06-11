package AppSystem;

/*
 * FoodItem.java
 * -------------
 * Represents a single item on the food menu.
 * Stored inside the FoodBST — the tree nodes hold FoodItem objects.
 * Items are sorted in the BST alphabetically by name.
 */
public class FoodItem {
    String name;
    double price;

    public FoodItem(String name, double price) {
        this.name  = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return name + " (RM" + String.format("%.2f", price) + ")";
    }
}
