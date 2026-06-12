package AppSystem;

/*
 * BSTNode.java
 * ------------
 * A single node in the Binary Search Tree (FoodBST).
 *
 * Each node holds one FoodItem and two child pointers:
 *   left  → items whose name comes BEFORE this node's name (A-Z)
 *   right → items whose name comes AFTER this node's name (A-Z)
 *
 * Visual example (sorted by name):
 *
 *           [Nasi Lemak]
 *           /           \
 *     [Burger]       [Sushi Set A]
 *          \           /
 *       [Miso Soup] [Rendang]
 */
public class BSTNode {

    // The food item that this node holds
    FoodItem data;

    // Points to the left child — a food item whose name comes earlier in the alphabet
    BSTNode  left;

    // Points to the right child — a food item whose name comes later in the alphabet
    BSTNode  right;

    // Constructor: creates a new node that holds the given food item
    // Both left and right start as null because the node has no children yet
    public BSTNode(FoodItem data) {
        this.data  = data;
        this.left  = null;  // no left child yet
        this.right = null;  // no right child yet
    }
}
