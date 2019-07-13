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

class GUI extends JFrame
{
    private Arduino main; // Arduino connection
    
    // UI components
    private DrawArea board; // Main display area
    private JButton nextBtn,prevBtn; // Buttons

    private Puzzle step; // Keeps track of which step it is in currently
    private boolean visited[];
    
    
    private Timer timer;
    
    public GUI(Arduino ard)
    {
        // Initialize components
        visited = new boolean[Puzzle.SIZE.getValue()];
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
    
    // Returns the right txt file for the current step
    public File findInstructionFile()
    {
        // Open the right instructions file
        try {
            // Figure out which file to open
            String filePath = "resources\\";
            switch(step) {
                case START:
                    filePath += "START.txt";
                    break;
                case OVERLAY:
                    filePath += "OVERLAY.txt";
                    break;
                case INK:
                    break;
                case LIES:
                    break;
                case MORSE:
                    break;
                case SUDOKU:
                    break;
                case JIGSAW:
                    break;
                case CIRCUIT:
                    break;
                case WIN:
                    break;
                default: break;
            }
            
            // Send file to an input stream
            File file = new File(filePath);
            return file;
        } catch(Exception e) { // If it does not exist, output error message in dialog box
            JOptionPane.showMessageDialog(null, "File Does Not Exist","Error:",JOptionPane.ERROR_MESSAGE);
        }
        
        return null;
    }
    
    class BtnListener implements ActionListener 
    {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getActionCommand().equals("Next Step")) {
                if(step == Puzzle.LIES) {
                    JFrame f = new JFrame();
                    String s = (String)JOptionPane.showInputDialog(f, "Please enter the code word", "Authentication Required", JOptionPane.QUESTION_MESSAGE);
                    
                    // Check if the code word is correct
                    if ((s != null) && (s.length() >= 5) && s.equals("MORSE")) {
                        JOptionPane.showMessageDialog(f, "Code Word Correct\nYou may proceed","Access Granted",JOptionPane.INFORMATION_MESSAGE);
                        step = step.next(); // Increment step
                    } else {
                        JOptionPane.showMessageDialog(f, "Code Word Incorrect\nYou may not proceed","Access Denied",JOptionPane.ERROR_MESSAGE);
                    }
                    
                    
                } else step = step.next(); // Increment step
                
                // Update everything
                board.updateBG();
                // Open the right instructions file
                try {
                    FileReader fileReader = new FileReader(findInstructionFile());
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                
                    int line = 0;
                    String input = bufferedReader.readLine();
                    while(input != null) {
                        // Print the line letter by letter
                        int letter=0;
                        timer = new Timer(50, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String str = input;
                                if(letter < input.length()){
                                    board.putChar(input,line,letter++);
                                } else {
                                    timer.stop();
                                    System.out.println("done");
                                }
                            }
                        });
                        timer.start();
                        /*
                        for(int i=0; i<input.length(); i++) {
                            board.putChar(input,line,i);
                            System.out.println(step.getValue());
                            // Output the entire file to the screen letter by letter if its the first time
                            if(!visited[step.getValue()]) Thread.sleep(50);
                        }*/
    
                        // Increment line count and read the next line
                        line++;
                        input = bufferedReader.readLine();
                    }
                    
                    // Set visited flag to true
                    visited[step.getValue()] = true;
                    
                    fileReader.close(); // Close the file
                } catch(Exception exc) { // If it does not exist, output error message in dialog box
                    JOptionPane.showMessageDialog(null, "File Does Not Exist","Error:",JOptionPane.ERROR_MESSAGE);
                }
                
                repaint();
                
                // Do not let the user go to the "next step" if they are now at the final puzzle
                if(step == Puzzle.CIRCUIT) nextBtn.setEnabled(false);
                prevBtn.setEnabled(true); // DO allow the user to go back
            } else if(e.getActionCommand().equals("Prev Step")) {
                step = step.prev(); // Decrement step
                
                // Update everything
                board.updateBG();
                // Open the right instructions file
                try {
                    FileReader fileReader = new FileReader(findInstructionFile());
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                
                    int line = 0;
                    String input = bufferedReader.readLine();
                    while(input != null) {
                        // Print the line letter by letter
                        for(int i=0; i<input.length(); i++) {
                            board.putChar(input,line,i);
                        }
    
                        // Increment line count and read the next line
                        line++;
                        input = bufferedReader.readLine();
                    }
                    
                    fileReader.close(); // Close the file
                } catch(Exception exc) { // If it does not exist, output error message in dialog box
                    JOptionPane.showMessageDialog(null, "File Does Not Exist","Error:",JOptionPane.ERROR_MESSAGE);
                }
                
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
            updateBG();
        }
        
        public void updateBG()
        {
            // Border
            g2d.setColor(new Color(100,100,100));
            g2d.fillRect(0,0,601,601);
            
            g2d.setColor(new Color(0,0,0));
            g2d.fillRect(1,1,600,600);
            
            //g2d.drawString("12345678901234567890123456789012345678901234567890",0,20);
            //g2d.drawString(""+step, 250, 300);
            
            repaint();
        }
        
        public void updatePassword(boolean isCorrect)
        {
            updateBG();
            
            // Set Font
            Font font = new Font("Courier New", Font.PLAIN, 72);
            g2d.setFont(font);
            
            // Display whether password for puzzle 6 is correct
            if(isCorrect) {
                g2d.setColor(new Color(0,255,0));
                g2d.drawString("CORRECT", 100, 400);
                step = step.next(); // Increment step to win screen
            } else {
                g2d.setColor(new Color(255,0,0));
                g2d.drawString("INCORRECT", 100, 400);
            }
            
            repaint();
        }
        
        public void putChar(String input, int line, int i)
        {
            // Set Font and get font metrics
            g2d.setColor(new Color(0,255,0));
            Font font = new Font("Courier New", Font.PLAIN, 24); // 43 Characters per line
            g2d.setFont(font);
            FontMetrics metrics = g2d.getFontMetrics(font);
            int fontHeight = metrics.getHeight(); // Font height
            int fontAdvance = metrics.stringWidth(input.substring(0,i));
            
            g2d.drawString(""+input.charAt(i),7+fontAdvance,20+line*fontHeight); // Draw the character at the specified position
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
