import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JLabel statusLabel = new JLabel(" ");

    public LoginFrame() {

        // Create pop-up window
        setTitle("ACED Streaming - Sign In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        // Make the boxes for username and password smaller
        Font smallFont = new Font("SansSerif", Font.PLAIN, 13);
        Insets smallMargin = new Insets(1, 6, 1, 6);
        Dimension compactSize = new Dimension(160, 24);

        usernameField.setColumns(12);
        usernameField.setFont(smallFont);
        usernameField.setMargin(smallMargin);
        usernameField.setPreferredSize(compactSize);
        usernameField.setMaximumSize(compactSize);

        passwordField.setColumns(12);
        passwordField.setFont(smallFont);
        passwordField.setMargin(smallMargin);
        passwordField.setPreferredSize(compactSize);
        passwordField.setMaximumSize(compactSize);

        // create panel to add content
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(24, 24, 24, 24));
        content.setBackground(new Color(255, 215, 50));   // gold background 

        // Form setup
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        // add logo, used label so it would scale
        JLabel logoLabel = buildLogoLabel();
        if (logoLabel != null) {
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            form.add(logoLabel);
            form.add(Box.createVerticalStrut(12));
        }

        form.add(buildField("Username", usernameField));
        form.add(buildField("Password", passwordField));
        form.add(Box.createVerticalStrut(12));

        // Sign in button
        JButton login = new JButton("Sign In");
        login.setAlignmentX(Component.CENTER_ALIGNMENT);
        login.setBackground(new Color(66, 133, 244));
        login.setForeground(Color.BLACK);
        login.setFocusPainted(false);
        login.addActionListener(event -> attemptLogin());

        // allow pressing enter to trigger login action listener
        getRootPane().setDefaultButton(login);

        form.add(login);

        // Status label to tell user what is happening i.e. "login failed"
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(smallFont);
        form.add(Box.createVerticalStrut(8));
        form.add(statusLabel);

        // add form and center it
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(form);

        content.add(wrapper, BorderLayout.CENTER);
        setContentPane(content);
    }

    // method to add photo as label
    private JLabel buildLogoLabel() {
        ImageIcon icon = new ImageIcon("ACEDLogo.png");
        if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
            return null;
        }

        int targetWidth = 160;
        int targetHeight = (int) ((double) icon.getIconHeight() / icon.getIconWidth() * targetWidth);
        Image scaled = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(scaled));
    }

    // method to create fields
    private JPanel buildField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(4, 0, 4, 0));
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(jLabel, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    // method to login
    private void attemptLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Enter both username and password.");
            return;
        }

        

        SwingUtilities.invokeLater(() -> {
            dispose(); // close the login frame
            if ("admin".equalsIgnoreCase(user)) {
                new AdminHomePage(user).setVisible(true);
            } else {
                new MemberHomePage(user).setVisible(true);
            }
        });
    }
}
