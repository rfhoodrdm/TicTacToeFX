package edu.usf.mail.rfhood.TicTacToeFX.logic;

import edu.usf.mail.rfhood.TicTacToeFX.logic.exception.AttemptedCPUMoveFailedException;
import edu.usf.mail.rfhood.TicTacToeFX.logic.exception.GameAIException;
import edu.usf.mail.rfhood.TicTacToeFX.logic.exception.NoAvailableMovesException;
import edu.usf.mail.rfhood.TicTacToeFX.state.GameBoard;
import edu.usf.mail.rfhood.TicTacToeFX.state.GameState;

import java.util.*;

import static edu.usf.mail.rfhood.TicTacToeFX.logic.MinMaxStrategy.MIN_OR_MAX.MAX;
import static edu.usf.mail.rfhood.TicTacToeFX.logic.MinMaxStrategy.MIN_OR_MAX.MIN;
import static edu.usf.mail.rfhood.TicTacToeFX.state.GameState.POSITION_STATE.O;
import static edu.usf.mail.rfhood.TicTacToeFX.state.GameState.POSITION_STATE.X;

/**
 * MinMaxStrategy represents the AI's ability to play a perfect game of TicTacToe.
 */
public class MinMaxStrategy extends Strategy {

    /**
     * MinMax strategy makes the move for the GameAI in the current position.
     * @param gameState
     * @throws GameAIException
     */
    @Override
    public void makeComputerMove(GameState gameState) throws GameAIException {
        //get the game board position state and the marker of the turn player from the game state.
        GameState.POSITION_STATE currentTurnPlayerMarker = determineMarkerToPlace(gameState);
        GameBoard gameBoard = gameState.getGameBoard();

        GameState.POSITIONS chosenMove = chooseMoveByMinMax(gameBoard, currentTurnPlayerMarker);
        boolean result = gameState.claimTerritory(currentTurnPlayerMarker, chosenMove.column, chosenMove.row);
        if ( false == result ) {
            throw new AttemptedCPUMoveFailedException(currentTurnPlayerMarker, chosenMove.column, chosenMove.row);
        }
    }

    /**
     * Easy utility method to get the opponent's marker. If Player is X, opponent must be O, and vice versa.
     * @param playerMarker
     * @return
     */
    private GameState.POSITION_STATE determineOpponentMarker( GameState.POSITION_STATE playerMarker ) {
        return  (playerMarker  ==  X)  ?
                O  :  X;
    }


    /**
     * Leverage the min-max algorithm to score all possible moves. Then, select from amongst the highest ranked.
     * @param gameBoard
     * @param playerMarker
     * @return
     * @throws GameAIException
     */
    public GameState.POSITIONS chooseMoveByMinMax( GameBoard gameBoard, GameState.POSITION_STATE playerMarker ) throws GameAIException {
        //for each possible move available, we must score each one and pick the highest rated position as our move.
        //if there is a tie for highest scored moves, pick one at random.
        Map<GameState.POSITIONS, Integer> moveScoreMap = new HashMap<>();    //map holding the score for each possible move.
        GameState.POSITION_STATE opponentMarker = determineOpponentMarker( playerMarker );
        int highestMoveScore = Integer.MIN_VALUE;       //assume the worst initially...

        for ( GameState.POSITIONS tentativeMove: gameBoard.getUnclaimedPositions() ) {
            //now clone the board, make the tentative move, and rank the new position.
            //also, compare the move score to our current highest score seen. If this score exceeds it, it becomes
            //the new highest.
            GameBoard tentativeNewBoard = gameBoard.clone();
            tentativeNewBoard.setPositionState( tentativeMove.column, tentativeMove.row, playerMarker );
            int moveScore = scoreGameBoard(tentativeNewBoard, opponentMarker, MIN);   //opponent chooses minimum scores here.
            moveScoreMap.put( tentativeMove, moveScore );
            if ( moveScore > highestMoveScore ) {
                highestMoveScore = moveScore;
            }
        }

        //now, pick one of the highest scored moves and return that as our selected move.
        //if there are no moves to pick from, then throw an exception.
        if ( 0 >= moveScoreMap.size() ) {
            throw new NoAvailableMovesException();
        }

        //now, create a set of moves tying for highest score.
        Set<GameState.POSITIONS> preferredMoves = new HashSet<>();
        for ( GameState.POSITIONS currentPosition: moveScoreMap.keySet() ) {
            Integer currentPositionMoveScore = moveScoreMap.get(currentPosition);
            if ( currentPositionMoveScore.equals(Integer.valueOf(highestMoveScore)) ) {
                preferredMoves.add(currentPosition);
            }
        }

        logger.info("Ranked moves:  " + moveScoreMap.toString());
        return chooseRandomMoveFromSet(preferredMoves);
    }


    /**
     * Score the game board according to the minimax algorithm.
     * We don't keep track of depth, since the game is simple enough to keep going until we hit a terminal state.
     * @param gameBoard
     * @param playerMarker
     * @param minOrMax Are we minimizing or maximizing the score from the player's perspective.
     * @return
     */
    public int scoreGameBoard(GameBoard gameBoard, GameState.POSITION_STATE playerMarker, MIN_OR_MAX minOrMax ) {
        //recursive base cases: terminal states.
        //if the board is a terminal state for a turn player's victory,
        //  return 1 for MAX, -1 for MIN.
        //if the board is a terminal state for the opponent player's victory
        //  return -1 for MAX, 1 for MIN.
        //failing both of these, return 0 if the game board is in a draw state.
        GameState.POSITION_STATE opponentMarker = determineOpponentMarker(playerMarker);
        boolean noMovesLeft = ( 0  == gameBoard.getUnclaimedPositions().size() );
        boolean xVictory = gameBoard.checkForVictory(X);
        boolean oVictory = gameBoard.checkForVictory(O);
        boolean gameWon = xVictory || oVictory;
        boolean turnPlayerVictory = ( xVictory && X.equals(playerMarker) ) || ( oVictory && O.equals(playerMarker) );
        boolean maxBoardValue = ( turnPlayerVictory  &&  MAX.equals(minOrMax) )  ||  (!turnPlayerVictory  &&  MIN.equals(minOrMax));

        if ( gameWon && maxBoardValue ) {
            return 1;
        } else if ( gameWon && !maxBoardValue ) {
            return -1;
        } else if ( noMovesLeft ) {
            //All draws worth 0 points.
            return 0;
        }

        //if we're not in a terminal state, then we have to evaluate the position for the player based on
        //whether we're minimizing or maximizing.
        if ( MAX == minOrMax ) {

            //find the move with the highest score available.
            int maxScoreSoFar = Integer.MIN_VALUE;       //assume the worst initially...
            for ( GameState.POSITIONS moveToMake: gameBoard.getUnclaimedPositions() ) {
                //clone the game board, make the move, and score the move.
                GameBoard tentativeGameBoard = gameBoard.clone();
                tentativeGameBoard.setPositionState( moveToMake, playerMarker );
                int tentativeMoveScore = scoreGameBoard( tentativeGameBoard, opponentMarker, MIN);      //opponent will choose minimums here
                if ( tentativeMoveScore > maxScoreSoFar ) {
                    maxScoreSoFar = tentativeMoveScore;
                }
            }

            return maxScoreSoFar;

        } else {

            //find the move with the lowest score availale.
            int minScoreSoFar = Integer.MAX_VALUE;      //assume the worst initially...
            for ( GameState.POSITIONS moveToMake: gameBoard.getUnclaimedPositions() ) {
                //clone the game board, make the move, and score it.
                GameBoard tentativeGameBoard = gameBoard.clone();
                tentativeGameBoard.setPositionState( moveToMake, playerMarker );
                int tentativeMoveScore = scoreGameBoard ( tentativeGameBoard, opponentMarker, MAX);     //opponent will choose maximums here.
                if ( tentativeMoveScore < minScoreSoFar ) {
                    minScoreSoFar = tentativeMoveScore;
                }
            }

            return minScoreSoFar;
        }

    }


//    public int scoreGameBoard(GameBoard gameBoard, GameState.POSITION_STATE playerMarker, boolean isTurnPlayer) {
//        //recursive base cases: terminal states
//        //if the present board is a victory board for the current player, return 1.
//        //else if a victory for the opponent, return -1.
//        //failing both of these, return 0 if the game is a draw.
//        GameState.POSITION_STATE opponentMarker = determineOpponentMarker(playerMarker);
//        boolean noMovesLeft = ( 0  == gameBoard.getUnclaimedPositions().size() );
//        if ( gameBoard.checkForVictory(playerMarker) ) {
//            return 1;
//        } else if ( gameBoard.checkForVictory(opponentMarker) ) {
//            return -1;
//        } else if ( noMovesLeft ) {
//            return 0;
//        }
//
//
//        //if we're not in a terminal state, then score the possible moves according to the utility and
//        //whether or not the turn player is to make the move or not.
//        if ( isTurnPlayer ) {
//
//            //if the turn player is the one making the move being evaluated, find maximal score.
//            int bestBoardScore = Integer.MIN_VALUE;
//            for ( GameState.POSITIONS moveToMake: gameBoard.getUnclaimedPositions() ) {
//                //clone the game board, make the tentative move, and test the move by scoring the new board.
//                //if the score is a new best we've seen, remember it.
//                GameBoard tentativeGameBoard = gameBoard.clone();
//                tentativeGameBoard.setPositionState( moveToMake, playerMarker);
//                int tentativeMoveScore = scoreGameBoard( tentativeGameBoard, opponentMarker, false);
//                if ( tentativeMoveScore > bestBoardScore ) {
//                    bestBoardScore = tentativeMoveScore;
//                }
//            }
//
//            //whatever the best score we've seen, that's the rating we assign to the given game board for current turn player.
//            return bestBoardScore;
//        } else {
//
//            //if the opposing player is the one making the move being evaluated, find the minimum score.
//            int worstBoardScore = Integer.MAX_VALUE;
//            for ( GameState.POSITIONS moveToMake: gameBoard.getUnclaimedPositions() ) {
//                //clone the game board, make the tentative move, and test the move by scoring the new board.
//                //if the score is a new worse we've seen, remember it.
//                GameBoard tentativeGameBoard = gameBoard.clone();
//                tentativeGameBoard.setPositionState( moveToMake, opponentMarker);
//                int tentativeMoveScore = -1 * scoreGameBoard( tentativeGameBoard, playerMarker, true);
//                if ( tentativeMoveScore < worstBoardScore ) {
//                    worstBoardScore = tentativeMoveScore;
//                }
//            }
//
//            //whatever the worst score we've seen, that's the rating we assign to the given game board for the opponent player.
//            return worstBoardScore;
//        }
//    }

    /**
     * Are we minimizing or maximizing the score?
     */
    public static enum MIN_OR_MAX {
        MIN,
        MAX;
    }

}
