package edu.usf.mail.rfhood.logic;

import edu.usf.mail.rfhood.GameState;
import edu.usf.mail.rfhood.logic.exception.AttemptedCPUMoveFailedException;
import edu.usf.mail.rfhood.logic.exception.GameAIException;
import edu.usf.mail.rfhood.logic.exception.NoAvailableMovesException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * RandomStrategy chooses an unoccupied position on the game board in completely random fashion.
 * No regard is given for the game state or position.
 */
public class RandomStrategy extends Strategy {

    @Override
    public void makeComputerMove( GameState gameState ) throws GameAIException {

        //determine whether we should put an X or an O, depending on the current player to move.
        GameState.POSITION_STATE markerToPlace = this.determineMarkerToPlace(gameState);

        //take stock of which positions are still open.
        Set<GameState.POSITIONS> potentialMoves = gameState.getUnclaimedPositions();

        //pick one of the available moves and make it.
        //throws an exception if we have no moves, or else could not complete the given move for the given player.
        GameState.POSITIONS chosenMove = chooseRandomMoveFromSet(potentialMoves);
        boolean result = gameState.claimTerritory(markerToPlace, chosenMove.column, chosenMove.row);
        if ( false == result ) {
            throw new AttemptedCPUMoveFailedException(markerToPlace, chosenMove.column, chosenMove.row);
        }
    }

}
