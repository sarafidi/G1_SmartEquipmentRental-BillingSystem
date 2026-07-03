package strategy;

import model.Rental;

public class DamagePenalty implements PenaltyRule {
    @Override
    public double computePenalty(Rental rental) {
        if ("Damaged".equalsIgnoreCase(rental.getCondition())) {
            // surcharge 150% of the equipments daily rate
            return rental.getEquipment().getDailyRate() * 1.5;
        }
        return 0.0;
    }

    @Override
    public String getRuleDescription() {
        return "Damage Fee: 150% of daily rate if returned damaged";
    }
}