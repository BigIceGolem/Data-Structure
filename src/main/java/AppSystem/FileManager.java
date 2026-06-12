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

    // The names of the CSV files where data is saved
    // These files are created in the same folder the program is run from
    private static final String CUSTOMERS_FILE   = "customers.csv";
    private static final String RESTAURANTS_FILE = "restaurants.csv";
    private static final String FOOD_FILE        = "foodmenu.csv";
    private static final String ORDERS_FILE      = "orders.csv";
    private static final String RIDERS_FILE      = "riders.csv";

    // ================================================================
    //  CUSTOMERS — each line has the format: id,name,address
    // ================================================================

    // Writes all customers in the list to the customers.csv file
    // Each customer gets one line in the file
    public static void saveCustomers(List<Customer> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer c : list)
                // escape() makes sure any commas inside name or address don't break the CSV
                pw.println(c.id + "," + escape(c.name) + "," + escape(c.address));
        } catch (IOException e) {
            System.out.println("[File] Error saving customers: " + e.getMessage());
        }
    }

    // Reads all customers from the customers.csv file and returns them as a list
    // If the file doesn't exist yet, returns an empty list instead of crashing
    public static List<Customer> loadCustomers() {
        List<Customer> list = new ArrayList<>();
        File f = new File(CUSTOMERS_FILE);
        if (!f.exists()) return list; // file hasn't been created yet — return empty list

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // skip blank lines

                // Split the line into at most 3 parts: id, name, address
                String[] parts = line.split(",", 3);
                if (parts.length == 3)
                    list.add(new Customer(
                            Integer.parseInt(parts[0]),       // id (convert text to number)
                            unescape(parts[1]),               // name (restore any commas)
                            unescape(parts[2])));             // address (restore any commas)
            }
        } catch (IOException e) {
            System.out.println("[File] Error loading customers: " + e.getMessage());
        }
        return list;
    }

    // ================================================================
    //  RESTAURANTS — each line has the format: id,name,cuisine
    // ================================================================

    // Writes all restaurants to the restaurants.csv file
    public static void saveRestaurants(List<Restaurant> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RESTAURANTS_FILE))) {
            for (Restaurant r : list)
                pw.println(r.id + "," + escape(r.name) + "," + escape(r.cuisine));
        } catch (IOException e) {
            System.out.println("[File] Error saving restaurants: " + e.getMessage());
        }
    }

    // Reads all restaurants from the restaurants.csv file
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
    //  FOOD MENU — each line has the format: name,price
    // ================================================================

    // Writes all food items to the foodmenu.csv file
    public static void saveFoodMenu(List<FoodItem> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FOOD_FILE))) {
            for (FoodItem item : list)
                pw.println(escape(item.name) + "," + item.price);
        } catch (IOException e) {
            System.out.println("[File] Error saving food menu: " + e.getMessage());
        }
    }

    // Reads all food items from the foodmenu.csv file
    public static List<FoodItem> loadFoodMenu() {
        List<FoodItem> list = new ArrayList<>();
        File f = new File(FOOD_FILE);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                // Split into exactly 2 parts: name and price
                String[] parts = line.split(",", 2);
                if (parts.length == 2)
                    list.add(new FoodItem(
                            unescape(parts[0]),
                            Double.parseDouble(parts[1]))); // convert price text to a decimal number
            }
        } catch (IOException e) {
            System.out.println("[File] Error loading food menu: " + e.getMessage());
        }
        return list;
    }

    // ================================================================
    //  ORDERS — format: orderId,customerId,status,item1|item2|item3
    //
    //  Items are joined using "|" (pipe character) as a separator
    //  because each order can have multiple items, and we need to
    //  fit them all into a single CSV field.
    // ================================================================

    // Writes all order records to the orders.csv file
    public static void saveOrders(List<OrderRecord> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ORDERS_FILE))) {
            for (OrderRecord o : list) {
                // Join all item names into one string separated by "|"
                // e.g. ["Whopper Meal", "Onion Rings"] becomes "Whopper Meal|Onion Rings"
                String items = String.join("|", o.items);
                pw.println(o.orderId + "," + o.customerId + ","
                        + escape(o.status) + "," + escape(items));
            }
        } catch (IOException e) {
            System.out.println("[File] Error saving orders: " + e.getMessage());
        }
    }

    // Reads all order records from the orders.csv file
    public static List<OrderRecord> loadOrders() {
        List<OrderRecord> list = new ArrayList<>();
        File f = new File(ORDERS_FILE);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Split into exactly 4 parts: orderId, customerId, status, items
                String[] parts = line.split(",", 4);
                if (parts.length == 4) {
                    int    orderId    = Integer.parseInt(parts[0]);
                    int    customerId = Integer.parseInt(parts[1]);
                    String status     = unescape(parts[2]);
                    String itemStr    = unescape(parts[3]);

                    // Split the pipe-separated item string back into a list of item names
                    List<String> items = itemStr.isEmpty()
                            ? new ArrayList<>()
                            : new ArrayList<>(Arrays.asList(itemStr.split("\\|")));

                    OrderRecord rec = new OrderRecord(orderId, customerId, items);
                    rec.status = status; // restore the saved status (e.g. "Delivered")
                    list.add(rec);
                }
            }
        } catch (IOException e) {
            System.out.println("[File] Error loading orders: " + e.getMessage());
        }
        return list;
    }

    // ================================================================
    //  RIDERS — each line has the format: id,name,distanceKm
    // ================================================================

    // Writes all riders to the riders.csv file
    public static void saveRiders(List<Rider> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RIDERS_FILE))) {
            for (Rider r : list)
                pw.println(r.id + "," + escape(r.name) + "," + r.distanceKm);
        } catch (IOException e) {
            System.out.println("[File] Error saving riders: " + e.getMessage());
        }
    }

    // Reads all riders from the riders.csv file
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
                            Integer.parseInt(parts[2]))); // distanceKm as an integer
            }
        } catch (IOException e) {
            System.out.println("[File] Error loading riders: " + e.getMessage());
        }
        return list;
    }

    // ================================================================
    //  ESCAPE / UNESCAPE HELPERS
    //
    //  Problem: commas inside a field value (e.g. "Kuala Lumpur, Malaysia")
    //  would look like a separator when we read the CSV back, breaking the format.
    //
    //  Solution: before saving, replace commas with {{COMMA}} and newlines with
    //  {{NEWLINE}}. When loading, replace them back with the original characters.
    // ================================================================

    // Replaces commas and newlines in a string before saving to CSV
    private static String escape(String s) {
        return s.replace(",", "{{COMMA}}").replace("\n", "{{NEWLINE}}");
    }

    // Restores commas and newlines in a string after loading from CSV
    private static String unescape(String s) {
        return s.replace("{{COMMA}}", ",").replace("{{NEWLINE}}", "\n");
    }
}
