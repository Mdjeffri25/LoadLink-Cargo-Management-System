package ui;

import service.AppContext;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel implements MainDashboard.Refreshable {

    private JLabel trucksValue, bookingsValue, revenueValue, utilValue;

    public HomePanel(MainDashboard parent) {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(UIConstants.TEXT_DARK);
        add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 4, 20, 0));
        cards.setOpaque(false);
        cards.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        trucksValue = new JLabel("0");
        bookingsValue = new JLabel("0");
        revenueValue = new JLabel("Rs. 0");
        utilValue = new JLabel("0%");

        cards.add(statCard("Total Trucks", trucksValue, UIConstants.PRIMARY));
        cards.add(statCard("Total Bookings", bookingsValue, UIConstants.ACCENT));
        cards.add(statCard("Total Revenue", revenueValue, new Color(0xEF6C00)));
        cards.add(statCard("Avg. Utilization", utilValue, new Color(0x6A1B9A)));

        add(cards, BorderLayout.CENTER);

        JLabel welcome = new JLabel("<html><br>Welcome to <b>LoadLink</b> — the Smart Cargo Space "
                + "Optimization & Load Sharing Platform.<br>Use the sidebar to register trucks, "
                + "search routes, book cargo, optimize return trips, and view analytics & reports.</html>");
        welcome.setFont(UIConstants.FONT_BODY);
        welcome.setForeground(UIConstants.TEXT_MUTED);
        add(welcome, BorderLayout.SOUTH);
    }

    private JPanel statCard(String label, JLabel valueLabel, Color accent) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, accent),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_SMALL);
        lbl.setForeground(UIConstants.TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(UIConstants.TEXT_DARK);

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(8));
        panel.add(valueLabel);
        return panel;
    }

    @Override
    public void refresh() {
        service.AnalyticsService analytics = AppContext.getInstance().getAnalyticsService();
        trucksValue.setText(String.valueOf(analytics.totalTrucks()));
        bookingsValue.setText(String.valueOf(analytics.totalBookings()));
        revenueValue.setText(String.format("Rs. %.2f", analytics.totalRevenue()));
        utilValue.setText(String.format("%.1f%%", analytics.averageUtilization()));
    }
}
