// The GUI for the escape room
// Will contain instructions and a final screen indicating whether the password
// has been successfully entered

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*; // Rotating text
import java.awt.image.*; // BufferedImage
import java.awt.event.*;  // Needed for ActionListener
import javax.swing.event.*;  // Needed for ActionListener
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.Timer;
import javax.imageio.*; // Allows exporting to a png
import java.io.*; // File I/O
import java.util.ArrayList;

class SudokuHints extends JFrame
{
    // UI components
    private JButton goBtn; // Button to start/stop timer
    private JLabel currentTime; // Displays current time
    
    private int time; // To store countdown
    
    // Set up Swing timer for countdown
    private Timer threeMinuteTimer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            time--;
            currentTime.setText(time/60 + ":" + time%60);
            if(time<0) time = 180; // Reset timer
        }
    });
    
    public SudokuHints()
    {
        // Initialize components
        time = 180; // Countdown for 3 minutes
        
        BtnListener btnListener = new BtnListener(); // listener for all buttons
        
        goBtn = new JButton("Start");
        goBtn.addActionListener(btnListener);
        
        // Current time display, centered
        Font font = new Font("Courier New", Font.BOLD, 72);
        currentTime = new JLabel("3:00", SwingConstants.CENTER);
        currentTime.setFont(font);

        // Create content pane, set layout
        JPanel content = new JPanel();        // Create a content pane
        content.setLayout(new BorderLayout(5,0)); // Use BorderLayout for panel
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(5,3)); // Grid for displaying everyone's hint count
        
        // Tool tips
        goBtn.setToolTipText("Start Timer");
        
        // Add components to content area
        //north.add(prevBtn);
        //north.add(nextBtn);
        
        // Input area
        content.add(goBtn, "South");
        // Output areas
        content.add(currentTime,"North");
        content.add(center,"Center");
        content.setBorder(BorderFactory.createEmptyBorder(0,5,5,5)); // Add a 5 pixel margin on left, right, and bottom of window

        // Set window attributes
        setContentPane(content);
        pack();
        setTitle("Sudoku Hint Helper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);// Center window
    }
    
    // Sets the hint GUI active
    public void sendActive(boolean active)
    {
        if(active) { // Set everything and make visible
            setVisible(true);
            time = 180; // Make sure to reset time
        } else { // Stop everything and make non visible
            setVisible(false);
            if(threeMinuteTimer.isRunning()) threeMinuteTimer.stop(); // Stop timer if necessary
        }
    }
    
    class BtnListener implements ActionListener 
    {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getActionCommand().equals("Start")) {
                goBtn.setText("Stop");
                goBtn.setToolTipText("Stop Timer");
                threeMinuteTimer.start();
            } else if(e.getActionCommand().equals("Stop")) {
                goBtn.setText("Start");
                goBtn.setToolTipText("Start Timer");
                threeMinuteTimer.stop();
            }
        }
    }
}
