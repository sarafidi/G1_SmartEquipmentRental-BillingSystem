package controller;

import java.util.List;

import model.RentalSystemFacade;
import model.UserType;
import model.rental.Rental;
import model.user.User;
import util.Validator;

public class RentalController {
    private final RentalSystemFacade facade = RentalSystemFacade.getInstance();

    public Rental rentEquipment(String userId, String equipmentId, int days) {
        if (!Validator.isNonEmpty(userId) || !Validator.isNonEmpty(equipmentId) || !Validator.isValidDays(days)) {
            throw new IllegalArgumentException("Invalid Input: Please ensure IDs are not empty and days > 0.");
        }

        // non-staff can only rent for themselves
        User loggedInUser = facade.getCurrentUser();
        if (loggedInUser.getUserType() != UserType.ADMIN && loggedInUser.getUserType() != UserType.STAFF) {
            if (!loggedInUser.getUserId().equalsIgnoreCase(userId)) {
                throw new IllegalArgumentException("Access Denied: Students can only rent equipment for their own User ID");
            }
        }
        return facade.rentEquipment(userId, equipmentId, days);
    }

    public void returnEquipment(String rentalId, String condition) {
        if (!Validator.isNonEmpty(rentalId) || !Validator.isNonEmpty(condition)) {
            throw new IllegalArgumentException("Rental ID and equipment condition cannot be empty.");
        }
        
        // students can only return their own rentals
        User loggedInUser = facade.getCurrentUser();
        if (loggedInUser.getUserType() != UserType.ADMIN && loggedInUser.getUserType() != UserType.STAFF) {
            Rental rental = facade.findRentalById(rentalId);
            if (rental != null && !rental.getUser().getUserId().equalsIgnoreCase(loggedInUser.getUserId())) {
                throw new IllegalArgumentException("Access Denied: You can only return your own rentals.");
            }
        }
        
        facade.returnEquipment(rentalId, condition);
    }

    public void cancelRental(String rentalId) {
        if (!Validator.isNonEmpty(rentalId)) {
            throw new IllegalArgumentException("Rental ID cannot be empty.");
        }
        
        // Security Check: Students can only cancel their own rentals
        User loggedInUser = facade.getCurrentUser();
        if (loggedInUser.getUserType() != UserType.ADMIN && loggedInUser.getUserType() != UserType.STAFF) {
            Rental rental = facade.findRentalById(rentalId);
            if (rental != null && !rental.getUser().getUserId().equalsIgnoreCase(loggedInUser.getUserId())) {
                throw new IllegalArgumentException("Access Denied: You can only cancel your own rentals.");
            }
        }
        
        facade.cancelRental(rentalId);
    }

    public Rental findRentalById(String id) {
        if (!Validator.isNonEmpty(id)) {
            return null;
        }
        return facade.findRentalById(id);
    }

    public List<Rental> getUserRentals(String userId) {
        if (!Validator.isNonEmpty(userId)) {
            return List.of();
        }
        return facade.getUserRentals(userId);
    }

    public List<Rental> listAllRentals() {
        return facade.listAllRentals();
    }

    public String getCurrentUserId() {
        User currUser = facade.getCurrentUser();
        return currUser.getUserId();
    }
}