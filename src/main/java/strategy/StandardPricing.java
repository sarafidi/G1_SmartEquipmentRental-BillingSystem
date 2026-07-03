package strategy;

import model.equipment.Equipment;
import model.user.User;

public class StandardPricing implements PricingStrategy {
    @Override
    public double calculateFee(Equipment equipment, int days) {
        return equipment.calculateRentalFee(days);
    }

    @Override
    public double applyDiscount(User user, double fee) {
        return 0.0;     // standard user get no discount
    }
}