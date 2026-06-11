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

    private BSTNode root; // the top of the tree (null if tree is empty)
    private String lastPrintedRestaurant = ""; // Used for formatting headers

    /*
     * Public insert — starts the recursive insertion from the root.
     */
    public void insert(FoodItem item) {
        root = insertRec(root, item);
        System.out.println("[BST] Inserted: " + item);
    }

    /*
     * Recursive insert helper.
     * Compares names to decide whether to go left (before) or right (after).
     * When it reaches a null spot, that's where the new node goes.
     */
    private BSTNode insertRec(BSTNode node, FoodItem item) {
        if (node == null) return new BSTNode(item); // empty spot found — insert here

        int cmp = item.name.compareToIgnoreCase(node.data.name);
        if      (cmp < 0) node.left  = insertRec(node.left,  item); // item name comes before
        else if (cmp > 0) node.right = insertRec(node.right, item); // item name comes after
        else System.out.println("[BST] Duplicate item ignored: " + item.name);

        return node;
    }

    /*
     * Public search — starts the recursive search from the root.
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
     * At each node we compare names:
     * match  → return this node
     * before → go left
     * after  → go right
     * Returns null if we fall off the tree without finding it.
     */
    private BSTNode searchRec(BSTNode node, String name) {
        if (node == null) return null; // reached a dead end — not found

        int cmp = name.compareToIgnoreCase(node.data.name);
        if      (cmp == 0) return node;                      // found it
        else if (cmp <  0) return searchRec(node.left,  name); // look left
        else               return searchRec(node.right, name); // look right
    }

    /*
     * Prints all food items formatted with Restaurant headers.
     * Because of the BST rule, this naturally groups by restaurant A-Z.
     */
    public void inOrderTraversal() {
        System.out.println("\n[BST] Food Menu (Sorted by Restaurant & Item):");
        lastPrintedRestaurant = ""; // Reset for fresh traversal
        inOrderRec(root);
        System.out.println(); // Final line break
    }

    private void inOrderRec(BSTNode node) {
        if (node == null) return;      
        
        inOrderRec(node.left);         // visit everything that comes before

        // Process current node
        String fullName = node.data.name; 
        String[] parts = fullName.split(" - ", 2);
        String restaurant = parts.length > 1 ? parts[0] : "Other";
        String foodItem = parts.length > 1 ? parts[1] : fullName;

        // Print header if the restaurant name changes
        if (!restaurant.equals(lastPrintedRestaurant)) {
            System.out.println("\n--- " + restaurant + " ---");
            lastPrintedRestaurant = restaurant;
        }
        
        System.out.println("  " + foodItem + " (RM" + String.format("%.2f", node.data.price) + ")");

        inOrderRec(node.right);        // visit everything that comes after
    }

    /*
     * Collects all items into a list using in-order traversal.
     * Used by FileManager to save the menu to CSV.
     */
    public List<FoodItem> toList() {
        List<FoodItem> list = new ArrayList<>();
        collectInOrder(root, list);
        return list;
    }

    private void collectInOrder(BSTNode node, List<FoodItem> list) {
        if (node == null) return;
        collectInOrder(node.left, list);
        list.add(node.data);
        collectInOrder(node.right, list);
    }
}