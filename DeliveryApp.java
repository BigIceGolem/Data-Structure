// DeliveryApp.java
package Delivery;

import java.util.*;

public class DeliveryApp {

    // 1. Initialize your completed DataManager
    private static DataManager db = new DataManager();
    private static Scanner scanner = new Scanner(System.in);
    private static DeliverySystem ds = new DeliverySystem();
    private static BST tree = new BST();
    private static Graph routeGraph = new Graph();
    private static OrderProcessor orderProcessor = new OrderProcessor();
    private static Stack<String> currentOrderStack = new Stack<>();
    private static Queue<List<String>> orderQueue = new LinkedList<>();

    public static void main(String[] args) {
        initializeRouteNetwork();
        initializeFoodMenu();
        
        System.out.println("==================================================");
        System.out.println("  Welcome to Smart Food Delivery & Order System   ");
        System.out.println("==================================================");

        boolean running = true;

        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Manage Users & Restaurants (Hash Map & Linked List)");
            System.out.println("2. Search & Recommend Food (BST)");
            System.out.println("3. Process Orders (Queue & Stack)");
            System.out.println("4. Assign Delivery Rider (Min Heap)");
            System.out.println("5. Route Optimization (Graph & Dijkstra)");
            System.out.println("0. Exit System");
            System.out.println("Enter your choice: "); 

            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    menuManageData();
                    break;
                case 2:
                    menuSearchFood();
                    break;
                case 3:
                    menuProcessOrders();
                    break;
                case 4:
                    menuAssignRider();
                    break;
                case 5:
                    menuCalculateRoute();
                    break;
                case 0:
                    System.out.println("Exiting System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void initializeRouteNetwork() {
        routeGraph.addEdge("Mid Valley", "Bangsar", 5);
        routeGraph.addEdge("Mid Valley", "KLCC", 15);
        routeGraph.addEdge("Bangsar", "Bukit Bintang", 10);
        routeGraph.addEdge("Bukit Bintang", "KLCC", 4);
        routeGraph.addEdge("1 Utama", "Bangsar", 12);
        routeGraph.addEdge("1 Utama", "Mid Valley", 18);
    }
    
    private static void initializeFoodMenu() {
        tree.insert("Nasi Lemak", 12.50);
        tree.insert("Chicken Chop", 18.00);
        tree.insert("Beef Burger", 15.00);
        tree.insert("Mee Goreng", 8.50);
        tree.insert("Teh Tarik", 3.00);
    }

  

    private static void menuManageData() {
        System.out.println("\n-- User & Restaurant Management --");
        System.out.println("1. View All Users");
        System.out.println("2. View All Restaurants");
        System.out.println("3. Find User by ID");
        System.out.println("Select action: "); 
        
        String action = scanner.nextLine().trim();
        if (action.equals("1")) {
            db.displayAllUsers();
        } else if (action.equals("2")) {
            db.displayAllRestaurants();
        } else if (action.equals("3")) {
            System.out.println("Enter User ID (e.g., U001): "); 
            String id = scanner.nextLine().trim();
            User u = db.getUser(id);
            if (u != null) System.out.println("Found: " + u);
            else System.out.println("User not found.");
        } else {
            System.out.println("Invalid action.");
        }
    }

    // 2. Ammar's Module Placeholder
    private static void menuSearchFood() {
        System.out.println("\n-- Search & Recommend Food --");
        System.out.println("1. View Full Menu Sorted Alphabetically");
        System.out.println("2. Search food item by name");
        System.out.println("Select action: "); 
        
        String action = scanner.nextLine().trim();
        
        if (action.equals("1")) {
            tree.inorder(); 
        } else if (action.equals("2")) {
            System.out.println("Enter food item to search: "); 
            String foodQuery = scanner.nextLine().trim();
            tree.searchByName(foodQuery);
        } else {
            System.out.println("Invalid option.");
        }
    }

    // 3. Haziq's Module Placeholder
    private static void menuProcessOrders() {
        System.out.println("\n-- Order Processing --");
        System.out.println("1. Create New Order");
        System.out.println("2. Process Next Order in Queue");
        System.out.println("Select action: "); 
        
        String action = scanner.nextLine().trim();
        
        if (action.equals("1")) {
            currentOrderStack.clear();
            boolean ordering = true;
            System.out.println("\n--- Cart Menu ---");
            System.out.println("Type food name to add.");
            System.out.println("Type 'undo' to remove the last added item.");
            System.out.println("Type 'confirm' to place the order.");
            
            while (ordering) {
                System.out.println("\nCurrent Cart (Stack): " + currentOrderStack);
                System.out.print("Input: ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("undo")) {
                    if (!currentOrderStack.isEmpty()) {
                        String removed = currentOrderStack.pop();
                        System.out.println("[System]: Removed '" + removed + "' from cart.");
                    } else {
                        System.out.println("[System]: Cart is empty. Nothing to undo.");
                    }
                } else if (input.equalsIgnoreCase("confirm")) {
                    if (!currentOrderStack.isEmpty()) {
                        orderQueue.add(new ArrayList<>(currentOrderStack));
                        System.out.println("[System]: Order confirmed! Sent to processing queue.");
                        currentOrderStack.clear();
                    } else {
                        System.out.println("[System]: Cannot confirm an empty order.");
                    }
                    ordering = false;
                } else if (!input.isEmpty()) {
                    currentOrderStack.push(input);
                    System.out.println("[System]: Added '" + input + "' to cart.");
                }
            }
        } else if (action.equals("2")) {
            if (!orderQueue.isEmpty()) {
                List<String> nextOrder = orderQueue.poll();
                System.out.println("[System]: Processing Order -> " + nextOrder);
            } else {
                System.out.println("[System]: The order queue is currently empty.");
            }
        } else {
            System.out.println("Invalid option.");
        }
    }

    // 4. Aasim's Module Placeholder
   private static void menuAssignRider() {
    System.out.println("\n-- Delivery Rider Assignment --");
    System.out.print("Enter Order ID to assign rider: ");
    String orderId = scanner.nextLine();
    
    System.out.println("[System]: Finding optimal rider for order " + orderId + "...");
    
    if (ds.riderheap.isEmpty()) {
        System.out.println("[System]: Sorry, no riders currently available.");
    } else {
        Rider bestRider = ds.assignRider();
        System.out.println("[System]: " + bestRider.getName() + " has been assigned for order " + orderId);
    }
        
}

    // 5. Hazim's Module Placeholder
    private static void menuCalculateRoute() {
        System.out.println("\n-- Route Optimization --");
        System.out.println("Available Locations: Mid Valley, Bangsar, KLCC, Bukit Bintang, 1 Utama");
        
        String startNode = "";
        while (startNode.isEmpty()) {
            System.out.println("Enter Restaurant Location Node: ");
            startNode = scanner.nextLine().trim(); 
        }
        
        String endNode = "";
        while (endNode.isEmpty()) {
            System.out.println("Enter Customer Location Node: ");
            endNode = scanner.nextLine().trim();
        }
        
        System.out.println("[System]: Calculating shortest path from '" + startNode + "' to '" + endNode + "'...");
        routeGraph.findShortestPath(startNode, endNode);
    }
}