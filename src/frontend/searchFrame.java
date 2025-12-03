/*
 * This is the page where we will implement the search functionality
 */
package frontend;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

import backend.*;


public class searchFrame extends JFrame {
    private final JTextField searchField = new JTextField();
    private final JComboBox<String> filterBox = new JComboBox<>(new String[]{
            "All",
            "Title",
            "Actor",
            "Director",
            "Genre",
            "Sequel(s)"
    });
    private String username;

    private JPanel centerPanel;
    private JPanel formWrapper;
    private JButton returnButton;


    public searchFrame(String username) {

        this.username = username;

        // Create pop-up window
        setTitle("ACED Streaming - Movie Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setMinimumSize(new Dimension(900, 620));
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
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JButton backButton = new JButton("Back");
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(66, 133, 244));
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        backButton.addActionListener(e -> {
            dispose();
            new MemberHomePage(username).setVisible(true);
        });
        topPanel.add(backButton, BorderLayout.WEST);

        // return to search button, shown after results are rendered
        returnButton = new JButton("Return to Search");
        returnButton.setFocusPainted(false);
        returnButton.setBackground(new Color(66, 133, 244));
        returnButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        returnButton.setVisible(false);
        returnButton.addActionListener(e -> {
            centerPanel.removeAll();
            centerPanel.add(formWrapper, BorderLayout.CENTER);
            centerPanel.revalidate();
            centerPanel.repaint();
            returnButton.setVisible(false);
        });
        topPanel.add(returnButton, BorderLayout.EAST);
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

        formWrapper = wrapper;

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(formWrapper, BorderLayout.CENTER);

        content.add(centerPanel, BorderLayout.CENTER);
        setContentPane(content);
    }

    // method to add photo as label
    private JLabel buildLogoLabel() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/ACEDLogo.png"));
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

                JPanel grid = new JPanel(new GridLayout(0, 3, 12, 12));
                grid.setOpaque(false);

                for (Media m : results) {
                    grid.add(buildPosterCard(m));
                }

                JScrollPane scrollPane = new JScrollPane(grid);
                scrollPane.setBorder(BorderFactory.createEmptyBorder());
                scrollPane.setPreferredSize(new java.awt.Dimension(900, 360));
                scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.getVerticalScrollBar().setUnitIncrement(16);

                showTableInCenter(scrollPane);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error while searching:\n" + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    // See Movies by Rewards
    public void showAwardMovies() {
        try {
            java.util.List<java.util.Map<String, Object>> list = BackendService.getAwardWinningMovies();
            if (list == null || list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No award-winning movies found.");
                return;
            }

            JPanel grid = new JPanel(new GridLayout(0, 3, 12, 12));
            grid.setOpaque(false);

            for (java.util.Map<String, Object> row : list) {
                Media m = new Media(
                        null,
                        (String) row.get("title"),
                        (String) row.get("genre"),
                        (String) row.get("release_date"),
                        null,
                        null,
                        (String) row.get("IMBD_link")
                );
                BackendService.enrichPoster(m);
                grid.add(buildPosterCard(m));
            }

            JScrollPane scrollPane = new JScrollPane(grid);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setPreferredSize(new java.awt.Dimension(900, 360));
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            showTableInCenter(scrollPane);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading award-winning movies:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showNewSeries() {
        try {
            int userId = BackendService.getMemberIdByUsername(username); // 👈 converts username → ID
            if (userId == -1) {
                JOptionPane.showMessageDialog(this, "User not found in database.");
                return;
            }

            List<Map<String, Object>> list = BackendService.getUnwatchedSeriesByUser(userId);
            if (list == null || list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No new series found!");
                return;
            }

            JPanel grid = new JPanel(new GridLayout(0, 3, 12, 12));
            grid.setOpaque(false);

            for (Map<String, Object> row : list) {
                Media m = new Media(
                        null,
                        (String) row.get("title"),
                        (String) row.get("genre"),
                        (String) row.get("release_date"),
                        (Integer) row.get("season"),
                        (Integer) row.get("episode"),
                        (String) row.get("IMBD_link")
                );
                BackendService.enrichPoster(m);
                grid.add(buildPosterCard(m));
            }

            JScrollPane scrollPane = new JScrollPane(grid);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setPreferredSize(new java.awt.Dimension(900, 360));
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            showTableInCenter(scrollPane);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading new series:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }
    private JPanel buildPosterCard(Media media) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JLabel posterLabel = new JLabel(loadPosterIcon(media));
        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        posterLabel.setVerticalAlignment(SwingConstants.CENTER);
        card.add(posterLabel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("<html><div style='text-align:center; width:140px;'>" + media.getTitle() + "</div></html>", SwingConstants.CENTER);
        titleLabel.setForeground(Color.DARK_GRAY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(6, 4, 4, 4));
        card.add(titleLabel, BorderLayout.SOUTH);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showDetailDialog(media);
            }
        });
        return card;
    }

    private ImageIcon loadPosterIcon(Media media) {
        int targetWidth = 140;
        int targetHeight = 210;

        String posterUrl = media.getPosterUrl();
        try {
            if (posterUrl != null && !posterUrl.isBlank()) {
                Image img = ImageIO.read(new URL(posterUrl));
                if (img != null) {
                    return new ImageIcon(scaleImage(img, targetWidth, targetHeight));
                }
            }
        } catch (Exception ignored) {
            // fall through to placeholder
        }

        return new ImageIcon(createPlaceholderPoster(targetWidth, targetHeight, media.getTitle()));
    }

    private void showDetailDialog(Media media) {
        JDialog dialog = new JDialog(this, media.getTitle(), true);
        dialog.setSize(620, 760);
        dialog.setMinimumSize(new Dimension(620, 760));
        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(255, 215, 50));

        JLabel poster = new JLabel(loadPosterIcon(media));
        poster.setHorizontalAlignment(SwingConstants.CENTER);
        root.add(poster, BorderLayout.NORTH);

        JPanel info = new JPanel(new BorderLayout());
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        info.add(buildInfoLabel("Title: " + media.getTitle()));
        info.add(buildInfoLabel("Genre: " + media.getGenre()));
        info.add(buildInfoLabel("Release: " + media.getReleaseDate()));
        if (media.getSeason() != null) {
            info.add(buildInfoLabel("Season: " + media.getSeason()));
        }
        if (media.getEpisode() != null) {
            info.add(buildInfoLabel("Episode: " + media.getEpisode()));
        }
    

        info.add(Box.createVerticalStrut(12));

        JButton IMBDButton = new JButton("View on IMBD");
        IMBDButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        IMBDButton.setBackground(new Color(66, 133, 244));
        IMBDButton.setFocusPainted(false);
        IMBDButton.addActionListener(e -> openIMBDLink(media));
        info.add(IMBDButton, BorderLayout.EAST);


        JButton streamButton = new JButton("Stream");
        streamButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        streamButton.setBackground(new Color(66, 133, 244));
        streamButton.setFocusPainted(false);
        streamButton.addActionListener(e -> streamMedia(media));
        info.add(streamButton, BorderLayout.EAST);

        root.add(info, BorderLayout.CENTER);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private JLabel buildInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(Color.DARK_GRAY);
        label.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        return label;
    }

    private void openIMBDLink(Media media) {
        String link = media.getImdbLink();
        if (link == null || link.isBlank()) {
            JOptionPane.showMessageDialog(this, "No stream link available for this title yet.");
            return;
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(link));
            } else {
                JOptionPane.showMessageDialog(this, "Cannot open browser on this device.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to open link:\n" + e.getMessage());
        }
    }

    private Image createPlaceholderPoster(int width, int height, String title) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(220, 220, 220));
        g.fillRect(0, 0, width, height);
        g.setColor(new Color(66, 133, 244));
        g.drawRect(2, 2, width - 4, height - 4);

        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        String text = (title == null || title.isBlank()) ? "No Poster" : title;
        if (text.length() > 22) {
            text = text.substring(0, 22) + "...";
        }
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = Math.max(6, (width - textWidth) / 2);
        int y = height / 2;
        g.drawString(text, x, y);
        g.dispose();
        return img;
    }

    private Image scaleImage(Image img, int width, int height) {
        return img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
    private void showTableInCenter(JScrollPane scrollPane) {
        centerPanel.removeAll();
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
        if (returnButton != null) {
            returnButton.setVisible(true);
        }
    }

    private void streamMedia(Media media){

        JOptionPane.showMessageDialog(null, "Now streaming " + media.getTitle());

        //Add logic for tracking stream
        int userId = BackendService.getMemberIdByUsername(username);
        String mediaId = media.getMediaID();
        try{
            BackendService.addMediaToWatchHistory(userId, mediaId);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Unable to add media to watch history");
        }

    }
}
