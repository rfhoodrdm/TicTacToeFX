package edu.usf.mail.rfhood;

/**
 * The GameBoard class represents all of the tic-tac-toe grid positions
 * as well as the player moves made on them for a single game.
 */
public class GameBoard {

    /* **************************************************************
                                Fields and Constants
       ************************************************************** */

    private GameState.POSITION_STATE[][] positionStates;  //states for each game board position, by column and row.


    /* **************************************************************
                                Constructor/Initialization
       ************************************************************** */

    public GameBoard() {
        positionStates = new GameState.POSITION_STATE[3][3];
    }

    /* **************************************************************
                                API
        ************************************************************** */

    /**
     * Set all board squares to the initial, unclaimed state.
     */
    public void clearGameBoard() {
        for ( int column = 0;  column < 3;  ++column) {
            for ( int row = 0;  row < 3;  ++row ) {
                positionStates[column][row] = GameState.POSITION_STATE.UNCLAIMED;
            }
        }
    }

    /**
     * Get the state in a given square.
     * @param column
     * @param row
     * @return
     */
    public GameState.POSITION_STATE getPositionState( int column, int row ) {
        //TODO need to decide what to do on out-of-bounds access. Honor system for now, since this
        //was refactored from the GameState proper to do the advanced AI -- rfhood -- 1/17/2020
        return positionStates[column][row];
    }

    /**
     * Set the game board state to the given new state. Then return true to indicate success.
     * @param column
     * @param row
     * @param newState
     * @return
     */
    public boolean setPositionState(int column, int row, GameState.POSITION_STATE newState ) {
        positionStates[column][row] = newState;
        return true;
    }

    /**
     * Create a copy of the game board in question.
     * @return
     */
    public GameBoard clone() {
        GameBoard clonedBoard = new GameBoard();
        clonedBoard.clearGameBoard();

        //now set all of the positions.
        for ( int column = 0;  column < 3;  ++column ) {
            for ( int row = 0;  row < 3;  ++row ) {
                clonedBoard.setPositionState( column, row, this.getPositionState(column, row) );
            }
        }

        //return the clone.
        return clonedBoard;
    }

    /**
     * Checks for game board victory on the given column for the given marker.
     * @param column
     * @return
     */
    public boolean checkVictoryOnColumn( int column, GameState.POSITION_STATE marker ) {
        return  getPositionState(column, 0) == marker  &&
                getPositionState(column, 1) == marker  &&
                getPositionState(column, 2) == marker;
    }

    /**
     * Checks for game board victory on the given column for the given marker.
     * @param row
     * @param marker
     * @return
     */
    public boolean checkVictoryOnRow( int row, GameState.POSITION_STATE marker ) {
        return  getPositionState(0, row) == marker  &&
                getPositionState(1, row) == marker  &&
                getPositionState(2, row) == marker;
    }

    /**
     * Checks for game board victory on the forward diagonal (top-left to bottom right)
     * for the given marker.
     * @param marker
     * @return
     */
    public boolean checkVictoryOnForwardDiagonal( GameState.POSITION_STATE marker ) {
        return  getPositionState(0, 0) == marker  &&
                getPositionState(1, 1) == marker  &&
                getPositionState(2, 2) == marker;
    }

    /**
     * Checks for game board victory on the back diagonal (top-right to bottom left)
     * for the given marker.
     * @param marker
     * @return
     */
    public boolean checkVictoryOnBackwardDiagonal( GameState.POSITION_STATE marker ) {
        return  getPositionState(2, 0) == marker  &&
                getPositionState(1, 1) == marker  &&
                getPositionState(0, 2) == marker;
    }


    /**
     * Checks for any victory for the given marker. Unlike the GameState-level method,
     * we don't care WHICH line scores the victory; only that it exists.
     * @param marker
     * @return
     */
    public boolean checkForVictory( GameState.POSITION_STATE marker ) {
        //check each column
        for ( int column = 0;  column < 3;  ++column ) {
            if ( checkVictoryOnColumn(column, marker) ) {
                //three in a row found!
                return true;
            }
        }

        //check each row
        for ( int row = 0;  row < 3;  ++row ) {
            if ( checkVictoryOnRow(row, marker) ) {
                //three in a row found!
                return true;
            }
        }

        //check diagonal #1
        if (    checkVictoryOnForwardDiagonal(marker) ) {
            //three in a row found!
            return true;
        }

        //check diagonal #2
        if (    checkVictoryOnBackwardDiagonal(marker) ) {
            //three in a row found!
            return true;
        }

        //if we haven't found a winner yet, return false.
        return false;
    }
}
