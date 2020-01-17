package edu.usf.mail.rfhood.logic.exception;

import edu.usf.mail.rfhood.GameState;

public class AttemptedCPUMoveFailedException extends GameAIException {

    public AttemptedCPUMoveFailedException(GameState.POSITION_STATE attemptedMarker, int column, int row) {
        super("The Strategy was requested to make a move for " + attemptedMarker.toString() + ", but the game state rejected the request.  Column: " + column + "  Row: " + row);
    }
}
