package ui;

import model.Truck;
import service.AppContext;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SearchRoutePanel extends JPanel implements MainDashboard.Refreshable {

    private JTextField sourceField, destField;
    private DefaultTableModel tableModel;
    private JLabel recommendationLabel;

    public SearchRoutePanel(MainDashboard parent) {
        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Route Search Engine");
        title.setFont(UIConstants.FONT_TITLE);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        searchBar.setOpaque(false);
        sourceField = new JTextField("Salem", 14);
        destField = new JTextField("Bangalore", 14);
        sourceField.setFont(UIConstants.FONT_BODY);
        destField.setFont(UIConstants.FONT_BODY);

        JButton searchBtn = new JButton("Search Matching Trucks");
        searchBtn.setFont(UIConstants.FONT_BUTTON);
        UIConstants.stylePrimaryButton(searchBtn);
        searchBtn.addActionListener(e -> doSearch());

        searchBar.add(labelOf("Source:"));
        searchBar.add(sourceField);
        searchBar.add(labelOf("Destination:"));
        searchBar.add(destField);
        searchBar.add(searchBtn);

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(title, BorderLayout.NORTH);
        north.add(searchBar, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Rank", "Truck ID", "Number", "Owner",
                "Route", "Available(T)", "Avail Updated", "Rs/Kg", "Utilization %"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(UIConstants.FONT_BODY);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Matching Trucks (cheapest first)"));
        add(scroll, BorderLayout.CENTER);

        recommendationLabel = new JLabel(" ");
        recommendationLabel.setFont(UIConstants.FONT_HEADING);
        recommendationLabel.setForeground(UIConstants.ACCENT);
        recommendationLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(recommendationLabel, BorderLayout.SOUTH);
    }

    private JLabel labelOf(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIConstants.FONT_BODY);
        return l;
    }

    private void doSearch() {
        String source = sourceField.getText().trim();
        String dest = destField.getText().trim();
        if (source.isEmpty() || dest.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter both source and destination.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Truck> matches = AppContext.getInstance().getTruckService().searchRoute(source, dest);
        tableModel.setRowCount(0);
        int rank = 1;
        for (Truck t : matches) {
            tableModel.addRow(new Object[]{rank++, t.getTruckId(), t.getTruckNumber(), t.getOwnerName(),
                    t.getRouteString(), String.format("%.1f", t.getAvailableCapacity()),
                    t.getAvailabilityUpdatedAtText(), t.getCostPerKg(),
                    String.format("%.1f", t.getUtilizationPercent())});
        }

        if (matches.isEmpty()) {
            recommendationLabel.setText("No trucks currently match this route. Try Return Trip Optimization.");
            recommendationLabel.setForeground(UIConstants.DANGER);
        } else {
            Truck best = matches.get(0);
            recommendationLabel.setText("✔ Recommended (lowest cost / best capacity): " + best.getTruckNumber()
                    + " — Rs." + best.getCostPerKg() + "/kg, " + String.format("%.1f", best.getAvailableCapacity())
                    + "T available @ " + best.getAvailabilityUpdatedAtText());
            recommendationLabel.setForeground(UIConstants.ACCENT);
        }
    }

    @Override
    public void refresh() {
        // search runs on demand; nothing to preload
    }
}
