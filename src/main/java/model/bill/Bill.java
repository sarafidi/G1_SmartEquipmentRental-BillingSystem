package model.bill;

import java.time.LocalDate;

public class Bill {
    private final String billId;
    private final String rentalId;
    private final double baseRentalFee;
    private final double discountAmount;
    private final double penaltyAmount;
    private final double latePenalty;
    private final double damagePenalty;
    private final double netPayable;
    private final LocalDate billDate;

    /*  private constructor for Builder Pattern.
        By making the constructor private, we force callers to use Bill.Builder
        This prevents creating inconsistent Bill objs
    */
    private Bill(Builder builder) {
        this.billId = builder.billId;
        this.rentalId = builder.rentalId;
        this.baseRentalFee = builder.baseRentalFee;
        this.discountAmount = builder.discountAmount;
        this.latePenalty = builder.latePenalty;
        this.damagePenalty = builder.damagePenalty;
        this.penaltyAmount = builder.latePenalty + builder.damagePenalty;
        this.netPayable = Math.max(0.0, builder.baseRentalFee - builder.discountAmount + this.penaltyAmount);
        this.billDate = LocalDate.now();
    }

    /*  Calculation encapsulation.
        Keeps billing math inside the model class where it belongs,
        rather than inside view or controllers.
    */
    public double computeNet() {
        return Math.max(0.0, baseRentalFee - discountAmount + penaltyAmount);
    }

    // getters

    public String getBillId() {
        return billId;
    }
    public String getRentalId() {
        return rentalId;
    }
    public double getBaseRentalFee() {
        return baseRentalFee;
    }
    public double getDiscountAmount() {
        return discountAmount;
    }
    public double getPenaltyAmount() {
        return penaltyAmount;
    }
    public double getLatePenalty() {
        return latePenalty;
    }
    public double getDamagePenalty() {
        return damagePenalty;
    }
    public double getNetPayable() {
        return netPayable;
    }
    public LocalDate getBillDate() {
        return billDate;
    }

    // ===================================
    // inner builder class
    // ===================================
    public static class Builder {
        private final String billId;
        private final String rentalId;
        private double baseRentalFee;
        private double discountAmount = 0.0;
        private double latePenalty = 0.0;
        private double damagePenalty = 0.0;

        public Builder(String billId, String rentalId) {
            this.billId = billId;
            this.rentalId = rentalId;
        }

        public Builder baseRentalFee(double baseRentalFee) {
            this.baseRentalFee = baseRentalFee;
            return this;
        }

        public Builder discountAmount(double discountAmount) {
            this.discountAmount = discountAmount;
            return this;
        }

        public Builder latePenalty(double latePenalty) {
            this.latePenalty = latePenalty;
            return this;
        }

        public Builder damagePenalty(double damagePenalty) {
            this.damagePenalty = damagePenalty;
            return this;
        }

        public Bill build() {
            return new Bill(this);
        }
    }
}