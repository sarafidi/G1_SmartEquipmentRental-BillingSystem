package util;

import model.bill.Bill;
import model.rental.Rental;
import model.equipment.Equipment;
import model.user.User;

import java.util.ArrayList;

public class IDGenerator {
    private static int extractMaxNumeric(ArrayList<String> ids) {
        // id.split("-") gives ["PAT", "0023"]
        // [1] gives "0023"
        // parseInt converts to 23
        int max = 0;
        for (String id : ids) {
            if (id == null || !id.contains("-")) continue;
            try {
                int num = Integer.parseInt(id.split("-")[1]);
                if (num > max) max = num;
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                // ignore malformed IDs
            }
        }
        return max;
    }

    public static String generateUserId() {
        ArrayList<String> ids = new ArrayList<>();
        for (User p : DataStore.getInstance().getUsers()) {
            ids.add(p.getUserId());
        }
        // %04d -> integer, min 4 digits, pad with zeros on left
        return String.format("USR-%04d", extractMaxNumeric(ids) + 1);
    }

    public static String generateEquipmentId() {
        ArrayList<String> ids = new ArrayList<>();
        for (Equipment p : DataStore.getInstance().getEquipments()) {
            ids.add(p.getEquipmentId());
        }
        // %04d -> integer, min 4 digits, pad with zeros on left
        return String.format("EQ-%04d", extractMaxNumeric(ids) + 1);
    }

    public static String generateRentalId() {
        ArrayList<String> ids = new ArrayList<>();
        for (Rental p : DataStore.getInstance().getRentals()) {
            ids.add(p.getRentalId());
        }
        // %04d -> integer, min 4 digits, pad with zeros on left
        return String.format("RN-%04d", extractMaxNumeric(ids) + 1);
    }

    public static String generateBillId() {
        ArrayList<String> ids = new ArrayList<>();
        for (Bill p : DataStore.getInstance().getBills()) {
            ids.add(p.getBillId());
        }
        // %04d -> integer, min 4 digits, pad with zeros on left
        return String.format("BL-%04d", extractMaxNumeric(ids) + 1);
    }
}