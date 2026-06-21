package ui;

import model.Booking;
import model.Truck;
import service.AppContext;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BookCargoPanel extends JPanel implements MainDashboard.Refreshable {

    private JComboBox<Truck> truckCombo;
    private JTextField customerField, weightField;
    private JLabel costPreview;
    private JLabel availabilityPreview;
    private DefaultTableModel tableModel;

    public BookCargoPanel(MainDashboard parent) {
        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Book Cargo");
        title.setFont(UIConstants.FONT_TITLE);
        add(title, BorderLayout.NORTH);

        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setOpaque(false);
        formWrap.add(buildForm(), BorderLayout.NORTH);
        add(formWrap, BorderLayout.WEST);

        tableModel = new DefaultTableModel(new Object[]{"Booking ID", "Truck", "Customer", "Route",
                "Weight (T)", "Total Cost (Rs.)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(UIConstants.FONT_BODY);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Booking History"));
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIConstants.CARD_BG);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0E0E0)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        form.setPreferredSize(new Dimension(360, 420));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        truckCombo = new JComboBox<>();
        truckCombo.setFont(UIConstants.FONT_BODY);
        truckCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setFont(UIConstants.FONT_BODY);
            label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            if (value == null) {
                label.setText(index < 0 ? "Select Truck" : "No truck selected");
            } else {
                label.setText(value.getTruckNumber() + " | " + value.getRouteString()
                        + " | Avail: " + String.format("%.1f", value.getAvailableCapacity()) + "T"
                        + " | Updated: " + value.getAvailabilityUpdatedAtText());
            }
            if (isSelected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }
            return label;
        });
        customerField = new JTextField(AppContext.getInstance().getCurrentUser() != null
                ? AppContext.getInstance().getCurrentUser().getName() : "");
        weightField = new JTextField("1.0");
        costPreview = new JLabel("Estimated Cost: Rs. 0.00");
        costPreview.setFont(UIConstants.FONT_BODY);
        costPreview.setForeground(UIConstants.ACCENT);
        availabilityPreview = new JLabel("Availability Updated: -");
        availabilityPreview.setFont(UIConstants.FONT_SMALL);
        availabilityPreview.setForeground(UIConstants.TEXT_MUTED);

        weightField.getDocument().addDocumentListener(new SimpleDocListener(this::updateCostPreview));
        truckCombo.addActionListener(e -> updateCostPreview());

        int y = 0;
        y = addField(form, gbc, y, "Select Truck", truckCombo);
        y = addField(form, gbc, y, "Customer Name", customerField);
        y = addField(form, gbc, y, "Cargo Weight (Tons)", weightField);

        gbc.gridy = y++;
        form.add(costPreview, gbc);

        gbc.gridy = y++;
        form.add(availabilityPreview, gbc);

        JButton bookBtn = new JButton("Confirm Booking");
        bookBtn.setFont(UIConstants.FONT_BUTTON);
        UIConstants.stylePrimaryButton(bookBtn);
        bookBtn.addActionListener(e -> doBooking());
        gbc.gridy = y++;
        gbc.insets = new Insets(20, 0, 6, 0);
        form.add(bookBtn, gbc);

        return form;
    }

    private int addField(JPanel form, GridBagConstraints gbc, int y, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_SMALL);
        gbc.gridy = y++;
        form.add(lbl, gbc);
        gbc.gridy = y++;
        form.add(field, gbc);
        return y;
    }

    private void updateCostPreview() {
        try {
            Truck truck = (Truck) truckCombo.getSelectedItem();
            double weight = Double.parseDouble(weightField.getText().trim());
            if (truck == null) {
                costPreview.setText("Estimated Cost: Rs. 0.00");
                availabilityPreview.setText("Availability Updated: -");
                return;
            }
            double cost = weight * 1000.0 * truck.getCostPerKg();
            String warn = weight > truck.getAvailableCapacity() ? "  ⚠ exceeds available capacity" : "";
            costPreview.setText(String.format("Estimated Cost: Rs. %.2f%s", cost, warn));
            costPreview.setForeground(weight > truck.getAvailableCapacity() ? UIConstants.DANGER : UIConstants.ACCENT);
            availabilityPreview.setText("Availability Updated: " + truck.getAvailabilityUpdatedAtText());
        } catch (NumberFormatException ex) {
            costPreview.setText("Estimated Cost: Rs. -");
            availabilityPreview.setText("Availability Updated: -");
        }
    }

    private void doBooking() {
        Truck truck = (Truck) truckCombo.getSelectedItem();
        String customer = customerField.getText().trim();
        if (truck == null) {
            JOptionPane.showMessageDialog(this, "No trucks available. Register a truck first.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (customer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter customer name.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double weight = Double.parseDouble(weightField.getText().trim());
            Booking booking = AppContext.getInstance().getBookingService()
                    .bookCargo(truck, null, customer, weight);

            if (booking == null) {
                JOptionPane.showMessageDialog(this,
                        "Booking Rejected: Not enough available capacity on this truck.\nAvailable: "
                        + truck.getAvailableCapacity() + " T\nUpdated: " + truck.getAvailabilityUpdatedAtText(),
                        "Capacity Check Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, "Booking Confirmed!\nBooking ID: " + booking.getBookingId()
                    + "\nTotal Cost: Rs. " + String.format("%.2f", booking.getTotalCost())
                    + "\nAvailability Updated: " + truck.getAvailabilityUpdatedAtText(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            refresh();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Weight must be numeric.", "Validation", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void refresh() {
        Truck selected = (Truck) truckCombo.getSelectedItem();
        DefaultComboBoxModel<Truck> model = new DefaultComboBoxModel<>();
        for (Truck t : AppContext.getInstance().getTruckService().getAvailableTrucks()) {
            model.addElement(t);
        }
        truckCombo.setModel(model);
        if (selected != null && selected.getAvailableCapacity() > 0) {
            truckCombo.setSelectedItem(selected);
        } else if (model.getSize() > 0) {
            truckCombo.setSelectedIndex(0);
        } else {
            truckCombo.setSelectedItem(null);
        }

        tableModel.setRowCount(0);
        for (Booking b : AppContext.getInstance().getBookingService().getBookingHistory()) {
            tableModel.addRow(new Object[]{b.getBookingId(), b.getTruckNumber(), b.getCustomerName(),
                    b.getRoute(), b.getBookedWeight(), String.format("%.2f", b.getTotalCost())});
        }
        updateCostPreview();
    }

    private static class SimpleDocListener implements javax.swing.event.DocumentListener {
        private final Runnable action;
        SimpleDocListener(Runnable action) { this.action = action; }
        public void insertUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
    }
}