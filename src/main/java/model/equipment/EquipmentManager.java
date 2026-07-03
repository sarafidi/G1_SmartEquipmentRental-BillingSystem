package model.equipment;

import util.DataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EquipmentManager {
    private List<Equipment> equipmentList;

    public EquipmentManager(List<Equipment> equipmentList) {
        // load in-memory list stored from JSON by DataStore
        this.equipmentList = DataStore.getInstance().getEquipment();
    }

    public Equipment findById(String id) {
        return equipmentList.stream()
                .filter(e -> e.getEquipmentId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
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