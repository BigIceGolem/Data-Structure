// BST.java
package Delivery;

class BST {
    Node root;
    
    void insert(String foodName, double price) {
        root = insertRec(root, foodName, price);
    }
    
    Node insertRec(Node node, String foodName, double price) {
        if (node == null) {
            return new Node(foodName, price);
        }
        
        int compareResult = foodName.compareToIgnoreCase(node.foodName);
        
        if (compareResult < 0) {
            node.left = insertRec(node.left, foodName, price);
        } else if (compareResult > 0) {
            node.right = insertRec(node.right, foodName, price);
        }
        return node;
    }
    
    void searchByName(String foodQuery) {
        System.out.println("[System]: Searching for '" + foodQuery + "'...");
        searchByNameRec(root, foodQuery);
    }
    
    private void searchByNameRec(Node node, String foodQuery) {
        if (node == null) {
            System.out.println("-> Item not found in menu.");
            return;
        }
        
        int compareResult = foodQuery.compareToIgnoreCase(node.foodName);
        
        if (compareResult == 0) {
            System.out.printf("-> Found: %s | Price: RM %.2f\n", node.foodName, node.price);
        } else if (compareResult < 0) {
            searchByNameRec(node.left, foodQuery);
        } else {
            searchByNameRec(node.right, foodQuery);
        }
    }
    
    void inorder() {
         System.out.println("\n--- Current Menu (Alphabetical Order) ---");
         inorderRec(root);
    }
    
    void inorderRec(Node node) {
        if (node == null) {
            return;
        }
        inorderRec(node.left);
        System.out.printf("%s - RM %.2f\n", node.foodName, node.price);
        inorderRec(node.right);
    }
}