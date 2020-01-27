package edu.usf.mail.rfhood.TicTacToeFX.logic;

import edu.usf.mail.rfhood.TicTacToeFX.state.GameState;
import edu.usf.mail.rfhood.TicTacToeFX.logic.exception.GameAIException;
import edu.usf.mail.rfhood.TicTacToeFX.logic.exception.NoAvailableMovesException;
import edu.usf.mail.rfhood.TicTacToeFX.logic.exception.PlayerCannotMoveNowException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Base class for a strategy for playing Tic-Tac-Toe.
 */
public abstract class Strategy {

    protected Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    public abstract void makeComputerMove( GameState gameState ) throws GameAIException;

    /**
     * Is the computer making a move for X or for O?
     * @param gameState
     * @return
     */
    protected GameState.POSITION_STATE determineMarkerToPlace( GameState gameState ) throws GameAIException {

        //the marker to place is determined by what phase the game is in.
        GameState.POSITION_STATE currentPlayerMarker;
        if ( GameState.GAME_PHASE.X_TO_PLAY == gameState.getGamePhase() ) {
            currentPlayerMarker = GameState.POSITION_STATE.X;
        } else if (GameState.GAME_PHASE.O_TO_PLAY == gameState.getGamePhase() ) {
            currentPlayerMarker = GameState.POSITION_STATE.O;
        } else {
            throw new PlayerCannotMoveNowException();
        }

        return currentPlayerMarker;
    }

    /**
     * Which marker represents the opponent?
     * @param gameState
     * @return
     * @throws GameAIException
     */
    protected GameState.POSITION_STATE determineOpponentMarker( GameState gameState ) throws GameAIException {

        //whatever the current player's marker, the opponent has the opposite.
        GameState.POSITION_STATE currentPlayerMarker = determineMarkerToPlace(gameState);
        GameState.POSITION_STATE opponentMarker = ( currentPlayerMarker == GameState.POSITION_STATE.X )  ?
                GameState.POSITION_STATE.O  :  GameState.POSITION_STATE.X;
        return opponentMarker;
    }

    /**
     * Find a list of all positions on the board which will make for a win.
     * E.g. There are two markers in a line, and a third unoccupied space.
     * @param gameState
     * @param markerToCheck X or O.
     * @return
     */
    protected Set<GameState.POSITIONS> getCompletingMoves(GameState gameState, GameState.POSITION_STATE markerToCheck ) {
        Set<GameState.POSITIONS> winningPositions = new HashSet<>();

        winningPositions.addAll(getPotentialMovesFromPartiallyOccupiedRows(gameState, markerToCheck, 2));
        winningPositions.addAll(getPotentialMovesFromPartiallyOccupiedColumns(gameState, markerToCheck, 2));
        winningPositions.addAll(getPotentialMovesFromPartiallyOccupiedDiagonals(gameState, markerToCheck, 2));

        return winningPositions;
    }

    /**
     *
     * @param gameState
     * @param markerToCheck X or O.
     * @param numberOfMarkersToFind 1 or 2.
     * @return
     */
    private Set<GameState.POSITIONS> getPotentialMovesFromPartiallyOccupiedRows(GameState gameState, GameState.POSITION_STATE markerToCheck, int numberOfMarkersToFind) {

        //For each row on the game board, find those that have the requisite combination of blank spaces and matching markers.
        //remember the blank positions as potential moves, and return them.
        Set<GameState.POSITIONS> potentialMoves = new HashSet<>();
        for ( int row = 0;  row < 3;  ++row ) {

            int matchingMarkerCount = 0;
            int blankPositionCount = 0;
            Set<GameState.POSITIONS> blankPositions = new HashSet<>();

            for ( int column = 0;  column < 3;  ++column) {

                GameState.POSITION_STATE currentPositionState = gameState.getPositionState(column, row);
                if ( currentPositionState.equals(markerToCheck) ) {
                    matchingMarkerCount += 1;
                } else if ( currentPositionState.equals(GameState.POSITION_STATE.UNCLAIMED) ) {
                    blankPositionCount += 1;
                    blankPositions.add( GameState.POSITIONS.fromColumnAndRow(column, row) );
                }
            }

            //check to see if the marker count matches what we're looking for.
            int numberOfBlanksToFind = 3 - numberOfMarkersToFind;       //if they're not already claimed, they should be blank.
            if ( (blankPositionCount == numberOfBlanksToFind)  &&  (matchingMarkerCount == numberOfMarkersToFind)  ) {
                potentialMoves.addAll(blankPositions);
            }
        }

        return potentialMoves;
    }

    /**
     *
     * @param gameState
     * @param markerToCheck X or O.
     * @param numberOfMarkersToFind 1 or 2.
     * @return
     */
    private Set<GameState.POSITIONS> getPotentialMovesFromPartiallyOccupiedColumns(GameState gameState, GameState.POSITION_STATE markerToCheck, int numberOfMarkersToFind) {

        //For each column on the game board, find those that have the requisite combination of blank spaces and matching markers.
        //remember the blank positions as potential moves, and return them.
        Set<GameState.POSITIONS> potentialMoves = new HashSet<>();
        for ( int column = 0;  column < 3;  ++column ) {

            int matchingMarkerCount = 0;
            int blankPositionCount = 0;
            Set<GameState.POSITIONS> blankPositions = new HashSet<>();

            for ( int row = 0;  row < 3;  ++row) {

                GameState.POSITION_STATE currentPositionState = gameState.getPositionState(column, row);
                if ( currentPositionState.equals(markerToCheck) ) {
                    matchingMarkerCount += 1;
                } else if ( currentPositionState.equals(GameState.POSITION_STATE.UNCLAIMED) ) {
                    blankPositionCount += 1;
                    blankPositions.add( GameState.POSITIONS.fromColumnAndRow(column, row) );
                }
            }

            //check to see if the marker count matches what we're looking for.
            int numberOfBlanksToFind = 3 - numberOfMarkersToFind;       //if they're not already claimed, they should be blank.
            if ( (blankPositionCount == numberOfBlanksToFind)  &&  (matchingMarkerCount == numberOfMarkersToFind)  ) {
                potentialMoves.addAll(blankPositions);
            }
        }

        return potentialMoves;
    }


    /**
     *
     * @param gameState
     * @param markerToCheck X or O.
     * @param numberOfMarkersToFind 1 or 2.
     * @return
     */
    private Set<GameState.POSITIONS> getPotentialMovesFromPartiallyOccupiedDiagonals(GameState gameState, GameState.POSITION_STATE markerToCheck, int numberOfMarkersToFind) {
        //for both diagonals on the game board, find those that have the requisite combination of blank spaces and matching markers.
        //remember the blank positions as potential moves, and return them.
        Set<GameState.POSITIONS> potentialMoves = new HashSet<>();

        //forward diagonal has identical columns/row values.
        int matchingMarkerCount = 0;
        int blankPositionCount = 0;
        Set<GameState.POSITIONS> blankPositions = new HashSet<>();
        for (int columnRow = 0;  columnRow < 3;  ++columnRow ) {
            GameState.POSITION_STATE currentPositionState = gameState.getPositionState(columnRow, columnRow);
            if ( currentPositionState.equals(markerToCheck) ) {
                matchingMarkerCount += 1;
            } else if ( currentPositionState.equals(GameState.POSITION_STATE.UNCLAIMED) ) {
                blankPositionCount += 1;
                blankPositions.add( GameState.POSITIONS.fromColumnAndRow(columnRow, columnRow) );
            }
        }
        //check to see if the marker count matches what we're looking for.
        int numberOfBlanksToFind = 3 - numberOfMarkersToFind;       //if they're not already claimed, they should be blank.
        if ( (blankPositionCount == numberOfBlanksToFind)  &&  (matchingMarkerCount == numberOfMarkersToFind)  ) {
            potentialMoves.addAll(blankPositions);
        }


        //now reset and do the backward diagonal, which has row = (2 - column).
        matchingMarkerCount = 0;
        blankPositionCount = 0;
        blankPositions = new HashSet<>();
        for (int column = 0;  column < 3;  ++column ) {
            int row = 2 - column;
            GameState.POSITION_STATE currentPositionState = gameState.getPositionState(column, row);
            if ( currentPositionState.equals(markerToCheck) ) {
                matchingMarkerCount += 1;
            } else if ( currentPositionState.equals(GameState.POSITION_STATE.UNCLAIMED) ) {
                blankPositionCount += 1;
                blankPositions.add( GameState.POSITIONS.fromColumnAndRow(column, row) );
            }
        }
        //check to see if the marker count matches what we're looking for.
        numberOfBlanksToFind = 3 - numberOfMarkersToFind;       //if they're not already claimed, they should be blank.
        if ( (blankPositionCount == numberOfBlanksToFind)  &&  (matchingMarkerCount == numberOfMarkersToFind)  ) {
            potentialMoves.addAll(blankPositions);
        }

        return potentialMoves;
    }

    /**
     * Find a list of all positions which would make for a 2nd in an unobstructed line.
     * E.g. there is one marker and two blank spaces.
     * @param gameState
     * @param markerToCheck
     * @return
     */
    protected Set<GameState.POSITIONS> getSetupMoves (GameState gameState, GameState.POSITION_STATE markerToCheck ) {
        Set<GameState.POSITIONS> setupPositions = new HashSet<>();

        setupPositions.addAll(getPotentialMovesFromPartiallyOccupiedRows(gameState, markerToCheck, 1));
        setupPositions.addAll(getPotentialMovesFromPartiallyOccupiedColumns(gameState, markerToCheck, 1));
        setupPositions.addAll(getPotentialMovesFromPartiallyOccupiedDiagonals(gameState, markerToCheck, 1));

        return setupPositions;
    }

    /**
     * From a list of equally weighing positions to choose, pick one.
     * @param positionSet
     * @return
     */
    protected GameState.POSITIONS chooseRandomMoveFromSet( Set<GameState.POSITIONS> positionSet ) throws GameAIException {

        //convert the set to an indexed list of positions. Then,
        //if the list has any moves at all, pick one at random. If not, throw an exception.
        List<GameState.POSITIONS> positionList = new ArrayList<>();
        positionList.addAll(positionSet);
        if (0 == positionList.size() ) {
            throw new NoAvailableMovesException();
        }

        int chosenMoveIndex = (int) Math.floor(Math.random() * positionList.size());
        GameState.POSITIONS chosenMove = positionList.get(chosenMoveIndex);
        return chosenMove;
    }

}
