package model;

public abstract class Equipment {
    // fields are private. subclasses use getters to retrieve them
    private final String equipmentId;
    private final String name;
    private final String category;
    private final double dailyRate;
    private boolean available;  // changes during transaction

    public Equipment(String equipmentId, String name, String category, double dailyRate) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.category = category;
        this.dailyRate = dailyRate;
        this.available = true;
    }

    public abstract double calculateRentalFee(int days);

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}