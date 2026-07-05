package model.user;

import model.UserType;

public abstract class User {
    // encapsulation decision: marked private to prevent direct external modification
    private final String userId;
    private String name;
    private String email;
    private String password;
    private final UserType userType;

    public User(String userId, String name, String email, String password, UserType userType) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    // abstract: subclass must override to define their unique discount rate
    public abstract double getDiscountRate();

    // abstract: subclass must override to return their unique studentId or staffId
    public abstract String getCardId();

    public String getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public UserType getUserType() {
        return userType;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}