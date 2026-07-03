package model;

public class ElectronicsEquipment extends Equipment {
    private int warrantyMonths;

    public ElectronicsEquipment(String equipmentId, String name, double dailyRate, int warrantyMonths) {
        super(equipmentId, name, "Electronics", dailyRate);
        this.warrantyMonths = warrantyMonths;
    }

    @Override
    public double calculateRentalFee(int days) {
        return getDailyRate() * days;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }
}