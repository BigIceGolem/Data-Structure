package AppSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

/*
 * MainDashboard.java
 * ------------------
 * The main window (GUI) of the application built using Java Swing.
 * All the data structures (linked lists, BST, queue, heap, graph, hashmap)
 * are connected here and controlled through button clicks on screen.
 *
 * This class:
 *   - Sets up all the backend data structures as shared (static) fields
 *   - Builds the tabbed window with panels for each feature
 *   - Loads saved data from CSV files when the app starts
 *   - Saves all data back to CSV files when the app closes
 *   - Redirects System.out so all console messages appear in the dark text box
 */
public class MainDashboard extends JFrame {

    // -----------------------------------------------------------------------
    //  SHARED DATA STRUCTURES
    //  These are static so every part of the program uses the same single copy
    // -----------------------------------------------------------------------

    // Linked list of all registered customers
    static CustomerLinkedList   customers   = new CustomerLinkedList();

    // Linked list of all partner restaurants
    static RestaurantLinkedList restaurants = new RestaurantLinkedList();

    // FIFO queue of confirmed orders waiting to be processed
    static OrderQueue           orderQueue  = new OrderQueue();

    // Min-heap of available riders, sorted by distance (nearest first)
    static DeliveryAssignment   delivery    = new DeliveryAssignment();

    // Binary search tree of food items, sorted alphabetically by name
    static FoodBST              menu        = new FoodBST();

    // HashMap-based system for fast lookup of user profiles and order records
    static DataRetrievalSystem  dataSystem  = new DataRetrievalSystem();

    // Graph representing the delivery map — built dynamically from current data
    static Graph                map;

    // -----------------------------------------------------------------------
    //  ID COUNTERS
    //  Each new record gets a unique ID. We count upward from a starting number.
    // -----------------------------------------------------------------------
    static int nextCustomerId   = 1;    // customers start at ID 1
    static int nextRestaurantId = 101;  // restaurants start at ID 101
    static int nextRiderId      = 201;  // riders start at ID 201
    static int nextOrderId      = 1001; // orders start at ID 1001

    // -----------------------------------------------------------------------
    //  GUI COMPONENTS
    // -----------------------------------------------------------------------

    // The dark text area at the bottom where all system messages are displayed
    private JTextArea consoleArea;

    // The shopping cart for the customer currently placing an order
    // null means no order is in progress
    private OrderCart currentCart = null;

    // Dropdown menus (combo boxes) that get refreshed when restaurants/customers change
    private JComboBox<String> comboOrderRest;  // restaurant picker on the Order tab
    private JComboBox<String> comboOrderFood;  // food item picker on the Order tab
    private JComboBox<String> comboMenuRest;   // restaurant picker on the Food Menu tab
    private JComboBox<String> comboRoutesSrc;  // source (restaurant) picker on the Routes tab
    private JComboBox<String> comboRoutesDest; // destination (customer) picker on the Routes tab

    // -----------------------------------------------------------------------
    //  CONSTRUCTOR — builds the window and starts everything up
    // -----------------------------------------------------------------------
    public MainDashboard() {
        super("GoodTech Smart Food Delivery & Order Management System");
        setSize(1050, 750);
        setLocationRelativeTo(null); // centre the window on the screen
        // DO_NOTHING_ON_CLOSE means we intercept the close event ourselves
        // so we can save data before the program exits
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // When the user clicks the X button to close, save everything first then exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveAllData(); // write all data to CSV files
                System.exit(0);
            }
        });

        // Load data from CSV files (or use seed data if files don't exist yet)
        loadAllData();

        // Use BorderLayout: tabs go at the top (NORTH), console at the centre
        setLayout(new BorderLayout(10, 10));

        // Create a tabbed pane — each tab is a different feature of the system
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Order & Delivery Workflow", createOrderDeliveryPanel());
        tabbedPane.addTab("Management", createManagementPanel());
        tabbedPane.addTab("Routes", createRoutesPanel());
        tabbedPane.addTab("Food Menu", createMenuPanel());
        tabbedPane.addTab("Data Lookup", createDataPanel());

        // Every time the user switches to a different tab, refresh the dropdowns
        // and rebuild the map in case new restaurants or customers were added
        tabbedPane.addChangeListener(e -> {
            refreshDropdowns();
            buildDynamicMap();
        });

        add(tabbedPane, BorderLayout.NORTH);

        // Set up the dark console text area at the bottom of the window
        consoleArea = new JTextArea();
        consoleArea.setEditable(false); // user can read but not type in the console
        consoleArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        consoleArea.setBackground(new Color(30, 30, 30));   // very dark background
        consoleArea.setForeground(new Color(200, 200, 200)); // light grey text
        JScrollPane scrollPane = new JScrollPane(consoleArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("System Console"));
        add(scrollPane, BorderLayout.CENTER);

        // Button to clear all the text from the console
        JButton btnClear = new JButton("Clear Console");
        btnClear.addActionListener(e -> consoleArea.setText(""));
        add(btnClear, BorderLayout.SOUTH);

        // Make all System.out.println() calls show up in the console text area
        // instead of the default terminal window
        redirectSystemStreams();

        refreshDropdowns();   // fill in the dropdown menus with current data
        buildDynamicMap();    // build the delivery map graph
        System.out.println("System Initialized. Welcome to the GoodTech Delivery Dashboard.\n");
    }

    /*
     * Redirects System.out and System.err to the on-screen console text area.
     * After this is called, every System.out.println() in the whole program
     * will appear in the dark text box instead of the terminal.
     *
     * SwingUtilities.invokeLater() is needed because Swing UI updates must
     * happen on the "Event Dispatch Thread" (Swing's dedicated UI thread).
     */
    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                // Each character printed is appended to the console area
                SwingUtilities.invokeLater(() -> {
                    consoleArea.append(String.valueOf((char) b));
                    // Auto-scroll to the bottom so the latest message is always visible
                    consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                });
            }
        };
        System.setOut(new PrintStream(out, true)); // redirect standard output
        System.setErr(new PrintStream(out, true)); // redirect error output too
    }

    /*
     * Refreshes all dropdown menus with the latest data from the linked lists.
     * Called whenever a restaurant or customer is added, or when switching tabs.
     * We remember what was selected before refreshing so the selection doesn't reset.
     */
    private void refreshDropdowns() {
        // If the dropdowns haven't been created yet, do nothing
        if (comboOrderRest == null || comboMenuRest == null || comboRoutesSrc == null || comboRoutesDest == null) return;

        // Remember what was selected in each dropdown before we clear them
        String selectedOrderRest = (String) comboOrderRest.getSelectedItem();
        String selectedMenuRest  = (String) comboMenuRest.getSelectedItem();
        String selectedSrc       = (String) comboRoutesSrc.getSelectedItem();
        String selectedDest      = (String) comboRoutesDest.getSelectedItem();

        // Clear all dropdown menus
        comboOrderRest.removeAllItems();
        comboMenuRest.removeAllItems();
        comboRoutesSrc.removeAllItems();
        comboRoutesDest.removeAllItems();

        // Re-populate restaurant dropdowns with all current restaurants
        for (Restaurant r : restaurants.toList()) {
            comboOrderRest.addItem(r.name);
            comboMenuRest.addItem(r.name);
            comboRoutesSrc.addItem(r.name);
        }

        // Re-populate the destination dropdown with all current customers
        for (Customer c : customers.toList()) {
            comboRoutesDest.addItem(c.name);
        }

        // Restore the previous selections if they still exist
        if (selectedOrderRest != null) comboOrderRest.setSelectedItem(selectedOrderRest);
        if (selectedMenuRest  != null) comboMenuRest.setSelectedItem(selectedMenuRest);
        if (selectedSrc       != null) comboRoutesSrc.setSelectedItem(selectedSrc);
        if (selectedDest      != null) comboRoutesDest.setSelectedItem(selectedDest);

        // Also refresh the food item dropdown based on the selected restaurant
        updateFoodDropdown();
    }

    /*
     * Updates the food item dropdown to only show items from the currently selected restaurant.
     * Food items are stored in the BST as "RestaurantName - FoodName".
     * We filter for items that start with the selected restaurant's name.
     */
    private void updateFoodDropdown() {
        if (comboOrderFood == null || comboOrderRest == null) return;
        comboOrderFood.removeAllItems();
        String selectedRest = (String) comboOrderRest.getSelectedItem();
        if (selectedRest != null) {
            for (FoodItem f : menu.toList()) {
                // Only include items that belong to the selected restaurant
                if (f.name.startsWith(selectedRest + " - ")) {
                    // Strip the "RestaurantName - " prefix so only the food name is shown in the dropdown
                    comboOrderFood.addItem(f.name.substring(selectedRest.length() + 3));
                }
            }
        }
    }

    /*
     * Builds the delivery route map (Graph) from the current list of restaurants and customers.
     * Each restaurant and customer becomes a node in the graph.
     * Roads (edges) are generated between them with approximate distances.
     *
     * This is called every time a new restaurant or customer is added,
     * so the map is always up to date.
     */
    private void buildDynamicMap() {
        List<Restaurant> rList = restaurants.toList();
        List<Customer>   cList = customers.toList();
        int size = rList.size() + cList.size(); // total number of nodes in the graph

        if (size == 0) return; // nothing to build if there's no data yet

        // Create names for each node: restaurants are labelled [Rest], customers are [Cust]
        String[] nodeNames = new String[size];
        int index = 0;
        for (Restaurant r : rList) nodeNames[index++] = "[Rest] " + r.name;
        for (Customer   c : cList)  nodeNames[index++] = "[Cust] " + c.name;

        // Create a fresh graph with those nodes
        map = new Graph(size, nodeNames);

        // Connect restaurants in a ring so every restaurant can reach the next one
        // The formula (i * 3) % 4 + 2 produces varied distances between 2 and 5 km
        for (int i = 0; i < rList.size(); i++) {
            int next = (i + 1) % rList.size(); // wrap around to connect last to first
            map.addEdge(i, next, (i * 3) % 4 + 2);
        }

        // Add a shortcut road across the middle if there are more than 3 restaurants
        if (rList.size() > 3) {
            map.addEdge(0, rList.size() / 2, 6);
        }

        // Connect each customer to two different restaurants to give multiple possible routes
        for (int j = 0; j < cList.size(); j++) {
            int cIndex = rList.size() + j; // the customer's index in the graph
            int r1 = j % rList.size();           // first restaurant to connect to
            int r2 = (j + 2) % rList.size();     // second restaurant to connect to

            map.addEdge(r1, cIndex, (j * 2) % 5 + 3); // distance between 3 and 7 km
            map.addEdge(r2, cIndex, (j * 3) % 4 + 4); // distance between 4 and 7 km
        }
    }

    /*
     * Creates the "Order & Delivery Workflow" tab panel.
     * This tab has two columns:
     *   Left  — build a customer's cart and confirm the order (uses Stack + Queue)
     *   Right — process the next order and assign the nearest rider (uses Min-Heap)
     */
    private JPanel createOrderDeliveryPanel() {
        // Two columns side by side
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---------------------------------------------------------------
        //  LEFT COLUMN: Build and confirm a customer order
        // ---------------------------------------------------------------
        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBorder(BorderFactory.createTitledBorder("1. Receive Customer Order (Stack)"));

        // Row 1: enter customer name and start their cart
        JPanel pnlRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtCustName = new JTextField(15);
        JButton btnStartCart = new JButton("Start Cart");
        pnlRow1.add(new JLabel("Customer Name:"));
        pnlRow1.add(txtCustName);
        pnlRow1.add(btnStartCart);

        // Row 2: choose which restaurant to order from
        JPanel pnlRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboOrderRest = new JComboBox<>();
        comboOrderRest.setPreferredSize(new Dimension(200, 25));
        // When the restaurant changes, update the food dropdown to match
        comboOrderRest.addActionListener(e -> updateFoodDropdown());
        pnlRow2.add(new JLabel("Restaurant:"));
        pnlRow2.add(comboOrderRest);

        // Row 3: choose a food item and add it to the cart
        JPanel pnlRow3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboOrderFood = new JComboBox<>();
        comboOrderFood.setPreferredSize(new Dimension(200, 25));
        JButton btnAddItem = new JButton("Add Item");
        pnlRow3.add(new JLabel("Food Item:"));
        pnlRow3.add(comboOrderFood);
        pnlRow3.add(btnAddItem);

        // Row 4: cart management buttons
        JPanel pnlRow4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnUndo    = new JButton("Undo Last Item");
        JButton btnViewCart = new JButton("View Cart");
        JButton btnConfirm  = new JButton("Confirm Order (Send to Queue)");
        pnlRow4.add(btnUndo);
        pnlRow4.add(btnViewCart);
        pnlRow4.add(btnConfirm);

        orderPanel.add(pnlRow1);
        orderPanel.add(pnlRow2);
        orderPanel.add(pnlRow3);
        orderPanel.add(pnlRow4);

        // "Start Cart" — creates a new customer and opens an empty cart for them
        btnStartCart.addActionListener(e -> {
            String cName = txtCustName.getText().trim();
            if (!cName.isEmpty()) {
                int newId = nextCustomerId++; // assign the next available ID
                customers.addCustomer(newId, cName, "Client Location");
                // Also create a user profile so order history can be tracked
                dataSystem.addUserProfile(new UserProfile(newId, cName,
                        cName.toLowerCase().replace(" ", "") + "@email.com"));
                currentCart = new OrderCart(newId); // open a new cart for this customer
                String formattedId = String.format("#%04d", newId); // e.g. "#0001"
                System.out.println("[GUI] New cart started for " + cName + " (ID: " + formattedId + ")");
                FileManager.saveCustomers(customers.toList()); // save the new customer to file
                refreshDropdowns();
                buildDynamicMap();
            } else {
                System.out.println("[!] Please enter a Customer Name.");
            }
        });

        // "Add Item" — pushes the selected food item onto the cart stack
        btnAddItem.addActionListener(e -> {
            if (currentCart != null) {
                String selectedRest = (String) comboOrderRest.getSelectedItem();
                String selectedFood = (String) comboOrderFood.getSelectedItem();
                if (selectedRest != null && selectedFood != null) {
                    // Store the full name "Restaurant - Food" so we know where it's from
                    currentCart.addItem(selectedRest + " - " + selectedFood);
                } else {
                    System.out.println("[!] No food item selected.");
                }
            } else {
                System.out.println("[!] Please start a cart first.");
            }
        });

        // "Undo Last Item" — pops the most recent item off the cart stack
        btnUndo.addActionListener(e -> { if (currentCart != null) currentCart.undoLastItem(); });

        // "View Cart" — shows what's currently in the cart without removing anything
        btnViewCart.addActionListener(e -> { if (currentCart != null) currentCart.displayCart(); });

        // "Confirm Order" — finalises the cart, creates an Order, and adds it to the queue
        btnConfirm.addActionListener(e -> {
            if (currentCart != null) {
                Order order = currentCart.confirmOrder(nextOrderId++); // lock in the cart contents
                orderQueue.enqueue(order);           // add to the FIFO order queue
                dataSystem.storeOrder(order);        // save to HashMap for fast lookup
                FileManager.saveOrders(dataSystem.getAllOrders()); // persist to file
                currentCart = null;                  // cart is now empty for the next customer
                txtCustName.setText("");             // clear the name field
            } else {
                System.out.println("[!] No active cart to confirm.");
            }
        });

        // ---------------------------------------------------------------
        //  RIGHT COLUMN: Process orders and assign riders
        // ---------------------------------------------------------------
        JPanel dispatchPanel = new JPanel();
        dispatchPanel.setLayout(new BoxLayout(dispatchPanel, BoxLayout.Y_AXIS));
        dispatchPanel.setBorder(BorderFactory.createTitledBorder("2. Dispatch & Rider Assignment (Min-Heap)"));

        JButton btnViewQueue = new JButton("View Pending Order Queue");
        JButton btnProcess   = new JButton("Process Next Order & Assign Rider");

        JTextField txtRiderName = new JTextField(12);
        JTextField txtRiderDist = new JTextField(5);
        JButton btnAddRider        = new JButton("Register New Rider on Map");
        JButton btnAvailableRiders = new JButton("View Rider Heap");

        JPanel dRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dRow1.add(btnViewQueue);
        dRow1.add(btnProcess);

        JPanel dRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dRow2.add(new JLabel("Rider Name:"));
        dRow2.add(txtRiderName);
        dRow2.add(new JLabel("Dist (km):"));
        dRow2.add(txtRiderDist);
        dRow2.add(btnAddRider);

        JPanel dRow3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dRow3.add(btnAvailableRiders);

        dispatchPanel.add(dRow1);
        dispatchPanel.add(dRow2);
        dispatchPanel.add(dRow3);

        // "View Pending Order Queue" — shows all orders waiting to be processed
        btnViewQueue.addActionListener(e -> orderQueue.displayQueue());

        // "Process Next Order & Assign Rider" — dequeues the next order and picks the nearest rider
        btnProcess.addActionListener(e -> {
            Order processed = orderQueue.processNextOrder(); // take the first order from the queue
            if (processed != null) {
                Rider best = delivery.assignBestRider(); // pick the nearest rider from the heap
                if (best != null) {
                    // Update the order's status to show which rider was assigned
                    dataSystem.updateOrderStatus(processed.orderId, "Assigned to " + best.name);
                    System.out.println("[System] Order " + processed.orderId
                            + " paired with nearest rider: " + best.name);
                } else {
                    // No riders available — mark the order as preparing and wait
                    dataSystem.updateOrderStatus(processed.orderId, "Preparing (Pending Rider)");
                    System.out.println("[System] Order " + processed.orderId
                            + " sent to kitchen. No riders available right now.");
                }
                FileManager.saveOrders(dataSystem.getAllOrders()); // save updated order statuses
                FileManager.saveRiders(delivery.toList());         // save updated rider list
            }
        });

        // "Register New Rider" — adds a new rider to the min-heap
        btnAddRider.addActionListener(e -> {
            try {
                String name = txtRiderName.getText().trim();
                int dist    = Integer.parseInt(txtRiderDist.getText().trim());
                if (!name.isEmpty()) {
                    delivery.addRider(new Rider(nextRiderId++, name, dist));
                    FileManager.saveRiders(delivery.toList()); // save to file
                    txtRiderName.setText(""); txtRiderDist.setText("");
                }
            } catch (NumberFormatException ex) {
                System.out.println("[!] Invalid Distance.");
            }
        });

        // "View Rider Heap" — shows all riders currently in the min-heap
        btnAvailableRiders.addActionListener(e -> delivery.displayRiders());

        panel.add(orderPanel);
        panel.add(dispatchPanel);
        return panel;
    }

    /*
     * Creates the "Management" tab panel.
     * Lets the user add and view customers and partner restaurants.
     */
    private JPanel createManagementPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10)); // 2 rows: customers on top, restaurants below
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---------------------------------------------------------------
        //  TOP HALF: Manage customers
        // ---------------------------------------------------------------
        JPanel custPanel = new JPanel();
        custPanel.setLayout(new BoxLayout(custPanel, BoxLayout.Y_AXIS));
        custPanel.setBorder(BorderFactory.createTitledBorder("Registered Customers"));

        JTextField txtCustName = new JTextField(15);
        JTextField txtCustAddr = new JTextField(20);
        JButton btnAddCust  = new JButton("Add Customer");
        JButton btnViewCust = new JButton("View All");

        JPanel cRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cRow1.add(new JLabel("Name:")); cRow1.add(txtCustName);
        cRow1.add(new JLabel("Address:")); cRow1.add(txtCustAddr);

        JPanel cRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cRow2.add(btnAddCust); cRow2.add(btnViewCust);

        custPanel.add(cRow1); custPanel.add(cRow2);

        // "Add Customer" — adds to the linked list and creates a user profile
        btnAddCust.addActionListener(e -> {
            String name = txtCustName.getText().trim();
            String addr = txtCustAddr.getText().trim();
            if (!name.isEmpty() && !addr.isEmpty()) {
                customers.addCustomer(nextCustomerId, name, addr);
                dataSystem.addUserProfile(new UserProfile(nextCustomerId, name,
                        name.toLowerCase() + "@email.com"));
                nextCustomerId++;
                FileManager.saveCustomers(customers.toList());
                txtCustName.setText(""); txtCustAddr.setText("");
                refreshDropdowns();
                buildDynamicMap();
            }
        });

        // "View All" — prints the full customer linked list to the console
        btnViewCust.addActionListener(e -> customers.displayCustomers());

        // ---------------------------------------------------------------
        //  BOTTOM HALF: Manage restaurants
        // ---------------------------------------------------------------
        JPanel restPanel = new JPanel();
        restPanel.setLayout(new BoxLayout(restPanel, BoxLayout.Y_AXIS));
        restPanel.setBorder(BorderFactory.createTitledBorder("Partner Restaurants"));

        JTextField txtRestName    = new JTextField(15);
        JTextField txtRestCuisine = new JTextField(15);
        JButton btnAddRest  = new JButton("Add Restaurant");
        JButton btnViewRest = new JButton("View All Restaurants");

        JPanel rRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rRow1.add(new JLabel("Name:")); rRow1.add(txtRestName);
        rRow1.add(new JLabel("Cuisine:")); rRow1.add(txtRestCuisine);

        JPanel rRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rRow2.add(btnAddRest); rRow2.add(btnViewRest);

        restPanel.add(rRow1); restPanel.add(rRow2);

        // "Add Restaurant" — adds to the linked list and refreshes dropdowns and the map
        btnAddRest.addActionListener(e -> {
            String name    = txtRestName.getText().trim();
            String cuisine = txtRestCuisine.getText().trim();
            if (!name.isEmpty() && !cuisine.isEmpty()) {
                restaurants.addRestaurant(nextRestaurantId++, name, cuisine);
                FileManager.saveRestaurants(restaurants.toList());
                txtRestName.setText(""); txtRestCuisine.setText("");
                refreshDropdowns();
                buildDynamicMap();
            }
        });

        // "View All Restaurants" — prints the full restaurant linked list to the console
        btnViewRest.addActionListener(e -> restaurants.displayRestaurants());

        panel.add(custPanel);
        panel.add(restPanel);
        return panel;
    }

    /*
     * Creates the "Routes" tab panel.
     * Lets the user find the shortest delivery route between a restaurant and a customer
     * using Dijkstra's algorithm on the dynamic graph.
     */
    private JPanel createRoutesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        comboRoutesSrc  = new JComboBox<>(); // source: pick a restaurant
        comboRoutesDest = new JComboBox<>(); // destination: pick a customer
        JButton btnFindRoute  = new JButton("Find Shortest Route");
        JButton btnViewEdges  = new JButton("View All Road Connections");

        JPanel rRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rRow1.add(new JLabel("Pickup (Restaurant):")); rRow1.add(comboRoutesSrc);
        rRow1.add(new JLabel("Dropoff (Customer):")); rRow1.add(comboRoutesDest);

        JPanel rRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rRow2.add(btnFindRoute); rRow2.add(btnViewEdges);

        panel.add(rRow1);
        panel.add(rRow2);

        // "Find Shortest Route" — runs Dijkstra's algorithm on the current map
        btnFindRoute.addActionListener(e -> {
            int srcListIndex  = comboRoutesSrc.getSelectedIndex();  // index of the selected restaurant
            int destListIndex = comboRoutesDest.getSelectedIndex(); // index of the selected customer

            if (srcListIndex != -1 && destListIndex != -1 && map != null) {
                // Customers are stored after restaurants in the graph, so we offset the index
                int graphDestIndex = restaurants.toList().size() + destListIndex;
                map.dijkstra(srcListIndex, graphDestIndex); // run Dijkstra and print the result
            } else {
                System.out.println("[!] Ensure both a source and destination are selected.");
            }
        });

        // "View All Road Connections" — prints every edge in the current delivery map graph
        btnViewEdges.addActionListener(e -> {
            if (map != null) map.displayEdges();
            else System.out.println("[!] Network map is empty.");
        });

        return panel;
    }

    /*
     * Creates the "Food Menu" tab panel.
     * Lets the user add new food items to the BST and search for existing ones.
     */
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        comboMenuRest = new JComboBox<>();        // pick which restaurant the food belongs to
        JTextField txtName  = new JTextField(15); // the name of the new food item
        JTextField txtPrice = new JTextField(5);  // the price of the new food item
        JButton btnAddFood  = new JButton("Add Food");
        JTextField txtSearch = new JTextField(15); // name to search for in the BST
        JButton btnSearch    = new JButton("Search Food");
        JButton btnViewMenu  = new JButton("View Full Menu (A-Z)");

        // Row 1: restaurant picker, food name, and price
        JPanel mRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mRow1.add(new JLabel("Restaurant:")); mRow1.add(comboMenuRest);
        mRow1.add(new JLabel("Food Name:")); mRow1.add(txtName);
        mRow1.add(new JLabel("Price (RM):")); mRow1.add(txtPrice);

        // Row 2: add food button
        JPanel mRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mRow2.add(btnAddFood);

        // Row 3: search field and view-all button
        JPanel mRow3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mRow3.add(new JLabel("Search:")); mRow3.add(txtSearch);
        mRow3.add(btnSearch); mRow3.add(btnViewMenu);

        panel.add(mRow1);
        panel.add(mRow2);
        panel.add(mRow3);

        // "Add Food" — inserts a new FoodItem into the BST and saves the updated menu
        btnAddFood.addActionListener(e -> {
            try {
                String rName = (String) comboMenuRest.getSelectedItem(); // restaurant name
                String fName = txtName.getText().trim();                 // food name
                double price = Double.parseDouble(txtPrice.getText().trim());
                if (rName != null && !fName.isEmpty()) {
                    // Store as "RestaurantName - FoodName" so we can group by restaurant later
                    menu.insert(new FoodItem(rName + " - " + fName, price));
                    FileManager.saveFoodMenu(menu.toList()); // persist to file
                    txtName.setText(""); txtPrice.setText(""); // clear the input fields
                    refreshDropdowns(); // update the ordering dropdown with the new item
                }
            } catch (NumberFormatException ex) {
                System.out.println("[!] Invalid Price.");
            }
        });

        // "Search Food" — searches the BST for an item by name (O(log n))
        btnSearch.addActionListener(e -> menu.search(txtSearch.getText().trim()));

        // "View Full Menu (A-Z)" — prints the entire menu in alphabetical order using in-order traversal
        btnViewMenu.addActionListener(e -> menu.inOrderTraversal());

        return panel;
    }

    /*
     * Creates the "Data Lookup" tab panel.
     * Uses the HashMap in DataRetrievalSystem to look up user profiles and orders in O(1) time.
     */
    private JPanel createDataPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField txtUserId      = new JTextField(8);
        JButton btnSearchUser     = new JButton("Find User Profile");
        JButton btnViewAllUsers   = new JButton("View All Profiles");

        JTextField txtOrderId     = new JTextField(8);
        JButton btnSearchOrder    = new JButton("Find Order Record");

        JTextField txtUpdateStatus = new JTextField(12);
        JButton btnUpdateStatus    = new JButton("Update Order Status");

        // Row 1: look up a user profile by their ID
        JPanel dRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dRow1.add(new JLabel("User ID:")); dRow1.add(txtUserId);
        dRow1.add(btnSearchUser); dRow1.add(btnViewAllUsers);

        // Row 2: look up an order record by order ID
        JPanel dRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dRow2.add(new JLabel("Order ID:")); dRow2.add(txtOrderId);
        dRow2.add(btnSearchOrder);

        // Row 3: update the status of an existing order
        JPanel dRow3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dRow3.add(new JLabel("New Status:")); dRow3.add(txtUpdateStatus);
        dRow3.add(btnUpdateStatus);

        panel.add(dRow1);
        panel.add(dRow2);
        panel.add(dRow3);

        // "Find User Profile" — retrieves a single user profile from the HashMap by ID
        btnSearchUser.addActionListener(e -> {
            try { dataSystem.getUserProfile(Integer.parseInt(txtUserId.getText().trim())); }
            catch (NumberFormatException ex) { System.out.println("[!] Invalid User ID."); }
        });

        // "View All Profiles" — loops through all customers and retrieves each profile
        btnViewAllUsers.addActionListener(e -> {
            System.out.println("\n--- All Registered User Profiles ---");
            for (Customer c : customers.toList()) {
                dataSystem.getUserProfile(c.id);
            }
        });

        // "Find Order Record" — retrieves a single order record from the HashMap by order ID
        btnSearchOrder.addActionListener(e -> {
            try { dataSystem.getOrder(Integer.parseInt(txtOrderId.getText().trim())); }
            catch (NumberFormatException ex) { System.out.println("[!] Invalid Order ID."); }
        });

        // "Update Order Status" — changes the status text of an order (e.g. to "Delivered")
        btnUpdateStatus.addActionListener(e -> {
            try {
                int    oid    = Integer.parseInt(txtOrderId.getText().trim());
                String status = txtUpdateStatus.getText().trim();
                if (!status.isEmpty()) {
                    dataSystem.updateOrderStatus(oid, status);
                    FileManager.saveOrders(dataSystem.getAllOrders()); // save the updated status
                }
            } catch (NumberFormatException ex) {
                System.out.println("[!] Enter Order ID to update.");
            }
        });

        return panel;
    }

    /*
     * Loads all data from CSV files when the app starts.
     * If a file doesn't exist yet (first run), we call the seed methods
     * to populate the system with default example data.
     */
    static void loadAllData() {
        // Load customers — or use seed data if no file exists
        List<Customer> savedCustomers = FileManager.loadCustomers();
        if (!savedCustomers.isEmpty()) {
            for (Customer c : savedCustomers) {
                customers.addCustomer(c.id, c.name, c.address);
                // Recreate the user profile for each loaded customer
                dataSystem.addUserProfile(new UserProfile(c.id, c.name,
                        c.name.toLowerCase() + "@email.com"));
                // Make sure the ID counter starts above any existing ID
                if (c.id >= nextCustomerId) nextCustomerId = c.id + 1;
            }
        } else {
            seedCustomers(); // no saved data — add default customers
        }

        // Load restaurants — or use seed data
        List<Restaurant> savedRestaurants = FileManager.loadRestaurants();
        if (!savedRestaurants.isEmpty()) {
            for (Restaurant r : savedRestaurants) {
                restaurants.addRestaurant(r.id, r.name, r.cuisine);
                if (r.id >= nextRestaurantId) nextRestaurantId = r.id + 1;
            }
        } else {
            seedRestaurants();
        }

        // Load food menu — or use seed data
        List<FoodItem> savedFood = FileManager.loadFoodMenu();
        if (!savedFood.isEmpty()) {
            for (FoodItem f : savedFood) menu.insert(f);
        } else {
            seedMenu();
        }

        // Load previous orders — no seed data for orders (they come from real usage)
        List<OrderRecord> savedOrders = FileManager.loadOrders();
        if (!savedOrders.isEmpty()) {
            for (OrderRecord o : savedOrders) {
                dataSystem.loadOrderRecord(o);
                if (o.orderId >= nextOrderId) nextOrderId = o.orderId + 1;
            }
        }

        // Load riders — or use seed data
        List<Rider> savedRiders = FileManager.loadRiders();
        if (!savedRiders.isEmpty()) {
            for (Rider r : savedRiders) {
                delivery.addRider(r);
                if (r.id >= nextRiderId) nextRiderId = r.id + 1;
            }
        } else {
            seedRiders();
        }
    }

    /*
     * Saves all current data to CSV files.
     * Called automatically when the user closes the app.
     */
    static void saveAllData() {
        FileManager.saveCustomers(customers.toList());
        FileManager.saveRestaurants(restaurants.toList());
        FileManager.saveFoodMenu(menu.toList());
        FileManager.saveOrders(dataSystem.getAllOrders());
        FileManager.saveRiders(delivery.toList());
        System.out.println("\n[File] All data saved successfully.");
    }

    // -----------------------------------------------------------------------
    //  SEED METHODS — provide default data on first run
    // -----------------------------------------------------------------------

    // Adds 5 default customers so the app has something to work with on first launch
    static void seedCustomers() {
        customers.addCustomer(nextCustomerId++, "Alice Tan",    "123 Maple Street");
        customers.addCustomer(nextCustomerId++, "Bob Smith",    "456 Oak Avenue");
        customers.addCustomer(nextCustomerId++, "Charlie Lee",  "789 Pine Road");
        customers.addCustomer(nextCustomerId++, "Diana Prince", "101 Island Way");
        customers.addCustomer(nextCustomerId++, "Evan Wright",  "202 Tech Park");
    }

    // Adds 6 default restaurants
    static void seedRestaurants() {
        restaurants.addRestaurant(nextRestaurantId++, "Burger King",    "Fast Food");
        restaurants.addRestaurant(nextRestaurantId++, "Pizza Hut",      "Italian");
        restaurants.addRestaurant(nextRestaurantId++, "Sushi King",     "Japanese");
        restaurants.addRestaurant(nextRestaurantId++, "Mamak Corner",   "Malaysian");
        restaurants.addRestaurant(nextRestaurantId++, "Seoul Garden",   "Korean");
        restaurants.addRestaurant(nextRestaurantId++, "Salad Atelier",  "Healthy");
    }

    // Adds default food items for each restaurant into the BST
    static void seedMenu() {
        menu.insert(new FoodItem("Burger King - Whopper Meal",        18.50));
        menu.insert(new FoodItem("Burger King - Onion Rings",          6.00));
        menu.insert(new FoodItem("Burger King - Hershey's Sundae",     5.50));

        menu.insert(new FoodItem("Pizza Hut - Pepperoni Pizza",        24.00));
        menu.insert(new FoodItem("Pizza Hut - Hawaiian Chicken Pizza", 26.00));
        menu.insert(new FoodItem("Pizza Hut - Garlic Bread",           8.00));

        menu.insert(new FoodItem("Sushi King - Salmon Sashimi",       15.00));
        menu.insert(new FoodItem("Sushi King - Chicken Katsu Don",    16.50));

        menu.insert(new FoodItem("Mamak Corner - Nasi Kandar",        12.00));
        menu.insert(new FoodItem("Mamak Corner - Maggi Goreng",        7.50));
        menu.insert(new FoodItem("Mamak Corner - Teh Tarik",           3.50));

        menu.insert(new FoodItem("Seoul Garden - Beef Bulgogi",       22.00));
        menu.insert(new FoodItem("Seoul Garden - Kimchi Soup",        18.00));

        menu.insert(new FoodItem("Salad Atelier - Caesar Salad",      16.00));
        menu.insert(new FoodItem("Salad Atelier - Avocado Smoothie",  12.00));
    }

    // Adds 4 default riders into the min-heap (sorted automatically by distance)
    static void seedRiders() {
        delivery.addRider(new Rider(nextRiderId++, "Hafiz",   3));
        delivery.addRider(new Rider(nextRiderId++, "Wei Jie", 1));
        delivery.addRider(new Rider(nextRiderId++, "Muthu",   5));
        delivery.addRider(new Rider(nextRiderId++, "Adam",    8));
    }

    /*
     * The main entry point of the application.
     * SwingUtilities.invokeLater() ensures the window is created on the correct thread
     * (Swing requires all UI work to happen on the "Event Dispatch Thread").
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainDashboard().setVisible(true));
    }
}
