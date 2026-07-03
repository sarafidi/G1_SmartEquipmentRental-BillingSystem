package controller;

import model.RentalSystemFacade;
import model.bill.Bill;

import java.util.List;

public class BillingController {
    private final RentalSystemFacade facade = RentalSystemFacade.getInstance();

    public Bill generateBill(String rentalId) {
        // TODO: TO BE IMPLEMENTED BY MEMBER B
        // FIX: return type calls facade billing methods
        return null;
    }

    public Bill findBillByRental(String rentalId) {
        // TODO: TO BE IMPLEMENTED BY MEMBER B
        // FIX: return type calls facade billing methods
        return null;
    }

    public List<Bill> getBillHistory(String userId) {
        // TODO: TO BE IMPLEMENTED BY MEMBER B
        // FIX: return type calls facade billing methods
        return null;
    }

}