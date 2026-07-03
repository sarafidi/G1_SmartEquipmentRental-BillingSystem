package model.strategy;

import model.rental.Rental;

public class LatePenalty implements PenaltyRule {
    private final double penaltyRatePerDay;

    public LatePenalty(double penaltyRatePerDay) {
        this.penaltyRatePerDay = penaltyRatePerDay;
    }

    @Override
    public double computePenalty(Rental rental) {
        if (!rental.isOverdue()) {
            return 0.0;
        }
        return rental.getDaysOverdue() * penaltyRatePerDay;
    }

    @Override
    public String getRuleDescription() {
        return "Late Return: $" + penaltyRatePerDay + "/day overdue";
    }
}