// Node.java
package Delivery;

class Node {
    String foodName;
    double price;

    Node left, right;
    
    Node(String foodName, double price) {
        this.foodName = foodName;
        this.price = price;
        this.left = null;
        this.right = null;
    }
}