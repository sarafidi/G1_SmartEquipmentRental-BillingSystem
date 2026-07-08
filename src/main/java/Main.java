import util.DataStore;
import view.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // init database files first so data lists are populated
        try {
            DataStore.getInstance().init();
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
                    mainFrame.showLoginAndBuild();
                } catch (Exception e) {
                    System.err.println("Fatal: Failed to launch graphical interface");
                    e.printStackTrace();
                }
            }
        });
    }
}