package controller;

import java.util.List;

import model.RentalSystemFacade;
import model.rental.Rental;
import model.user.User;
import util.Validator;

public class RentalController {
    private final RentalSystemFacade facade = RentalSystemFacade.getInstance();

    public Rental rentEquipment(String userId, String equipmentId, int days) {
        if (!Validator.isNonEmpty(userId) || !Validator.isNonEmpty(equipmentId) || !Validator.isValidDays(days)) {
            throw new IllegalArgumentException("Invalid Input: Please ensure IDs are not empty and days > 0.");
        }
        return facade.rentEquipment(userId, equipmentId, days);
    }

    public void returnEquipment(String rentalId, String condition) {
        if (!Validator.isNonEmpty(rentalId) || !Validator.isNonEmpty(condition)) {
            throw new IllegalArgumentException("Rental ID and equipment condition cannot be empty.");
        }
        facade.returnEquipment(rentalId, condition);
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