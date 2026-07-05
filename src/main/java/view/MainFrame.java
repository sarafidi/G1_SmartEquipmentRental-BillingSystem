package view;

import model.UserType;
import util.SessionManager;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;

    private EquipmentPanel equipmentPanel;
    private RentalPanel rentalPanel;
    private BillPanel billPanel;
    private UserPanel userPanel;
    private ReportPanel reportPanel;

    public MainFrame() {
        setTitle("Smart Equipment Rental & Billing System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        // if login fails or is canceled, dialog component stays
        while (!SessionManager.getInstance().isLoggedIn()) {
            // show login dialog before building main UI
            LoginDialog loginDialog = new LoginDialog(this);
            loginDialog.setVisible(true);

            // if user closes LoginDialog without logging in, while condition re-evaluates.
            // LoginDialog should wire a close/camcel button that
            //      calls System.exit(0) with a confirmation prompt
            //      if the user truly wants to quit.
        }

        buildUI();
    }

    private void buildUI() {
        tabbedPane = new JTabbedPane();

        equipmentPanel = new EquipmentPanel();
        rentalPanel = new RentalPanel();
        billPanel = new BillPanel();
        userPanel = new UserPanel();
        reportPanel = new ReportPanel();

        tabbedPane.addTab("Equipment", equipmentPanel);
        tabbedPane.addTab("Rentals", rentalPanel);
        tabbedPane.addTab("Billing", billPanel);

        // only Admin can see User Management tab
        if (SessionManager.getInstance().getCurrentUser().getUserType()
                == UserType.ADMIN) {
            tabbedPane.addTab("User Management", userPanel);
            tabbedPane.addTab("Reports", reportPanel);
        }

        add(tabbedPane, BorderLayout.CENTER);
    }
}