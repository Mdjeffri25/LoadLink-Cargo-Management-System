                                                                                                 import service.AppContext;
import ui.LoginScreen;
import util.SampleDataLoader;
                                                                                                import util.UIConstants;

import javax.swing.*;
                                                                                                import javax.swing.border.Border;

/**
 * LoadLink - Smart Cargo Space Optimization and Load Sharing Platform.
 * Entry point: loads sample data, then shows the Login screen.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            UIManager.put("Button.background", UIConstants.PRIMARY);
            UIManager.put("Button.foreground", java.awt.Color.WHITE);
            UIManager.put("Button.focusPainted", Boolean.FALSE);
            UIManager.put("Button.contentAreaFilled", Boolean.TRUE);
            UIManager.put("Button.opaque", Boolean.TRUE);
            UIManager.put("Button.border", BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.PRIMARY_DARK),
                    BorderFactory.createEmptyBorder(6, 14, 6, 14)));

            AppContext ctx = AppContext.getInstance();
            SampleDataLoader.load(ctx.getUserService(), ctx.getTruckService(), ctx.getBookingService());

            new LoginScreen().setVisible(true);
        });
    }
}

