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
    FoodItem data;   // the food item stored at this node
    BSTNode  left;   // subtree with names that come before this one
    BSTNode  right;  // subtree with names that come after this one

    public BSTNode(FoodItem data) {
        this.data  = data;
        this.left  = null;
        this.right = null;
    }
}
