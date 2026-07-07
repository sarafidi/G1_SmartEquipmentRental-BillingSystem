package model.strategy;

import model.RentalStatus;
import model.rental.Rental;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LatePenalty implements PenaltyRule {
    private final double penaltyRatePerDay;

    public LatePenalty(double penaltyRatePerDay) {
        this.penaltyRatePerDay = penaltyRatePerDay;
    }

    @Override
    public double computePenalty(Rental rental) {
        if (rental.getStatus() == RentalStatus.CANCELLED) {
            return 0.0;
        }

        LocalDate end = (rental.getReturnDate() != null) ? rental.getReturnDate() : LocalDate.now();
        if (end.isAfter(rental.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(rental.getDueDate(), end);
            return overdueDays * penaltyRatePerDay;
        }
        return 0.0;
    }

    @Override
    public String getRuleDescription() {
        return "Late Return: $" + penaltyRatePerDay + "/day overdue";
    }
}