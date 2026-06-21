package ui;

import model.Booking;
import model.Truck;
import service.AppContext;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;

public class ReportsPanel extends JPanel implements MainDashboard.Refreshable {

    private JTextArea reportArea;

    public ReportsPanel(MainDashboard parent) {
        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Reports");
        title.setFont(UIConstants.FONT_TITLE);
        add(title, BorderLayout.NORTH);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        buttonBar.setOpaque(false);
        JButton truckBtn = makeButton("Truck Report");
        JButton bookingBtn = makeButton("Booking Report");
        JButton revenueBtn = makeButton("Revenue Report");
        truckBtn.addActionListener(e -> showTruckReport());
        bookingBtn.addActionListener(e -> showBookingReport());
        revenueBtn.addActionListener(e -> showRevenueReport());
        buttonBar.add(truckBtn);
        buttonBar.add(bookingBtn);
        buttonBar.add(revenueBtn);

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(title, BorderLayout.NORTH);
        north.add(buttonBar, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        reportArea.setBackground(Color.WHITE);
        reportArea.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        JScrollPane scroll = new JScrollPane(reportArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Report Output"));
        add(scroll, BorderLayout.CENTER);

        showTruckReport();
    }

    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFont(UIConstants.FONT_BUTTON);
        UIConstants.stylePrimaryButton(b);
        return b;
    }

    private void showTruckReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========== TRUCK REPORT ===========\n\n");
        for (Truck t : AppContext.getInstance().getTruckService().getAllTrucks()) {
            sb.append(String.format("%-8s %-14s %-15s %-30s Total:%-5.1f Used:%-5.1f Avail:%-5.1f Updated:%-16s Util:%5.1f%%  Rs.%.1f/kg\n",
                    t.getTruckId(), t.getTruckNumber(), t.getOwnerName(), t.getRouteString(),
                t.getTotalCapacity(), t.getUsedCapacity(), t.getAvailableCapacity(), t.getAvailabilityUpdatedAtText(),
                    t.getUtilizationPercent(), t.getCostPerKg()));
        }
        sb.append("\nTotal Trucks: ").append(AppContext.getInstance().getTruckService().totalTrucks());
        reportArea.setText(sb.toString());
    }

    private void showBookingReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========== BOOKING REPORT ===========\n\n");
        for (Booking b : AppContext.getInstance().getBookingService().getBookingHistory()) {
            sb.append(String.format("%-8s %-12s %-18s %-30s %5.1fT  Rs.%.2f  [%s]\n",
                    b.getBookingId(), b.getTruckNumber(), b.getCustomerName(), b.getRoute(),
                    b.getBookedWeight(), b.getTotalCost(), b.getBookedAt()));
        }
        sb.append("\nTotal Bookings: ").append(AppContext.getInstance().getBookingService().totalBookings());
        reportArea.setText(sb.toString());
    }

    private void showRevenueReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========== REVENUE REPORT ===========\n\n");
        double total = AppContext.getInstance().getBookingService().totalRevenue();
        sb.append(String.format("Total Revenue Collected: Rs. %.2f\n", total));
        sb.append(String.format("Total Bookings: %d\n", AppContext.getInstance().getBookingService().totalBookings()));
        sb.append(String.format("Most Active Route: %s\n", AppContext.getInstance().getBookingService().mostActiveRoute()));
        sb.append(String.format("Average Capacity Utilization: %.1f%%\n",
                AppContext.getInstance().getAnalyticsService().averageUtilization()));
        reportArea.setText(sb.toString());
    }

    @Override
    public void refresh() {
        showTruckReport();
    }
}
