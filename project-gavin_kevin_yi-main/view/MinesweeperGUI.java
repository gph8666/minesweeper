package view;

import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.GameState;
import model.Location;
import model.Minesweeper;
import model.MinesweeperException;
import model.Observer;

public class MinesweeperGUI extends Application{
    private Minesweeper minesweeper;
    private final int ROWS = 6, COLS = 8, MINES = 10;
    private Button[][] buttons = new Button[ROWS][COLS];
    // weird janky fix to have moves label update on button press
    private Label numMoves = new Label();
    Label gameState = new Label();
    public static final Image MINE_IMG = new Image("file:media/images/mine24.png");

    private Button makeCellButton(int r, int c) {
        Button button = new Button();
        button.setText("-");
        button.setPrefSize(50, 50);
        button.setOnAction((event) -> {
            try{ 
                minesweeper.makeSelection(new Location(r, c));
            } catch(MinesweeperException ex) {}
        });
        /*
        updated backend to change the state of the minesweeper

        */
        return button;
    }

    public GridPane makeBoard() {
        GridPane board = new GridPane();
        for(int r = 0; r < ROWS; r++) {
            for(int c = 0; c < COLS; c++) {
                Button button = makeCellButton(r, c);
                buttons[r][c] = button;
                board.add(button, r, c);
            }
        }
        for(Node n : board.getChildren()) {
            GridPane.setHgrow(n, Priority.ALWAYS);
            GridPane.setVgrow(n, Priority.ALWAYS);
        }
        return board;
    }

    @Override
    public void start(Stage stage) throws Exception {
        minesweeper = new Minesweeper(ROWS, COLS, MINES);
        MinesweeperObserver observer = new  MinesweeperObserver(minesweeper, buttons);
        minesweeper.register(observer);

        HBox hbox = new HBox();
        numMoves.setText("Number of Moves: " + minesweeper.getMoveCount() + " ");
        Label numOfMines = new Label();
        numOfMines.setText("Number of Mines: " + MINES + " ");
        gameState.setText(minesweeper.getGameState().toString() + "\t");
        
        // the textbox that displays a win/lose/reset message
        Label textbox = new Label("");
        textbox.setMaxWidth(Double.POSITIVE_INFINITY);
        textbox.setBackground(new Background(new BackgroundFill(Color.BEIGE, CornerRadii.EMPTY, Insets.EMPTY)));
        // adding to hbox and vbox
        hbox.getChildren().add(gameState);
        hbox.getChildren().add(numOfMines);
        hbox.getChildren().add(numMoves);
        VBox vbox = new VBox();
        GridPane gridPane = makeBoard();

        HBox buttonbox = new HBox();

        Button reset = new Button();
        reset.setText("reset");
        reset.setPrefSize(150, 50);
        reset.setOnAction((event) -> {
            try {
                start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        Button hint = new Button();
        hint.setText("hint");
        hint.setPrefSize(150, 50);
        hint.setOnAction((event) -> {
            List<String> hints = minesweeper.getPossibleSelections();
            Random random = new Random();
            int randomIndex = random.nextInt(hints.size());
            String[] tokens = hints.get(randomIndex).split(", ");
            int row = Integer.parseInt(tokens[0].split(" ")[1]);
            int col = Integer.parseInt(tokens[1].split(" ")[1]);
            buttons[row][col].setStyle("-fx-background-color: #00ff00; ");
            buttons[row][col].getBackground();
        });

        buttonbox.getChildren().add(reset);
        buttonbox.getChildren().add(hint);

        vbox.getChildren().add(gridPane);
        vbox.getChildren().add(hbox);
        vbox.getChildren().add(buttonbox);

        stage.setScene(new Scene(vbox));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    class MinesweeperObserver implements Observer{
        private Minesweeper thissweeper;
        private Button[][] buttons;
    
        public MinesweeperObserver(Minesweeper minesweeper, Button[][] buttons){
            this.thissweeper = minesweeper;
            this.buttons = buttons;
        }
    
        @Override
        public void cellUpdated(Location location) {
            Button button = buttons[location.getRow()][location.getCol()];
            if(thissweeper.getSymbol(location) == '0'){
                button.setText("");
            }
            else{
                // TO DO - Make it set the image to a mine if a mine, change colour of text depending on number
                setButton(button, location);
            }
            button.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            button.setBorder(new Border(new BorderStroke(Color.DIMGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            if(thissweeper.getGameState() != GameState.WON && thissweeper.getGameState() != GameState.LOST){
                numMoves.setText("Number of Moves: " + thissweeper.getMoveCount());
                gameState.setText(thissweeper.getGameState().toString() + " ");
            }
            else{
                for(int r = 0; r < thissweeper.getRow(); r++){
                    for(int c = 0; c < thissweeper.getCol(); c++){
                        thissweeper.setIsCovered(false, r, c);
                        if(thissweeper.getSymbol(new Location(r, c)) == '0'){
                            buttons[r][c].setText("");
                        }
                        else{
                            setButton(buttons[r][c], new Location(r, c));
                        }
                        buttons[r][c].setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                        buttons[r][c].setBorder(new Border(new BorderStroke(Color.DIMGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    }
                }
                gameState.setText(thissweeper.getGameState().toString());
            }
            
        }
        /**
         * Just a switch statement that changes the colour from 1 (red) though the rainbow until 8 (purple)
         * Made a private function due to how long it is.
         * @param button The current button being altered
         * @param location The location the button is located, used to find what symbol to use.
         */
        private void setButton(Button button, Location location) {
            char symbol = minesweeper.getSymbol(location);
            // using nested ifs because we never went through switch statements and i'm not sure if i'm allowed to use them
            if (symbol == '1') {
                button.setText("1"); // jank fix due to symbol being a char
                button.setTextFill(Color.RED);
            }
            else if (symbol == '2') {
                button.setText("2");
                button.setTextFill(Color.ORANGE);
            }
            else if (symbol == '3') {
                button.setText("3");
                button.setTextFill(Color.YELLOW);
            }
            else if (symbol == '4') {
                button.setText("4");
                button.setTextFill(Color.GREENYELLOW);
            }
            else if (symbol == '5') {
                button.setText("5");
                button.setTextFill(Color.GREEN);
            }
            else if (symbol == '6') {
                button.setText("6");
                button.setTextFill(Color.BLUE);
            }
            else if (symbol == '7') {
                button.setText("7");
                button.setTextFill(Color.BLUEVIOLET);
            }
            else if (symbol == '8') {
                button.setText("8");
                button.setTextFill(Color.PURPLE);
            }
            // mine case
            else if (symbol == 'M') {
                button.setText("");
                button.setGraphic(new ImageView(MINE_IMG));
            }
        }
    }

}
