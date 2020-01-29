package edu.usf.mail.rfhood.TicTacToeFX.logic;

import edu.usf.mail.rfhood.TicTacToeFX.logic.exception.AttemptedCPUMoveFailedException;
import edu.usf.mail.rfhood.TicTacToeFX.logic.exception.GameAIException;
import edu.usf.mail.rfhood.TicTacToeFX.state.GameState;

import java.util.Set;


/**
 * OneMoveStrategy is capable of thinking only one move ahead in the game.
 * It expresses moves in order of the following criteria.
 *      1) If there is a winning move available, take one of those.
 *      2) If there is an opponent's win on the next move, acquire the space to block.
 *      3) Fill in the 2nd of three positions to set up a win on the next move.
 *      4) Claim a 1st of three position.
 */
public class OneMoveStrategy extends Strategy {

    @Override
    public void makeComputerMove(GameState gameState) throws GameAIException {
        GameState.POSITION_STATE currentPlayerMarker = determineMarkerToPlace(gameState);
        GameState.POSITION_STATE opponentMarker = determineOpponentMarker(gameState);

        Set<GameState.POSITIONS> preferredMoves;       //list of equally weighted, preferred moves. (i.e. it doesn't matter which one we make as long as we make one of these)

        //check for otherwise unspecial, open moves.
        Set<GameState.POSITIONS> ordinaryMoves = gameState.getUnclaimedPositions();
        preferredMoves = ordinaryMoves;     //if we don't find anything better to do, move at random.
        logger.info("Ordinary moves detected: " + ordinaryMoves.size() + "  : " + ordinaryMoves.toString());

        //check for possible setup moves (2nd out of three) to set up a win.
        Set<GameState.POSITIONS> setupMoves = getSetupMoves( gameState, currentPlayerMarker );
        if ( setupMoves.size() > 0 ) {
            preferredMoves = setupMoves;        //if we have any setup moves, we'd rather make those.
            logger.info("Setup moves detected: " + setupMoves.size() + "  : " + setupMoves.toString());
        }

        //check for necessary blocks to make.
        Set<GameState.POSITIONS> blockingMoves = getCompletingMoves( gameState, opponentMarker );   //completing moves for opponent are needed blocks for us.
        if ( blockingMoves.size() > 0 ) {
            preferredMoves = blockingMoves;        //we must block over setting up a win, because we'd lose on the next turn.
            logger.info("Blocking moves detected: " + blockingMoves.size() + "  : " + blockingMoves.toString());
        }

        // see if we have any win conditions:
        Set<GameState.POSITIONS> winningMoves = getCompletingMoves( gameState, currentPlayerMarker );
        if ( winningMoves.size() > 0 ) {
            preferredMoves = winningMoves;        //if we can win on the next turn, then by all means do that first.
            logger.info("Winning moves detected: " + winningMoves.size() + "  : " + winningMoves.toString());
        }

        //now that we have a list of equally weighted moves
        //select one at random and make the move for the player.
        GameState.POSITIONS chosenMove = chooseRandomMoveFromSet(preferredMoves);
        boolean result = gameState.claimTerritory(currentPlayerMarker, chosenMove.column, chosenMove.row);
        if ( false == result ) {
            throw new AttemptedCPUMoveFailedException(currentPlayerMarker, chosenMove.column, chosenMove.row);
        }

    }

}
