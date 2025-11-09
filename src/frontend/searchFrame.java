/*
 * This is the page where we will implement the search functionality
 */
package frontend;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import backend.*;


public class searchFrame extends JFrame {
    private final JTextField searchField = new JTextField();
    private final JComboBox<String> filterBox = new JComboBox<>(new String[]{
            "All",
            "Actor",
            "Director",
            "Genre",
            "Sequel(s)"
    });


    public searchFrame(String username) {

        // Create pop-up window
        setTitle("ACED Streaming - Movie Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 550);
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
        JButton backButton = new JButton("←");
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(66, 133, 244));
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        backButton.addActionListener(e -> {
            dispose();
            new MemberHomePage(username).setVisible(true);
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

        form.add(buildField("Search", searchField));
        form.add(Box.createVerticalStrut(12));
        form.add(buildField("Filter By", filterBox));
        form.add(Box.createVerticalStrut(12));


        //Button to show award-winning movies
        JButton awardButton = new JButton("See Award Winning Movies!");
        awardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        awardButton.setBackground(new Color(66, 133, 244));
        awardButton.setForeground(Color.BLACK);
        awardButton.setFocusPainted(false);
        awardButton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        awardButton.addActionListener(e -> showAwardMovies());
        form.add(awardButton);
        form.add(Box.createVerticalStrut(12));

        //Button to show series member hasn't watched
        JButton newSeriesButton = new JButton("Discover new series!");
        newSeriesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newSeriesButton.setBackground(new Color(66, 133, 244));
        newSeriesButton.setForeground(Color.BLACK);
        newSeriesButton.setFocusPainted(false);
        newSeriesButton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        newSeriesButton.addActionListener(e -> showNewSeries());
        form.add(newSeriesButton);
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

    // method to search
    private void attemptSearch() {
        String searchText = searchField.getText().trim();
        String selectedFilter = (String) filterBox.getSelectedItem();

        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter something to search.");
            return;
        }
        else{
            // Call backend to get results from Railway
            try {
                java.util.List<Media> results = BackendService.searchMedia(searchText, selectedFilter);

                if (results == null || results.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No results found ''" + searchText + "'.");
                    return;
                }

                // Build a readable String for results
                StringBuilder message = new StringBuilder("Results for " + searchText + ":\n\n");
                for (Media m : results) {
                    message.append("• ")
                            .append(m.getTitle())
                            .append(" (").append(m.getGenre())
                            .append(", ").append(m.getReleaseDate())
                            .append(")\n");
                }

                // Display your results message
                JOptionPane.showMessageDialog(this, message.toString(),
                        "Search Results", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error while searching:\n" + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }


        // just to show how the filter works
        JOptionPane.showMessageDialog(this,
                "Searching for: " + searchText + "\nFilter: " + selectedFilter);

    }

    private void showAwardMovies(){
        //runs query to show movies that have won awards
        JOptionPane.showMessageDialog(this, "Showing Award Winning Movies!");
    }

    private void showNewSeries(){
        //runs query to show series member hasn't watched
        JOptionPane.showMessageDialog(this, "Showing new series!");
    }
}
