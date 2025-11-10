package frontend;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import backend.*;
public class AdminMemberStreaming extends JFrame{
    private final JTextField searchField = new JTextField();

    public AdminMemberStreaming(String username) {

        // Create pop-up window
        setTitle("ACED Streaming - Admin Member Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Make the box for search
        Font smallFont = new Font("SansSerif", Font.PLAIN, 13);
        Insets smallMargin = new Insets(1, 6, 1, 6);
        Dimension compactSize = new Dimension(160, 24);

        searchField.setColumns(12);
        searchField.setFont(smallFont);
        searchField.setMargin(smallMargin);
        searchField.setPreferredSize(compactSize);
        searchField.setMaximumSize(compactSize);

        // create panel to add content
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(24, 24, 24, 24));
        content.setBackground(new Color(255, 215, 50));   // gold background

        // Back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topPanel.setOpaque(false);
        JButton backButton = new JButton("Back");
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(66, 133, 244));
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        backButton.addActionListener(e -> {
            dispose();
            new AdminHomePage(username).setVisible(true);
        });
        topPanel.add(backButton);
        content.add(topPanel, BorderLayout.NORTH);

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

        form.add(buildField("Search Movie", searchField));
        form.add(Box.createVerticalStrut(12));


        // Search button
        JButton search = new JButton("Search");
        search.setAlignmentX(Component.CENTER_ALIGNMENT);
        search.setBackground(new Color(66, 133, 244));
        search.setForeground(Color.BLACK);
        search.setFocusPainted(false);
        search.addActionListener(event -> attemptSearch());

        // allow pressing enter to trigger login action listener
        getRootPane().setDefaultButton(search);

        form.add(search);


        // add form and center it
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(form);

        content.add(wrapper, BorderLayout.CENTER);
        setContentPane(content);
    }

    // method to add photo as label
    private JLabel buildLogoLabel() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/ACEDLogo.png"));
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

    // method to search
    private void attemptSearch() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter something to search.");
            return;
        }
        else{
            JOptionPane.showMessageDialog(this,
                    "Searching for all members who have streamed: " + searchText);
            //call backend to get results
            try{
                java.util.List<Member> results = BackendService.getWatchHistoryByMedia(searchText);

                if(results == null || results.isEmpty()){
                    JOptionPane.showMessageDialog(this, "No results found ''" + searchText + "'.");
                    return;
                }
                // Build a readable String for results
                StringBuilder message = new StringBuilder("Results for " + searchText + ":\n\n");
                for (Member m : results) {
                    message.append("• ")
                            .append(m.getMemberID())
                            .append(" (").append(m.getMemberName())
                            .append(")\n");
                }
                JOptionPane.showMessageDialog(this, message.toString(),
                        "Search Results", JOptionPane.INFORMATION_MESSAGE);

            }
            catch(Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error while searching:\n" + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }



    }
}
