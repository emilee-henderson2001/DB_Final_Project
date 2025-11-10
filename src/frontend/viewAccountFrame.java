/*
 * This is the page where we will implement the view account functionality 
 */
package frontend;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import backend.*;

public class viewAccountFrame extends JFrame {
  


    public viewAccountFrame(String username) {

        // Create pop-up window
        setTitle("ACED Streaming - Account Information");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        // create panel to add content
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(24, 24, 24, 24));
        content.setBackground(new Color(255, 215, 50));

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

        JLabel history = new JLabel("show all media " + username + " has streamed");
        content.add(topPanel, BorderLayout.NORTH);
        try{
            java.util.List<Media> results = BackendService.getWatchHistoryByUser(username);

            if(results == null || results.isEmpty()){
                JOptionPane.showMessageDialog(this, "No results found ''" + username + "'.");
                return;
            }
            // Build a readable String for results
            StringBuilder message = new StringBuilder("Results for " + username + ":\n\n");
            for (Media m : results) {
                message.append("• ")
                        .append(m.getMediaID())
                        .append(" (").append(m.getTitle())
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

