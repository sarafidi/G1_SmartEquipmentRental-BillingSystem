package model.bill;

import model.rental.Rental;
import model.rental.RentalManager;
import model.strategy.DiscountedPricing;
import model.strategy.PenaltyRule;
import model.strategy.PricingStrategy;
import util.DataStore;
import util.IDGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        String billId = IDGenerator.generateBillId();   // auto generate bill id

        // get active pricing strategy directly from injected
        PricingStrategy pricing = (rentalManager != null)
                ? rentalManager.getPricingStrategy()
                : new DiscountedPricing();

        // 1. base fee and discount calc
        double baseFee = pricing.calculateFee(rental.getEquipment(), rental.getDaysRented());
        double discount = pricing.applyDiscount(rental.getUser(), baseFee);

        // 2. fetch penalties from RentalManager rules context calculate penalties
        double totalPenalty = 0.0;
        // TODO: update to work dynamic
        List<PenaltyRule> rules = (rentalManager != null)
                ? rentalManager.getPenaltyRules()
                : new ArrayList<>();

        for (PenaltyRule rule : rules) {
            totalPenalty += rule.computePenalty(rental);
        }

        // construct using Builder pattern
        Bill bill = new Bill.Builder(billId, rental.getRentalId())
                .baseRentalFee(baseFee)
                .discountAmount(discount)
                .penaltyAmount(totalPenalty)
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