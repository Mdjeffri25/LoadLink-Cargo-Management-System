package ui;

import model.Truck;
import service.AppContext;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AnalyticsPanel extends JPanel implements MainDashboard.Refreshable {

    private JLabel trucksValue, bookingsValue, revenueValue, routeValue, utilValue;
    private DefaultTableModel utilTableModel;

    public AnalyticsPanel(MainDashboard parent) {
        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Analytics Dashboard");
        title.setFont(UIConstants.FONT_TITLE);
        add(title, BorderLayout.NORTH);

        JPanel metrics = new JPanel(new GridLayout(1, 5, 14, 0));
        metrics.setOpaque(false);
        metrics.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        trucksValue = new JLabel("0");
        bookingsValue = new JLabel("0");
        revenueValue = new JLabel("0");
        routeValue = new JLabel("N/A");
        utilValue = new JLabel("0%");

        metrics.add(metricCard("Total Trucks", trucksValue));
        metrics.add(metricCard("Total Bookings", bookingsValue));
        metrics.add(metricCard("Total Revenue (Rs.)", revenueValue));
        metrics.add(metricCard("Most Active Route", routeValue));
        metrics.add(metricCard("Avg. Utilization", utilValue));

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(title, BorderLayout.NORTH);
        north.add(metrics, BorderLayout.SOUTH);
        removeAll();
        add(north, BorderLayout.NORTH);

        utilTableModel = new DefaultTableModel(new Object[]{"Truck Number", "Owner", "Total(T)",
                "Used(T)", "Available(T)", "Avail Updated", "Utilization %"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(utilTableModel);
        table.setRowHeight(26);
        table.setFont(UIConstants.FONT_BODY);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Per-Truck Capacity Utilization"));
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel metricCard(String label, JLabel valueLabel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, UIConstants.PRIMARY),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_SMALL);
        lbl.setForeground(UIConstants.TEXT_MUTED);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(UIConstants.TEXT_DARK);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(6));
        panel.add(valueLabel);
        return panel;
    }

    @Override
    public void refresh() {
        service.AnalyticsService analytics = AppContext.getInstance().getAnalyticsService();
        trucksValue.setText(String.valueOf(analytics.totalTrucks()));
        bookingsValue.setText(String.valueOf(analytics.totalBookings()));
        revenueValue.setText(String.format("%.2f", analytics.totalRevenue()));
        routeValue.setText(analytics.mostActiveRoute());
        utilValue.setText(String.format("%.1f%%", analytics.averageUtilization()));

        utilTableModel.setRowCount(0);
        for (Truck t : AppContext.getInstance().getTruckService().getAllTrucks()) {
            utilTableModel.addRow(new Object[]{t.getTruckNumber(), t.getOwnerName(), t.getTotalCapacity(),
                    t.getUsedCapacity(), t.getAvailableCapacity(), t.getAvailabilityUpdatedAtText(),
                    String.format("%.1f", t.getUtilizationPercent())});
        }
    }
}
