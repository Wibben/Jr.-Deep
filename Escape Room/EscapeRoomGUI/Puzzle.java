
/**
 * Enumeration class Puzzle - includes all the steps of the puzzle
 */
public enum Puzzle
{
    START,
    OVERLAY, 
    INK,
    LIES,
    MORSE,
    SUDOKU,
    JIGSAW,
    CIRCUIT,
    WIN;
    
    private static Puzzle vals[] = values();
    
    // Iteration functions, the iterations will wrap around,
    // but in the actual program disallow the wrap around bu disabling the buttons
    public Puzzle next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }
    
    public Puzzle prev()
    {
        return vals[(this.ordinal()+vals.length-1) % vals.length]; // Prevent modulo of a negative number
    }
}
