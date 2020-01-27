package edu.usf.mail.rfhood.TicTacToeFX.logic.exception;

public class NoAvailableMovesException extends GameAIException {

    public NoAvailableMovesException() {
        super("The Strategy was requested to make a move, but there were actually no moves available.");
    }
}
