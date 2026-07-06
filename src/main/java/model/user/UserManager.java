package model.user;

import util.DataStore;
import util.IDGenerator;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private List<User> users;
    private final DataStore instance = DataStore.getInstance();

    public UserManager() {
        this.users = instance.getUsers();
    }

    public void addUser(User u) {
        users.add(u);
        instance.saveUsers();
    }

    public User createUser(String name, String email, String rawPassword, String userType, String additional) {
        // Generate IDs
        String userId = IDGenerator.generateUserId();
        String cardId = userType.equals("Student")
                ? IDGenerator.generateStudentId()
                : IDGenerator.generateStaffId();

        // Hash password
        String hashedPassword = util.HashUtil.sha256(rawPassword);

        // Create user object
        User newUser;
        if (userType.equals("Student")) {
            newUser = new Student(userId, name, email, hashedPassword, cardId, Integer.parseInt(additional));
        } else {
            newUser = new Staff(userId, name, email, hashedPassword, cardId, additional);
        }

        // Add user
        addUser(newUser);
        return newUser;
    }

    public void removeUser(String id) {
        users.removeIf(u -> u.getUserId().equalsIgnoreCase(id));
        instance.saveUsers();
    }

    public User findById(String id) {
        return users.stream()
                .filter(u -> u.getUserId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public List<User> listAll() {
        return new ArrayList<>(users);
    }

    public User authenticate(String userId, String password) {
        User u = findById(userId);
        if (u != null && u.checkPassword(password)) return u;
        return null;
    }
}