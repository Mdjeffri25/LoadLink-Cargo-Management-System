package ui;

import model.Truck;
import service.AppContext;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddTruckPanel extends JPanel implements MainDashboard.Refreshable {

    private JTextField numberField, ownerField, routeField, capacityField, usedField, costField;
    private JLabel availablePreview;
    private DefaultTableModel tableModel;
    private JTable table;
    private final boolean isAdmin;
    private final String currentOwnerName;

    public AddTruckPanel(MainDashboard parent) {
        currentOwnerName = AppContext.getInstance().getCurrentUser() != null
                ? AppContext.getInstance().getCurrentUser().getName() : "";
        isAdmin = AppContext.getInstance().getCurrentUser() != null
                && AppContext.getInstance().getCurrentUser().getRole() == model.User.Role.ADMIN;

        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Truck Registration");
        title.setFont(UIConstants.FONT_TITLE);
        add(title, BorderLayout.NORTH);

        JPanel form = buildForm();
        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(BorderFactory.createEmptyBorder());
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        formScroll.getViewport().setBackground(UIConstants.BG);
        formScroll.setPreferredSize(new Dimension(380, 0));
        add(formScroll, BorderLayout.WEST);

        if (isAdmin) {
            tableModel = new DefaultTableModel(new Object[]{"ID", "Number", "Owner", "Route",
                    "Total(T)", "Used(T)", "Available(T)", "Avail Updated", "Util %", "Rs/Kg"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(tableModel);
            table.setRowHeight(26);
            table.setFont(UIConstants.FONT_BODY);
            table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scroll = new JScrollPane(table);
            scroll.setBorder(BorderFactory.createTitledBorder("Registered Trucks"));

            JPanel centerWrap = new JPanel(new BorderLayout());
            centerWrap.setOpaque(false);
            centerWrap.add(scroll, BorderLayout.CENTER);

            JPanel deleteBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            deleteBar.setOpaque(false);
            JButton deleteBtn = new JButton("Delete Selected Truck");
            deleteBtn.setFont(UIConstants.FONT_BUTTON);
            UIConstants.styleDangerButton(deleteBtn);
            deleteBtn.addActionListener(e -> deleteSelectedTruck());
            JLabel adminNote = new JLabel("Admin only");
            adminNote.setFont(UIConstants.FONT_SMALL);
            adminNote.setForeground(UIConstants.TEXT_MUTED);
            deleteBar.add(deleteBtn);
            deleteBar.add(adminNote);
            centerWrap.add(deleteBar, BorderLayout.SOUTH);

            add(centerWrap, BorderLayout.CENTER);
        } else {
            tableModel = new DefaultTableModel(new Object[]{"Number", "Route",
                    "Total(T)", "Used(T)", "Available(T)", "Avail Updated", "Util %", "Rs/Kg"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(tableModel);
            table.setRowHeight(26);
            table.setFont(UIConstants.FONT_BODY);
            JScrollPane scroll = new JScrollPane(table);
            scroll.setBorder(BorderFactory.createTitledBorder("Your Registered Trucks"));

            JPanel centerWrap = new JPanel(new BorderLayout());
            centerWrap.setOpaque(false);
            centerWrap.add(scroll, BorderLayout.CENTER);

            JLabel note = new JLabel("Only your own registered trucks are shown here.");
            note.setFont(UIConstants.FONT_SMALL);
            note.setForeground(UIConstants.TEXT_MUTED);
            note.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 0));
            centerWrap.add(note, BorderLayout.SOUTH);

            add(centerWrap, BorderLayout.CENTER);
        }
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIConstants.CARD_BG);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0E0E0)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        form.setPreferredSize(new Dimension(340, 480));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        numberField = new JTextField("TN-0X-XX-0000");
        ownerField = new JTextField(AppContext.getInstance().getCurrentUser() != null
                ? AppContext.getInstance().getCurrentUser().getName() : "");
        routeField = new JTextField("Chennai, Salem, Bangalore");
        capacityField = new JTextField("10");
        usedField = new JTextField("0");
        costField = new JTextField("5.0");
        availablePreview = new JLabel("Available Capacity: 10.0 T");
        availablePreview.setFont(UIConstants.FONT_BODY);
        availablePreview.setForeground(UIConstants.ACCENT);

        JLabel availabilityTime = new JLabel("Availability Updated: " + java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        availabilityTime.setFont(UIConstants.FONT_SMALL);
        availabilityTime.setForeground(UIConstants.TEXT_MUTED);

        usedField.getDocument().addDocumentListener(new SimpleDocListener(this::updatePreview));
        capacityField.getDocument().addDocumentListener(new SimpleDocListener(this::updatePreview));

        int y = 0;
        y = addField(form, gbc, y, "Truck Number", numberField);
        y = addField(form, gbc, y, "Owner Name", ownerField);
        y = addField(form, gbc, y, "Route (comma separated stops)", routeField);
        y = addField(form, gbc, y, "Total Capacity (Tons)", capacityField);
        y = addField(form, gbc, y, "Used Capacity (Tons)", usedField);
        y = addField(form, gbc, y, "Cost per Kg (Rs.)", costField);

        gbc.gridy = y++;
        form.add(availablePreview, gbc);

        gbc.gridy = y++;
        form.add(availabilityTime, gbc);

        JButton addBtn = new JButton("Register Truck");
        addBtn.setFont(UIConstants.FONT_BUTTON);
        UIConstants.stylePrimaryButton(addBtn);
        addBtn.addActionListener(e -> registerTruck());
        gbc.gridy = y++;
        gbc.insets = new Insets(20, 0, 6, 0);
        form.add(addBtn, gbc);

        return form;
    }

    private int addField(JPanel form, GridBagConstraints gbc, int y, String label, JTextField field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_SMALL);
        gbc.gridy = y++;
        form.add(lbl, gbc);
        gbc.gridy = y++;
        field.setFont(UIConstants.FONT_BODY);
        form.add(field, gbc);
        return y;
    }

    private void updatePreview() {
        try {
            double total = Double.parseDouble(capacityField.getText().trim());
            double used = Double.parseDouble(usedField.getText().trim());
            availablePreview.setText(String.format("Available Capacity: %.1f T", total - used));
        } catch (NumberFormatException ex) {
            availablePreview.setText("Available Capacity: -");
        }
    }

    private void registerTruck() {
        try {
            String number = numberField.getText().trim();
            String owner = ownerField.getText().trim();
            List<String> route = new ArrayList<>();
            for (String stop : routeField.getText().split(",")) {
                if (!stop.trim().isEmpty()) route.add(stop.trim());
            }
            double total = Double.parseDouble(capacityField.getText().trim());
            double used = Double.parseDouble(usedField.getText().trim());
            double cost = Double.parseDouble(costField.getText().trim());

            if (number.isEmpty() || owner.isEmpty() || route.size() < 2) {
                JOptionPane.showMessageDialog(this, "Please fill all fields. Route needs at least 2 stops.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (used > total) {
                JOptionPane.showMessageDialog(this, "Used capacity cannot exceed total capacity.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Truck truck = AppContext.getInstance().getTruckService()
                    .registerTruck(number, owner, route, total, used, cost);

            JOptionPane.showMessageDialog(this, "Truck registered successfully!\nTruck ID: " + truck.getTruckId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            refresh();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Capacity / cost fields must be numeric.",
                    "Validation", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedTruck() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a truck to delete.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String truckId = String.valueOf(tableModel.getValueAt(selectedRow, 0));
        String truckNumber = String.valueOf(tableModel.getValueAt(selectedRow, 1));
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete truck " + truckNumber + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean removed = AppContext.getInstance().getTruckService().removeTruck(truckId);
        if (!removed) {
            JOptionPane.showMessageDialog(this, "Truck could not be deleted.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        refresh();
        JOptionPane.showMessageDialog(this, "Truck deleted successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void refresh() {
        if (tableModel == null) {
            return;
        }
        tableModel.setRowCount(0);
        for (Truck t : AppContext.getInstance().getTruckService().getAllTrucks()) {
            if (!isAdmin && !t.getOwnerName().equalsIgnoreCase(currentOwnerName)) {
                continue;
            }
            if (isAdmin) {
                tableModel.addRow(new Object[]{
                        t.getTruckId(), t.getTruckNumber(), t.getOwnerName(), t.getRouteString(),
                        t.getTotalCapacity(), t.getUsedCapacity(), t.getAvailableCapacity(),
                        t.getAvailabilityUpdatedAtText(), String.format("%.1f", t.getUtilizationPercent()),
                        t.getCostPerKg()
                });
            } else {
                tableModel.addRow(new Object[]{
                        t.getTruckNumber(), t.getRouteString(),
                        t.getTotalCapacity(), t.getUsedCapacity(), t.getAvailableCapacity(),
                        t.getAvailabilityUpdatedAtText(), String.format("%.1f", t.getUtilizationPercent()),
                        t.getCostPerKg()
                });
            }
        }
    }

    /** Minimal DocumentListener adapter to avoid repeating boilerplate. */
    private static class SimpleDocListener implements javax.swing.event.DocumentListener {
        private final Runnable action;
        SimpleDocListener(Runnable action) { this.action = action; }
        public void insertUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
    }
}