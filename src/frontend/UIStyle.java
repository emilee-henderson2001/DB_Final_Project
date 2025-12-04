package frontend;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

/**
 * Shared UI styling helpers so buttons and fonts stay consistent across screens.
 */
public final class UIStyle {
    public static final Color GOLD = new Color(255, 215, 50);
    public static final Color BLUE = new Color(66, 133, 244);

    public static final Font BASE_FONT = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font HEADING_FONT = new Font("SansSerif", Font.BOLD, 20);

    private UIStyle() {
    }

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button);
        return button;
    }

    public static void styleButton(JButton button) {
        styleButton(button, new Insets(10, 18, 10, 18));
    }

    public static void styleButton(JButton button, Insets padding) {
        button.setBackground(BLUE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(BASE_FONT);
        int top = padding != null ? padding.top : 10;
        int left = padding != null ? padding.left : 18;
        int bottom = padding != null ? padding.bottom : 10;
        int right = padding != null ? padding.right : 18;
        button.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    }
}
