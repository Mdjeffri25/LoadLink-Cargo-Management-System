package util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;

/** Shared color/font constants for a consistent, modern Swing look. */
public class UIConstants {
    public static final Color PRIMARY = new Color(0x1565C0);
    public static final Color PRIMARY_DARK = new Color(0x0D47A1);
    public static final Color ACCENT = new Color(0x2E7D32);
    public static final Color DANGER = new Color(0xC62828);
    public static final Color BG = new Color(0xF4F6F8);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color SIDEBAR = new Color(0x0F2C4C);
    public static final Color SIDEBAR_HOVER = new Color(0x1B3F66);
    public static final Color TEXT_DARK = new Color(0x1A1A1A);
    public static final Color TEXT_MUTED = new Color(0x6B7280);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    public static void stylePrimaryButton(JButton button) {
        styleButton(button, PRIMARY, Color.WHITE);
    }

    public static void styleDangerButton(JButton button) {
        styleButton(button, DANGER, Color.WHITE);
    }

    public static void styleSidebarButton(JButton button) {
        styleButton(button, SIDEBAR_HOVER, Color.WHITE);
    }

    private static void styleButton(JButton button, Color background, Color foreground) {
        button.setUI(new BasicButtonUI());
        button.setBackground(background);
        button.setForeground(foreground);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(background.darker(), 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
    }
}
