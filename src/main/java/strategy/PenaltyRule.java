package strategy;

import model.Rental;

public interface PenaltyRule {
    double computePenalty(Rental rental);
    String getRuleDescription();
}