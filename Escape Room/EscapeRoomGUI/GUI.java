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
import javax.imageio.*; // Allows exporting to a png

class GUI extends JFrame
{
    private Arduino main; // Arduino connection
    
    // UI components
    private DrawArea board; // Main display area
    private JButton nextBtn,prevBtn; // Buttons

    private Puzzle step; // Keeps track of which step it is in currently
    
    public GUI(Arduino ard)
    {
        // Initialize components
        step = Puzzle.START;
        main = ard;
        BtnListener btnListener = new BtnListener(); // listener for all buttons
        
        prevBtn = new JButton("Prev Step");
        prevBtn.addActionListener(btnListener);
        nextBtn = new JButton("Next Step");
        nextBtn.addActionListener(btnListener);

        // Create content pane, set layout
        JPanel content = new JPanel();        // Create a content pane
        content.setLayout(new BorderLayout(5,0)); // Use BorderLayout for panel
        JPanel north = new JPanel();
        north.setLayout(new FlowLayout()); // Use FlowLayout for input area

        board = new DrawArea(602,602); // Main image
        
        // Tool tips
        prevBtn.setToolTipText("Previous Puzzle's Instructions");
        prevBtn.setEnabled(false); // Initially there is no previous step
        nextBtn.setToolTipText("Next Puzzle's Instructions");
        
        // Add components to content area
        north.add(prevBtn);
        north.add(nextBtn);
        
        // Input area
        content.add(north,"North");
        // Output areas
        content.add(board,"Center");
        content.setBorder(BorderFactory.createEmptyBorder(0,5,5,5)); // Add a 5 pixel margin on left, right, and bottom of window

        // Set window attributes
        setContentPane(content);
        pack();
        setTitle("The Great Escape");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);// Center window
    }
    
    // Receives whether the password is correct or incorrect
    public void sendPass(boolean isCorrect)
    {
        // Only do something if the information is sent over during the last puzzle
        if(step==Puzzle.CIRCUIT) board.updatePassword(isCorrect);
    }
    
    class BtnListener implements ActionListener 
    {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getActionCommand().equals("Next Step")) {
                step = step.next(); // Increment step
                
                // Update everything
                board.updateAll();
                repaint();
                
                // Do not let the user go to the "next step" if they are now at the final puzzle
                if(step == Puzzle.CIRCUIT) nextBtn.setEnabled(false);
                prevBtn.setEnabled(true); // DO allow the user to go back
            } else if(e.getActionCommand().equals("Prev Step")) {
                step = step.prev(); // Decrement step
                
                // Update everything
                board.updateAll();
                repaint();
                
                // Do not let the user go to the "previous step" if they are now at the first puzzle
                if(step == Puzzle.START) prevBtn.setEnabled(false);
                nextBtn.setEnabled(true); // DO allow the user to go forward
            }
        }
    }
    
    // Main image that is being scanned
    // For 300 x 300 image, each "pixel" is a 2x2 rectangle
    class DrawArea extends JPanel
    {
        private BufferedImage img = new BufferedImage(602 ,602, BufferedImage.TYPE_INT_ARGB);
        private Graphics2D g2d = img.createGraphics();
        
        public DrawArea(int width, int height)
        {
            this.setPreferredSize(new Dimension(width,height)); // size
            updateAll();
        }
        
        public void updateAll()
        {
            // Border
            g2d.setColor(new Color(100,100,100));
            g2d.fillRect(0,0,601,601);
            
            g2d.setColor(new Color(0,0,0));
            g2d.fillRect(1,1,600,600);
            
            // Set Font
            g2d.setColor(new Color(0,255,0));
            Font font = new Font("Courier New", Font.PLAIN, 24); // 43 Characters per line
            g2d.setFont(font);
            g2d.drawString("12345678901234567890123456789012345678901234567890",0,20);
            g2d.drawString(""+step, 250, 300);
            
            repaint();
        }
        
        public void updatePassword(boolean isCorrect)
        {
            updateAll();
            
            // Set Font
            Font font = new Font("Courier New", Font.PLAIN, 72);
            g2d.setFont(font);
            
            // Display whether password for puzzle 6 is correct
            if(isCorrect) {
                g2d.setColor(new Color(0,255,0));
                g2d.drawString("CORRECT", 100, 400);
            } else {
                g2d.setColor(new Color(255,0,0));
                g2d.drawString("INCORRECT", 100, 400);
            }
            
            repaint();
        }

        public void paintComponent(Graphics g)
        {
            g.drawImage(img,0,0,null);
        }
        
        public BufferedImage getImg()
        {
            return img;
        }
    }
}
