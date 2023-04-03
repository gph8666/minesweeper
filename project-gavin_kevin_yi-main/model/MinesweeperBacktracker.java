package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import backtracker.Backtracker;
import backtracker.Configuration;

public class MinesweeperBacktracker implements Configuration{
    private final Minesweeper minesweeper;
    private String[] moves = {};
    

    public MinesweeperBacktracker(Minesweeper minesweeper){
        this.minesweeper = minesweeper;
    }
    public MinesweeperBacktracker(Minesweeper minesweeper, String[] moves){
        this.minesweeper = minesweeper;
        this.moves = moves;
    }


    @Override
    public Collection<Configuration> getSuccessors() {
        List<Configuration> successors = new ArrayList<>();
        int length = moves.length;

        Minesweeper copySweeper = new Minesweeper(minesweeper);
        for(int i = 0; i < moves.length; i++){
            String[] tokens = moves[i].split(", ");
            int row = Integer.parseInt(tokens[0].split(" ")[1]);
            int col = Integer.parseInt(tokens[1].split(" ")[1]);
            copySweeper.setIsCovered(false, row, col);
        }

        for(int possibleMove = 0; possibleMove < copySweeper.getPossibleSelections().size(); possibleMove++){
            String[] movesNewCopy = Arrays.copyOf(moves, length + 1);
            List<String> possiblemoves = copySweeper.getPossibleSelections();
            movesNewCopy[length] = possiblemoves.get(possibleMove);
            successors.add(new MinesweeperBacktracker(copySweeper, movesNewCopy)); 
        }
        
        return successors;
    }

    @Override
    public boolean isValid() {
        for(String move : moves){
            String[] tokens = move.split(", ");
            int row = Integer.parseInt(tokens[0].split(" ")[1]);
            int col = Integer.parseInt(tokens[1].split(" ")[1]);
            if(minesweeper.getSymbol(new Location(row, col)) == Minesweeper.MINE){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isGoal() {
        boolean gameWon = false;

        int stillCovered = 0;
        for(int i = 0; i < minesweeper.getRow(); i++){
            for(int j = 0; j < minesweeper.getCol(); j++){
                if(minesweeper.isCovered(new Location(i, j)) == true){
                    stillCovered++;
                }
            }
        }
        if(stillCovered == minesweeper.getMineCount()){
            gameWon = false;
        }
        
        return isValid() && gameWon;
    }

    public static Configuration solution(Minesweeper minesweeper) {
        Backtracker backtracker = new Backtracker(false);
        MinesweeperBacktracker minesweeperBacktracker = new MinesweeperBacktracker(minesweeper);
        Configuration solution = backtracker.solve(minesweeperBacktracker);
        if(solution == null) {
            return null;
        } 
        else{
            return solution;
        }
    }

    public static void main(String[] args) {
        Minesweeper minesweeper = new Minesweeper(5, 5, 5);
        Configuration config = new MinesweeperBacktracker(minesweeper);
        Backtracker backtracker = new Backtracker(true);
        Configuration sol = backtracker.solve(config);
        if(sol == null) {
            System.out.println("No solution.");
        } else {
            System.out.println(sol);
        }
    }

    
}
