package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import controller.ReportController;
import model.bill.Bill;
import model.rental.Rental;

public class ReportPanel extends JPanel {
    private final ReportController controller = new ReportController();
    private final JLabel lastUpdatedLabel;

    public ReportPanel() {
        setLayout(new BorderLayout(0, 10));
        add(buildSummaryCards(), BorderLayout.NORTH);
        add(buildDetailsTab(), BorderLayout.CENTER);

        // timestamp footer
        lastUpdatedLabel = new JLabel("", SwingConstants.CENTER);
        lastUpdatedLabel.setForeground(Color.GRAY);
        lastUpdatedLabel.setFont(new Font("Courier", Font.ITALIC, 12));
        add(lastUpdatedLabel, BorderLayout.SOUTH);
        updateTimeStamp();
    }

    private JPanel buildSummaryCards() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 5));

        JPanel cards = new JPanel(new GridLayout(1, 4, 10, 10));
        cards.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        cards.add(makeCard("Total Equipment", String.valueOf(controller.getTotalEquipmentCount())));
        cards.add(makeCard("Available", String.valueOf(controller.getAvailableEquipmentCount())));
        cards.add(makeCard("Total Rentals", String.valueOf(controller.getTotalRentalCount())));
        cards.add(makeCard("Total Revenue", String.format("RM %.2f", controller.getTotalRevenue())));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshReportPanel());

        JButton downloadBtn = new JButton("Download Report (CSV)");
        downloadBtn.setFont(new Font("Arial", Font.BOLD, 12));
        downloadBtn.addActionListener(e -> onDownloadClick());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(refreshBtn);
        btnPanel.add(downloadBtn);

        wrapper.add(cards, BorderLayout.CENTER);
        wrapper.add(btnPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel makeCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createTitledBorder(title));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Courier", Font.BOLD, 22));
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // == CENTER TABS =============================================

    private JTabbedPane buildDetailsTab() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Overdue Rentals", buildOverdueTable());
        tabs.addTab("All Bills", buildBillsTable());
        return tabs;
    }

    private JScrollPane buildOverdueTable() {
        String[] cols = {"Rental ID", "User", "Equipment", "Due Date", "Days Overdue"};
        DefaultTableModel tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Rental r : controller.getOverdueRentals()) {
            tableModel.addRow(new Object[]{
                    r.getRentalId(),
                    r.getUser().getName(),
                    r.getEquipment().getName(),
                    r.getDueDate(),
                    r.getDaysOverdue() + " days"
            });
        }
        return new JScrollPane(new JTable(tableModel));
    }

    private JScrollPane buildBillsTable() {
        String[] cols = {"Bill ID", "Rental ID", "Base Fee", "Discount", "Penalty", "Net Payable", "Date"};
        DefaultTableModel tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Bill b : controller.getAllBills()) {
            tableModel.addRow(new Object[]{
                    b.getBillId(),
                    b.getRentalId(),
                    String.format("RM %.2f", b.getBaseRentalFee()),
                    String.format("RM %.2f", b.getDiscountAmount()),
                    String.format("RM %.2f", b.getPenaltyAmount()),
                    String.format("RM %.2f", b.getNetPayable()),
                    b.getBillDate()
            });
        }
        return new JScrollPane(new JTable(tableModel));
    }

    public void refreshReportPanel() {
        removeAll();
        add(buildSummaryCards(), BorderLayout.NORTH);
        add(buildDetailsTab(), BorderLayout.CENTER);
        add(lastUpdatedLabel, BorderLayout.SOUTH);

        updateTimeStamp();
        revalidate();
        repaint();
    }

    // == CSV DOWNLOAD =============================================
    private void onDownloadClick() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("rental_report_" + LocalDate.now() + ".csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
            fw.write(controller.buildCsvReport());
            JOptionPane.showMessageDialog(this, "Report saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Save failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateTimeStamp();
    }

    // == TIMESTAMP FOOTER =============================================
    private void updateTimeStamp() {
        String now  = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"));
        lastUpdatedLabel.setText("Last updated: " + now);
    }


}