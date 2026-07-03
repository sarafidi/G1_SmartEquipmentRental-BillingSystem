package model.equipment;

public class LabEquipment extends Equipment {
    private int hazardLevel;
    private boolean requiresCertificate;

    public LabEquipment(String equipmentId, String name, double dailyRate, int hazardLevel, boolean requiresCertificate) {
        super(equipmentId, name, "Lab", dailyRate);
        this.hazardLevel = hazardLevel;
        this.requiresCertificate = requiresCertificate;
    }

    @Override
    public double calculateRentalFee(int days) {
        double base = getDailyRate() * days;
        if (hazardLevel > 2) {
            base += 50.00; // flat safety handling surcharge
        }
        return base;
    }

    public int getHazardLevel() {
        return hazardLevel;
    }

    public boolean isRequiresCertificate() {
        return requiresCertificate;
    }
}