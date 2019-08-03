// Sudoku solver class that will take in the puzzles
// in their incomplete form

import java.awt.*;
import javax.swing.*;
import java.io.*; // File I/O

public class Sudoku
{
    // Holds puzzle - 0 indicates an empty square
    public int[][] puzzle,solution;
    
    private boolean solved; // Flag for whether the puzzle has been solved

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
        // Copy over the puzzle and begin to solve
        solution = new int[9][9];
        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                solution[i][j] = puzzle[i][j];
            }
        }
        
        solved = false;
        
        solve(0,0);
    }
    
    // Recursively solves a sudoku puzzle using a brute force method
    private void solve(int r, int c)
    {
        if(solved) return; // DO nothing if puzzle has been solved
        
        // Rows go from 0 to 8, so when row = 9, all squares have been filled
        // and a validity check is required
        if(r==9) {
            boolean rowtaken[] = new boolean[]{false,false,false,false,false,false,false,false,false,false};
            boolean coltaken[] = new boolean[]{false,false,false,false,false,false,false,false,false,false};
            boolean boxtaken[] = new boolean[]{false,false,false,false,false,false,false,false,false,false};
            boolean okay = true;
    
            for(int i=0; i<9; i++) {
                if(rowtaken[solution[8][i]]) okay = false;
                rowtaken[solution[8][i]] = true;
            }
            for(int i=0; i<9; i++) {
                if(coltaken[solution[i][8]]) okay = false;
                coltaken[solution[i][8]] = true;
            }
            for(int i=6; i<9; i++) {
                for(int j=6; j<9; j++) {
                    if(boxtaken[solution[i][j]]) okay = false;
                    boxtaken[solution[i][j]] = true;
                }
            }
            
            // Sets solved flag if puzzle is valid
            if(okay) solved = true;
            /*
            // Prints the board if it's okay, used for debugging purposes
            if(okay) {
                for(int i=0; i<9; i++) {
                    for(int j=0; j<9; j++)
                        System.out.print(solution[i][j] + " ");
                    System.out.println();
                }
                System.out.println();
            }
            */
        } else if(solution[r][c]==0) { // 0 indicates an empty box, so must fill it in
            boolean avail[] = new boolean[]{true,true,true,true,true,true,true,true,true,true};
            int rblock,cblock;
            
            // Find the block that the row and column is in
            // rblock and cblock holds the top left corner of the current block
            rblock = (r/3) * 3;
            cblock = (c/3) * 3;
            
            // Figure out which numbers are currently available
            for(int i=0; i<9; i++)
                if(solution[i][c]!=0) avail[solution[i][c]] = false;
            for(int i=0; i<9; i++)
                if(solution[r][i]!=0) avail[solution[r][i]] = false;
            for(int i=rblock; i<rblock+3; i++)
                for(int j=cblock; j<cblock+3; j++)
                    if(solution[i][j]!=0) avail[solution[i][j]] = false;
    
            // Try all available numbers (until solution is found)
            for(int i=1; i<10; i++) {
                if(avail[i] && !solved) {
                    solution[r][c] = i;
    
                    if(c==8) solve(r+1,0);
                    else solve(r,c+1);
                    
                    if(!solved) solution[r][c] = 0;
                }
            }
        } else { // Square has been filled as part of the puzzle, move on to next one
            if(c==8) solve(r+1,0);
            else solve(r,c+1);
        }
    }
    
    // Getters for private members
    public int getPuzzleSquare(int row, int col) 
    {
        return puzzle[row][col];
    }
    
    // Reveals the value of one of the puzzle's squares
    public int revealPuzzleSquare(int row, int col)
    {
        // Edit the actual puzzle square with the solution
        puzzle[row][col] = solution[row][col];
        return puzzle[row][col];
    }
}
