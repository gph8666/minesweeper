package view;

import java.util.List;
import java.util.Scanner;

import model.GameState;
import model.Location;
import model.Minesweeper;
import model.MinesweeperException;

public class MinesweeperCLI {
    private Minesweeper minesweeper;
    private final int ROWS = 6, COLS = 8, MINES = 5;

    public MinesweeperCLI() {
        this.minesweeper = new Minesweeper(ROWS, COLS, MINES); // row and col are created 
    }

    /**
     * Prints out the current minesweeper board.
     * Since minesweeper already has a toString() function, just prints it out
     */
    public void printBoard() {
        System.out.println("\n" + minesweeper);
    }
    /**
     * Prints out all of the commands for Minesweeper.
     */
    public void help() {
        System.out.println("\n===== COMMAND LIST =====\n" +
        "help - prints out commands\n" + 
        "pick <row> <col> - uncover a covered cell at location (row, col)\n" +
        "hint - displays an available move\n" +
        "reset - clears the board and resets the game\n" +
        "quit - quits the game\n");
    }
    /**
     * Attempts to uncover a certain row, col position on the board.
     * If the selection is invalid, throw a MinesweeperException.
     * @param row The row position that was picked.
     * @param col The column position that was picked.
     */
    public void pick(int row, int col) {
        Location location = new Location(row, col);
        try {
            minesweeper.makeSelection(location);
        } 
        catch (MinesweeperException e) {
            System.out.printf("You need the row and/or column! Rows must be less than %d, columns must be less than %d\n", minesweeper.getRow(), minesweeper.getCol());
            System.out.println("Command is: pick <row> <col>\n");
        }
        
        if(minesweeper.getGameState() == GameState.LOST){
            for(int r = 0; r < minesweeper.getRow(); r++){
                for(int c = 0; c < minesweeper.getCol(); c++){
                    minesweeper.setIsCovered(false, row, col);
                }
            }
            System.out.println("You Lost!");
            System.out.println("Enter 'reset' to try again, or 'quit' to stop playing");;
        }
        this.printBoard();
    }
    /**
     * Uses successors and backtracking to find a valid position.
     * Prints the row and column (row, col) that is valid.
     */
    public void hint() {
        List<String> possible_moves = minesweeper.getPossibleSelections();
        System.out.println("Possible moves:\n");
        for(String coordinate : possible_moves){
            System.out.println(coordinate + "\n");
        }
    }
    /**
     * Resets the minesweeper board.
     */
    public void reset() {
        this.minesweeper = new Minesweeper(ROWS, COLS, MINES);
        printBoard();
    }
    public static void main(String[] args) {
        // initiate board and scanner
        Scanner scanner = new Scanner(System.in);
        MinesweeperCLI minesweeper = new MinesweeperCLI();
        minesweeper.printBoard();
        while(true) {
            // initiate scanner and check for commands
            System.out.print("Enter command: ");
            String command = scanner.nextLine();
            command.toLowerCase();
            // split comands into parts (if possible)
            String[] parsedCommands = command.split(" ");
            
            // actual commands
            if (parsedCommands[0].equals("quit")) {
                break;
            }
            else if (parsedCommands[0].equals("hint")) {
                minesweeper.hint();
            }
            else if (parsedCommands[0].equals("reset")) {
                minesweeper.reset();
            }
            else if (parsedCommands[0].equals("pick")) {
                try{
                    int row = Integer.parseInt(parsedCommands[1]);
                    int col = Integer.parseInt(parsedCommands[2]);
                    minesweeper.pick(row, col);
                }
                catch(IndexOutOfBoundsException e){
                    System.out.printf("You need the row and/or column! Rows must be less than %d, columns must be less than %d\n", minesweeper.minesweeper.getRow(), minesweeper.minesweeper.getCol());
                    System.out.println("Command is: pick <row> <col>\n");
                }
                    
            }
            // if the command is not valid or the command is help, just print out all commands
            else {
                minesweeper.help();
            }
        }
        scanner.close();
    }
}
