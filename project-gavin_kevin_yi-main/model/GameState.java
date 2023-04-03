package model;

public enum GameState {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    WON("You Won!"),
    LOST("You Lost");

    private String gameState;

    private GameState(String string){
        gameState = string;
    }

    public String toString(){
        return gameState;
    }
}
