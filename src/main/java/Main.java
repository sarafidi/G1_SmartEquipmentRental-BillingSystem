import util.DataStore;

import javax.swing.*;

public class Main {
    public static void main() {
        // init database files first so data lists are populated
        try {
            System.out.println("Initializing JSON Database...");
            DataStore.getInstance().init();
            System.out.println("Database successfully initialized");
        } catch (Exception e) {
            System.err.println("Fatal: Failed to initialize datastore");
            e.printStackTrace();
            System.exit(1);
        }

        // reason: Swing is not thread-safe — all UI must be created on the EDT
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // MainFrame is the top-level Swing JFrame wrapper
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                } catch (Exception e) {
                    System.err.println("Fatal: Failed to launch graphical interface");
                    e.printStackTrace();
                }
            }
        });
    }
}