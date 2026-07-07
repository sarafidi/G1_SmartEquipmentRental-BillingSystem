package controller;

import model.RentalSystemFacade;
import model.bill.Bill;
import model.user.User;
import util.Validator;

import java.util.Collections;
import java.util.List;

public class BillingController {
    private final RentalSystemFacade facade = RentalSystemFacade.getInstance();

    public Bill generateBill(String rentalId) {
        if (!Validator.isNonEmpty(rentalId)) {
            throw new IllegalArgumentException("Please enter a Rental ID.");
        }
        return facade.generateBill(rentalId);
    }

    public Bill findBillByRental(String rentalId) {
        if (!Validator.isNonEmpty(rentalId)) {
            return null;
        }
        return facade.findBillByRental(rentalId);
    }

    public List<Bill> getBillHistory(String userId) {
        if (!Validator.isNonEmpty(userId)) {
            return Collections.emptyList();
        }
        return facade.getBillHistory(userId);
    }

    public String getCurrentUserId() {
        User currUser = facade.getCurrentUser();
        return currUser.getUserId();
    }
}