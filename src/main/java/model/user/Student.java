package model.user;

import model.UserType;

public class Student extends User {
    private String studentId;
    private int yearOfStudy;

    public Student(String userId, String name, String email, String password, String studentId, int yearOfStudy) {
        // auto determine if they are a STUDENT or FINAL_YEAR_STUDENT based on study year
        super(userId, name, email, password, (yearOfStudy >= 3) ? UserType.FINAL_YEAR_STUDENT : UserType.STUDENT);
        this.studentId = studentId;
        this.yearOfStudy = yearOfStudy;
    }

    @Override
    public double getDiscountRate() {
        // students get 0% discount; Final year students (year >= 3) gets 10% discount
        return isFinalYear() ? 0.10 : 0.0;
    }

    public boolean isFinalYear() {
        return yearOfStudy >= 3;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    @Override
    public String getCardId() {
        return studentId;
    }
}