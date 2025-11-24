/*
 * This is the page where we will have buttons to direct member
 * to different functions of the application
 */
package frontend;

import backend.BackendService;
import backend.Media;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MemberHomePage extends JFrame {
    private static final Color GOLD = new Color(255, 215, 50);
    private static final Color BLUE = new Color(66, 133, 244);

    private JPanel centerPanel;
    private JPanel formWrapper;

    public MemberHomePage(String username) {

        // create window
        setTitle("ACED Streaming - Member Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // background + border
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(24, 24, 24, 24));
        content.setBackground(GOLD);

        // TOP
        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        northContainer.setOpaque(false);

        //Return button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topPanel.setOpaque(false);

        JButton returnButton = new JButton("Return");
        returnButton.setFocusPainted(false);
        returnButton.setBackground(BLUE);
        returnButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        returnButton.addActionListener(e -> {
            centerPanel.removeAll();
            centerPanel.add(formWrapper, BorderLayout.CENTER);
            centerPanel.revalidate();
            centerPanel.repaint();
        });
        topPanel.add(returnButton);

        northContainer.add(topPanel);

        // header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel logo = loadLogo();
        if (logo != null) {
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);
            header.add(logo);
            header.add(Box.createVerticalStrut(10));
        }

        JLabel welcome = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcome.setForeground(Color.DARK_GRAY);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(welcome);
        header.add(Box.createVerticalStrut(18));

        northContainer.add(header);
        content.add(northContainer, BorderLayout.NORTH);

        // CENTER

        // menu buttons
        JPanel options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
        options.setOpaque(false);

        options.add(createPrimaryButton("Search Movies", () -> searchPage(username)));
        options.add(Box.createVerticalStrut(12));

        // options.add(createPrimaryButton("View Account", () -> viewAccount(username)));
        // options.add(Box.createVerticalStrut(12));

        options.add(createPrimaryButton("View History", () -> viewHistory(username)));
        options.add(Box.createVerticalStrut(12));

        options.add(createPrimaryButton("Logout", this::logout));

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(options);

        formWrapper = centerWrapper;

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(formWrapper, BorderLayout.CENTER);

        content.add(centerPanel, BorderLayout.CENTER);

        setContentPane(content);
    }



    // creates the main buttons and wires up actions
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

    // loads the logo
    private JLabel loadLogo() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/ACEDLogo.png"));
        if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
            return null;
        }
        int targetWidth = 150;
        int targetHeight = (int) ((double) icon.getIconHeight() / icon.getIconWidth() * targetWidth);
        Image scaled = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(scaled));
    }

    private void searchPage(String username) {
        dispose();
        new searchFrame(username).setVisible(true);
    }

    private void viewAccount(String username) {
        dispose();
        new viewAccountFrame(username).setVisible(true);
    }

    // opens new login window when logout is clicked
    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }

    private void viewHistory(String username) {
        try {
            List<Media> results = BackendService.getWatchHistoryByUser(username);

            if (results == null || results.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No watch history found for '" + username + "'.");
                return;
            }

            String[] columns = {"Title", "Genre", "Release Date"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);

            for (Media m : results) {
                model.addRow(new Object[]{
                        m.getTitle(),
                        m.getGenre(),
                        m.getReleaseDate()
                });
            }

            JTable table = new JTable(model);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            table.setFillsViewportHeight(true);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            showTableInCenter(scrollPane);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error while loading watch history:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTableInCenter(JScrollPane scrollPane) {
        centerPanel.removeAll();
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }
}
