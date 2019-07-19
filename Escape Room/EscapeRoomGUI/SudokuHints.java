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
    private JButton goBtn;
    private JButton useHintBtn[];
    private JLabel currentTime; // Displays current time
    private JLabel hintCount[]; // Displays hint count for each team
    
    private int time; // To store countdown
    private int hints[] = {0,0,0,0,0}; // To store the number of hints for each team
    
    // Set up Swing timer for countdown
    private Timer threeMinuteTimer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            time--;
            // Separate into digits for time display
            currentTime.setText(time/60 + ":" + ((time%60)/10) + "" + time%10);
            if(time<0) { // Reset timer
                time = 180;
                for(int i=0; i<5; i++) {
                    hints[i]++;
                    hintCount[i].setText(""+hints[i]);
                }
            }
        }
    });
    
    public SudokuHints()
    {
        // Initialize components
        time = 180; // Countdown for 3 minutes
        
        BtnListener btnListener = new BtnListener(); // listener for all buttons
        
        goBtn = new JButton("Start");
        goBtn.addActionListener(btnListener);
        useHintBtn = new JButton[5];
        
        // Current time display, centered
        hintCount = new JLabel[5];
        Font font = new Font("Courier New", Font.BOLD, 72);
        currentTime = new JLabel("3:00", SwingConstants.CENTER);
        currentTime.setFont(font);

        // Create content pane, set layout
        JPanel content = new JPanel();        // Create a content pane
        content.setLayout(new BorderLayout(5,0)); // Use BorderLayout for panel
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(5,3,0,5)); // Grid for displaying everyone's hint count
        
        // Tool tips
        goBtn.setToolTipText("Start Timer");
        
        // Add components to content area
        for(int i=0; i<5; i++) {
            center.add(new JLabel("Group " + (i+1) + " hints:"));
            hintCount[i] = new JLabel("0", SwingConstants.CENTER);
            center.add(hintCount[i]);
            useHintBtn[i] = new JButton("Use Hint");
            useHintBtn[i].setToolTipText("Use One of Group " + (i+1) + "'s Hints");
            useHintBtn[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Find out which button was pressed (and subsequently be able to determine which array index to check)
                    int group = ((JButton)e.getSource()).getToolTipText().charAt(17) - '1';
                    // Deduct hints if required
                    if(hints[group]>0) {
                        hints[group]++;
                        hintCount[group].setText(""+hints[group]);
                    } else { // Error pop up
                        JFrame f = new JFrame();
                        JOptionPane.showMessageDialog(f, "Not Enough Hints","Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            center.add(useHintBtn[i]);
        }
        
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
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);// Center window
    }
    
    // Sets the hint GUI active
    public void setActive(boolean active)
    {
        if(active) { // Set everything and make visible
            setVisible(true);
            // Make sure to reset time and hints
            time = 180; 
            for(int i=0; i<5; i++) {
                hints[i] = 0;
            }
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
