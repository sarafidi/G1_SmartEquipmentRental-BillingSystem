package model;

import model.equipment.Equipment;
import model.user.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Rental {
    private final String rentalId;
    private final User user;
    private final Equipment equipment;
    private final LocalDate startDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;   // can be null if item not return yet
    private RentalStatus status;
    private String condition;       // condition upon return "Excellent", "Damaged", etc

    public Rental(String rentalId, User user, Equipment equipment, LocalDate startDate, int daysRequested) {
        this.rentalId = rentalId;
        this.user = user;
        this.equipment = equipment;
        this.startDate = startDate;
        this.dueDate = startDate.plusDays(daysRequested);
        this.returnDate = null;     // null on initial checkout
        this.status = RentalStatus.ACTIVE;
        this.condition = "Good";    // default condition
    }

    /*
        1. null safety on returnDate
        2. rental is active until returned. if returnDate == null, check against LocalDate.now()
            see if its past the dueDate
        3. prevents NullPointerException
    */
    public boolean isOverdue() {
        if (status == RentalStatus.RETURNED || status == RentalStatus.CANCELLED) {
            return false;
        }
        LocalDate dateToCheck = (returnDate != null) ? returnDate : LocalDate.now();
        return dateToCheck.isAfter(dueDate);
    }

    /*
        1. using ChronoUnit for precise day calc.
        2. ChronoUnit.DAYS.between() calc exact days between dates
            which handles month changes and leap year auto
    */
    public int getDaysRented() {
        LocalDate end = (returnDate != null) ? returnDate : LocalDate.now();
        return (int) ChronoUnit.DAYS.between(startDate, end);
    }

    public int getDaysOverdue() {
        LocalDate end = (returnDate != null) ? returnDate : LocalDate.now();
        return (int) ChronoUnit.DAYS.between(dueDate, end);
    }

    // getters and setters

    public String getRentalId() {
        return rentalId;
    }
    public User getUser() {
        return user;
    }
    public Equipment getEquipment() {
        return equipment;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
    public LocalDate getReturnDate() {
        return returnDate;
    }
    public RentalStatus getStatus() {
        return status;
    }
    public String getCondition() {
        return condition;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    public void setStatus(RentalStatus status) {
        this.status = status;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }
}