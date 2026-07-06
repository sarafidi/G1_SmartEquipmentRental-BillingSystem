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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.FlowLayout;

import controller.BillingController;
import model.bill.Bill;

public class BillPanel extends JPanel {
    private final BillingController controller = new BillingController();

    // Generate / lookup bar 
    private JTextField rentalIdField;

    // Receipt display
    private JTextArea billArea;

    // Bill history table 
    private JTable historyTable;
    private DefaultTableModel historyModel;
    private List<Bill> currentHistory = new ArrayList<>();

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
        // Receipt area
        billArea = new JTextArea();
        billArea.setEditable(false);
        billArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        billArea.setText("Enter a Rental ID above and click \"Generate / View Bill\",\n" +
                "or select a bill from the history table below.");

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
        }
    }

    private void onHistoryRowSelected() {
        int row = historyTable.getSelectedRow();
        if (row >= 0 && row < currentHistory.size()) {
            displayBill(currentHistory.get(row));
        }
    }

    public void displayBill(Bill bill) {
        if (bill == null) {
            billArea.setText("No bill found for that Rental ID.\n" +
                    "(A bill is only generated after the equipment has been returned.)");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("         RENTAL INVOICE / RECEIPT\n");
        sb.append("========================================\n");
        sb.append(String.format("Bill ID    : %s%n", bill.getBillId()));
        sb.append(String.format("Rental ID  : %s%n", bill.getRentalId()));
        sb.append(String.format("Bill Date  : %s%n", bill.getBillDate()));
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-20s RM %10.2f%n", "Base Rental Fee", bill.getBaseRentalFee()));
        sb.append(String.format("%-20s -RM %9.2f%n", "User Discount", bill.getDiscountAmount()));
        sb.append(String.format("%-20s +RM %9.2f%n", "Penalties", bill.getPenaltyAmount()));
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-20s RM %10.2f%n", "NET PAYABLE", bill.getNetPayable()));
        sb.append("========================================\n");

        billArea.setText(sb.toString());
        billArea.setCaretPosition(0);
    }

    public void onPrintClick() {
        if (billArea.getText() == null || billArea.getText().isBlank()) {
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