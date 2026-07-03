package model.equipment;

import util.DataStore;

import java.util.ArrayList;
import java.util.List;

public class EquipmentManager {
    private List<Equipment> equipmentList;

    public EquipmentManager(List<Equipment> equipmentList) {
        // load in-memory list stored from JSON by DataStore
        this.equipmentList = DataStore.getInstance().getEquipment();
    }

    public Equipment findById(String id) {
        for (Equipment e : equipmentList) {
            if (e.getEquipmentId().equalsIgnoreCase(id)) {
                return e;
            }
        }
        return null;
    }

    public void removeEquipment(String id) {
        equipmentList.removeIf(e -> e.getEquipmentId().equalsIgnoreCase(id));
        DataStore.getInstance().saveEquipment();
    }

    public List<Equipment> listAvailable() {
        List<Equipment> availableList = new ArrayList<>();
        for (Equipment e : equipmentList) {
            if (e.isAvailable()) {
                availableList.add(e);
            }
        }
        return availableList;
    }

    public List<Equipment> listAll() {
        return new ArrayList<>(equipmentList);
    }
}