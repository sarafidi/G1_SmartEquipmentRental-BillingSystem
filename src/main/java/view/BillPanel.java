package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.FlowLayout;

import controller.BillingController;
import model.bill.Bill;
import model.equipment.LabEquipment;
import model.rental.Rental;

public class BillPanel extends JPanel {
    private final BillingController controller = new BillingController();

    // Generate / lookup bar 
    private JTextField rentalIdField;

    // Receipt display
    private JTextPane billArea;

    // Bill history table 
    private JTable historyTable;
    private DefaultTableModel historyModel;
    private List<Bill> currentHistory = new ArrayList<>();
    private Bill activeBill;

    // Constructor 
    public BillPanel() {
        setLayout(new BorderLayout(0, 5));
        buildTopBar();
        buildCenter();
        refreshHistory();
    }

    // Generate / View bar
    private void buildTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        rentalIdField = new JTextField(12);
        JButton generateBtn = new JButton("Generate / View Bill");
        JButton printBtn = new JButton("Print Receipt");
        JButton refreshBtn = new JButton("Refresh History");

        rentalIdField.addActionListener(e -> onGenerateClick());
        generateBtn.addActionListener(e -> onGenerateClick());
        printBtn.addActionListener(e -> onPrintClick());
        refreshBtn.addActionListener(e -> refreshHistory());

        topBar.add(new JLabel("Rental ID: "));
        topBar.add(rentalIdField);
        topBar.add(generateBtn);
        topBar.add(printBtn);
        topBar.add(refreshBtn);

        add(topBar, BorderLayout.NORTH);
    }

    // Receipt display (top) + Bill history table (bottom) 
    private void buildCenter() {
        billArea = new JTextPane();
        billArea.setContentType("text/html");
        billArea.setEditable(false);
        billArea.setText("<html><pre style='font-family: monospaced; font-size: 10px; margin: 0;'>" +
                "Enter a Rental ID above and click \"Generate / View Bill\",\n" +
                "or select a bill from the history table below.</pre></html>");

        JScrollPane receiptScroll = new JScrollPane(billArea);
        receiptScroll.setBorder(BorderFactory.createTitledBorder("Bill Receipt"));

        // History table 
        String[] columns = {"Bill ID", "Rental ID", "Bill Date", "Net Payable (RM)"};
        historyModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(historyModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onHistoryRowSelected();
            }
        });

        JScrollPane historyScroll = new JScrollPane(historyTable);
        historyScroll.setBorder(BorderFactory.createTitledBorder("My Bill History"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, receiptScroll, historyScroll);
        split.setResizeWeight(0.55);
        split.setDividerLocation(280);

        add(split, BorderLayout.CENTER);
    }

    // Actions 
    private void onGenerateClick() {
        String rentalId = rentalIdField.getText().trim();
        try {
            Bill bill = controller.generateBill(rentalId);
            displayBill(bill);
            refreshHistory();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Unable to Generate Bill", JOptionPane.ERROR_MESSAGE);
            displayBill(null);
        }
    }

    private void onHistoryRowSelected() {
        int row = historyTable.getSelectedRow();
        if (row >= 0 && row < currentHistory.size()) {
            Bill selectedBill = currentHistory.get(row);
            rentalIdField.setText(selectedBill.getRentalId());
            displayBill(selectedBill);
        }
    }

    public void displayBill(Bill bill) {
        this.activeBill = bill;
        if (bill == null) {
            billArea.setText("<html><pre style='font-family: monospaced; font-size: 10px; margin: 0;'>" +
                    "No bill found for that Rental ID.\n" +
                    "(A bill is only generated after the equipment has been returned.)</pre></html>");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<html><pre style='font-family: monospaced; font-size: 10px; margin: 0;'>");
        sb.append("<b>========================================</b>\n");
        sb.append("<b>         RENTAL INVOICE / RECEIPT</b>\n");
        sb.append("<b>========================================</b>\n");
        sb.append(String.format("Bill ID    : %s%n", bill.getBillId()));
        sb.append(String.format("Rental ID  : %s%n", bill.getRentalId()));
        sb.append(String.format("Bill Date  : %s%n", bill.getBillDate()));
        sb.append("----------------------------------------\n");
        
        sb.append(String.format("<b>%-20s RM %10.2f</b>%n", "Base Rental Fee", bill.getBaseRentalFee()));

        // rental fee calculation
        Rental rentalForBase = controller.findRentalById(bill.getRentalId());
        if (rentalForBase != null && rentalForBase.getEquipment() instanceof LabEquipment lab) {
            if (lab.getHazardLevel() > 2) {
                double rawFee = lab.getDailyRate() * rentalForBase.getDaysRented();
                sb.append(String.format("  - Daily Rental      :  RM %10.2f%n", rawFee));
                sb.append(String.format("    (RM%.2f × %dd)%n", lab.getDailyRate(), rentalForBase.getDaysRented()));
                sb.append(String.format("  - Lab Safety Surchg :  RM %10.2f%n", 50.00));
            }
        }

        // discount calculation
        sb.append(String.format("\n<b>%-20s -RM %9.2f</b>%n", "User Discount", bill.getDiscountAmount()));

        // penalty calculation
        if (bill.getPenaltyAmount() > 0) {
            sb.append(String.format("<b>%-20s +RM %9.2f</b>%n", "Total Penalties", bill.getPenaltyAmount()));
            Rental rental = controller.findRentalById(bill.getRentalId());
            if (rental != null) {
                double lateFee = bill.getLatePenalty();
                double damageFee = bill.getDamagePenalty();
                
                // if this is a legacy bill with no saved itemized penalties
                if (lateFee == 0.0 && damageFee == 0.0) {
                    java.time.LocalDate end = (rental.getReturnDate() != null) ? rental.getReturnDate() : java.time.LocalDate.now();
                    if (end.isAfter(rental.getDueDate())) {
                        long lateDays = java.time.temporal.ChronoUnit.DAYS.between(rental.getDueDate(), end);
                        lateFee = lateDays * 10.00;
                    }
                    if ("Damaged".equalsIgnoreCase(rental.getCondition())) {
                        damageFee = rental.getEquipment().getDailyRate() * 1.5;
                    }
                }
                
                if (lateFee > 0) {
                    sb.append(String.format("  - Late Return Fee : +RM %9.2f%n", lateFee));
                }
                if (damageFee > 0) {
                    sb.append(String.format("  - Damage Surcharge: +RM %9.2f%n", damageFee));
                }
            }
        } else {
            sb.append(String.format("\n<b>%-20s +RM %9.2f</b>%n", "Penalties", 0.0));
        }
        sb.append("----------------------------------------\n");
        
        // Net Payable value
        sb.append(String.format("<b>%-20s RM %10.2f</b>%n", "NET PAYABLE", bill.getNetPayable()));
        sb.append("========================================\n");
        sb.append("</pre></html>");

        billArea.setText(sb.toString());
        billArea.setCaretPosition(0);
    }

    public void onPrintClick() {
        String rentalIdEntered = rentalIdField.getText().trim();
        if (activeBill == null || !activeBill.getRentalId().equalsIgnoreCase(rentalIdEntered)) {
            JOptionPane.showMessageDialog(this, "Generate or select a bill before printing.");
            return;
        }

        try {
            boolean printed = billArea.print();
            if (!printed) {
                JOptionPane.showMessageDialog(this, "Print job was cancelled.");
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Unable to print: " + ex.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Bill history for the currently logged-in user
    public void refreshHistory() {
        String userId = controller.getCurrentUserId();
        currentHistory = controller.getBillHistory(userId);

        historyModel.setRowCount(0);
        for (Bill b : currentHistory) {
            historyModel.addRow(new Object[]{
                    b.getBillId(),
                    b.getRentalId(),
                    b.getBillDate(),
                    String.format("%.2f", b.getNetPayable())
            });
        }
    }
}