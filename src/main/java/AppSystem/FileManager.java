package AppSystem;

import java.io.*;
import java.util.*;

/*
 * FileManager.java
 * ----------------
 * Handles all reading and writing of data to CSV files.
 * No other class touches the files directly — everything goes through here.
 *
 * CSV (Comma-Separated Values) format:
 *   Each line = one record. Fields inside a line are separated by commas.
 *   Example line in customers.csv:
 *     1,Alice,Jalan Bunga 1
 *
 * One file per data type:
 *   customers.csv    — customer ID, name, address
 *   restaurants.csv  — restaurant ID, name, cuisine
 *   foodmenu.csv     — food name, price
 *   orders.csv       — order ID, customer ID, status, items (pipe-separated)
 *   riders.csv       — rider ID, name, distance
 *
 * Special characters:
 *   If a field value contains a comma (e.g. "Kuala Lumpur, Malaysia") it
 *   would break the CSV format. We escape commas as {{COMMA}} before saving
 *   and unescape them when loading.
 */
public class FileManager {

    // file names — stored in the project root folder
    private static final String CUSTOMERS_FILE   = "customers.csv";
    private static final String RESTAURANTS_FILE = "restaurants.csv";
    private static final String FOOD_FILE        = "foodmenu.csv";
    private static final String ORDERS_FILE      = "orders.csv";
    private static final String RIDERS_FILE      = "riders.csv";

    // ================================================================
    //  CUSTOMERS — format: id,name,address
    // ================================================================

    public static void saveCustomers(List<Customer> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer c : list)
                pw.println(c.id + "," + escape(c.name) + "," + escape(c.address));
        } catch (IOException e) {
            System.out.println("[File] Error saving customers: " + e.getMessage());
        }
    }

    public static List<Customer> loadCustomers() {
        List<Customer> list = new ArrayList<>();
        File f = new File(CUSTOMERS_FILE);
        if (!f.exists()) return list; // no file yet — return empty list
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 3); // max 3 parts: id, name, address
                if (parts.length == 3)
                    list.add(new Customer(
                            Integer.parseInt(parts[0]),
                            unescape(parts[1]),
                            unescape(parts[2])));
            }
        } catch (IOException e) {
            System.out.println("[File] Error loading customers: " + e.getMessage());
        }
        return list;
    }

    // ================================================================
    //  RESTAURANTS — format: id,name,cuisine
    // ================================================================

    public static void saveRestaurants(List<Restaurant> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RESTAURANTS_FILE))) {
            for (Restaurant r : list)
                pw.println(r.id + "," + escape(r.name) + "," + escape(r.cuisine));
        } catch (IOException e) {
            System.out.println("[File] Error saving restaurants: " + e.getMessage());
        }
    }

    public static List<Restaurant> loadRestaurants() {
        List<Restaurant> list = new ArrayList<>();
        File f = new File(RESTAURANTS_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 3);
                if (parts.length == 3)
                    list.add(new Restaurant(
                            Integer.parseInt(parts[0]),
                            unescape(parts[1]),
                            unescape(parts[2])));
            }
        } catch (IOException e) {
            System.out.println("[File] Error loading restaurants: " + e.getMessage());
        }
        return list;
    }

    // ================================================================
    //  FOOD MENU — format: name,price
    // ================================================================

    public static void saveFoodMenu(List<FoodItem> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FOOD_FILE))) {
            for (FoodItem item : list)
                pw.println(escape(item.name) + "," + item.price);
        } catch (IOException e) {
            System.out.println("[File] Error saving food menu: " + e.getMessage());
        }
    }

    public static List<FoodItem> loadFoodMenu() {
        List<FoodItem> list = new ArrayList<>();
        File f = new File(FOOD_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 2); // name, price
                if (parts.length == 2)
                    list.add(new FoodItem(
                            unescape(parts[0]),
                            Double.parseDouble(parts[1])));
            }
        } catch (IOException e) {
            System.out.println("[File] Error loading food menu: " + e.getMessage());
        }
        return list;
    }

    // ================================================================
    //  ORDERS — format: orderId,customerId,status,item1|item2|item3
    //  Items are joined with "|" so multiple items fit in one CSV field.
    // ================================================================

    public static void saveOrders(List<OrderRecord> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ORDERS_FILE))) {
            for (OrderRecord o : list) {
                // join all item names with a pipe character as separator
                String items = String.join("|", o.items);
                pw.println(o.orderId + "," + o.customerId + ","
                        + escape(o.status) + "," + escape(items));
            }
        } catch (IOException e) {
            System.out.println("[File] Error saving orders: " + e.getMessage());
        }
    }

    public static List<OrderRecord> loadOrders() {
        List<OrderRecord> list = new ArrayList<>();
        File f = new File(ORDERS_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 4); // 4 fields max
                if (parts.length == 4) {
                    int          orderId    = Integer.parseInt(parts[0]);
                    int          customerId = Integer.parseInt(parts[1]);
                    String       status     = unescape(parts[2]);
                    String       itemStr    = unescape(parts[3]);
                    // split items back out from the pipe-separated string
                    List<String> items = itemStr.isEmpty()
                            ? new ArrayList<>()
                            : new ArrayList<>(Arrays.asList(itemStr.split("\\|")));
                    OrderRecord rec = new OrderRecord(orderId, customerId, items);
                    rec.status = status; // restore the saved status
                    list.add(rec);
                }
            }
        } catch (IOException e) {
            System.out.println("[File] Error loading orders: " + e.getMessage());
        }
        return list;
    }

    // ================================================================
    //  RIDERS — format: id,name,distanceKm
    // ================================================================

    public static void saveRiders(List<Rider> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RIDERS_FILE))) {
            for (Rider r : list)
                pw.println(r.id + "," + escape(r.name) + "," + r.distanceKm);
        } catch (IOException e) {
            System.out.println("[File] Error saving riders: " + e.getMessage());
        }
    }

    public static List<Rider> loadRiders() {
        List<Rider> list = new ArrayList<>();
        File f = new File(RIDERS_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 3);
                if (parts.length == 3)
                    list.add(new Rider(
                            Integer.parseInt(parts[0]),
                            unescape(parts[1]),
                            Integer.parseInt(parts[2])));
            }
        } catch (IOException e) {
            System.out.println("[File] Error loading riders: " + e.getMessage());
        }
        return list;
    }

    // ================================================================
    //  ESCAPE / UNESCAPE HELPERS
    //  Commas inside field values would break CSV parsing, so we
    //  replace them with a placeholder before saving and restore them
    //  after loading.
    // ================================================================

    private static String escape(String s) {
        return s.replace(",", "{{COMMA}}").replace("\n", "{{NEWLINE}}");
    }

    private static String unescape(String s) {
        return s.replace("{{COMMA}}", ",").replace("{{NEWLINE}}", "\n");
    }
}
