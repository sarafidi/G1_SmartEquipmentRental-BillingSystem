package model;

import strategy.*;
import util.DataStore;
import util.IDGenerator;

import java.util.ArrayList;
import java.util.List;

public class BillManager {
    private List<Bill> bills;
    private final DataStore instance = DataStore.getInstance();

    public BillManager() {
        this.bills = instance.getBills();
    }

    public Bill generateBill(Rental rental) {
        String billId = IDGenerator.generateBillId();   // auto generate bill id

        // select pricing strategy dynamically at runtime based on user's role
        // TODO: REDO to make it dynamic
        PricingStrategy pricing = new DiscountedPricing();

        // 1. base fee and discount calc
        double baseFee = pricing.calculateFee(rental.getEquipment(), rental.getDaysRented());
        double discount = pricing.applyDiscount(rental.getUser(), baseFee);

        // 2. calculate penalties
        double totalPenalty = 0.0;
        // TODO: update to work dynamic
        List<PenaltyRule> rules = new ArrayList<>();
        rules.add(new LatePenalty(10.00));
        rules.add(new DamagePenalty());

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
                .toList();
    }

    private Rental findRentalForBill(String rentalId) {
        return instance.getRentals().stream()
                .filter(r -> r.getRentalId().equalsIgnoreCase(rentalId))
                .findFirst()
                .orElse(null);
    }
}