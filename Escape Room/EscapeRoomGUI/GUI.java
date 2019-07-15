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

class GUI extends JFrame
{
    private Arduino main; // Arduino connection
    
    // UI components
    private DrawArea board; // Main display area
    private JButton nextBtn,prevBtn; // Buttons

    private Puzzle step; // Keeps track of which step it is in currently
    private boolean visited[];
    private ArrayList<String> currentInstructions;
    
    private Timer t;
    private int instructionRow,instructionCol;
    
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
    public void readInstructionFile()
    {
        // Start a new ArrayList
        currentInstructions = new ArrayList<String>();
        
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
                filePath += "START.txt";
                    break;
                case LIES:
                filePath += "START.txt";
                    break;
                case MORSE:
                filePath += "START.txt";
                    break;
                case SUDOKU:
                filePath += "START.txt";
                    break;
                case JIGSAW:
                filePath += "START.txt";
                    break;
                case CIRCUIT:
                filePath += "START.txt";
                    break;
                case WIN:
                    break;
                default: break;
            }
            
            // Send file to an input stream
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            // Read the input into current instructions
            String input = bufferedReader.readLine();
            while(input != null) {
                currentInstructions.add(input);
                input = bufferedReader.readLine();
            }
            
            fileReader.close(); // Close the file
        } catch(Exception e) { // If it does not exist, output error message in dialog box
            JOptionPane.showMessageDialog(null, "File Does Not Exist","Error:",JOptionPane.ERROR_MESSAGE);
        }
        
        instructionRow = currentInstructions.size()-1;
        instructionCol = currentInstructions.get(instructionRow).length();
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
                readInstructionFile();
                // If first time draw letters one by one
                if(!visited[step.getValue()]) {
                    instructionRow=0;
                    instructionCol=0;
                    
                    // Start a swing timer to draw the letters
                    t = new Timer(40, new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            if(instructionRow<currentInstructions.size()) {
                                board.updateAll();
                                instructionCol++;
                                if(instructionCol>currentInstructions.get(instructionRow).length()) {
                                    instructionCol = 0;
                                    instructionRow++;
                                }
                            } else t.stop();
                        }
                    });
                    t.start();
                    
                    visited[step.getValue()] = true;
                } else {
                    board.updateAll();
                }
                
                repaint();
                
                // Do not let the user go to the "next step" if they are now at the final puzzle
                if(step == Puzzle.CIRCUIT) nextBtn.setEnabled(false);
                prevBtn.setEnabled(true); // DO allow the user to go back
            } else if(e.getActionCommand().equals("Prev Step")) {
                step = step.prev(); // Decrement step
                
                // Update everything
                readInstructionFile();
                board.updateAll();
                
                repaint();
                
                // Do not let the user go to the "previous step" if they are now at the first puzzle
                if(step == Puzzle.START) prevBtn.setEnabled(false);
                nextBtn.setEnabled(true); // DO allow the user to go forward
            }
        }
    }
    
    // Main display
    class DrawArea extends JPanel
    {
        private BufferedImage img = new BufferedImage(602 ,602, BufferedImage.TYPE_INT_ARGB);
        private Graphics2D g2d = img.createGraphics();
        
        public DrawArea(int width, int height)
        {
            this.setPreferredSize(new Dimension(width,height)); // size
            readInstructionFile();
            updateAll();
        }
        
        public void updateAll()
        {
            // Border
            g2d.setColor(new Color(100,100,100));
            g2d.fillRect(0,0,601,601);
            
            // Background
            g2d.setColor(new Color(0,0,0));
            g2d.fillRect(1,1,600,600);
            
            // Set Font and get font metrics
            g2d.setColor(new Color(0,255,0));
            Font font = new Font("Courier New", Font.PLAIN, 24); // 43 Characters per line
            g2d.setFont(font);
            FontMetrics metrics = g2d.getFontMetrics(font);
            int fontHeight = metrics.getHeight(); // Font height
            
            // Draw the text up to the current instruction row and col
            for(int i=0; i<instructionRow; i++) {
                g2d.drawString(currentInstructions.get(i),7,20+i*fontHeight); // Draw each row of text
            }
            g2d.drawString(currentInstructions.get(instructionRow).substring(0,instructionCol),7,20+instructionRow*fontHeight); // Draw the last row up to the current col
            
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
                step = step.next(); // Increment step to win screen
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
