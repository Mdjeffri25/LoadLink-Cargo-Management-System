package service;

import model.User;
import util.IdGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages users in memory.
 *
 * DSA CHOICE: HashMap<String, User> keyed by userId.
 * WHY HashMap instead of linear search through a List:
 *   - Login/lookup by ID is O(1) average case instead of O(n) for a
 *     linear scan through an ArrayList of users.
 *   - As the user base grows, lookup performance stays flat rather than
 *     degrading linearly, which matters for a login screen hit on every
 *     session start.
 */
public class UserService {

    private Map<String, User> usersById = new HashMap<>();
    // Secondary index for login-by-phone, also O(1) average lookup.
    private Map<String, User> usersByPhone = new HashMap<>();

    public User register(String name, String phone, User.Role role) {
        String id = IdGenerator.nextUserId();
        User user = new User(id, name, phone, role);
        usersById.put(id, user);
        usersByPhone.put(phone, user);
        return user;
    }

    public User findByPhone(String phone) {
        return usersByPhone.get(phone);
    }

    public User findById(String userId) {
        return usersById.get(userId);
    }

    public int totalUsers() {
        return usersById.size();
    }
}
