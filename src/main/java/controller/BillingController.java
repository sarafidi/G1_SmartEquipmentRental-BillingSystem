package controller;

import model.RentalSystemFacade;
import model.bill.Bill;
import model.rental.Rental;
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
        
        // Security Check: Students can only view/generate bills for their own rentals
        User loggedInUser = facade.getCurrentUser();
        if (loggedInUser.getUserType() != model.UserType.ADMIN && loggedInUser.getUserType() != model.UserType.STAFF) {
            model.rental.Rental rental = facade.findRentalById(rentalId);
            if (rental != null && !rental.getUser().getUserId().equalsIgnoreCase(loggedInUser.getUserId())) {
                throw new IllegalArgumentException("Access Denied: You can only view bills for your own rentals.");
            }
        }
        
        return facade.generateBill(rentalId);
    }

    public Bill findBillByRental(String rentalId) {
        if (!Validator.isNonEmpty(rentalId)) {
            return null;
        }
        
        // Security Check: Students can only search for their own bills
        User loggedInUser = facade.getCurrentUser();
        if (loggedInUser.getUserType() != model.UserType.ADMIN && loggedInUser.getUserType() != model.UserType.STAFF) {
            model.rental.Rental rental = facade.findRentalById(rentalId);
            if (rental != null && !rental.getUser().getUserId().equalsIgnoreCase(loggedInUser.getUserId())) {
                return null;
            }
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

    public Rental findRentalById(String rentalId) {
        if (!Validator.isNonEmpty(rentalId)) {
            return null;
        }
        return facade.findRentalById(rentalId);
    }
}