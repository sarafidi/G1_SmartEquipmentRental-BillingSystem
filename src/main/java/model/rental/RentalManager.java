package model.rental;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.RentalStatus;
import model.equipment.Equipment;
import model.strategy.PenaltyRule;
import model.strategy.PricingStrategy;
import model.user.User;
import util.DataStore;
import util.IDGenerator;

public class RentalManager {
    private List<Rental> rentals;
    private PricingStrategy pricingStrategy;
    private List<PenaltyRule> penaltyRules;
    private final DataStore instance = DataStore.getInstance();

    public RentalManager(PricingStrategy initialStrategy, List<PenaltyRule> initialRules) {
        this.rentals = instance.getRentals();
        this.pricingStrategy = initialStrategy;
        this.penaltyRules = initialRules;
    }

    public Rental createRental(User user, Equipment equipment, int days) {
        if (!equipment.isAvailable()) {
            throw new IllegalStateException("Equipment is already rented.");
        }

        // generate auto rental ID
        String rentalId = IDGenerator.generateRentalId();
        Rental rental = new Rental(rentalId, user, equipment, LocalDate.now(), days);
        equipment.setAvailable(false);  // mark item as unavailable
        rentals.add(rental);

        // saved changes to JSON files
        instance.saveEquipment();
        instance.saveRental();

        return rental;
    }

    public Rental closeRental(String id, String condition) {
        Rental rental = findById(id);
        if (rental == null) throw new IllegalArgumentException("Rental record not found");
        if (rental.getStatus() == RentalStatus.RETURNED) throw new IllegalStateException("Rental is already closed");

        rental.setReturnDate(LocalDate.now());
        rental.setCondition(condition);
        rental.setStatus(RentalStatus.RETURNED);
        rental.getEquipment().setAvailable(true);   // return item to inventory

        // save changes to JSON files
        instance.saveEquipment();
        instance.saveRental();

        return rental;
    }

    public Rental findById(String id) {
        return rentals.stream()
                .filter(r -> r.getRentalId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public List<Rental> getRentalsByUser(String userId) {
        return rentals.stream()
                .filter(r -> r.getUser().getUserId().equalsIgnoreCase(userId))
                .collect(Collectors.toList());
    }

    public List<Rental> listAll() {
        // creates brand-new list containing same item
        // caller can modify list without affecting original list
        return new ArrayList<>(rentals);
    }

    // allow changing strategy at runtime
    public void setPricingStrategy(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
    }

    public List<PenaltyRule> getPenaltyRules() {
        return penaltyRules;
    }
}