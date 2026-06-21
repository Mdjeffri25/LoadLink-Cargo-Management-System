package ui;

import model.User;
import service.AppContext;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Login screen with separate login boxes for Admin, Owner, and Customer.
 */
public class LoginScreen extends JFrame {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LoginScreen() {
        setTitle("LoadLink - Smart Cargo Space Optimization Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIConstants.PRIMARY_DARK);
        setContentPane(root);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(36, 20, 14, 20));

        JLabel title = new JLabel("LOADLINK");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Smart Cargo Space Optimization & Load Sharing");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(new Color(0xBBDEFB));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);
        root.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(20, 12));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(0, 30, 26, 30));

        JLabel nowLabel = new JLabel("Current Date/Time: " + LocalDateTime.now().format(DATE_TIME_FORMATTER));
        nowLabel.setFont(UIConstants.FONT_SMALL);
        nowLabel.setForeground(new Color(0xD6E4F0));
        nowLabel.setHorizontalAlignment(SwingConstants.CENTER);
        content.add(nowLabel, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 18, 0));
        cards.setOpaque(false);
        cards.add(buildLoginCard("Admin Login", User.Role.ADMIN, UIConstants.PRIMARY_DARK, "Admin as it is"));
        cards.add(buildLoginCard("Owner Login", User.Role.OWNER, UIConstants.ACCENT, "Add truck only"));
        cards.add(buildLoginCard("Customer Login", User.Role.CUSTOMER, UIConstants.PRIMARY, "Book cargo only"));
        content.add(cards, BorderLayout.CENTER);

        JLabel hint = new JLabel("<html><center>Sample logins: 9000000001 (Admin), 9000000002 (Owner), 9000000004 (Customer)</center></html>");
        hint.setFont(UIConstants.FONT_SMALL);
        hint.setForeground(UIConstants.TEXT_MUTED);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        content.add(hint, BorderLayout.SOUTH);

        root.add(content, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildLoginCard(String titleText, User.Role role, Color accent, String noteText) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent.darker()),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 0, 7, 0);
        gbc.weightx = 1.0;

        JLabel title = new JLabel(titleText);
        title.setFont(UIConstants.FONT_HEADING);
        title.setForeground(UIConstants.TEXT_DARK);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField nameField = new JTextField();
        nameField.setFont(UIConstants.FONT_BODY);
        nameField.setPreferredSize(new Dimension(250, 34));

        JTextField phoneField = new JTextField();
        phoneField.setFont(UIConstants.FONT_BODY);
        phoneField.setPreferredSize(new Dimension(250, 34));

        JLabel timeLabel = new JLabel("Date/Time: " + LocalDateTime.now().format(DATE_TIME_FORMATTER));
        timeLabel.setFont(UIConstants.FONT_SMALL);
        timeLabel.setForeground(UIConstants.TEXT_MUTED);

        JLabel note = new JLabel(noteText);
        note.setFont(UIConstants.FONT_SMALL);
        note.setForeground(accent.darker());
        note.setHorizontalAlignment(SwingConstants.CENTER);

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(UIConstants.FONT_BUTTON);
        UIConstants.stylePrimaryButton(loginBtn);
        loginBtn.setBackground(accent);
        loginBtn.addActionListener(e -> doLogin(nameField.getText().trim(), phoneField.getText().trim(), role));

        gbc.gridy = 0; card.add(title, gbc);
        gbc.gridy = 1; card.add(new JLabel("Full Name"), gbc);
        gbc.gridy = 2; card.add(nameField, gbc);
        gbc.gridy = 3; card.add(new JLabel("Phone Number"), gbc);
        gbc.gridy = 4; card.add(phoneField, gbc);
        gbc.gridy = 5; card.add(timeLabel, gbc);
        gbc.gridy = 6; card.add(note, gbc);
        gbc.gridy = 7; gbc.insets = new Insets(14, 0, 0, 0); card.add(loginBtn, gbc);

        return card;
    }

    private void doLogin(String name, String phone, User.Role role) {
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a phone number.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = AppContext.getInstance().getUserService().findByPhone(phone);
        if (user == null) {
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "New phone number detected. Please enter your name to register.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            user = AppContext.getInstance().getUserService().register(name, phone, role);
        }

        AppContext.getInstance().setCurrentUser(user);
        if (user.getRole() == User.Role.ADMIN) {
            new MainDashboard().setVisible(true);
        } else if (user.getRole() == User.Role.OWNER) {
            new OwnerDashboard().setVisible(true);
        } else {
            new CustomerDashboard().setVisible(true);
        }
        dispose();
    }
}
