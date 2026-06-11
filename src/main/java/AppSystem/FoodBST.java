package AppSystem;

public class FoodBST {
    private BSTNode root;

    public void insert(FoodItem item) {
        root = insertRec(root, item);
        System.out.println("[BST] Inserted: " + item);
    }

    private BSTNode insertRec(BSTNode node, FoodItem item) {
        if (node == null) return new BSTNode(item);
        int cmp = item.name.compareToIgnoreCase(node.data.name);
        if (cmp < 0)      node.left  = insertRec(node.left,  item);
        else if (cmp > 0) node.right = insertRec(node.right, item);
        else System.out.println("[BST] Duplicate item ignored: " + item.name);
        return node;
    }

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

    private BSTNode searchRec(BSTNode node, String name) {
        if (node == null) return null;
        int cmp = name.compareToIgnoreCase(node.data.name);
        if (cmp == 0) return node;
        if (cmp < 0)  return searchRec(node.left,  name);
        return            searchRec(node.right, name);
    }

    public void inOrderTraversal() {
        System.out.println("[BST] Food Menu (sorted A-Z):");
        inOrderRec(root);
    }

    private void inOrderRec(BSTNode node) {
        if (node == null) return;
        inOrderRec(node.left);
        System.out.println("  " + node.data);
        inOrderRec(node.right);
    }

    // returns all items in sorted order (used for CSV saving)
    public java.util.List<FoodItem> toList() {
        java.util.List<FoodItem> list = new java.util.ArrayList<>();
        collectInOrder(root, list);
        return list;
    }

    private void collectInOrder(BSTNode node, java.util.List<FoodItem> list) {
        if (node == null) return;
        collectInOrder(node.left, list);
        list.add(node.data);
        collectInOrder(node.right, list);
    }
}
