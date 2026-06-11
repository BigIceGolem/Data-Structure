package AppSystem;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    int userId;
    String name;
    String email;
    List<Integer> orderHistory;

    public UserProfile(int userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.orderHistory = new ArrayList<>();
    }

    public void addOrderToHistory(int orderId) {
        orderHistory.add(orderId);
    }

    @Override
    public String toString() {
        return "UserProfile{id=" + userId + ", name=" + name
                + ", email=" + email + ", orders=" + orderHistory + "}";
    }
}
