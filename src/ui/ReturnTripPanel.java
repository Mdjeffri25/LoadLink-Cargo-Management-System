package ui;

import model.CargoRequest;
import model.Truck;
import service.AppContext;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReturnTripPanel extends JPanel implements MainDashboard.Refreshable {

    private JTextField cityField;
    private DefaultTableModel emptyTrucksModel;
    private DefaultTableModel matchesModel;

    public ReturnTripPanel(MainDashboard parent) {
        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Return Trip Optimization");
        title.setFont(UIConstants.FONT_TITLE);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        searchBar.setOpaque(false);
        cityField = new JTextField("Bangalore", 16);
        cityField.setFont(UIConstants.FONT_BODY);
        JButton findBtn = new JButton("Find Empty Trucks & Cargo Matches");
        findBtn.setFont(UIConstants.FONT_BUTTON);
        UIConstants.stylePrimaryButton(findBtn);
        findBtn.addActionListener(e -> doSearch());

        JLabel lbl = new JLabel("Arrival City (where truck is now empty):");
        lbl.setFont(UIConstants.FONT_BODY);
        searchBar.add(lbl);
        searchBar.add(cityField);
        searchBar.add(findBtn);

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(title, BorderLayout.NORTH);
        north.add(searchBar, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 16));
        center.setOpaque(false);

        emptyTrucksModel = new DefaultTableModel(new Object[]{"Truck ID", "Number", "Owner",
                "Route", "Utilization %"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable emptyTable = new JTable(emptyTrucksModel);
        emptyTable.setRowHeight(24);
        JScrollPane scroll1 = new JScrollPane(emptyTable);
        scroll1.setBorder(BorderFactory.createTitledBorder("Trucks Returning Empty from this City"));

        matchesModel = new DefaultTableModel(new Object[]{"Request ID", "Customer", "Pickup",
                "Drop", "Weight (T)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable matchesTable = new JTable(matchesModel);
        matchesTable.setRowHeight(24);
        JScrollPane scroll2 = new JScrollPane(matchesTable);
        scroll2.setBorder(BorderFactory.createTitledBorder("Suggested Cargo Requests (pickup = this city)"));

        center.add(scroll1);
        center.add(scroll2);
        add(center, BorderLayout.CENTER);
    }

    private void doSearch() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) return;

        List<Truck> emptyTrucks = AppContext.getInstance().getTruckService().findEmptyReturnTrucks(city);
        emptyTrucksModel.setRowCount(0);
        for (Truck t : emptyTrucks) {
            emptyTrucksModel.addRow(new Object[]{t.getTruckId(), t.getTruckNumber(), t.getOwnerName(),
                    t.getRouteString(), String.format("%.1f", t.getUtilizationPercent())});
        }

        List<CargoRequest> matches = AppContext.getInstance().getBookingService().findReturnMatches(city);
        matchesModel.setRowCount(0);
        for (CargoRequest r : matches) {
            matchesModel.addRow(new Object[]{r.getRequestId(), r.getCustomerName(), r.getPickupLocation(),
                    r.getDropLocation(), r.getCargoWeight()});
        }

        if (emptyTrucks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No trucks are currently empty/near-empty arriving at " + city + ".",
                    "No Matches", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void refresh() {
        // on-demand search; nothing to preload
    }
}
