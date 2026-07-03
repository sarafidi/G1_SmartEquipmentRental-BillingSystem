package controller;

import model.RentalSystemFacade;
import model.rental.Rental;

import java.util.List;

public class RentalController {
    private final RentalSystemFacade facade = RentalSystemFacade.getInstance();

    public Rental rentEquipment(String userId, String equipmentId, int days) {
        // TODO: TO BE IMPLEMENTED BY MEMBER A
        // validate inputs using Validator before calling facade

        // FIX: return type calls facade rental methods
        return null;
    }

    public void returnEquipment(String rentalId, String condition) {
        // TODO: TO BE IMPLEMENTED BY MEMBER A
        // condition options: "Excellent", "Good", "Damaged"
    }

    public Rental findRentalById(String id) {
        // TODO: TO BE IMPLEMENTED BY MEMBER A
        // FIX: return type calls facade rental methods
        return null;
    }

    public List<Rental> getUserRentals(String userId) {
        // TODO: TO BE IMPLEMENTED BY MEMBER A
        // FIX: return type calls facade
        return null;
    }

    public List<Rental> listAllRentals() {
        // TODO: TO BE IMPLEMENTED BY MEMBER A
        // FIX: return type calls facade rental methods
        return null;
    }
}