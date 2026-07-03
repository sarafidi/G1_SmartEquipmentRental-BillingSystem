package strategy;

import model.equipment.Equipment;
import model.user.User;

public interface PricingStrategy {
    double calculateFee(Equipment equipment, int days);
    double applyDiscount(User user, double fee);
}