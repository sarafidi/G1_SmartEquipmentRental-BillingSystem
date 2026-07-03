package model;

public class MediaEquipment extends Equipment {
    private boolean requiresDeposit;
    private double depositAmount;

    public MediaEquipment(String equipmentId, String name, double dailyRate, boolean requiresDeposit, double depositAmount) {
        super(equipmentId, name, "Media", dailyRate);
        this.requiresDeposit = requiresDeposit;
        this.depositAmount = depositAmount;
    }

    @Override
    public double calculateRentalFee(int days) {
        return (getDailyRate() * days) + (requiresDeposit ? depositAmount : 0);
    }

    public boolean isRequiresDeposit() {
        return requiresDeposit;
    }

    public double getDepositAmount() {
        return depositAmount;
    }
}