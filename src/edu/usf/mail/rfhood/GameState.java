package edu.usf.mail.rfhood;

import edu.usf.mail.rfhood.logic.GameAI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameState {

    /* **************************************************************
                                Fields and Constants
       ************************************************************** */

    private POSITION_STATE[][] positionStates;  //states for each game board position, by column and row.

    private boolean[][] victoryConfiguration;   //marker for if the position in question is part of the win configuration.

    private boolean xPlayerControlled;
    private boolean oPlayerControlled;

    private GAME_PHASE gamePhase;       //what phase of the game? E.g. X to play, game over, etc.

    private int gameAILevel;     //how tough is the computer AI?


    /* **************************************************************
                                Constructor/Initialization
       ************************************************************** */

    public GameState (boolean xPlayerControlledSetting, boolean oPlayerControlledSetting, int gameAILevelSetting) {
        resetState(xPlayerControlledSetting, oPlayerControlledSetting, gameAILevelSetting) ;
    }


    /* **************************************************************
                                API
        ************************************************************** */

    /**
     * Reset game to initial state.
     */
    public void resetState(boolean xPlayerControlledSetting, boolean oPlayerControlledSetting, int gameAILevelSetting) {
        gamePhase = GAME_PHASE.NEW_GAME;

        //clear game board.
        positionStates = new POSITION_STATE[3][3];
        for ( int column = 0;  column < 3;  ++column) {
            for ( int row = 0;  row < 3;  ++row ) {
                positionStates[column][row] = POSITION_STATE.UNCLAIMED;
            }
        }

        //clear marked winner positions.
        victoryConfiguration = new boolean[3][3];
        for ( int column = 0;  column < 3;  ++column) {
            for ( int row = 0;  row < 3;  ++row ) {
                victoryConfiguration[column][row] = false;
            }
        }

        //set passed-in values for this game. Boundary check the gameAILevelSetting.
        this.xPlayerControlled = xPlayerControlledSetting;
        this.oPlayerControlled = oPlayerControlledSetting;
        this.gameAILevel = gameAILevelSetting;
        if ( gameAILevel < GameAI.MIN_AI_LEVEL) { gameAILevel = GameAI.MIN_AI_LEVEL; }
        if ( gameAILevel > GameAI.MAX_AI_LEVEL) { gameAILevel = GameAI.MAX_AI_LEVEL; }

    }


    public boolean getVictoryConfiguration(int column, int row ) {
        //check boundaries. If within the game board, return that position's state. If not, return false.
        if ( column >= 0  &&  column < 3  &&  row >= 0  &&  row < 3 ) {
            return victoryConfiguration[column][row];
        }

        return false;
    }


    /**
     * A player or CPU attempts to claim a square.
     * @param potentialState    New state of the square if the claim is successful.
     * @param column
     * @param row
     * @return
     */
    public boolean claimTerritory(POSITION_STATE potentialState, int column, int row) {

        //first, get check the state in question. If the state is anything but unclaimed, then we fail immediately.
        if ( POSITION_STATE.UNCLAIMED != positionStates[column][row] ) { return false; }

        //else, we mark the territory as now claimed.
        positionStates[column][row] = potentialState;
        return true;
    }


    /**
     * choose the next logical game phase.
     *  1) If it was a new game, then go to X to play.
     *  2) Check for win condition. If it exists, that player wins.
     *      2b) If both players won, we have an error state.
     *  3) Check for draw. If draw occurs, go to Game Over: Draw state.
     *  4) Flip the player's turn. If it was X's turn, go to O's turn, and vice versa.
     */
    public void advanceGamePhase() {
        boolean wasNewGame = (GAME_PHASE.NEW_GAME == gamePhase);
        boolean xWins = checkForXVictory();
        boolean oWins = checkForOVictory();
        boolean draw = checkForDraw();
        boolean errorHappened = xWins && oWins;
        boolean wasXTurn = (GAME_PHASE.X_TO_PLAY == gamePhase);

        if ( errorHappened ) {
            gamePhase = GAME_PHASE.ERROR;
        } else if ( xWins ) {
            gamePhase = GAME_PHASE.GAME_OVER_X_WINS;
        } else if ( oWins ) {
            gamePhase = GAME_PHASE.GAME_OVER_O_WINS;
        } else if ( draw ) {
            gamePhase = GAME_PHASE.GAME_OVER_DRAW;
        } else if (wasNewGame) {
            gamePhase = GAME_PHASE.X_TO_PLAY;
        } else if ( wasXTurn ) {
            gamePhase = GAME_PHASE.O_TO_PLAY;
        } else {
            gamePhase = GAME_PHASE.X_TO_PLAY;
        }
    }

    private boolean checkForXVictory() {
        return checkForVictory(POSITION_STATE.X);
    }

    private boolean checkForOVictory() {
        return checkForVictory(POSITION_STATE.O);
    }

    private boolean checkForVictory( POSITION_STATE marker ) {
        //check each column
        for ( int column = 0;  column < 3;  ++column ) {
            if ( positionStates[column][0] == marker  &&
                    positionStates[column][1] == marker  &&
                    positionStates[column][2] == marker ) {
                //three in a row found!
                victoryConfiguration[column][0] = true;  victoryConfiguration[column][1] = true;  victoryConfiguration[column][2] = true;
                return true;
            }
        }

        //check each row
        for ( int row = 0;  row < 3;  ++row ) {
            if ( positionStates[0][row] == marker  &&
                    positionStates[1][row] == marker  &&
                    positionStates[2][row] == marker ) {
                //three in a row found!
                victoryConfiguration[0][row] = true;  victoryConfiguration[1][row] = true;  victoryConfiguration[2][row] = true;
                return true;
            }
        }

        //check diagonal #1
        if (    positionStates[0][0] == marker  &&
                positionStates[1][1] == marker  &&
                positionStates[2][2] == marker ) {
            //three in a row found!
            victoryConfiguration[0][0] = true;  victoryConfiguration[1][1] = true;  victoryConfiguration[2][2] = true;
            return true;
        }

        //check diagonal #2
        if (    positionStates[2][0] == marker  &&
                positionStates[1][1] == marker  &&
                positionStates[0][2] == marker ) {
            //three in a row found!
            victoryConfiguration[2][0] = true;  victoryConfiguration[1][1] = true;  victoryConfiguration[0][2] = true;
            return true;
        }

        //if we haven't found a winner yet, return false.
        return false;
    }

    private boolean checkForDraw() {
        // a draw game only happens if all spaces are claimed, and there is still no winner.
        // since we check elsewhere for victory, we only check for a full game board here.
        for ( int column = 0;  column < 3;  ++column ) {
            for (int row = 0;  row < 3;  ++row ) {
                if ( POSITION_STATE.UNCLAIMED == positionStates[column][row] ) {
                    return false;
                }
            }
        }

        //if we get this far without finding an unclaimed tile, then the board is full. Draw game.
        return true;
    }

    /**
     * Invalidate the game state. Proceed straight to an error state.
     */
    public void tilt() {
        this.gamePhase = GAME_PHASE.ERROR;
    }

    /**
     * Returns the set of all unclaimed positions on the game board.
     * @return
     */
    public Set<POSITIONS> getUnclaimedPositions() {

        //get the state of the given position from the board. If it's unclaimed, it's a potential move.
        Set<POSITIONS> unclaimedPositions = new HashSet<>();
        for (GameState.POSITIONS currentPosition : GameState.POSITIONS.values() ) {
            GameState.POSITION_STATE currentPositionState = getPositionState(currentPosition.column, currentPosition.row);
            if (GameState.POSITION_STATE.UNCLAIMED == currentPositionState) {
                unclaimedPositions.add(currentPosition);
            }
        }

        return unclaimedPositions;
    }

    /* **************************************************************
                                Simple Getters/Setters
        ************************************************************** */

    public String getGameStatus() { return gamePhase.toString(); }

    public POSITION_STATE getPositionState( int column, int row ) {
        //check boundaries. If within the game board, return that position's state. If not, return OUT_OF_BOUNDS so we don't return null.
        if ( column >= 0  &&  column < 3  &&  row >= 0  &&  row < 3 ) {
            return positionStates[column][row];
        }

        return POSITION_STATE.OUT_OF_BOUNDS;
    }

    public boolean isxPlayerControlled() { return xPlayerControlled; }
    public boolean isoPlayerControlled() { return oPlayerControlled; }

    public GAME_PHASE getGamePhase() { return gamePhase; }

    public boolean isComputerTurn() {
        return  ( GAME_PHASE.X_TO_PLAY == gamePhase  &&  !xPlayerControlled ) ||
                ( GAME_PHASE.O_TO_PLAY == gamePhase  &&  !oPlayerControlled );

    }

    public int getGameAILevel() { return gameAILevel; }

    /* **************************************************************
                                Enums/Inner Classes
        ************************************************************** */

    /**
     * Enum denoting game board position states. Which player controls what territory?
     */
    public static enum POSITION_STATE {
        X,
        O,
        UNCLAIMED,
        OUT_OF_BOUNDS;      //error state.
    }

    /**
     * Enum representing what phase the game is currently in.
     */
    public static enum GAME_PHASE {
        NEW_GAME("New Game"),
        X_TO_PLAY("Player X to play..."),
        O_TO_PLAY("Player O to play..."),
        GAME_OVER_X_WINS("Game over. Player X wins!!"),
        GAME_OVER_O_WINS("Game over. Player O wins!!"),
        GAME_OVER_DRAW("Game over. Draw game."),
        ERROR("Game error. Sorry about that. Please try starting a new game.");

        private String gamePhaseString;     //corresponding string status of this game state.

        GAME_PHASE(String gamePhaseString) {
            this.gamePhaseString = gamePhaseString;
        }

        public String toString() {
            return this.gamePhaseString;
        }
    }


    /**
     * Enumerated positions for ease of processing.
     */
    public static enum POSITIONS {

        TOP_LEFT(0,0),
        TOP_CENTER(1, 0),
        TOP_RIGHT(2,0),

        MID_LEFT(0, 1),
        MID_CENTER(1,1),
        MID_RIGHT( 2, 1 ),

        BOTTOM_LEFT(0, 2),
        BOTTOM_CENTER(1,2),
        BOTTOM_RIGHT(2,2);

        //public properties for column and row, since they are read-only.
        public final int column;
        public final int row;

        private static POSITIONS[][] positionArrays = new POSITIONS[3][3];   //reference by column and row.

        static {
            for (POSITIONS currentPosition: values() ) {
                positionArrays[currentPosition.column][currentPosition.row] = currentPosition;
            }
        }

        POSITIONS(int pColumn, int pRow ) {
            this.column = pColumn;
            this.row = pRow;
        }

        /**
         * Get position from int column, row.
         * @param column
         * @param row
         * @return
         */
        public static POSITIONS fromColumnAndRow( int column, int row ) {
                return positionArrays[column][row];
        }

    }
}
