/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Delivery;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class OrderProcessor {
    
// Queue (FIFO) to handle orders sequentially in real-time
    private final Queue<List<String>> orderQueue;
    
    // Stack (LIFO) for the current user's cart to allow 'undo'
    private final Stack<String> currentCart;

    public OrderProcessor() {
        this.orderQueue = new LinkedList<>();
        this.currentCart = new Stack<>();
    }

    // --- STACK OPERATIONS (Undo Feature) ---
    
    public void addItemToCart(String item) {
        currentCart.push(item);
        System.out.println("[System]: Added '" + item + "' to cart.");
    }

    public void undoLastItem() {
        if (!currentCart.isEmpty()) {
            String removed = currentCart.pop();
            System.out.println("[System]: Removed '" + removed + "' from cart.");
        } else {
            System.out.println("[System]: Cart is empty. Nothing to undo.");
        }
    }

    public void viewCart() {
        System.out.println("\nCurrent Cart (Stack): " + currentCart);
    }
    
    public void clearCart() {
        currentCart.clear();
    }

    // --- QUEUE OPERATIONS (Order Flow) ---

    public boolean confirmOrder() {
        if (!currentCart.isEmpty()) {
            // Transfer items from Stack to Queue
            orderQueue.add(new ArrayList<>(currentCart));
            System.out.println("[System]: Order confirmed! Sent to processing queue.");
            currentCart.clear();
            return true;
        } else {
            System.out.println("[System]: Cannot confirm an empty order.");
            return false;
        }
    }

    public void processNextOrder() {
        if (!orderQueue.isEmpty()) {
            List<String> nextOrder = orderQueue.poll();
            System.out.println("[System]: Processing Order -> " + nextOrder);
        } else {
            System.out.println("[System]: The order queue is currently empty.");
        }
    }
}
