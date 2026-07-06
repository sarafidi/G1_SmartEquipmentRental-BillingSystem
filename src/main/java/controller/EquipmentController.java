package controller;

import model.RentalSystemFacade;
import model.UserType;
import model.equipment.Equipment;
import model.user.User;

import java.util.List;

public class EquipmentController {
    private final RentalSystemFacade facade = RentalSystemFacade.getInstance();

    public void addEquipment(Equipment equipment) {
        facade.addEquipment(equipment);
    }

    public void removeEquipment(String id) {
        facade.removeEquipment(id);
    }

    public Equipment findEquipmentById(String id) {
        return facade.findEquipmentById(id);
    }

    public List<Equipment> listAvailableEquipment() {
        return facade.listAvailableEquipment();
    }

    public List<Equipment> listAllEquipment() {
        return facade.listAllEquipment();
    }

    public boolean isCurrentUserAdmin() {
        User currUser = facade.getCurrentUser();
        return currUser != null && currUser.getUserType() == UserType.ADMIN;
    }
}