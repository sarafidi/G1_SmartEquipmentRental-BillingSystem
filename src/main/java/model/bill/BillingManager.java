package model.bill;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.RentalStatus;
import model.rental.Rental;
import model.rental.RentalManager;
import model.strategy.DiscountedPricing;
import model.strategy.PenaltyRule;
import model.strategy.PricingStrategy;
import util.DataStore;
import util.IDGenerator;

public class BillingManager {
    private List<Bill> bills;
    private RentalManager rentalManager;    // injected reference
    private final DataStore instance = DataStore.getInstance();

    public BillingManager() {
        this.bills = instance.getBills();
    }

    // setter for dependency injection
    public void setRentalManager (RentalManager rentalManager) {
        this.rentalManager = rentalManager;
    }

    public Bill generateBill(Rental rental) {
        // bill should generate after rental is completed and returned
        if (rental.getStatus() == RentalStatus.ACTIVE) {
            throw new IllegalArgumentException("Cannot generate bill: Rental is still ACTIVE. Equipment must be returned first.");
        }

        // a rental can only have 1 bill
        Bill exists = findBillByRental(rental.getRentalId());
        if (exists != null) return exists;

        String billId = IDGenerator.generateBillId();   // auto generate bill id

        // get active pricing strategy directly from injected
        PricingStrategy pricing = (rentalManager != null)
                ? rentalManager.getPricingStrategy()
                : new DiscountedPricing();

        // 1. base fee and discount calc
        double baseFee = pricing.calculateFee(rental.getEquipment(), rental.getDaysRented());
        double discount = pricing.applyDiscount(rental.getUser(), baseFee);

        // 2. fetch penalties from RentalManager rules context calculate penalties
        double latePenalty = 0.0;
        double damagePenalty = 0.0;
        List<PenaltyRule> rules = (rentalManager != null)
                ? rentalManager.getPenaltyRules()
                : new ArrayList<>();

        for (PenaltyRule rule : rules) {
            double penalty = rule.computePenalty(rental);
            if (rule instanceof model.strategy.LatePenalty) {
                latePenalty += penalty;
            } else if (rule instanceof model.strategy.DamagePenalty) {
                damagePenalty += penalty;
            }
        }

        // construct using Builder pattern
        Bill bill = new Bill.Builder(billId, rental.getRentalId())
                .baseRentalFee(baseFee)
                .discountAmount(discount)
                .latePenalty(latePenalty)
                .damagePenalty(damagePenalty)
                .build();
        bills.add(bill);
        instance.saveBill();
        return bill;
    }

    public Bill findBillByRental(String rentalId) {
        return bills.stream()
                .filter(b -> b.getRentalId().equalsIgnoreCase(rentalId))
                .findFirst()
                .orElse(null);
    }

    public List<Bill> getBillHistory(String userId) {
        return bills.stream()
                .filter(b -> {
                    Rental r = findRentalForBill(b.getRentalId());
                    return r != null && r.getUser().getUserId().equalsIgnoreCase(userId);
                })
                .collect(Collectors.toList());
    }

    private Rental findRentalForBill(String rentalId) {
        return instance.getRentals().stream()
                .filter(r -> r.getRentalId().equalsIgnoreCase(rentalId))
                .findFirst()
                .orElse(null);
    }

    public List<Bill> listAll() {
        return new ArrayList<>(bills);
    }
}