package model.user;

import model.UserType;

public class Staff extends User {
    private String staffId;
    private String department;

    // standard staff constructor
    public Staff(String userId, String name, String email, String password, String staffId, String department) {
        super(userId, name, email, password, UserType.STAFF);
        this.staffId = staffId;
        this.department = department;
    }

    // overloaded constructor for admin staff (UserType.ADMIN)
    public Staff(String userId, String name, String email, String password, String staffId, String department, UserType userType) {
        super(userId, name, email, password, userType);
        this.staffId = staffId;
        this.department = department;
    }

    @Override
    public double getDiscountRate() {
        // staff gets 15% discount
        return 0.15;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public String getCardId() {
        return staffId;
    }
}