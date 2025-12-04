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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.imageio.ImageIO;

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
        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/ACEDLogo.png"));
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

            JPanel grid = new JPanel(new GridLayout(0, 2, 12, 12));
            grid.setOpaque(false);

            for (Media m : results) {

                BackendService.enrichPoster(m);

                grid.add(buildPosterCard(m));
            }

            JScrollPane scrollPane = new JScrollPane(grid);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setPreferredSize(new Dimension(900, 360));
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);

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
                java.net.URL url = new java.net.URL(posterUrl);
                java.awt.Image img = ImageIO.read(url);
                if (img != null) {
                    return new ImageIcon(scaleImage(img, targetWidth, targetHeight));
                }
            }
        } catch (Exception ignored) {
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

        JPanel info = new JPanel();
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
        if (media.getImdbLink() != null && !media.getImdbLink().isBlank()) {
            JLabel imdbLabel = buildInfoLabel("IMDb: " + media.getImdbLink());
            imdbLabel.setForeground(Color.BLUE.darker());
            info.add(imdbLabel);
        }

        root.add(info, BorderLayout.CENTER);
        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private JLabel buildInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setForeground(Color.DARK_GRAY);
        label.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        return label;
    }

    private java.awt.Image createPlaceholderPoster(int width, int height, String title) {
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

    private java.awt.Image scaleImage(java.awt.Image img, int width, int height) {
        return img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
    }
}
