package ui;

import service.AppContext;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;

public class CustomerDashboard extends JFrame {

    public CustomerDashboard() {
        setTitle("LoadLink Customer Dashboard - " + AppContext.getInstance().getCurrentUser());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIConstants.BG);
        setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.SIDEBAR);
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("Customer Dashboard");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton logout = new JButton("⎋ Logout");
        logout.setFont(UIConstants.FONT_BUTTON);
        UIConstants.styleDangerButton(logout);
        logout.addActionListener(e -> {
            AppContext.getInstance().setCurrentUser(null);
            new LoginScreen().setVisible(true);
            dispose();
        });
        header.add(logout, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        BookCargoPanel bookCargoPanel = new BookCargoPanel(null);
        root.add(bookCargoPanel, BorderLayout.CENTER);
        bookCargoPanel.refresh();
    }
}