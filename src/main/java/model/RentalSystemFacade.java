package model;

import model.bill.Bill;
import model.bill.BillingManager;
import model.equipment.Equipment;
import model.equipment.EquipmentManager;
import model.rental.Rental;
import model.rental.RentalManager;
import model.user.User;
import model.user.UserManager;
import strategy.*;

import java.util.ArrayList;
import java.util.List;

public class RentalSystemFacade {
    private static RentalSystemFacade instance;
    
    private final EquipmentManager equipmentManager;
    private final RentalManager rentalManager;
    private final BillingManager billingManager;
    private final UserManager userManager;

    public RentalSystemFacade() {
        this.equipmentManager = new EquipmentManager();

        // define initial rules and strategy at system config layer
        // inject these into their subsystems to enforce loose coupling
        List<PenaltyRule> initialRules = new ArrayList<>();
        initialRules.add(new LatePenalty(10.00));   // default: RM10/day late fee
        initialRules.add(new DamagePenalty());  // default: damage surcharge rule
        this.rentalManager = new RentalManager(new DiscountedPricing(), initialRules);

        this.billingManager = new BillingManager();
        this.userManager = new UserManager();

        // dependency injection. wires subsystems tgt during startup
        // to avoid circular singleton calls
        this.billingManager.setRentalManager(this.rentalManager);
    }
    
    // singleton access point for controllers
    public static synchronized RentalSystemFacade getInstance() {
        if (instance == null) {
            instance = new RentalSystemFacade();
        }
        return instance;
    }

    // ===============================================
    // EQUIPMENT OPERATIONS
    // ===============================================
    public void addEquipment(Equipment equipment) {
        equipmentManager.addEquipment(equipment);
    }

    public void removeEquipment(String id) {
        equipmentManager.removeEquipment(id);
    }

    public Equipment findEquipmentById(String id) {
        return equipmentManager.findById(id);
    }

    public List<Equipment> listAvailableEquipment() {
        return equipmentManager.listAvailable();
    }

    public List<Equipment> listAllEquipment() {
        return equipmentManager.listAll();
    }

    // ===============================================
    // RENTAL OPERATIONS
    // ===============================================
    public Rental rentEquipment(String userId, String equipmentId, int days) {
        User user = userManager.findById(userId);
        Equipment equipment = equipmentManager.findById(equipmentId);

        if (user == null) throw new IllegalArgumentException("User ID not found: " + userId);
        if (equipment == null) throw new IllegalArgumentException("Equipment ID not found: " + equipmentId);

        // delegate transaction creation to RentalManager subsystem
        return rentalManager.createRental(user, equipment, days);
    }

    public void returnEquipment(String rentalId, String condition) {
        // 1. close transaction in RentalManager
        Rental rental = rentalManager.closeRental(rentalId, condition);

        // 2. auto generate invoice bill
        billingManager.generateBill(rental);
    }

    public Rental findRentalById(String id) {
        return rentalManager.findById(id);
    }

    public List<Rental> getUserRentals(String userId) {
        return rentalManager.getRentalsByUser(userId);
    }

    public List<Rental> listAllRentals() {
        return rentalManager.listAll();
    }

    // ===============================================
    // BILL OPERATIONS
    // ===============================================
    public Bill generateBill(String rentalId) {
        Rental rental = rentalManager.findById(rentalId);
        if (rental == null) throw new IllegalArgumentException("Rental ID not found.");
        return billingManager.generateBill(rental);
    }

    public Bill findBillByRental(String rentalId) {
        return billingManager.findBillByRental(rentalId);
    }

    public List<Bill> getBillHistory(String userId) {
        return billingManager.getBillHistory(userId);

    }

    // ===============================================
    // USER OPERATIONS
    // ===============================================
    public void addUser(User user) {
        userManager.addUser(user);
    }

    public void removeUser(String id) {
        userManager.removeUser(id);
    }

    public User findById(String id) {
        return userManager.findById(id);
    }

    public List<User> listAll() {
        return userManager.listAll();
    }

}