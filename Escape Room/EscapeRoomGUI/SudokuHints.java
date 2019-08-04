// The GUI for the hint system for sudoku
// Will allow the user to use hints to help solve the sudoku puzzle

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;  // Needed for ActionListener
import javax.swing.border.*;
import javax.swing.Timer;
import java.io.*; // File I/O

class SudokuHints extends JFrame
{
    // UI components
    private JButton goBtn;
    private JButton useHintBtn[];
    private JLabel currentTime; // Displays current time
    private JLabel hintCount[]; // Displays hint count for each team
    private JLabel interfaceHintCount[];
    
    private int time; // To store countdown
    private int hints[] = {0,0,0,0,0}; // To store the number of hints for each team
    private Sudoku puzzles[]; // To store the sudoku puzzles
    
    private JFrame currentFrame; // Reference to SudokuHints JFrame
    
    // Set up Swing timer for countdown
    private Timer threeMinuteTimer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            time--;
            // Separate into digits for time display
            currentTime.setText(time/60 + ":" + ((time%60)/10) + "" + time%10);
            if(time<1) { // Reset timer
                time = 180;
                for(int i=0; i<5; i++) {
                    hints[i]++;
                    hintCount[i].setText(""+hints[i]);
                    interfaceHintCount[i].setText(""+hints[i]);
                }
            }
        }
    });
    
    public SudokuHints()
    {
        // Initialize components
        currentFrame = this;
        
        time = 180; // Countdown for 3 minutes
        puzzles = new Sudoku[5];
        for(int i=1; i<=5; i++) {
            puzzles[i-1] = new Sudoku("resources\\SUDOKU-"+i+".txt");
        }
        
        BtnListener btnListener = new BtnListener(); // listener for all buttons
        
        goBtn = new JButton("Start");
        goBtn.addActionListener(btnListener);
        useHintBtn = new JButton[5];
        
        // Current time display, centered
        hintCount = new JLabel[5];
        interfaceHintCount = new JLabel[5];
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
            hintCount[i] = new JLabel(""+hints[i], SwingConstants.CENTER);
            interfaceHintCount[i] = new JLabel(""+hints[i]);
            center.add(hintCount[i]);
            useHintBtn[i] = new JButton("Use Hint");
            useHintBtn[i].setToolTipText("Use One of Group " + (i+1) + "'s Hints");
            useHintBtn[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Find out which button was pressed (and subsequently be able to determine which array index to check)
                    int group = ((JButton)e.getSource()).getToolTipText().charAt(17) - '1';
                    
                    // Open the hints interface for the selected group
                    openHintInterface(group);
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
        setLocationRelativeTo(null); // Center window
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
                hintCount[i].setText(""+hints[i]);
                interfaceHintCount[i].setText(""+hints[i]);
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
    
    // Hints interface for specific groups
    private void openHintInterface(int group)
    {
        // Disable Use Hint button so user won't open 2 of the same hint interfaces
        useHintBtn[group].setEnabled(false);
        
        // Hint interface JFrame, groups are from 1-5 but stored in array indices 0-4 so must add 1
        JFrame hintInterface = new JFrame("Group " + (group+1) + "'s Sudoku");
        
        // Create content panes and set layouts
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout(5,0));
        JPanel north = new JPanel();
        north.setLayout(new FlowLayout());
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(3,3,1,1));
        center.setBackground(Color.BLACK);
        
        // Add buttons to center area
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                // Set each block
                JPanel block = new JPanel();
                block.setLayout(new GridLayout(3,3,0,0));
                for(int k=0; k<3; k++) {
                    for(int l=0; l<3; l++) {
                        JButton square = new JButton(""+puzzles[group].getPuzzleSquare(i*3+k,j*3+l));
                        if(square.getText().equals("0")) { // Enable the blank buttons and make text blank instead of 0
                            square.setText("");
                        }
                        
                        square.setToolTipText("Row " + (i*3+k+1) + " Col " + (j*3+l+1));
                        square.setPreferredSize(new Dimension(30,30));
                        square.setBackground(Color.WHITE);
                        square.setBorder(new LineBorder(Color.BLACK));
                        square.setMargin(new Insets(0,0,0,0));
                        square.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                // Find out which button was pressed (and subsequently be able to determine which array index to check)
                                int row = ((JButton)e.getSource()).getToolTipText().charAt(4) - '1';
                                int col = ((JButton)e.getSource()).getToolTipText().charAt(10) - '1';
                                
                                // Only deduct hints if the square has not been revealed yet
                                if(((JButton)e.getSource()).getText().equals("")) {
                                    // Check if the user has a hint to use
                                    if(hints[group]>0) {
                                        hints[group]--;
                                        hintCount[group].setText(""+hints[group]);
                                        interfaceHintCount[group].setText(""+hints[group]);
                                        // Update and reveal square
                                        square.setText(""+puzzles[group].revealPuzzleSquare(row,col));
                                    }else { // Error pop up
                                        JOptionPane.showMessageDialog(hintInterface, "Not Enough Hints","Error",JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }
                        });
                        block.add(square);
                    }
                }
                // Add block to actual panel
                center.add(block);
            }
        }
        
        // Output Area
        north.add(new JLabel("Group " + (group+1) + "'s Hints: "));
        north.add(interfaceHintCount[group]);
        content.add(north,"North");
        content.add(center,"Center");
        content.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
        
        // Set window attributes
        hintInterface.setContentPane(content);
        hintInterface.pack();
        hintInterface.setVisible(true);
        hintInterface.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        hintInterface.setLocationRelativeTo(currentFrame); // Center window on hints frame
        hintInterface.setResizable(false);
        
        // Re-enable the Use Hint Button on close/dispose
        hintInterface.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                useHintBtn[group].setEnabled(true);
            }
        });
    }
}
