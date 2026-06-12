package AppSystem;

import java.util.ArrayList;
import java.util.List;

/*
 * FoodBST.java
 * ------------
 * A Binary Search Tree (BST) that stores FoodItem objects sorted by name.
 *
 * Why a BST?
 * - Search is O(log n) on average — at each node we go left or right,
 * cutting the remaining search space in half each time.
 * - In-order traversal (left -> node -> right) automatically gives us
 * all items in alphabetical order with no extra sorting step.
 * - Insertion also stays O(log n) because we follow the same path.
 *
 * BST Rule:
 * For every node N:
 * - All names in the LEFT  subtree come BEFORE N's name (A-Z)
 * - All names in the RIGHT subtree come AFTER  N's name (A-Z)
 */
public class FoodBST {

    // The top (root) node of the tree
    // If this is null, the tree is completely empty
    private BSTNode root;

    // Used during in-order printing to track when we need to print a new restaurant header
    private String lastPrintedRestaurant = "";

    /*
     * Public insert — the entry point for adding a new food item to the tree.
     * It calls the private recursive helper to find the right position.
     */
    public void insert(FoodItem item) {
        root = insertRec(root, item);
        System.out.println("[BST] Inserted: " + item);
    }

    /*
     * Recursive insert helper.
     * At each node, we compare the new item's name with the current node's name:
     *   - If the new name comes BEFORE (alphabetically), go LEFT
     *   - If the new name comes AFTER, go RIGHT
     *   - If we find an empty spot (null), that's where the new node goes
     * The method returns the (possibly updated) node at each level of recursion.
     */
    private BSTNode insertRec(BSTNode node, FoodItem item) {
        // Found an empty spot — place the new node here
        if (node == null) return new BSTNode(item);

        // Compare names (case-insensitive) to decide which direction to go
        int cmp = item.name.compareToIgnoreCase(node.data.name);

        if (cmp < 0) {
            // New item's name comes BEFORE this node's name — go left
            node.left = insertRec(node.left, item);
        } else if (cmp > 0) {
            // New item's name comes AFTER this node's name — go right
            node.right = insertRec(node.right, item);
        } else {
            // Exact same name already exists — skip it to avoid duplicates
            System.out.println("[BST] Duplicate item ignored: " + item.name);
        }

        return node; // return this node back up the recursive call chain
    }

    /*
     * Public search — the entry point for finding a food item by name.
     * It calls the private recursive helper starting from the root.
     */
    public FoodItem search(String name) {
        BSTNode result = searchRec(root, name);
        if (result != null) {
            System.out.println("[BST] Found: " + result.data);
            return result.data;
        } else {
            System.out.println("[BST] Item not found: " + name);
            return null;
        }
    }

    /*
     * Recursive search helper.
     * At each node, compare the target name with the current node:
     *   - Match  → we found it, return this node
     *   - Before → go left (the item should be in the left subtree)
     *   - After  → go right (the item should be in the right subtree)
     *   - null   → we fell off the tree without finding it
     */
    private BSTNode searchRec(BSTNode node, String name) {
        // Reached a dead end — the item is not in the tree
        if (node == null) return null;

        int cmp = name.compareToIgnoreCase(node.data.name);

        if (cmp == 0) {
            return node;                         // found the matching node
        } else if (cmp < 0) {
            return searchRec(node.left, name);   // look in the left subtree
        } else {
            return searchRec(node.right, name);  // look in the right subtree
        }
    }

    /*
     * Prints the full food menu grouped by restaurant, in alphabetical order.
     * In-order traversal (left → current → right) naturally visits nodes
     * in alphabetical order because of how the BST is structured.
     */
    public void inOrderTraversal() {
        System.out.println("\n[BST] Food Menu (Sorted by Restaurant & Item):");
        lastPrintedRestaurant = ""; // reset the restaurant tracker for a fresh print
        inOrderRec(root);
        System.out.println(); // blank line at the end for readability
    }

    private void inOrderRec(BSTNode node) {
        if (node == null) return; // base case: nothing to print here

        inOrderRec(node.left); // first, visit everything with a name that comes BEFORE this node

        // Now process the current node
        // Food item names are stored as "RestaurantName - FoodName"
        // We split them so we can print a restaurant header when the restaurant changes
        String fullName = node.data.name;
        String[] parts = fullName.split(" - ", 2);
        String restaurant = parts.length > 1 ? parts[0] : "Other";  // e.g. "Burger King"
        String foodItem   = parts.length > 1 ? parts[1] : fullName; // e.g. "Whopper Meal"

        // Print a restaurant header the first time we see a new restaurant name
        if (!restaurant.equals(lastPrintedRestaurant)) {
            System.out.println("\n--- " + restaurant + " ---");
            lastPrintedRestaurant = restaurant; // remember so we don't print it again
        }

        // Print the food item and its price
        System.out.println("  " + foodItem + " (RM" + String.format("%.2f", node.data.price) + ")");

        inOrderRec(node.right); // finally, visit everything with a name that comes AFTER this node
    }

    /*
     * Collects all food items into a regular list using in-order traversal.
     * The result is automatically sorted alphabetically.
     * Used by FileManager when saving the menu to a CSV file.
     */
    public List<FoodItem> toList() {
        List<FoodItem> list = new ArrayList<>();
        collectInOrder(root, list);
        return list;
    }

    // Recursive helper that adds each node's item to the list in alphabetical order
    private void collectInOrder(BSTNode node, List<FoodItem> list) {
        if (node == null) return;
        collectInOrder(node.left, list);  // collect left subtree first
        list.add(node.data);              // then add the current item
        collectInOrder(node.right, list); // then collect right subtree
    }
}
