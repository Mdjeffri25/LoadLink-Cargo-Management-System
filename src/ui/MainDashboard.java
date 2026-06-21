package ui;

import model.User;
import service.AppContext;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window. Uses a sidebar for navigation and a
 * CardLayout content area on the right so screens swap without
 * re-opening windows.
 */
public class MainDashboard extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private java.util.Map<String, Component> cards = new java.util.HashMap<>();

    public MainDashboard() {
        setTitle("LoadLink Dashboard - " + AppContext.getInstance().getCurrentUser());
        setSize(1100, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIConstants.BG);

        addCard(new HomePanel(this), "HOME");
        addCard(new AddTruckPanel(this), "ADD_TRUCK");
        addCard(new SearchRoutePanel(this), "SEARCH_ROUTE");
        addCard(new BookCargoPanel(this), "BOOK_CARGO");
        addCard(new ReturnTripPanel(this), "RETURN_TRIP");
        addCard(new AnalyticsPanel(this), "ANALYTICS");
        addCard(new ReportsPanel(this), "REPORTS");

        add(contentPanel, BorderLayout.CENTER);

        showDefaultCard();
    }

    private void addCard(Component comp, String name) {
        contentPanel.add(comp, name);
        cards.put(name, comp);
    }

    public void showCard(String name) {
        if (!canAccessCard(name)) {
            name = getDefaultCard();
        }
        cardLayout.show(contentPanel, name);
        Component current = cards.get(name);
        if (current instanceof Refreshable) {
            ((Refreshable) current).refresh();
        }
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(UIConstants.SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel logo = new JLabel("  LOADLINK");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 10, 25, 0));
        sidebar.add(logo);

        JLabel userLbl = new JLabel("  " + AppContext.getInstance().getCurrentUser());
        userLbl.setFont(UIConstants.FONT_SMALL);
        userLbl.setForeground(new Color(0xB0BEC5));
        userLbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 0));
        sidebar.add(userLbl);

        sidebar.add(navButton("Dashboard", "HOME"));

        User.Role role = getCurrentRole();
        if (role == User.Role.ADMIN) {
            sidebar.add(navButton("Add Truck", "ADD_TRUCK"));
            sidebar.add(navButton("Search Route", "SEARCH_ROUTE"));
            sidebar.add(navButton("Book Cargo", "BOOK_CARGO"));
            sidebar.add(navButton("Return Trip", "RETURN_TRIP"));
            sidebar.add(navButton("Analytics", "ANALYTICS"));
            sidebar.add(navButton("Reports", "REPORTS"));
        } else if (role == User.Role.OWNER) {
            sidebar.add(navButton("Add Truck", "ADD_TRUCK"));
        } else if (role == User.Role.CUSTOMER) {
            sidebar.add(navButton("Book Cargo", "BOOK_CARGO"));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton logout = new JButton("⎋  Logout");
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setMaximumSize(new Dimension(190, 34));
        logout.setPreferredSize(new Dimension(190, 34));
        logout.setMinimumSize(new Dimension(190, 34));
        UIConstants.styleDangerButton(logout);
        logout.setFont(UIConstants.FONT_BUTTON);
        logout.addActionListener(e -> {
            AppContext.getInstance().setCurrentUser(null);
            new LoginScreen().setVisible(true);
            dispose();
        });
        JPanel logoutWrap = new JPanel(new BorderLayout());
        logoutWrap.setOpaque(false);
        logoutWrap.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        logoutWrap.setMaximumSize(new Dimension(210, 50));
        logoutWrap.setPreferredSize(new Dimension(210, 50));
        logoutWrap.add(logout, BorderLayout.CENTER);
        sidebar.add(logoutWrap);

        return sidebar;
    }

    private JButton navButton(String label, String cardName) {
        JButton btn = new JButton(label);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(230, 44));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(UIConstants.FONT_BODY);
        UIConstants.styleSidebarButton(btn);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
        btn.addActionListener(e -> showCard(cardName));
        btn.addChangeListener(e -> {
            if (btn.getModel().isRollover()) {
                btn.setBackground(UIConstants.SIDEBAR_HOVER);
            } else {
                btn.setBackground(UIConstants.SIDEBAR);
            }
        });
        return btn;
    }

    private User.Role getCurrentRole() {
        User currentUser = AppContext.getInstance().getCurrentUser();
        return currentUser == null ? User.Role.CUSTOMER : currentUser.getRole();
    }

    private boolean canAccessCard(String name) {
        User.Role role = getCurrentRole();
        if (role == User.Role.ADMIN) {
            return true;
        }
        if (role == User.Role.OWNER) {
            return "HOME".equals(name) || "ADD_TRUCK".equals(name);
        }
        return "HOME".equals(name) || "BOOK_CARGO".equals(name);
    }

    private String getDefaultCard() {
        User.Role role = getCurrentRole();
        if (role == User.Role.OWNER) {
            return "ADD_TRUCK";
        }
        if (role == User.Role.CUSTOMER) {
            return "BOOK_CARGO";
        }
        return "HOME";
    }

    private void showDefaultCard() {
        showCard(getDefaultCard());
    }

    /** Implemented by panels that need to refresh their data each time they're shown. */
    public interface Refreshable {
        void refresh();
    }
}
