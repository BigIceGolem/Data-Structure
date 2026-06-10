package Delivery;

import java.util.Scanner;

public class DeliveryApp {

    // 1. Initialize your completed DataManager
    private static DataManager db = new DataManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Welcome to Smart Food Delivery & Order System   ");
        System.out.println("==================================================");

        boolean running = true;

        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Manage Users & Restaurants (DataManager)");
            System.out.println("2. Search & Recommend Food (BST)");
            System.out.println("3. Process Orders (Queue & Stack)");
            System.out.println("4. Assign Delivery Rider (Min Heap)");
            System.out.println("5. Route Optimization (Graph & Dijkstra)");
            System.out.println("0. Exit System");
            System.out.print("Enter your choice: ");

            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine());
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
    // Populate the graph nodes with mock delivery paths matching Amirun's locations
    private static void initializeRouteNetwork() {
        routeGraph.addEdge("Mid Valley", "Bangsar", 5);
        routeGraph.addEdge("Mid Valley", "KLCC", 15);
        routeGraph.addEdge("Bangsar", "Bukit Bintang", 10);
        routeGraph.addEdge("Bukit Bintang", "KLCC", 4);
        routeGraph.addEdge("1 Utama", "Bangsar", 12);
        routeGraph.addEdge("1 Utama", "Mid Valley", 18);
    }

    // --- SUB-MENUS & INTEGRATION STUBS ---

    // 1. Amirun's Module (Fully Integrated)
    private static void menuManageData() {
        System.out.println("\n-- User & Restaurant Management --");
        System.out.println("1. View All Users");
        System.out.println("2. View All Restaurants");
        System.out.println("3. Find User by ID (O(1) Hash Map)");
        System.out.print("Select action: ");
        
        String action = scanner.nextLine();
        if (action.equals("1")) {
            db.displayAllUsers();
        } else if (action.equals("2")) {
            db.displayAllRestaurants();
        } else if (action.equals("3")) {
            System.out.print("Enter User ID (e.g., U001): ");
            String id = scanner.nextLine();
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
        System.out.print("Enter food item to search: ");
        String foodQuery = scanner.nextLine();
        
        System.out.println("[System]: Searching for '" + foodQuery + "'...");
        // TODO (Ammar): Call your BST search method here. 
        // Example: bstTree.search(foodQuery);
    }

    // 3. Haziq's Module Placeholder
    private static void menuProcessOrders() {
        System.out.println("\n-- Order Processing --");
        System.out.println("1. Place New Order (Queue)");
        System.out.println("2. Undo Last Item (Stack)");
        System.out.println("3. Process Next Order");
        System.out.print("Select action: ");
        
        String action = scanner.nextLine();
        
        // TODO (Haziq): Add your Queue.enqueue(), Stack.pop(), and Queue.dequeue() methods inside these conditions.
        System.out.println("[System]: Haziq's Order Processing logic will execute here based on input '" + action + "'.");
    }

    // 4. Aasim's Module Placeholder
    private static void menuAssignRider() {
        System.out.println("\n-- Delivery Rider Assignment --");
        System.out.print("Enter Order ID to assign rider: ");
        String orderId = scanner.nextLine();
        
        System.out.println("[System]: Finding optimal rider for order " + orderId + "...");
        // TODO (Aasim): Call your Min Heap extraction method here.
        // Example: Rider bestRider = riderHeap.extractMin();
    }

    // 5. Hazim's Module (Fully Integrated)
    private static void menuCalculateRoute() {
        System.out.println("\n-- Route Optimization --");
        System.out.print("Enter Restaurant Location Node (e.g., Mid Valley): ");
        String startNode = scanner.nextLine();
        System.out.print("Enter Customer Location Node (e.g., KLCC): ");
        String endNode = scanner.nextLine();
        
        System.out.println("[System]: Calculating shortest path from '" + startNode + "' to '" + endNode + "'...");
        
        // Execute Hazim's Dijkstra implementation
        routeGraph.findShortestPath(startNode, endNode);
    }
}
