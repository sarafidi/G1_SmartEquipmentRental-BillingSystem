package model.user;

import model.UserType;

public class Staff extends User {
    private String staffId;
    private String department;

    public Staff(String userId, String name, String email, String staffId, String department) {
        super(userId, name, email, UserType.STAFF);
        this.staffId = staffId;
        this.department = department;
    }

    @Override
    public double getDiscountRate() {
        // staff gets 15% discount
        return 0.15;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getDepartment() {
        return department;
    }
}