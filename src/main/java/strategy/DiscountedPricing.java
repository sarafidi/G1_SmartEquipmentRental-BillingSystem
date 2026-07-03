package strategy;

import model.equipment.Equipment;
import model.user.User;

public class DiscountedPricing implements PricingStrategy {
    @Override
    public double calculateFee(Equipment equipment, int days) {
        return equipment.calculateRentalFee(days);
    }

    @Override
    public double applyDiscount(User user, double fee) {
        return fee * user.getDiscountRate();    // uses User's discount rate (Student = 10-20%, Staff = 15%)
    }
}