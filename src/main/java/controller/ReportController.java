package controller;

import model.RentalSystemFacade;
import model.bill.Bill;
import model.rental.Rental;

import java.time.LocalDate;
import java.util.List;

public class ReportController {
    private final RentalSystemFacade facade = RentalSystemFacade.getInstance();
    public List<Bill> getAllBills() {
        return facade.listAllBills();
    }

    public List<Rental> getOverdueRentals() {
        return facade.getOverdueRentals();
    }

    public double getTotalRevenue() {
        return facade.getTotalRevenue();
    }

    public int getTotalEquipmentCount() {
        return facade.listAllEquipment().size();
    }

    public int getAvailableEquipmentCount() {
        return facade.listAvailableEquipment().size();
    }

    public int getTotalRentalCount() {
        return facade.listAllRentals().size();
    }

    public String buildCsvReport() {
        StringBuilder sb = new StringBuilder();

        // == SUMMARY =============================================
        sb.append("SMART EQUIPMENT RENTAL & BILLING SYSTEM - REPORT\n");
        sb.append("Generated:,").append(LocalDate.now()).append("\n\n");

        sb.append("SUMMARY\n");
        sb.append("Total Equipment,").append(getTotalEquipmentCount()).append("\n");
        sb.append("Available Equipment,").append(getAvailableEquipmentCount()).append("\n");
        sb.append("Total Rentals,").append(getTotalRentalCount()).append("\n");
        sb.append(String.format("Total Revenue,RM %.2f\n", getTotalRevenue()));

        // == OVERDUE RENTALS =============================================
        sb.append("OVERDUE RENTALS\n");
        sb.append("Rental ID, User, Equipment, Due Date, Days Overdue\n");
        for (Rental r : getOverdueRentals()) {
            sb.append(String.join(",",
                    r.getRentalId(),
                    r.getUser().getName(),
                    r.getEquipment().getName(),
                    r.getDueDate().toString(),
                    r.getDaysOverdue() + " days\n"
            ));
        }

        // == ALL BILLS =============================================
        sb.append("\nALL BILLS\n");
        sb.append("Bill ID, Rental ID, Base Fee, Discount, Penalty, Net Payable, Date\n");
        for (Bill b : getAllBills()) {
            sb.append(String.format("%s, %s, %.2f, %.2f, %.2f, %.2f, %s\n",
                    b.getBillId(), b.getRentalId(),
                    b.getBaseRentalFee(),
                    b.getDiscountAmount(),
                    b.getPenaltyAmount(),
                    b.getNetPayable(),
                    b.getBillDate()
            ));
        }
        return sb.toString();
    }
}