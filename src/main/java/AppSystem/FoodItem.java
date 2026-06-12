package AppSystem;

/*
 * FoodItem.java
 * -------------
 * Represents a single item on the food menu.
 * Stored inside the FoodBST — the tree nodes hold FoodItem objects.
 * Items are sorted in the BST alphabetically by name.
 */
public class FoodItem {

    // The name of the food item, e.g. "Burger King - Whopper Meal"
    String name;

    // The price of the food item in Ringgit Malaysia
    double price;

    // Constructor: called when we want to create a new FoodItem
    // We pass in the name and price and they get stored in this object
    public FoodItem(String name, double price) {
        this.name  = name;   // save the name into this object
        this.price = price;  // save the price into this object
    }

    // toString() controls what gets printed when we do System.out.println(foodItem)
    // It formats the output to look like: "Whopper Meal (RM18.50)"
    @Override
    public String toString() {
        // String.format("%.2f", price) ensures price always shows 2 decimal places
        return name + " (RM" + String.format("%.2f", price) + ")";
    }
}
