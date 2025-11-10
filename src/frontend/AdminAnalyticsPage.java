package frontend;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import backend.*;

public class AdminAnalyticsPage extends JFrame {
    private static final Color GOLD = new Color(255, 215, 50);
    private static final Color BLUE = new Color(66, 133, 244);

    public AdminAnalyticsPage(String username) {

        // create window
        setTitle("ACED Streaming - Admin Analytics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // main content
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(24, 24, 24, 24));
        content.setBackground(GOLD);


        // back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topPanel.setOpaque(false);

        JButton backButton = new JButton("←");
        backButton.setFocusPainted(false);
        backButton.setBackground(BLUE);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        backButton.addActionListener(e -> {
            dispose();
            new AdminHomePage(username).setVisible(true);
        });

        topPanel.add(backButton);

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

        // Combine back button + header
        JPanel northWrapper = new JPanel();
        northWrapper.setLayout(new BoxLayout(northWrapper, BoxLayout.Y_AXIS));
        northWrapper.setOpaque(false);
        northWrapper.add(topPanel);
        northWrapper.add(header);

        content.add(northWrapper, BorderLayout.NORTH);


        JPanel options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
        options.setOpaque(false);

        options.add(createPrimaryButton("See top ten media of the month", this::viewMediaOfMonth));
        options.add(Box.createVerticalStrut(12));

        options.add(createPrimaryButton("See streaming trend for last 24 hours", this::viewTrend));
        options.add(Box.createVerticalStrut(12));

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(options);

        content.add(centerWrapper, BorderLayout.CENTER);

        setContentPane(content);
    }

    // creates buttons cleanly
    private JButton createPrimaryButton(String label, Runnable action) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(BLUE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        button.addActionListener(e -> action.run());
        return button;
    }

    // loads scaled logo
    private JLabel loadLogo() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/ACEDLogo.png"));
        if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) return null;

        int targetWidth = 150;
        int targetHeight = (int)((double)icon.getIconHeight() / icon.getIconWidth() * targetWidth);
        Image scaled = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(scaled));
    }

    private void viewMediaOfMonth(){
        //runs query to show top streamed of the month
        JOptionPane.showMessageDialog(this, "Showing top ten media of the month");
        //call backend to get top 10
        try{
            java.util.List<Media> results = BackendService.getTop10PopularMedia();

            //Build readable string
            StringBuilder message = new StringBuilder("Results:\n\n");
            for (Media m : results) {
                message.append("• ")
                        .append(m.getTitle())
                        .append(" (").append(m.getGenre())
                        .append(", ").append(m.getReleaseDate())
                        .append(")\n");
            }
            JOptionPane.showMessageDialog(this, message.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while searching:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewTrend(){
        //runs query to show 24 hour trend
        JOptionPane.showMessageDialog(this, "Showing 24 hour trend");
    }
}
