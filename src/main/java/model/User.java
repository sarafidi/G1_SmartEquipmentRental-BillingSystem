package model;

public abstract class User {
    // encapsulation decision: marked private to prevent direct external modification
    private final String userId;
    private String name;
    private String email;
    private final UserType userType;

    public User(String userId, String name, String email, UserType userType) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.userType = userType;
    }

    // abstract: subclass must override to define their unique discount rate
    public abstract double getDiscountRate();

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
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
}