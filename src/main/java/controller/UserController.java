package controller;

import model.RentalSystemFacade;
import model.user.User;
import util.SessionManager;

import java.util.List;

public class UserController {
    private final RentalSystemFacade facade = RentalSystemFacade.getInstance();

    public boolean logic(String userId, String rawPassword) {
        // TODO: TO BE IMPLEMENTED BY MEMBER C
        // Hint: Use HashUtil.verify(rawPassword, storedHash) to authenticate
        return false;
    }

    public void logout() {
        // TODO: TO BE IMPLEMENTED BY MEMBER C
        // Hint: Use SessionManager clearSession() method
    }

    public void addUser(User user) {
        // TODO: TO BE IMPLEMENTED BY MEMBER C
    }

    public void removeUser(String id) {
        // TODO: TO BE IMPLEMENTED BY MEMBER C
    }

    public User findById(String id) {
        // TODO: TO BE IMPLEMENTED BY MEMBER C
        // FIX: return type calls facade user methods
        return null;
    }

    public List<User> listAll() {
        // TODO: TO BE IMPLEMENTED BY MEMBER C
        // FIX: return type calls facade user methods
        return null;
    }
}