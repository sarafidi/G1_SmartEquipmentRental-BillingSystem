package model.strategy;

import model.rental.Rental;

public interface PenaltyRule {
    double computePenalty(Rental rental);
    String getRuleDescription();
}