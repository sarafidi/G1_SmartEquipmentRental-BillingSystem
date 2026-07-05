package model.user;

import util.DataStore;

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
        if (u == null) return null;
        return u.checkPassword(password) ? u : null;
    }
}