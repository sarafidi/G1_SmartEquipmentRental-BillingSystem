package model.equipment;

import util.DataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EquipmentManager {
    private List<Equipment> equipmentList;

    public EquipmentManager() {
        // load in-memory list stored from JSON by DataStore
        this.equipmentList = DataStore.getInstance().getEquipments();
    }

    public Equipment findById(String id) {
        return equipmentList.stream()
                .filter(e -> e.getEquipmentId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public void addEquipment(Equipment e) {
        equipmentList.add(e);
        DataStore.getInstance().saveEquipment();
    }

    public void removeEquipment(String id) {
        equipmentList.removeIf(e -> e.getEquipmentId().equalsIgnoreCase(id));
        DataStore.getInstance().saveEquipment();
    }

    public List<Equipment> listAvailable() {
        return equipmentList.stream()
                .filter(Equipment::isAvailable)
                .toList();
    }

    public List<Equipment> listAll() {
        return new ArrayList<>(equipmentList);
    }
}