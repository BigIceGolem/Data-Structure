package AppSystem;

import java.util.*;

public class Main {

    // -- shared state across all modules --
    static CustomerLinkedList    customers   = new CustomerLinkedList();
    static RestaurantLinkedList  restaurants = new RestaurantLinkedList();
    static OrderQueue            orderQueue  = new OrderQueue();
    static DeliveryAssignment    delivery    = new DeliveryAssignment();
    static FoodBST               menu        = new FoodBST();
    static DataRetrievalSystem   dataSystem  = new DataRetrievalSystem();
    static Graph                 map;

    // track next IDs so they never clash with saved data
    static int nextCustomerId   = 1;
    static int nextRestaurantId = 101;
    static int nextRiderId      = 201;
    static int nextOrderId      = 1001;

    static Scanner sc = new Scanner(System.in);

    // ================================================================
    //  ENTRY POINT
    // ================================================================
    public static void main(String[] args) {
        seedGraph();
        loadAllData();     // restore everything from CSV on startup
        showMainMenu();
        saveAllData();     // save everything to CSV on exit
    }

    // ================================================================
    //  SAVE & LOAD
    // ================================================================
    static void saveAllData() {
        FileManager.saveCustomers(customers.toList());
        FileManager.saveRestaurants(restaurants.toList());
        FileManager.saveFoodMenu(menu.toList());
        FileManager.saveOrders(dataSystem.getAllOrders());
        FileManager.saveRiders(delivery.toList());
        System.out.println("\n[File] All data saved.");
    }

    static void loadAllData() {
        boolean anyLoaded = false;

        // -- customers --
        List<Customer> savedCustomers = FileManager.loadCustomers();
        if (!savedCustomers.isEmpty()) {
            for (Customer c : savedCustomers) {
                customers.addCustomer(c.id, c.name, c.address);
                dataSystem.addUserProfile(new UserProfile(c.id, c.name, c.name.toLowerCase() + "@email.com"));
                if (c.id >= nextCustomerId) nextCustomerId = c.id + 1;
            }
            anyLoaded = true;
        }

        // -- restaurants --
        List<Restaurant> savedRestaurants = FileManager.loadRestaurants();
        if (!savedRestaurants.isEmpty()) {
            for (Restaurant r : savedRestaurants) {
                restaurants.addRestaurant(r.id, r.name, r.cuisine);
                if (r.id >= nextRestaurantId) nextRestaurantId = r.id + 1;
            }
            anyLoaded = true;
        }

        // -- food menu --
        List<FoodItem> savedFood = FileManager.loadFoodMenu();
        if (!savedFood.isEmpty()) {
            for (FoodItem f : savedFood) menu.insert(f);
            anyLoaded = true;
        } else {
            seedMenu();   // only seed defaults if no saved menu exists
        }

        // -- orders --
        List<OrderRecord> savedOrders = FileManager.loadOrders();
        if (!savedOrders.isEmpty()) {
            for (OrderRecord o : savedOrders) {
                dataSystem.loadOrderRecord(o);
                if (o.orderId >= nextOrderId) nextOrderId = o.orderId + 1;
            }
            anyLoaded = true;
        }

        // -- riders --
        List<Rider> savedRiders = FileManager.loadRiders();
        if (!savedRiders.isEmpty()) {
            for (Rider r : savedRiders) {
                delivery.addRider(r);
                if (r.id >= nextRiderId) nextRiderId = r.id + 1;
            }
            anyLoaded = true;
        }

        if (anyLoaded) {
            System.out.println("[File] Previous session data loaded successfully.\n");
        } else {
            System.out.println("[File] No saved data found. Starting fresh.\n");
        }
    }

    // ================================================================
    //  MAIN MENU
    // ================================================================
    static void showMainMenu() {
        while (true) {
            printHeader("SMART FOOD DELIVERY SYSTEM");
            System.out.println("  1. User & Restaurant Management");
            System.out.println("  2. Order Processing");
            System.out.println("  3. Delivery Assignment");
            System.out.println("  4. Route Optimization");
            System.out.println("  5. Food Menu (Search & Browse)");
            System.out.println("  6. Data Retrieval");
            System.out.println("  0. Exit");
            System.out.print("\nEnter choice: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": moduleUserRestaurant(); break;
                case "2": moduleOrderProcessing(); break;
                case "3": moduleDelivery(); break;
                case "4": moduleRoute(); break;
                case "5": moduleFood(); break;
                case "6": moduleData(); break;
                case "0": return;
                default:  System.out.println("[!] Invalid option. Try again.");
            }
        }
    }

    // ================================================================
    //  MODULE 1 — USER & RESTAURANT MANAGEMENT
    // ================================================================
    static void moduleUserRestaurant() {
        while (true) {
            printHeader("MODULE 1 — User & Restaurant Management");
            System.out.println("  1. Add Customer");
            System.out.println("  2. Remove Customer");
            System.out.println("  3. Display All Customers");
            System.out.println("  4. Add Restaurant");
            System.out.println("  5. Remove Restaurant");
            System.out.println("  6. Display All Restaurants");
            System.out.println("  0. Back");
            System.out.print("\nEnter choice: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": {
                    System.out.print("Customer name   : "); String name = sc.nextLine().trim();
                    System.out.print("Customer address: "); String addr = sc.nextLine().trim();
                    customers.addCustomer(nextCustomerId, name, addr);
                    dataSystem.addUserProfile(new UserProfile(nextCustomerId, name, name.toLowerCase() + "@email.com"));
                    nextCustomerId++;
                    FileManager.saveCustomers(customers.toList());
                    break;
                }
                case "2": {
                    System.out.print("Enter Customer ID to remove: ");
                    try {
                        int id = Integer.parseInt(sc.nextLine().trim());
                        customers.deleteCustomer(id);
                        FileManager.saveCustomers(customers.toList());
                    } catch (NumberFormatException e) { System.out.println("[!] Invalid ID."); }
                    break;
                }
                case "3": customers.displayCustomers(); break;
                case "4": {
                    System.out.print("Restaurant name: "); String name    = sc.nextLine().trim();
                    System.out.print("Cuisine type   : "); String cuisine = sc.nextLine().trim();
                    restaurants.addRestaurant(nextRestaurantId++, name, cuisine);
                    FileManager.saveRestaurants(restaurants.toList());
                    break;
                }
                case "5": {
                    System.out.print("Enter Restaurant ID to remove: ");
                    try {
                        int id = Integer.parseInt(sc.nextLine().trim());
                        restaurants.deleteRestaurant(id);
                        FileManager.saveRestaurants(restaurants.toList());
                    } catch (NumberFormatException e) { System.out.println("[!] Invalid ID."); }
                    break;
                }
                case "6": restaurants.displayRestaurants(); break;
                case "0": return;
                default:  System.out.println("[!] Invalid option.");
            }
            pause();
        }
    }

    // ================================================================
    //  MODULE 2 — ORDER PROCESSING (Queue + Stack)
    // ================================================================
    static void moduleOrderProcessing() {
        OrderCart cart = null;

        while (true) {
            printHeader("MODULE 2 — Order Processing");
            System.out.println("  1. Start new cart (pick customer)");
            System.out.println("  2. Add item to cart");
            System.out.println("  3. Undo last item (Stack)");
            System.out.println("  4. View cart");
            System.out.println("  5. Confirm & enqueue order");
            System.out.println("  6. Process next order from queue");
            System.out.println("  7. View pending order queue");
            System.out.println("  0. Back");
            System.out.print("\nEnter choice: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": {
                    System.out.print("Enter Customer ID: ");
                    try {
                        int cid = Integer.parseInt(sc.nextLine().trim());
                        cart = new OrderCart(cid);
                        System.out.println("[Cart] New cart started for Customer " + cid);
                    } catch (NumberFormatException e) { System.out.println("[!] Invalid ID."); }
                    break;
                }
                case "2": {
                    if (cart == null) { System.out.println("[!] Start a cart first (option 1)."); break; }
                    System.out.print("Item name: "); String item = sc.nextLine().trim();
                    cart.addItem(item);
                    break;
                }
                case "3": {
                    if (cart == null) { System.out.println("[!] Start a cart first (option 1)."); break; }
                    cart.undoLastItem();
                    break;
                }
                case "4": {
                    if (cart == null) { System.out.println("[!] No active cart."); break; }
                    cart.displayCart();
                    break;
                }
                case "5": {
                    if (cart == null) { System.out.println("[!] Start a cart first (option 1)."); break; }
                    Order order = cart.confirmOrder(nextOrderId++);
                    orderQueue.enqueue(order);
                    dataSystem.storeOrder(order);
                    FileManager.saveOrders(dataSystem.getAllOrders());
                    cart = null;
                    break;
                }
                case "6": {
                    Order processed = orderQueue.processNextOrder();
                    if (processed != null) {
                        dataSystem.updateOrderStatus(processed.orderId, "Preparing");
                        FileManager.saveOrders(dataSystem.getAllOrders());
                    }
                    break;
                }
                case "7": orderQueue.displayQueue(); break;
                case "0": return;
                default:  System.out.println("[!] Invalid option.");
            }
            pause();
        }
    }

    // ================================================================
    //  MODULE 3 — DELIVERY ASSIGNMENT (Min Heap)
    // ================================================================
    static void moduleDelivery() {
        while (true) {
            printHeader("MODULE 3 — Delivery Assignment");
            System.out.println("  1. Add a rider");
            System.out.println("  2. Assign best rider (nearest)");
            System.out.println("  3. View available riders");
            System.out.println("  0. Back");
            System.out.print("\nEnter choice: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": {
                    System.out.print("Rider name                   : "); String name = sc.nextLine().trim();
                    System.out.print("Distance from restaurant (km): ");
                    try {
                        int dist = Integer.parseInt(sc.nextLine().trim());
                        delivery.addRider(new Rider(nextRiderId++, name, dist));
                        FileManager.saveRiders(delivery.toList());
                    } catch (NumberFormatException e) { System.out.println("[!] Invalid distance."); }
                    break;
                }
                case "2": {
                    Rider best = delivery.assignBestRider();
                    if (best != null) {
                        System.out.print("Link to which Order ID? (leave blank to skip): ");
                        String oid = sc.nextLine().trim();
                        if (!oid.isEmpty()) {
                            try {
                                int orderId = Integer.parseInt(oid);
                                dataSystem.updateOrderStatus(orderId, "Assigned to " + best.name);
                                FileManager.saveOrders(dataSystem.getAllOrders());
                            } catch (NumberFormatException e) { System.out.println("[!] Invalid order ID."); }
                        }
                        FileManager.saveRiders(delivery.toList());
                    }
                    break;
                }
                case "3": delivery.displayRiders(); break;
                case "0": return;
                default:  System.out.println("[!] Invalid option.");
            }
            pause();
        }
    }

    // ================================================================
    //  MODULE 4 — ROUTE OPTIMIZATION (Graph + Dijkstra)
    // ================================================================
    static void moduleRoute() {
        String[] locationNames = {
            "Warehouse",        // 0
            "Restaurant A",     // 1
            "Restaurant B",     // 2
            "Junction X",       // 3
            "Customer Alice",   // 4
            "Customer Charlie"  // 5
        };

        while (true) {
            printHeader("MODULE 4 — Route Optimization (Dijkstra)");
            System.out.println("  Locations:");
            for (int i = 0; i < locationNames.length; i++)
                System.out.println("    " + i + " = " + locationNames[i]);
            System.out.println();
            System.out.println("  1. Find shortest route between two locations");
            System.out.println("  2. Show all edges (map)");
            System.out.println("  0. Back");
            System.out.print("\nEnter choice: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": {
                    try {
                        System.out.print("Source location index     : "); int src  = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Destination location index: "); int dest = Integer.parseInt(sc.nextLine().trim());
                        if (src < 0 || src >= locationNames.length || dest < 0 || dest >= locationNames.length)
                            System.out.println("[!] Index out of range.");
                        else
                            map.dijkstra(src, dest);
                    } catch (NumberFormatException e) { System.out.println("[!] Please enter valid numbers."); }
                    break;
                }
                case "2": map.displayEdges(); break;
                case "0": return;
                default:  System.out.println("[!] Invalid option.");
            }
            pause();
        }
    }

    // ================================================================
    //  MODULE 5 — FOOD MENU (BST)
    // ================================================================
    static void moduleFood() {
        while (true) {
            printHeader("MODULE 5 — Food Search & Menu (BST)");
            System.out.println("  1. Add food item");
            System.out.println("  2. Search food item");
            System.out.println("  3. Display full menu (sorted A-Z)");
            System.out.println("  0. Back");
            System.out.print("\nEnter choice: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": {
                    System.out.print("Food name : "); String name = sc.nextLine().trim();
                    System.out.print("Price (RM): ");
                    try {
                        double price = Double.parseDouble(sc.nextLine().trim());
                        menu.insert(new FoodItem(name, price));
                        FileManager.saveFoodMenu(menu.toList());
                    } catch (NumberFormatException e) { System.out.println("[!] Invalid price."); }
                    break;
                }
                case "2": {
                    System.out.print("Search food name: "); String name = sc.nextLine().trim();
                    menu.search(name);
                    break;
                }
                case "3": menu.inOrderTraversal(); break;
                case "0": return;
                default:  System.out.println("[!] Invalid option.");
            }
            pause();
        }
    }

    // ================================================================
    //  MODULE 6 — DATA RETRIEVAL (HashMap)
    // ================================================================
    static void moduleData() {
        while (true) {
            printHeader("MODULE 6 — Data Retrieval (HashMap)");
            System.out.println("  1. Look up user profile by ID");
            System.out.println("  2. Look up order record by ID");
            System.out.println("  3. Update order status");
            System.out.println("  0. Back");
            System.out.print("\nEnter choice: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": {
                    System.out.print("Enter Customer ID: ");
                    try { dataSystem.getUserProfile(Integer.parseInt(sc.nextLine().trim())); }
                    catch (NumberFormatException e) { System.out.println("[!] Invalid ID."); }
                    break;
                }
                case "2": {
                    System.out.print("Enter Order ID: ");
                    try { dataSystem.getOrder(Integer.parseInt(sc.nextLine().trim())); }
                    catch (NumberFormatException e) { System.out.println("[!] Invalid ID."); }
                    break;
                }
                case "3": {
                    System.out.print("Enter Order ID : ");
                    try {
                        int oid = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("New status     : "); String status = sc.nextLine().trim();
                        dataSystem.updateOrderStatus(oid, status);
                        FileManager.saveOrders(dataSystem.getAllOrders());
                    } catch (NumberFormatException e) { System.out.println("[!] Invalid ID."); }
                    break;
                }
                case "0": return;
                default:  System.out.println("[!] Invalid option.");
            }
            pause();
        }
    }

    // ================================================================
    //  HELPERS
    // ================================================================
    static void seedGraph() {
        String[] locations = {"Warehouse","Restaurant A","Restaurant B","Junction X","Customer Alice","Customer Charlie"};
        map = new Graph(6, locations);
        map.addEdge(0, 1,  4);
        map.addEdge(0, 2,  7);
        map.addEdge(1, 3,  3);
        map.addEdge(2, 3,  2);
        map.addEdge(3, 4,  5);
        map.addEdge(3, 5,  8);
        map.addEdge(1, 4, 10);
        map.addEdge(2, 5,  6);
    }

    static void seedMenu() {
        menu.insert(new FoodItem("Nasi Lemak",       8.50));
        menu.insert(new FoodItem("Burger",          12.00));
        menu.insert(new FoodItem("Sushi Set A",     25.00));
        menu.insert(new FoodItem("Miso Soup",        6.00));
        menu.insert(new FoodItem("Teh Tarik",        3.50));
        menu.insert(new FoodItem("Ayam Goreng",      9.00));
        menu.insert(new FoodItem("Rendang Chicken", 14.00));
    }

    static void printHeader(String title) {
        System.out.println("\n========================================");
        System.out.println("  " + title);
        System.out.println("========================================");
    }

    static void pause() {
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }
}
