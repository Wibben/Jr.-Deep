// Sudoku solver class that will take in the puzzles
// in their incomplete form

import java.awt.*;
import javax.swing.*;
import java.io.*; // File I/O

public class Sudoku
{
    // Holds puzzle - 0 indicates an empty square
    private int[][] puzzle;

    // Loads an empty sudoku puzzle
    public Sudoku()
    {
        // Initialization
        puzzle = new int[9][9];
        // initialize to empty board
        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                puzzle[i][j] = 0;
            }
        }
    }
    
    // Loads the sudoku and sovles it at the same time
    public Sudoku(String filePath)
    {
        // Initialization
        puzzle = new int[9][9];
        loadSudoku(filePath);
        solve();
    }
    
    // Loads a selected sudoku puzzle
    public void loadSudoku(String filePath)
    {
        // Open the right puzzle file
        try {
            // Send file to an input stream
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            // Read the input into current instructions
            String input = bufferedReader.readLine();
            int row = 0;
            while(input != null) {
                for(int col=0; col<9; col++) {
                    puzzle[row][col] = input.charAt(col)-'0'; // Convert character to int for array
                }
                input = bufferedReader.readLine();
                row++; // Next row
            }
            
            fileReader.close(); // Close the file
        } catch(Exception e) { // If it does not exist, output error message in dialog box
            JOptionPane.showMessageDialog(null, "File Does Not Exist","Error:",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Solves a puzzle, calls the recursive sudoku solve function
    public void solve()
    {
        solve(0,0);
    }
    
    // Recursively solves a sudoku puzzle using a brute force method
    private void solve(int r, int c)
    {
        // Rows go from 0 to 8, so when row = 9, all squares have been filled
        // and a validity check is required
        if(r==9) {
            boolean rowtaken[] = new boolean[]{false,false,false,false,false,false,false,false,false,false};
            boolean coltaken[] = new boolean[]{false,false,false,false,false,false,false,false,false,false};
            boolean boxtaken[] = new boolean[]{false,false,false,false,false,false,false,false,false,false};
            boolean okay = true;
    
            for(int i=0; i<9; i++) {
                if(rowtaken[puzzle[8][i]]) okay = false;
                rowtaken[puzzle[8][i]] = true;
            }
            for(int i=0; i<9; i++) {
                if(coltaken[puzzle[i][8]]) okay = false;
                coltaken[puzzle[i][8]] = true;
            }
            for(int i=6; i<9; i++) {
                for(int j=6; j<9; j++) {
                    if(boxtaken[puzzle[i][j]]) okay = false;
                    boxtaken[puzzle[i][j]] = true;
                }
            }
    
            // Prints the board if it's okay
            if(okay) {
                for(int i=0; i<9; i++) {
                    for(int j=0; j<9; j++)
                        System.out.print(puzzle[i][j] + " ");
                    System.out.println();
                }
                System.out.println();
            }
        } else if(puzzle[r][c]==0) { // 0 indicates an empty box, so must fill it in
            boolean avail[] = new boolean[]{true,true,true,true,true,true,true,true,true,true};
            int rblock,cblock;
            
            // Find the block that the row and column is in
            // rblock and cblock holds the top left corner of the current block
            rblock = (r/3) * 3;
            cblock = (c/3) * 3;
            //
            /*
            if(r<3) rblock = 0;
            else if(r<6) rblock = 3;
            else rblock = 6;
    
            if(c<3) cblock = 0;
            else if(c<6) cblock = 3;
            else cblock = 6;
            */
            // Figure out which numbers are currently available
            for(int i=0; i<9; i++)
                if(puzzle[i][c]!=0) avail[puzzle[i][c]] = false;
            for(int i=0; i<9; i++)
                if(puzzle[r][i]!=0) avail[puzzle[r][i]] = false;
            for(int i=rblock; i<rblock+3; i++)
                for(int j=cblock; j<cblock+3; j++)
                    if(puzzle[i][j]!=0) avail[puzzle[i][j]] = false;
    
            // Try all available numbers (until solution is found)
            for(int i=1; i<10; i++) {
                if(avail[i]) {
                    puzzle[r][c] = i;
    
                    if(c==8) solve(r+1,0);
                    else solve(r,c+1);
                    puzzle[r][c] = 0;
                }
            }
        } else { // Square has been filled as part of the puzzle, move on to next one
            if(c==8) solve(r+1,0);
            else solve(r,c+1);
        }
    }
}
