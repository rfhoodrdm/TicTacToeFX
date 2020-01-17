package edu.usf.mail.rfhood.logic.exception;

public class PlayerCannotMoveNowException extends GameAIException {

    public PlayerCannotMoveNowException() {
        super("The Strategy was requested to make a move, but game state indicated that it was not an appropriate time for a player to make a move.");
    }
}
