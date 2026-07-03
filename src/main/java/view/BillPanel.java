package view;

import controller.BillingController;
import model.bill.Bill;

import javax.swing.*;
import java.awt.*;

public class BillPanel extends JPanel {
    private final BillingController controller = new BillingController();
    private JTextArea billArea;

    public BillPanel() {
        setLayout(new BorderLayout());
        // TODO: TO BE IMPLEMENTED BY MEMBER B
    }

    public void displayBill(Bill bill) {
        // TODO: TO BE IMPLEMENTED BY MEMBER B
        // Format and render bill fields into billArea
    }

    public void onPrintClick() {
        // TODO: TO BE IMPLEMENTED BY MEMBER B
    }
}