package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Minesweeper {
    public static final char MINE = 'M';
    public static final char COVERED = '-';
    
    private int moveCount = 0;
    private GameState gameState = GameState.NOT_STARTED;
    private int rows; 
    private int cols;
    private int mineCount;
    private Observer observer = null;

    private char[][] board;
    private boolean[][] isCovered;

    public Minesweeper(int rows, int cols, int mineCount) {
        this.rows = rows;
        this.cols = cols;
        this.mineCount = mineCount;

        board = new char[rows][cols];
        isCovered = new boolean[rows][cols];

        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                board[r][c] = '0';
                isCovered[r][c] = true;
            }
        }

        Random random = new Random();
        for(int i = 0; i < mineCount; i++){
            int temp_row = random.nextInt(rows);
            int temp_col = random.nextInt(cols);
            board[temp_row][temp_col] = MINE;
        }

        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                if(board[r][c] == MINE){
                }
                else{
                    int nearby_mines = 0;

                    if((r - 1 >= 0) && (c - 1 >= 0) && board[r-1][c-1] == MINE){
                        nearby_mines++;
                    }
                    if((r - 1 >= 0) && board[r-1][c] == MINE){
                        nearby_mines++;
                    }
                    if((r - 1 >= 0) && (c + 1 < cols) && board[r-1][c+1] == MINE){
                        nearby_mines++;
                    }

                    if((c - 1 >= 0) && board[r][c-1] == MINE){
                        nearby_mines++;
                    }
                    if((c + 1 < cols) && board[r][c+1] == MINE){
                        nearby_mines++;
                    }

                    if((r + 1 < rows) && (c - 1 >= 0) && board[r+1][c-1] == MINE){
                        nearby_mines++;
                    }
                    if((r + 1 < rows) && board[r+1][c] == MINE){
                        nearby_mines++;
                    }
                    if((r + 1 < rows) && (c + 1 < cols) && board[r+1][c+1] == MINE){
                        nearby_mines++;
                    }

                    board[r][c] = (char)(nearby_mines + 48);
                }

                
            }
        }

    }

    public Minesweeper(Minesweeper minesweeper){
        
        this.board = new char[minesweeper.getRow()][minesweeper.getCol()];
        this.observer = null;
        this.isCovered = new boolean[minesweeper.getRow()][minesweeper.getCol()];
        this.moveCount = minesweeper.getMoveCount();
        this.gameState = minesweeper.getGameState();
        this.rows = minesweeper.getRow();
        this.cols = minesweeper.getCol();
        this.mineCount = minesweeper.mineCount;

        for(int r = 0; r < minesweeper.getRow(); r++){
            for(int c = 0; c < minesweeper.getCol(); c++){
                this.board[r][c] = minesweeper.getSymbol(new Location(r, c));
                this.isCovered[r][c] = minesweeper.isCovered(new Location(r, c));
            }
        }
    }

    @Override
    public String toString() {
        String all_values = "";
        for(int r = 0; r < rows; r++){
            String row_values = "";
            for(int c = 0; c < cols; c++){
                if(isCovered[r][c] == true){
                    row_values += "[" + COVERED + "] ";
                }
                else{
                    row_values += "[" + board[r][c] + "] ";
                }
            }
            all_values += row_values;
            all_values += "\n";
        }
        return all_values;
    }

    public void makeSelection(Location location) throws MinesweeperException{
        if(location.getRow() > rows || location.getRow() < 0 || location.getCol() > cols || location.getCol() < 0){
            throw new MinesweeperException("Error");
        }
        else if(isCovered[location.getRow()][location.getCol()] == false || gameState == GameState.LOST || gameState == GameState.WON){
        }
        else{
            isCovered[location.getRow()][location.getCol()] = false;
            if(this.gameState != GameState.LOST){
                moveCount++;
            }
            //changes gamestate to in progress from not started
            if(this.gameState == GameState.NOT_STARTED){
                this.gameState = GameState.IN_PROGRESS;
            }
            // checks if covered cells is equal to minecount, and sets gamestate to won if it is
            int stillCovered = 0;
            for(int i = 0; i < rows; i++){
                for(int j = 0; j < cols; j++){
                    if(isCovered[i][j] == true){
                        stillCovered++;
                    }
                }
            }
            if(stillCovered == mineCount){
                this.gameState = GameState.WON;
                for(int r = 0; r < this.rows; r++){
                    for(int c = 0; c < this.cols; c++){
                        notifyObserver(new Location(r, c));
                    }
                }
            }

            // sets gamestate to lost if you click a mine
            if(board[location.getRow()][location.getCol()] == MINE){
                this.gameState = GameState.LOST;
            }

        }
        
        notifyObserver(location);
        
    }

    public List<String> getPossibleSelections(){
        List<String> safe_moves = new ArrayList<>();
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                if(isCovered[r][c] == true && board[r][c] != MINE){
                    safe_moves.add("row " + r + ", column " + c);
                }
            }
        }
        return safe_moves;
    }

    public void register(Observer observer){
        this.observer = observer;
    }

    public void notifyObserver(Location location){
        if(observer != null){
            observer.cellUpdated(location);
        }
    }

    public char getSymbol(Location location){
        if(isCovered(location)){
            return COVERED;
        }
        else{
            return board[location.getRow()][location.getCol()];
        }  
    } 

    public boolean isCovered(Location location){
        return isCovered[location.getRow()][location.getCol()];
    }

    public int getMoveCount() {
        return moveCount;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getRow(){
        return rows;
    }
    public int getCol(){
        return cols;
    }
    public int getMineCount(){
        return mineCount;
    }
    public void setIsCovered(boolean covered, int row, int col){
        this.isCovered[row][col] = covered;
    }
}
