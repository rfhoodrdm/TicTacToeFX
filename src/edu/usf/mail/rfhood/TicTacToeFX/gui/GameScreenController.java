package edu.usf.mail.rfhood.TicTacToeFX.gui;

import edu.usf.mail.rfhood.TicTacToeFX.gui.audio.SFXManager;
import edu.usf.mail.rfhood.TicTacToeFX.logic.GameAI;
import edu.usf.mail.rfhood.TicTacToeFX.logic.exception.GameAIException;
import edu.usf.mail.rfhood.TicTacToeFX.state.GameState;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.usf.mail.rfhood.TicTacToeFX.gui.SceneDirectory.SCENE_NAME.TITLE_SCENE;
import static edu.usf.mail.rfhood.TicTacToeFX.gui.audio.SFXManager.SFX.*;
import static edu.usf.mail.rfhood.TicTacToeFX.state.GameState.POSITION_STATE.O;
import static edu.usf.mail.rfhood.TicTacToeFX.state.GameState.POSITION_STATE.X;

public class GameScreenController {

    /* **************************************************************
                                Fields and Constants
       ************************************************************** */

    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @FXML private Button boardPosition00;
    @FXML private Button boardPosition01;
    @FXML private Button boardPosition02;
    @FXML private Button boardPosition10;
    @FXML private Button boardPosition11;
    @FXML private Button boardPosition12;
    @FXML private Button boardPosition20;
    @FXML private Button boardPosition21;
    @FXML private Button boardPosition22;
    @FXML private Button resetGameButton;
    @FXML private RadioButton humanXRadioButton;
    @FXML private ToggleGroup playerRoleRadioGroup1;
    @FXML private RadioButton humanORadioButton;
    @FXML private ToggleGroup playerRoleRadioGroup2;
    @FXML private RadioButton cpuXRadioButton;
    @FXML private RadioButton cpuORadioButton;
    @FXML private Label gameStatusLabel;
    @FXML private Label gameBoardActiveStatusIndicatorLabel;
    @FXML private BorderPane sceneBorderPane;       //reference to root element of the scene.
    @FXML private Slider cpuDifficultySlider;

    private Button[][] gameBoardPositionButtons;    //references to position buttons.
    private GameState gameState;                    //reference to the current game's state.
    private GameAI gameAI;                          //reference to the AI component.
    private SFXManager sfxManager;                  //reference to the SFX Manager component.
    private Task computerMoveTask;                  //refernece to the Task that makes moves for the computer.

    public static final String X_TERRITORY_STYLE_CLASS = "x_territory";     //style class references
    public static final String O_TERRITORY_STYLE_CLASS = "o_territory";
    public static final String VICTORY_TERRITORY_STYLE_CLASS = "victory_territory";


    /* **************************************************************
                                Constructor/Initialization
       ************************************************************** */


    public void initialize() {
        //install a new label formatter to the CPU difficulty widget.
        cpuDifficultySlider.setLabelFormatter( new CpuDifficultyLabelFormatter() );

        //organize the game board position buttons into columns and rows for easier management.
        gameBoardPositionButtons = new Button[3][3];

        gameBoardPositionButtons[0][0] = boardPosition00;
        gameBoardPositionButtons[0][1] = boardPosition01;
        gameBoardPositionButtons[0][2] = boardPosition02;

        gameBoardPositionButtons[1][0] = boardPosition10;
        gameBoardPositionButtons[1][1] = boardPosition11;
        gameBoardPositionButtons[1][2] = boardPosition12;

        gameBoardPositionButtons[2][0] = boardPosition20;
        gameBoardPositionButtons[2][1] = boardPosition21;
        gameBoardPositionButtons[2][2] = boardPosition22;


        //create references to other game components, such as
        //a new GameAI logic instance and the sound effects manager.
        gameAI = new GameAI();
        sfxManager = SFXManager.getInstance();

        //make a new game state and initialize it. Then call the logic to advance the game state.
        humanXRadioButton.setSelected(true);
        cpuORadioButton.setSelected(true);
        cpuDifficultySlider.setValue(1.0);
        gameState = new GameState(true, false, 1);
        advanceGameState();

        //then update the game board GUI to reflect the game state.
        updateGUI();
    }


    /* **************************************************************
                        API
        ************************************************************** */

    public void updateGUI() {
        //update game state label.
        gameBoardActiveStatusIndicatorLabel.setText( gameState.getGameStatus() );

        //update game position buttons.
        for ( int column = 0;  column < 3;  ++column ) {
            for ( int row = 0;  row < 3;  ++row ) {
                GameState.POSITION_STATE positionState = gameState.getPositionState(column, row);
                Button button = gameBoardPositionButtons[column][row];
                boolean victoryPosition = gameState.getVictoryConfiguration(column, row);
                updateGameBoardButton( button, positionState, victoryPosition );
            }
        }
    }

    private void updateGameBoardButton( Button button, GameState.POSITION_STATE positionState, boolean victorySquare ) {
        //set the text for the button, and also set the style class for coloring.

        switch ( positionState ) {
            case X:
                button.setText("X");
                button.getStyleClass().add(X_TERRITORY_STYLE_CLASS);
                button.getStyleClass().remove(O_TERRITORY_STYLE_CLASS);
                break;
            case O:
                button.setText("O");
                button.getStyleClass().add(O_TERRITORY_STYLE_CLASS);
                button.getStyleClass().remove(X_TERRITORY_STYLE_CLASS);
                break;
            case UNCLAIMED:
                button.setText("");
                button.getStyleClass().remove(O_TERRITORY_STYLE_CLASS);
                button.getStyleClass().remove(X_TERRITORY_STYLE_CLASS);
                break;
            default:    //error case. Should not happen, but want to know when it does.
                button.setText("?");
                button.getStyleClass().remove(O_TERRITORY_STYLE_CLASS);
                button.getStyleClass().remove(X_TERRITORY_STYLE_CLASS);
                break;
        }

        //also, if the position is part of a victory configuration, mark it as so.
        if ( victorySquare ) {
            button.getStyleClass().add(VICTORY_TERRITORY_STYLE_CLASS);
        } else {
            button.getStyleClass().remove(VICTORY_TERRITORY_STYLE_CLASS);
        }

    }


    /**
     * This method handles advancing the game to the next state after a human move is made.
     * It also directs the computer to make moves for sides marked as controlled by a CPU.
     */
    private void advanceGameState()  {
        //move on to the next logical game state. X's turn changes to O's turn, etc.
        gameState.advanceGamePhase();
        playSFXForMove(gameState);
        updateGUI();

        //now the CPU makes moves for computer controlled sides.
        //If the computer is not playing a side, this part is essentially skipped.
        //If only one side is CPU controlled, the computer makes the move and control reverts back to the human immediately.
        //If both are CPU controlled, then the computer keeps making turns until the game is done.
        //make a new independent thread task to calculate the computer's move, and update the gui accordingly.
        computerMoveTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                //keep going until the computer is done. E.g. It is time for a human to move, or else the game is over.
                while ( gameState.isComputerTurn() ) {
                    try {
                        //pick the move to make.
                        Platform.runLater( () -> gameBoardActiveStatusIndicatorLabel.setText("Thinking...") );      //queue status update on gui thread.
                        Thread.sleep(1000);
                        gameAI.makeComputerMove(gameState);

                        //move to the next turn and update the game state.
                        gameState.advanceGamePhase();
                        playSFXForMove(gameState);
                        Platform.runLater( () -> updateGUI() );        //queue gui update on application thread.
                    } catch (GameAIException e) {
                        //report the error message. Then invalidate the game so the loop will exit.
                        logger.log(Level.SEVERE, "Game exception occurred: " + e.getMessage() );
                        gameState.tilt();
                    } catch (InterruptedException e) {
                        //if we're interrupted, take it as a sign that we're done playing this game.
                        logger.info("Received a request to stop making moves.");
                        return null;
                    }
                }

                return null;
            }
        };
        new Thread(computerMoveTask).start();       //start cpu move selection task.

    }

    /**
     * General event handler for a player click on a position button.
     * @param column
     * @param row
     */
    private void handlePlayerMove( int column, int row ) {
        //check to see if it's the computer's turn. If so, ignore the click.
        if ( gameState.isComputerTurn() ) { return; }

        //check to see if it's X's turn, or O's turn. Attempt to claim that region for the given player, if so.
        boolean result = false;     //result flag. Records if the move was successful.
        if ( gameState.getGamePhase() == GameState.GAME_PHASE.X_TO_PLAY ) {
            //Attempt to make a move for X.
            result = gameState.claimTerritory( X, column, row );
            logger.info("Player attempts to claim " + column + " / " + row + " for X. Result: " + result );

        } else if ( gameState.getGamePhase() == GameState.GAME_PHASE.O_TO_PLAY ) {
            //Attempt to make a move for O.
            result = gameState.claimTerritory( O, column, row );
            logger.info("Player attempts to claim " + column + " / " + row + " for O. Result: " + result );
        }

        //if the result indicates success, advance the game state. Else an illegal move was made.
        if ( result ) {
            advanceGameState();
        }

        //update the display after the move is made.
        updateGUI();
    }


    @FXML
    void handleBackToTitleButtonClick(ActionEvent event) {
        Stage primaryStage = (Stage) sceneBorderPane.getScene().getWindow();
        Scene titleScene = SceneDirectory.getInstance().getScene(TITLE_SCENE);
        primaryStage.setScene(titleScene);
    }

    @FXML
    void handleResetGameButtonClick(ActionEvent event) {
        //first, if there is cpu AI task running for the current game, we need to stop it.
        computerMoveTask.cancel();

        //get information about the new game from the gui.
        boolean xHumanPlayer = humanXRadioButton.isSelected();
        boolean oHumanPlayer = humanORadioButton.isSelected();
        int gameAILevelSetting = (int) cpuDifficultySlider.getValue();

        //create the new game state, just as if we first initialized things.
        gameState = new GameState(xHumanPlayer, oHumanPlayer, gameAILevelSetting);

        //advance the game state and update the gui.
        advanceGameState();
        updateGUI();

    }

    /*
     * These event handlers all note which location was chosen, then defer to a single handler to manage player moves.
     */
    @FXML void handlePosition00ButtonClick(ActionEvent event) { handlePlayerMove(0,0); }
    @FXML void handlePosition01ButtonClick(ActionEvent event) { handlePlayerMove(0,1); }
    @FXML void handlePosition02ButtonClick(ActionEvent event) { handlePlayerMove(0,2); }

    @FXML void handlePosition10ButtonClick(ActionEvent event) { handlePlayerMove(1,0); }
    @FXML void handlePosition11ButtonClick(ActionEvent event) { handlePlayerMove(1,1); }
    @FXML void handlePosition12ButtonClick(ActionEvent event) { handlePlayerMove(1,2); }

    @FXML void handlePosition20ButtonClick(ActionEvent event) { handlePlayerMove(2,0); }
    @FXML void handlePosition21ButtonClick(ActionEvent event) { handlePlayerMove(2,1); }
    @FXML void handlePosition22ButtonClick(ActionEvent event) { handlePlayerMove(2,2); }


    /**
     * If a sound effect is called for after a move, determine which one it should be, and play it accordingly.
     * ** NOTE: Should only be called AFTER the game state has been advanced.
     * @param gameState
     */
    private void playSFXForMove( GameState gameState ) {
        //First, determine if we are in an end-game state: one side has victory, or else there is a draw.
        //We should play a fanfare if one side has attained victory.
        //Only play defeat if the player lost vs playing a computer.
        boolean drawGame = gameState.getGamePhase().equals(GameState.GAME_PHASE.GAME_OVER_DRAW);
        boolean victoryGame = gameState.getGamePhase().equals(GameState.GAME_PHASE.GAME_OVER_X_WINS)    ||
            gameState.getGamePhase().equals(GameState.GAME_PHASE.GAME_OVER_O_WINS);
        boolean playDefeatFanfare = ( gameState.isoPlayerControlled()  &&
                                     !gameState.isxPlayerControlled()  &&
                                      gameState.getGamePhase().equals(GameState.GAME_PHASE.GAME_OVER_X_WINS) )    ||
                                   ( !gameState.isoPlayerControlled()  &&
                                      gameState.isxPlayerControlled()  &&
                                      gameState.getGamePhase().equals(GameState.GAME_PHASE.GAME_OVER_O_WINS));

        //otherwise, the move number can be found by counting the number of marks played. (9 - # of empty spaces)
        int moveNumber = 9 - gameState.getUnclaimedPositions().size();

        //decide on the sound to play.
        if ( playDefeatFanfare ) {
            sfxManager.playSFX(GAME_OVER_DEFEAT);
        } else if ( victoryGame ) {
            sfxManager.playSFX(GAME_OVER_VICTORY);
        } else if ( drawGame ) {
            sfxManager.playSFX(GAME_OVER_DRAW);
        } else {
            //play a plain move number sound.
            //we are only concerned for moves 1 to 8, because the game is guaranteed to be over at turn 9.
            switch (moveNumber){
                case 1:
                    sfxManager.playSFX(MOVE_1);
                    break;
                case 2:
                    sfxManager.playSFX(MOVE_2);
                    break;
                case 3:
                    sfxManager.playSFX(MOVE_3);
                    break;
                case 4:
                    sfxManager.playSFX(MOVE_4);
                    break;
                case 5:
                    sfxManager.playSFX(MOVE_5);
                    break;
                case 6:
                    sfxManager.playSFX(MOVE_6);
                    break;
                case 7:
                    sfxManager.playSFX(MOVE_7);
                    break;
                case 8:
                    sfxManager.playSFX(MOVE_8);
                    break;
                default:
                    logger.severe("Was asked to play a move sound for a move number out of range!");
                    break;
            }
        }
    }


    /* **************************************************************
                                Enums/Inner Classes
        ************************************************************** */

    /**
     * Handles text labels for the CPU difficulty slider on the game board screen.
     */
    public class CpuDifficultyLabelFormatter extends StringConverter<Double> {

        public static final String PERFECT_AI_SLIDER_LABEL = "Perfect AI";
        public static final String PET_PARROT_SLIDER_LABEL = "Pet Parrot";
        public static final String MOLDY_SOCK_SLIDER_LABEL = "Moldy Sock";

        @Override
        public String toString( Double sliderValue ) {
            String sliderTextLabel;     //our return value.

            if ( sliderValue >= 2.0 ) {
                sliderTextLabel = PERFECT_AI_SLIDER_LABEL;
            } else if ( sliderValue >= 1.0 ) {
                sliderTextLabel = PET_PARROT_SLIDER_LABEL;
            } else {
                sliderTextLabel = MOLDY_SOCK_SLIDER_LABEL;
            }

            return sliderTextLabel;
        }

        @Override
        public Double fromString( String sliderLabelString ) {
            Double sliderValue;     //our return value

            switch(sliderLabelString) {
                case PERFECT_AI_SLIDER_LABEL:
                    sliderValue = 2.0;
                    break;

                case PET_PARROT_SLIDER_LABEL:
                    sliderValue =  1.0;
                    break;

                case MOLDY_SOCK_SLIDER_LABEL:
                default:
                    sliderValue =  0.0;
                    break;
            }

            return sliderValue;
        }
    }

}
