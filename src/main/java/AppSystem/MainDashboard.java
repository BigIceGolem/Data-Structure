package AppSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

public class MainDashboard extends JFrame {

    // --- Shared Backend Data ---
    static CustomerLinkedList   customers   = new CustomerLinkedList();
    static RestaurantLinkedList restaurants = new RestaurantLinkedList();
    static OrderQueue           orderQueue  = new OrderQueue();
    static DeliveryAssignment   delivery    = new DeliveryAssignment();
    static FoodBST              menu        = new FoodBST();
    static DataRetrievalSystem  dataSystem  = new DataRetrievalSystem();
    static Graph                map;

    static int nextCustomerId   = 1;
    static int nextRestaurantId = 101;
    static int nextRiderId      = 201;
    static int nextOrderId      = 1001;

    private JTextArea consoleArea;
    private OrderCart currentCart = null;

    // UI Components for dynamic refreshing
    private JComboBox<String> comboOrderRest;
    private JComboBox<String> comboOrderFood;
    private JComboBox<String> comboMenuRest;
    private JComboBox<String> comboRoutesSrc;
    private JComboBox<String> comboRoutesDest;

    public MainDashboard() {
        super("GoodTech Smart Food Delivery & Order Management System");
        setSize(1050, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Save data on exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveAllData();
                System.exit(0);
            }
        });

        // Initialize Data
        loadAllData();

        // --- Setup UI Layout ---
        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Order & Delivery Workflow", createOrderDeliveryPanel());
        tabbedPane.addTab("Management", createManagementPanel());
        tabbedPane.addTab("Routes", createRoutesPanel());
        tabbedPane.addTab("Food Menu", createMenuPanel());
        tabbedPane.addTab("Data Lookup", createDataPanel());

        // Refresh dropdowns and dynamic map when switching tabs
        tabbedPane.addChangeListener(e -> {
            refreshDropdowns();
            buildDynamicMap();
        });

        add(tabbedPane, BorderLayout.NORTH);

        // Console Output Area
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        consoleArea.setBackground(new Color(30, 30, 30));
        consoleArea.setForeground(new Color(200, 200, 200));
        JScrollPane scrollPane = new JScrollPane(consoleArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("System Console"));
        add(scrollPane, BorderLayout.CENTER);

        JButton btnClear = new JButton("Clear Console");
        btnClear.addActionListener(e -> consoleArea.setText(""));
        add(btnClear, BorderLayout.SOUTH);

        // Redirect System.out and System.err to the console area
        redirectSystemStreams();
        
        refreshDropdowns(); 
        buildDynamicMap();
        System.out.println("System Initialized. Welcome to the GoodTech Delivery Dashboard.\n");
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                SwingUtilities.invokeLater(() -> {
                    consoleArea.append(String.valueOf((char) b));
                    consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                });
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    private void refreshDropdowns() {
        if (comboOrderRest == null || comboMenuRest == null || comboRoutesSrc == null || comboRoutesDest == null) return;

        String selectedOrderRest = (String) comboOrderRest.getSelectedItem();
        String selectedMenuRest = (String) comboMenuRest.getSelectedItem();
        String selectedSrc = (String) comboRoutesSrc.getSelectedItem();
        String selectedDest = (String) comboRoutesDest.getSelectedItem();

        comboOrderRest.removeAllItems();
        comboMenuRest.removeAllItems();
        comboRoutesSrc.removeAllItems();
        comboRoutesDest.removeAllItems();

        for (Restaurant r : restaurants.toList()) {
            comboOrderRest.addItem(r.name);
            comboMenuRest.addItem(r.name);
            comboRoutesSrc.addItem(r.name);
        }

        for (Customer c : customers.toList()) {
            comboRoutesDest.addItem(c.name);
        }

        if (selectedOrderRest != null) comboOrderRest.setSelectedItem(selectedOrderRest);
        if (selectedMenuRest != null) comboMenuRest.setSelectedItem(selectedMenuRest);
        if (selectedSrc != null) comboRoutesSrc.setSelectedItem(selectedSrc);
        if (selectedDest != null) comboRoutesDest.setSelectedItem(selectedDest);
        
        updateFoodDropdown();
    }

    private void updateFoodDropdown() {
        if (comboOrderFood == null || comboOrderRest == null) return;
        comboOrderFood.removeAllItems();
        String selectedRest = (String) comboOrderRest.getSelectedItem();
        if (selectedRest != null) {
            for (FoodItem f : menu.toList()) {
                if (f.name.startsWith(selectedRest + " - ")) {
                    comboOrderFood.addItem(f.name.substring(selectedRest.length() + 3));
                }
            }
        }
    }

    private void buildDynamicMap() {
        List<Restaurant> rList = restaurants.toList();
        List<Customer> cList = customers.toList();
        int size = rList.size() + cList.size();
        
        if (size == 0) return;

        String[] nodeNames = new String[size];
        int index = 0;
        
        for (Restaurant r : rList) nodeNames[index++] = "[Rest] " + r.name;
        for (Customer c : cList) nodeNames[index++] = "[Cust] " + c.name;

        map = new Graph(size, nodeNames);

        for(int i = 0; i < rList.size(); i++) {
            int next = (i + 1) % rList.size();
            map.addEdge(i, next, (i * 3) % 4 + 2); 
        }
        
        if (rList.size() > 3) {
            map.addEdge(0, rList.size() / 2, 6);
        }

        for(int j = 0; j < cList.size(); j++) {
            int cIndex = rList.size() + j;
            int r1 = j % rList.size();
            int r2 = (j + 2) % rList.size();
            
            map.addEdge(r1, cIndex, (j * 2) % 5 + 3); 
            map.addEdge(r2, cIndex, (j * 3) % 4 + 4); 
        }
    }

    private JPanel createOrderDeliveryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- LEFT COLUMN: Incoming Orders ---
        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBorder(BorderFactory.createTitledBorder("1. Receive Customer Order (Stack)"));
        
        JPanel pnlRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtCustName = new JTextField(15);
        JButton btnStartCart = new JButton("Start Cart");
        pnlRow1.add(new JLabel("Customer Name:")); 
        pnlRow1.add(txtCustName); 
        pnlRow1.add(btnStartCart);

        JPanel pnlRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboOrderRest = new JComboBox<>();
        comboOrderRest.setPreferredSize(new Dimension(200, 25)); 
        comboOrderRest.addActionListener(e -> updateFoodDropdown());
        pnlRow2.add(new JLabel("Restaurant:")); 
        pnlRow2.add(comboOrderRest);

        JPanel pnlRow3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboOrderFood = new JComboBox<>();
        comboOrderFood.setPreferredSize(new Dimension(200, 25)); 
        JButton btnAddItem = new JButton("Add Item");
        pnlRow3.add(new JLabel("Food Item:")); 
        pnlRow3.add(comboOrderFood); 
        pnlRow3.add(btnAddItem);

        JPanel pnlRow4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnUndo = new JButton("Undo Last Item");
        JButton btnViewCart = new JButton("View Cart");
        JButton btnConfirm = new JButton("Confirm Order (Send to Queue)");
        pnlRow4.add(btnUndo); 
        pnlRow4.add(btnViewCart); 
        pnlRow4.add(btnConfirm);

        orderPanel.add(pnlRow1);
        orderPanel.add(pnlRow2);
        orderPanel.add(pnlRow3);
        orderPanel.add(pnlRow4);

        btnStartCart.addActionListener(e -> {
            String cName = txtCustName.getText().trim();
            if (!cName.isEmpty()) {
                int newId = nextCustomerId++;
                customers.addCustomer(newId, cName, "Client Location");
                dataSystem.addUserProfile(new UserProfile(newId, cName, cName.toLowerCase().replace(" ", "") + "@email.com"));
                currentCart = new OrderCart(newId);
                String formattedId = String.format("#%04d", newId);
                System.out.println("[GUI] New cart started for " + cName + " (ID: " + formattedId + ")");
                FileManager.saveCustomers(customers.toList());
                refreshDropdowns();
                buildDynamicMap();
            } else {
                System.out.println("[!] Please enter a Customer Name.");
            }
        });

        btnAddItem.addActionListener(e -> {
            if (currentCart != null) {
                String selectedRest = (String) comboOrderRest.getSelectedItem();
                String selectedFood = (String) comboOrderFood.getSelectedItem();
                if (selectedRest != null && selectedFood != null) {
                    currentCart.addItem(selectedRest + " - " + selectedFood);
                } else {
                    System.out.println("[!] No food item selected.");
                }
            } else {
                System.out.println("[!] Please start a cart first.");
            }
        });

        btnUndo.addActionListener(e -> { if (currentCart != null) currentCart.undoLastItem(); });
        btnViewCart.addActionListener(e -> { if (currentCart != null) currentCart.displayCart(); });

        btnConfirm.addActionListener(e -> {
            if (currentCart != null) {
                Order order = currentCart.confirmOrder(nextOrderId++);
                orderQueue.enqueue(order);
                dataSystem.storeOrder(order);
                FileManager.saveOrders(dataSystem.getAllOrders());
                currentCart = null;
                txtCustName.setText("");
            } else System.out.println("[!] No active cart to confirm.");
        });

        // --- RIGHT COLUMN: Process Queue & Auto-Assign Rider ---
        JPanel dispatchPanel = new JPanel();
        dispatchPanel.setLayout(new BoxLayout(dispatchPanel, BoxLayout.Y_AXIS));
        dispatchPanel.setBorder(BorderFactory.createTitledBorder("2. Dispatch & Rider Assignment (Min-Heap)"));

        JButton btnViewQueue = new JButton("View Pending Order Queue");
        JButton btnProcess = new JButton("Process Next Order & Assign Rider");
        
        JTextField txtRiderName = new JTextField(12);
        JTextField txtRiderDist = new JTextField(5);
        JButton btnAddRider = new JButton("Register New Rider on Map");
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

        btnViewQueue.addActionListener(e -> orderQueue.displayQueue());

        btnProcess.addActionListener(e -> {
            Order processed = orderQueue.processNextOrder();
            if (processed != null) {
                Rider best = delivery.assignBestRider(); 
                if (best != null) {
                    dataSystem.updateOrderStatus(processed.orderId, "Assigned to " + best.name);
                    System.out.println("[System] Order " + processed.orderId + " paired with nearest rider: " + best.name);
                } else {
                    dataSystem.updateOrderStatus(processed.orderId, "Preparing (Pending Rider)");
                    System.out.println("[System] Order " + processed.orderId + " sent to kitchen. No riders available right now.");
                }
                FileManager.saveOrders(dataSystem.getAllOrders());
                FileManager.saveRiders(delivery.toList());
            }
        });

        btnAddRider.addActionListener(e -> {
            try {
                String name = txtRiderName.getText().trim();
                int dist = Integer.parseInt(txtRiderDist.getText().trim());
                if (!name.isEmpty()) {
                    delivery.addRider(new Rider(nextRiderId++, name, dist));
                    FileManager.saveRiders(delivery.toList());
                    txtRiderName.setText(""); txtRiderDist.setText("");
                }
            } catch (NumberFormatException ex) { System.out.println("[!] Invalid Distance."); }
        });

        btnAvailableRiders.addActionListener(e -> delivery.displayRiders());

        panel.add(orderPanel);
        panel.add(dispatchPanel);
        return panel;
    }

    private JPanel createManagementPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Customers Section
        JPanel custPanel = new JPanel();
        custPanel.setLayout(new BoxLayout(custPanel, BoxLayout.Y_AXIS));
        custPanel.setBorder(BorderFactory.createTitledBorder("Registered Customers"));
        
        JTextField txtCustName = new JTextField(15);
        JTextField txtCustAddr = new JTextField(20);
        JButton btnAddCust = new JButton("Add Customer");
        JButton btnViewCust = new JButton("View All");

        JPanel cRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cRow1.add(new JLabel("Name:")); cRow1.add(txtCustName);
        cRow1.add(new JLabel("Address:")); cRow1.add(txtCustAddr);

        JPanel cRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cRow2.add(btnAddCust); cRow2.add(btnViewCust);

        custPanel.add(cRow1); custPanel.add(cRow2);

        btnAddCust.addActionListener(e -> {
            String name = txtCustName.getText().trim();
            String addr = txtCustAddr.getText().trim();
            if (!name.isEmpty() && !addr.isEmpty()) {
                customers.addCustomer(nextCustomerId, name, addr);
                dataSystem.addUserProfile(new UserProfile(nextCustomerId, name, name.toLowerCase() + "@email.com"));
                nextCustomerId++;
                FileManager.saveCustomers(customers.toList());
                txtCustName.setText(""); txtCustAddr.setText("");
                refreshDropdowns();
                buildDynamicMap();
            }
        });
        btnViewCust.addActionListener(e -> customers.displayCustomers());

        // Restaurants Section
        JPanel restPanel = new JPanel();
        restPanel.setLayout(new BoxLayout(restPanel, BoxLayout.Y_AXIS));
        restPanel.setBorder(BorderFactory.createTitledBorder("Partner Restaurants"));
        
        JTextField txtRestName = new JTextField(15);
        JTextField txtRestCuisine = new JTextField(15);
        JButton btnAddRest = new JButton("Add Restaurant");
        JButton btnViewRest = new JButton("View All Restaurants");

        JPanel rRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rRow1.add(new JLabel("Name:")); rRow1.add(txtRestName);
        rRow1.add(new JLabel("Cuisine:")); rRow1.add(txtRestCuisine);

        JPanel rRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rRow2.add(btnAddRest); rRow2.add(btnViewRest);

        restPanel.add(rRow1); restPanel.add(rRow2);

        btnAddRest.addActionListener(e -> {
            String name = txtRestName.getText().trim();
            String cuisine = txtRestCuisine.getText().trim();
            if (!name.isEmpty() && !cuisine.isEmpty()) {
                restaurants.addRestaurant(nextRestaurantId++, name, cuisine);
                FileManager.saveRestaurants(restaurants.toList());
                txtRestName.setText(""); txtRestCuisine.setText("");
                refreshDropdowns();
                buildDynamicMap();
            }
        });
        btnViewRest.addActionListener(e -> restaurants.displayRestaurants());

        panel.add(custPanel);
        panel.add(restPanel);
        return panel;
    }

    private JPanel createRoutesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        comboRoutesSrc = new JComboBox<>();
        comboRoutesDest = new JComboBox<>();
        JButton btnFindRoute = new JButton("Find Shortest Route");
        JButton btnViewEdges = new JButton("View All Road Connections");

        JPanel rRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rRow1.add(new JLabel("Pickup (Restaurant):")); rRow1.add(comboRoutesSrc);
        rRow1.add(new JLabel("Dropoff (Customer):")); rRow1.add(comboRoutesDest);

        JPanel rRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rRow2.add(btnFindRoute); rRow2.add(btnViewEdges);

        panel.add(rRow1);
        panel.add(rRow2);

        btnFindRoute.addActionListener(e -> {
            int srcListIndex = comboRoutesSrc.getSelectedIndex();
            int destListIndex = comboRoutesDest.getSelectedIndex();
            
            if (srcListIndex != -1 && destListIndex != -1 && map != null) {
                int graphDestIndex = restaurants.toList().size() + destListIndex;
                map.dijkstra(srcListIndex, graphDestIndex);
            } else {
                System.out.println("[!] Ensure both a source and destination are selected.");
            }
        });

        btnViewEdges.addActionListener(e -> {
            if(map != null) map.displayEdges();
            else System.out.println("[!] Network map is empty.");
        });

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        comboMenuRest = new JComboBox<>();
        JTextField txtName = new JTextField(15);
        JTextField txtPrice = new JTextField(5);
        JButton btnAddFood = new JButton("Add Food");
        JTextField txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Search Food");
        JButton btnViewMenu = new JButton("View Full Menu (A-Z)");

        JPanel mRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mRow1.add(new JLabel("Restaurant:")); mRow1.add(comboMenuRest);
        mRow1.add(new JLabel("Food Name:")); mRow1.add(txtName);
        mRow1.add(new JLabel("Price (RM):")); mRow1.add(txtPrice);

        JPanel mRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mRow2.add(btnAddFood);

        JPanel mRow3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mRow3.add(new JLabel("Search:")); mRow3.add(txtSearch);
        mRow3.add(btnSearch); mRow3.add(btnViewMenu);

        panel.add(mRow1);
        panel.add(mRow2);
        panel.add(mRow3);

        btnAddFood.addActionListener(e -> {
            try {
                String rName = (String) comboMenuRest.getSelectedItem();
                String fName = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());
                if (rName != null && !fName.isEmpty()) {
                    menu.insert(new FoodItem(rName + " - " + fName, price));
                    FileManager.saveFoodMenu(menu.toList());
                    txtName.setText(""); txtPrice.setText("");
                    refreshDropdowns();
                }
            } catch (NumberFormatException ex) { System.out.println("[!] Invalid Price."); }
        });

        btnSearch.addActionListener(e -> menu.search(txtSearch.getText().trim()));
        btnViewMenu.addActionListener(e -> menu.inOrderTraversal());

        return panel;
    }

    private JPanel createDataPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField txtUserId = new JTextField(8);
        JButton btnSearchUser = new JButton("Find User Profile");
        JButton btnViewAllUsers = new JButton("View All Profiles");
        
        JTextField txtOrderId = new JTextField(8);
        JButton btnSearchOrder = new JButton("Find Order Record");
        
        JTextField txtUpdateStatus = new JTextField(12);
        JButton btnUpdateStatus = new JButton("Update Order Status");

        JPanel dRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dRow1.add(new JLabel("User ID:")); dRow1.add(txtUserId); 
        dRow1.add(btnSearchUser); dRow1.add(btnViewAllUsers);

        JPanel dRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dRow2.add(new JLabel("Order ID:")); dRow2.add(txtOrderId); 
        dRow2.add(btnSearchOrder);

        JPanel dRow3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dRow3.add(new JLabel("New Status:")); dRow3.add(txtUpdateStatus); 
        dRow3.add(btnUpdateStatus);

        panel.add(dRow1);
        panel.add(dRow2);
        panel.add(dRow3);

        btnSearchUser.addActionListener(e -> {
            try { dataSystem.getUserProfile(Integer.parseInt(txtUserId.getText().trim())); }
            catch (NumberFormatException ex) { System.out.println("[!] Invalid User ID."); }
        });

        btnViewAllUsers.addActionListener(e -> {
            System.out.println("\n--- All Registered User Profiles ---");
            for (Customer c : customers.toList()) {
                dataSystem.getUserProfile(c.id);
            }
        });

        btnSearchOrder.addActionListener(e -> {
            try { dataSystem.getOrder(Integer.parseInt(txtOrderId.getText().trim())); }
            catch (NumberFormatException ex) { System.out.println("[!] Invalid Order ID."); }
        });

        btnUpdateStatus.addActionListener(e -> {
            try {
                int oid = Integer.parseInt(txtOrderId.getText().trim());
                String status = txtUpdateStatus.getText().trim();
                if(!status.isEmpty()) {
                    dataSystem.updateOrderStatus(oid, status);
                    FileManager.saveOrders(dataSystem.getAllOrders());
                }
            } catch (NumberFormatException ex) { System.out.println("[!] Enter Order ID to update."); }
        });

        return panel;
    }

    static void loadAllData() {
        List<Customer> savedCustomers = FileManager.loadCustomers();
        if (!savedCustomers.isEmpty()) {
            for (Customer c : savedCustomers) {
                customers.addCustomer(c.id, c.name, c.address);
                dataSystem.addUserProfile(new UserProfile(c.id, c.name, c.name.toLowerCase() + "@email.com"));
                if (c.id >= nextCustomerId) nextCustomerId = c.id + 1;
            }
        } else {
            seedCustomers();
        }

        List<Restaurant> savedRestaurants = FileManager.loadRestaurants();
        if (!savedRestaurants.isEmpty()) {
            for (Restaurant r : savedRestaurants) {
                restaurants.addRestaurant(r.id, r.name, r.cuisine);
                if (r.id >= nextRestaurantId) nextRestaurantId = r.id + 1;
            }
        } else {
            seedRestaurants();
        }

        List<FoodItem> savedFood = FileManager.loadFoodMenu();
        if (!savedFood.isEmpty()) {
            for (FoodItem f : savedFood) menu.insert(f);
        } else {
            seedMenu();
        }

        List<OrderRecord> savedOrders = FileManager.loadOrders();
        if (!savedOrders.isEmpty()) {
            for (OrderRecord o : savedOrders) {
                dataSystem.loadOrderRecord(o);
                if (o.orderId >= nextOrderId) nextOrderId = o.orderId + 1;
            }
        }

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

    static void saveAllData() {
        FileManager.saveCustomers(customers.toList());
        FileManager.saveRestaurants(restaurants.toList());
        FileManager.saveFoodMenu(menu.toList());
        FileManager.saveOrders(dataSystem.getAllOrders());
        FileManager.saveRiders(delivery.toList());
        System.out.println("\n[File] All data saved successfully.");
    }

    static void seedCustomers() {
        customers.addCustomer(nextCustomerId++, "Alice Tan", "123 Maple Street");
        customers.addCustomer(nextCustomerId++, "Bob Smith", "456 Oak Avenue");
        customers.addCustomer(nextCustomerId++, "Charlie Lee", "789 Pine Road");
        customers.addCustomer(nextCustomerId++, "Diana Prince", "101 Island Way");
        customers.addCustomer(nextCustomerId++, "Evan Wright", "202 Tech Park");
    }

    static void seedRestaurants() {
        restaurants.addRestaurant(nextRestaurantId++, "Burger King", "Fast Food");
        restaurants.addRestaurant(nextRestaurantId++, "Pizza Hut", "Italian");
        restaurants.addRestaurant(nextRestaurantId++, "Sushi King", "Japanese");
        restaurants.addRestaurant(nextRestaurantId++, "Mamak Corner", "Malaysian");
        restaurants.addRestaurant(nextRestaurantId++, "Seoul Garden", "Korean");
        restaurants.addRestaurant(nextRestaurantId++, "Salad Atelier", "Healthy");
    }

    static void seedMenu() {
        menu.insert(new FoodItem("Burger King - Whopper Meal", 18.50));
        menu.insert(new FoodItem("Burger King - Onion Rings", 6.00));
        menu.insert(new FoodItem("Burger King - Hershey's Sundae", 5.50));
        
        menu.insert(new FoodItem("Pizza Hut - Pepperoni Pizza", 24.00));
        menu.insert(new FoodItem("Pizza Hut - Hawaiian Chicken Pizza", 26.00));
        menu.insert(new FoodItem("Pizza Hut - Garlic Bread", 8.00));
        
        menu.insert(new FoodItem("Sushi King - Salmon Sashimi", 15.00));
        menu.insert(new FoodItem("Sushi King - Chicken Katsu Don", 16.50));
        
        menu.insert(new FoodItem("Mamak Corner - Nasi Kandar", 12.00));
        menu.insert(new FoodItem("Mamak Corner - Maggi Goreng", 7.50));
        menu.insert(new FoodItem("Mamak Corner - Teh Tarik", 3.50));
        
        menu.insert(new FoodItem("Seoul Garden - Beef Bulgogi", 22.00));
        menu.insert(new FoodItem("Seoul Garden - Kimchi Soup", 18.00));
        
        menu.insert(new FoodItem("Salad Atelier - Caesar Salad", 16.00));
        menu.insert(new FoodItem("Salad Atelier - Avocado Smoothie", 12.00));
    }

    static void seedRiders() {
        delivery.addRider(new Rider(nextRiderId++, "Hafiz", 3));
        delivery.addRider(new Rider(nextRiderId++, "Wei Jie", 1));
        delivery.addRider(new Rider(nextRiderId++, "Muthu", 5));
        delivery.addRider(new Rider(nextRiderId++, "Adam", 8));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainDashboard().setVisible(true));
    }
}