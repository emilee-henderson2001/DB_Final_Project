/*
 * This is the page where we will have buttons to direct Admin to different functions of the application 
 */
package frontend;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminHomePage extends JFrame {
    private static final Color GOLD = new Color(255, 215, 50);
    private static final Color BLUE = new Color(66, 133, 244);

    public AdminHomePage(String username) {

        // create window
        setTitle("ACED Streaming - Admin Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // add background and border
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(24, 24, 24, 24));
        content.setBackground(GOLD);

        // add header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        // add logo 
        JLabel logo = loadLogo();
        if (logo != null) {
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);
            header.add(logo);
            header.add(Box.createVerticalStrut(10));
        }

        // send welcome message 
        JLabel welcome = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcome.setForeground(Color.DARK_GRAY);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(welcome);
        header.add(Box.createVerticalStrut(18));

        // add buttons 
        JPanel options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
        options.setOpaque(false);

        options.add(createPrimaryButton("Browse Users Streaming", () -> viewMemberStreaming(username)));
        options.add(Box.createVerticalStrut(12));

        options.add(createPrimaryButton("View Analytics", () -> viewAnalytics(username)));
        options.add(Box.createVerticalStrut(12));


        options.add(createPrimaryButton("Logout", this::logout));

        content.add(header, BorderLayout.NORTH);

        // puts buttons into the middle of the screen
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(options);
        content.add(centerWrapper, BorderLayout.CENTER);

        setContentPane(content);
    }

    // creates  the buttons and allows action listeners when clicked
    private JButton createPrimaryButton(String label, Runnable action) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(BLUE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        button.addActionListener(event -> action.run());
        return button;
    }

    // loads the picture
    private JLabel loadLogo() {
        ImageIcon icon = new ImageIcon("ACEDLogo.png");
        if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
            return null;
        }
        int targetWidth = 150;
        int targetHeight = (int) ((double) icon.getIconHeight() / icon.getIconWidth() * targetWidth);
        Image scaled = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(scaled));
    }

    // opens new login window when logout is clicked 
    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }

    private void viewAnalytics(String username){
        dispose();
        new AdminAnalyticsPage(username).setVisible(true);
    }

    private void viewMemberStreaming(String username){
        dispose();
        new AdminMemberStreaming(username).setVisible(true);
    }
}
