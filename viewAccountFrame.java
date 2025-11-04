/*
 * This is the page where we will implement the view account functionality 
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class viewAccountFrame extends JFrame {
  


    public viewAccountFrame() {

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


       
    }}

    // not really sure what all needs added here... figured this was where we could put account streaming info and such 