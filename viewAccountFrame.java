/*
 * This is the page where we will implement the view account functionality 
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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
        content.add(topPanel, BorderLayout.NORTH);

        setContentPane(content);

    }}

    // not really sure what all needs added here... figured this was where we could put account streaming info and such 