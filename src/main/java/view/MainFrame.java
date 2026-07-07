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

        long buildUiStart = System.currentTimeMillis();
        System.out.println("[PERF] Starting buildUI...");
//        buildUI();
        System.out.println("[PERF] buildUI took: " + (System.currentTimeMillis() - buildUiStart) + " ms");
    }

    public void showLoginAndBuild() {
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
        setVisible(true);
        revalidate();
        repaint();
    }

    private void buildUI() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        menuBar.add(Box.createHorizontalGlue());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Courier", Font.BOLD, 12));
        logoutBtn.setForeground(new Color(200, 50,50));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setFocusPainted(false);

        logoutBtn.addActionListener(e -> handleLogout());
        menuBar.add(logoutBtn);
        setJMenuBar(menuBar);

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

        tabbedPane.addChangeListener(e -> {
            equipmentPanel.refreshTable();
            rentalPanel.refreshTable();
            billPanel.refreshHistory();
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void handleLogout() {
        int confirmDialog = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?",
                "Logout confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirmDialog == JOptionPane.YES_OPTION) {
            SessionManager.getInstance().clearSession();
            setVisible(false);
            getContentPane().removeAll();
            setJMenuBar(null);
            showLoginAndBuild();
        }
    }
}