// DataManager.java
package Delivery;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

class User {
    private String userId;
    private String name;
    private String phone;

    public User(String userId, String name, String phone) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
    }
    
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    @Override
    public String toString() {
        return "User[" + userId + "] " + name + " (" + phone + ")";
    }
}

class Restaurant {
    private String restaurantId;
    private String name;
    private String location;

    public Restaurant(String restaurantId, String name, String location) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.location = location;
    }

    public String getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    @Override
    public String toString() {
        return "Restaurant[" + restaurantId + "] " + name + " - " + location;
    }
}

public class DataManager {

    private List<User> userList = new LinkedList<>();
    private List<Restaurant> restaurantList = new LinkedList<>();

    private Map<String, User> userMap = new HashMap<>();
    private Map<String, Restaurant> restaurantMap = new HashMap<>();

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9-]+$");

    public DataManager() {
        loadDummyData();
    }

    public boolean addUser(String id, String name, String phone) {
        if (id == null || id.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            System.err.println("Error: User ID and Name cannot be empty.");
            return false;
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            System.err.println("Error: Invalid phone number format.");
            return false;
        }
        if (userMap.containsKey(id)) {
            System.err.println("Error: User ID " + id + " already exists.");
            return false;
        }
        
        User newUser = new User(id, name, phone);
        userList.add(newUser);
        userMap.put(id, newUser);
        return true;
    }

    public User getUser(String id) {
        return userMap.get(id);
    }

    public List<User> getAllUsers() {
        return userList;
    }

    public boolean updateUserPhone(String id, String newPhone) {
        User user = userMap.get(id);
        if (user != null) {
            if (PHONE_PATTERN.matcher(newPhone).matches()) {
                user.setPhone(newPhone);
                return true;
            } else {
                System.err.println("Error: Invalid phone number format.");
            }
        }
        return false;
    }

    public boolean removeUser(String id) {
        User userToRemove = userMap.remove(id);
        if (userToRemove != null) {
            userList.remove(userToRemove);
            return true;
        }
        return false;
    }

    public void displayAllUsers() {
        System.out.println("\n--- Registered Users ---");
        for (User u : userList) {
            System.out.println(u);
        }
    }

    public boolean addRestaurant(String id, String name, String location) {
        if (id == null || id.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            return false;
        }
        if (restaurantMap.containsKey(id)) {
            return false;
        }
        Restaurant newRest = new Restaurant(id, name, location);
        restaurantList.add(newRest);
        restaurantMap.put(id, newRest);
        return true;
    }

    public Restaurant getRestaurant(String id) {
        return restaurantMap.get(id);
    }
    
    public List<Restaurant> getAllRestaurants() {
        return restaurantList;
    }

    public boolean updateRestaurantLocation(String id, String newLocation) {
        Restaurant rest = restaurantMap.get(id);
        if (rest != null && newLocation != null && !newLocation.trim().isEmpty()) {
            rest.setLocation(newLocation);
            return true;
        }
        return false;
    }

    public boolean removeRestaurant(String id) {
        Restaurant restToRemove = restaurantMap.remove(id);
        if (restToRemove != null) {
            restaurantList.remove(restToRemove);
            return true;
        }
        return false;
    }

    public void displayAllRestaurants() {
        System.out.println("\n--- Registered Restaurants ---");
        for (Restaurant r : restaurantList) {
            System.out.println(r);
        }
    }

    private void loadDummyData() {
        addUser("U001", "Amirun", "012-3456789");
        addUser("U002", "Haziq", "019-8765432");
        addUser("U003", "Aasim", "011-1112223");
        addUser("U004", "Hazim", "017-9998887");
        addUser("U005", "Ammar", "013-4445556");

        addRestaurant("R001", "KFC", "Mid Valley");
        addRestaurant("R002", "McDonald's", "Bangsar");
        addRestaurant("R003", "Hadramawt", "Bukit Bintang");
        addRestaurant("R004", "Johnny's Restaurant", "1 Utama");
        addRestaurant("R005", "Damascus", "KLCC");
    }
}